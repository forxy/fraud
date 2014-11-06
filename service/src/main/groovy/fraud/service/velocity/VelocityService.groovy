package fraud.service.velocity

import com.datastax.driver.core.utils.UUIDs
import fraud.api.v1.velocity.*
import fraud.db.dao.cassandra.ICassandraVelocityDAO
import fraud.db.dao.redis.IRedisVelocityDAO
import fraud.util.DBCache
import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static groovyx.gpars.GParsPool.withExistingPool
import static groovyx.gpars.GParsPool.withPool

/**
 * Implementation class for BlackListService business logic
 */
class VelocityService implements IVelocityService {

    private static final int DEFAULT_PAGE_SIZE = 50

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityService.class)

    ICassandraVelocityDAO cassandraDAO
    IRedisVelocityDAO redisDAO;
    DBCache dbCache
    private final static java.util.concurrent.ForkJoinPool FJP = new java.util.concurrent.ForkJoinPool(2)

    @Override
    def cassandraGetMetrics(final Map<String, String[]> velocityRQ, final boolean asyncUpdate) {
        def velocityMetrics = []
        withPool { ForkJoinPool pool ->
            dbCache.configs.collectParallel { VelocityConfig config ->
                withExistingPool(pool) {
                    GParsPool.runForkJoin(config, config.primaryMetrics.asList(), 0, [:], null, null, velocityRQ) {
                        VelocityConfig conf, List<String> primaryMetrics, int index, Map<String, String> key,
                        String keyType, String keyValue, Map<String, String[]> rq ->
                            def metrics = [:]
                            if (index < primaryMetrics.size()) {
                                String metric = primaryMetrics[index]
                                rq[metric].each {
                                    key << [(metric): it]
                                    String fullKeyType = keyType ? "$keyType:$metric" : metric
                                    String fullKeyValue = keyValue ? "$keyValue:$it" : it
                                    if (index + 1 < primaryMetrics.size()) {
                                        forkOffChild(conf, primaryMetrics, index + 1, key.clone(), fullKeyType, fullKeyValue, rq)
                                    } else {
                                        def velocityKey = key.clone()
                                        metrics << [(velocityKey): [:]]
                                        List<Metric> metricsLst = cassandraDAO.getMetrics(new PartitionKey(
                                                metricType: fullKeyType, metricValue: fullKeyValue))
                                        metricsLst.each {
                                            if (!metrics[velocityKey][(it.key.secondaryMetric)]) {
                                                metrics[velocityKey] << [(it.key.secondaryMetric): [:]]
                                            }
                                            metrics[velocityKey][(it.key.secondaryMetric)] <<
                                                    [(it.key.aggregationType): it.aggregatedValue]
                                            /*metrics[velocityKey] << [(it.key.secondaryMetric):
                                                                 [(it.key.aggregationType): it.aggregatedValue]]*/
                                        }
                                    }
                                }
                                childrenResults?.each {
                                    metrics += it
                                }
                            }
                            return metrics
                    }
                }
            }.each {
                it.each { key, value ->
                    velocityMetrics += new Velocity(primaryMetrics: key, aggregations: value)
                }
            }
        }
        if (asyncUpdate) {
            FJP.submit(new Runnable() {
                void run() {
                    updateMetricsCassandra(velocityRQ)
                }
            })
        }
        return velocityMetrics
    }

    void updateMetricsCassandra(Map<String, String[]> velocityRQ) {
        logCassandraData(velocityRQ)
        withPool { ForkJoinPool pool ->
            dbCache.configs?.eachParallel { VelocityConfig config ->
                withExistingPool(pool) {
                    GParsPool.runForkJoin(config, config.primaryMetrics.asList(), 0, null, null, null, velocityRQ) {
                        conf, List<String> primaryMetrics, int index, Set<UUID> tranIds, String keyType, String keyValue, rq ->
                            if (index < primaryMetrics.size()) {
                                String metric = primaryMetrics[index]
                                rq[metric].each {
                                    String fullKeyType = keyType ? "$keyType:$metric" : metric
                                    String fullKeyValue = keyValue ? "$keyValue:$it" : it

                                    Set<UUID> newIds = cassandraDAO.getHistoricalIDs(
                                            new PartitionKey(metricType: metric, metricValue: it), config.period)
                                    Set<UUID> tranIDsIntersection = tranIds ? tranIds.intersect(newIds) : newIds

                                    if (index + 1 < primaryMetrics.size()) {
                                        forkOffChild(config, primaryMetrics, index + 1, tranIDsIntersection,
                                                fullKeyType, fullKeyValue, rq)
                                    } else {
                                        def transactions = cassandraDAO.getHistoricalData(tranIDsIntersection)
                                        config.aggregationConfigs?.each { AggregationConfig aggConf ->

                                            List<String> history = transactions.collect {
                                                it.key.dataType == aggConf.secondaryMetric ? it.data : []
                                            }.flatten()
                                            cassandraDAO.saveMetric(new Metric(
                                                    key: new Metric.MetricCompositeKey(
                                                            id: new PartitionKey(
                                                                    metricType: fullKeyType,
                                                                    metricValue: fullKeyValue),
                                                            secondaryMetric: aggConf.secondaryMetric,
                                                            aggregationType: aggConf.aggregation
                                                    ),
                                                    aggregatedValue: aggConf.aggregation.apply(history)
                                            ))
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    void logCassandraData(final Map<String, String[]> velocityRQ) {
        UUID transactionID = UUIDs.startOf(DateTime.now().millis)
        velocityRQ.each { metricType, metricValues ->
            cassandraDAO.logTransaction(new TransactionData(
                    key: new TransactionData.TransactionCompositeKey(
                            transactionID: transactionID,
                            dataType: metricType
                    ),
                    data: metricValues
            ))
            metricValues?.each { metricValue ->
                cassandraDAO.logData(new History(
                        key: new History.HistoryCompositeKey(
                                id: new PartitionKey(metricType: metricType, metricValue: metricValue),
                                transactionID: transactionID
                        )
                ))
            }
        }
    }

    @Override
    def redisGetMetrics(Map<String, String[]> velocityRQ, final boolean asyncUpdate) {
        def velocityMetrics = []
        withPool { ForkJoinPool pool ->
            dbCache.configs.collectParallel { VelocityConfig config ->
                withExistingPool(pool) {
                    GParsPool.runForkJoin(config, config.primaryMetrics.asList(), 0, [:], null, velocityRQ) {
                        VelocityConfig conf, List<String> primaryMetrics, int index, Map<String, String> key,
                        String metricKey, Map<String, String[]> rq ->
                            def metrics = [:]
                            if (index < primaryMetrics.size()) {
                                String metric = primaryMetrics[index]
                                rq[metric].each {
                                    key << [(metric): it]
                                    String newKey = "$metric:$it"
                                    String fullKey = metricKey ? "$metricKey:$newKey" : newKey
                                    if (index + 1 < primaryMetrics.size()) {
                                        forkOffChild(conf, primaryMetrics, index + 1, key.clone(), fullKey, rq)
                                    } else {
                                        def velocityKey = key.clone()
                                        metrics << [(velocityKey): [:]]
                                        conf.aggregationConfigs.each {
                                            metrics[velocityKey] <<
                                                    [(it.secondaryMetric): redisDAO.getMetrics(
                                                            "metrics:$fullKey:$it.secondaryMetric".toString())]
                                        }
                                    }
                                }
                                childrenResults?.each {
                                    metrics += it
                                }
                            }
                            return metrics
                    }
                }
            }.each {
                it.each { key, value ->
                    velocityMetrics += new Velocity(primaryMetrics: key, aggregations: value)
                }
            }
        }
        if (asyncUpdate) {
            FJP.submit(new Runnable() {
                void run() {
                    updateMetricsRedis(velocityRQ)
                }
            })
        }
        return velocityMetrics
    }

    void updateMetricsRedis(Map<String, String[]> velocityRQ) {
        redisDAO.logData(velocityRQ)
        /*withPool() { ForkJoinPool pool ->
            dbCache.configs?.eachParallel { VelocityConfig config ->
                withExistingPool(pool) {
                    GParsPool.runForkJoin(config, config.primaryMetrics.asList(), 0, null, null, velocityRQ) {
                        conf, List<String> primaryMetrics, int index, Set<String> tranIds, String key, rq ->
                            if (index < primaryMetrics.size()) {
                                String metric = primaryMetrics[index]
                                rq[metric].each {
                                    String newKey = "$metric:$it"
                                    String fullKey = key ? "$key:$newKey" : newKey

                                    Set<String> tranIDsIntersection = []
                                    Set<String> newIds = redisDAO.getHistoricalIDs("$newKey:history", config.period)
                                    tranIDsIntersection = tranIds ? tranIds.intersect(newIds) : newIds

                                    if (index + 1 < primaryMetrics.size()) {
                                        forkOffChild(config, primaryMetrics, index + 1, tranIDsIntersection, fullKey, rq)
                                    } else {
                                        config.aggregationConfigs?.each { AggregationConfig aggConf ->
                                            List<String> history = redisDAO.getHistoricalData(
                                                    aggConf.secondaryMetric, tranIDsIntersection)
                                            redisDAO.saveMetric(
                                                    "metrics:$fullKey:$aggConf.secondaryMetric".toString(),
                                                    aggConf.aggregation, aggConf.aggregation.apply(history))
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }*/
    }

    @Override
    def cassandraGetHistory(final Map<String, String> filter, final DateTime startDate, final DateTime endDate,
                            final UUID startID, final UUID endID) {
        Long finishMs = endDate?.millis ?: startDate ? startDate.plusDays(1).withTimeAtStartOfDay().millis : DateTime.now().millis
        Long startMs = startDate?.millis ?: DateTime.now().withTimeAtStartOfDay().millis
        def history = []
        Set<UUID> tranIDs = cassandraDAO.getHistoricalIDs(
                new PartitionKey(metricType: 'ServiceInfo', metricValue: 'History'), startMs, finishMs, startID, endID)
        filter?.each { metricType, metricValue ->
            if (metricValue) {
                tranIDs = tranIDs.intersect(cassandraDAO.getHistoricalIDs(
                        new PartitionKey(metricType: metricType, metricValue: metricValue),
                        startMs, finishMs, startID, endID))
            }
        }
        cassandraDAO.getHistoricalData(tranIDs)
                .groupBy { it.key.transactionID }
                .each { id, transactionDataList ->
            def transaction = new Transaction(id: id, createDate: new Date(UUIDs.unixTimestamp(id)), data: [:])
            transactionDataList.each {
                transaction.data << [(it.key.dataType): it.data]
            }
            history << transaction
        }
        return history?.size() > DEFAULT_PAGE_SIZE ? history?.subList(0, DEFAULT_PAGE_SIZE) : history
    }

    @Override
    def redisGetHistory(final Map<String, String> filter, final DateTime startDate, final DateTime endDate,
                        final String startID, final String endID) {
        Long finish = endDate?.millis ?: startDate ? startDate.plusDays(1).withTimeAtStartOfDay().millis : DateTime.now().millis
        Long start = startDate?.millis ?: DateTime.now().withTimeAtStartOfDay().millis
        if (startID) start = redisDAO.getTransactionCreateDateTime(startID)
        if (endID) finish = redisDAO.getTransactionCreateDateTime(endID)
        Collection<String> tranIDs = []
        if (filter) {
            filter.each { metricType, metricValue ->
                if (metricType && metricValue) {
                    if (!tranIDs) {
                        tranIDs = redisDAO.getHistoricalIDs("$metricType:$metricValue:history", start, finish)
                    } else {
                        tranIDs = tranIDs.intersect(
                                redisDAO.getHistoricalIDs("$metricType:$metricValue:history", start, finish))
                    }
                }
            }
            tranIDs = tranIDs?.size() > DEFAULT_PAGE_SIZE ? tranIDs?.toList()?.subList(0, DEFAULT_PAGE_SIZE) : tranIDs
        } else {
            tranIDs = redisDAO.getHistoricalIDs('transactions:history', start, finish, DEFAULT_PAGE_SIZE)

        }
        if (startID) tranIDs.removeAll { Long.valueOf(it) <= Long.valueOf(startID) }
        if (endID) tranIDs.removeAll { Long.valueOf(it) >= Long.valueOf(endID) }
        return redisDAO.getHistoricalData(tranIDs)?.sort {it.id}
    }
}
