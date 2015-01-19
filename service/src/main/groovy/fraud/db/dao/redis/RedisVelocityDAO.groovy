package fraud.db.dao.redis

import fraud.api.v1.velocity.Aggregation
import fraud.api.v1.velocity.Transaction
import org.joda.time.DateTime
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Tiger on 24.09.14.
 */
class RedisVelocityDAO implements IRedisVelocityDAO {
    StringRedisTemplate redis

    @Transactional
    @Override
    void logData(final Map<String, String[]> newData) {
        Long now = DateTime.now().millis
        Long transactionID = redis.opsForValue().increment('id:transactions', 1)
        redis.opsForZSet().add('transactions:history', transactionID as String, now)
        redis.opsForHash().put("transaction:$transactionID:details" as String, 'createDate', now as String)

        redis.executePipelined(new RedisCallback<Object>() {
            @Override
            Object doInRedis(final RedisConnection connection) throws DataAccessException {
                StringRedisConnection c = connection as StringRedisConnection
                newData.each { metricType, metricValues ->
                    if (metricValues) {
                        c.lPush("transaction:$transactionID:data:$metricType".toString(), metricValues as String[])
                        metricValues?.each { metricValue ->
                            c.zAdd("$metricType:$metricValue:history".toString(), now, transactionID as String)
                        }
                    }
                }
                return null;
            }
        })
        redis.convertAndSend(Transaction.SUBSCRIPTION_PATTERN, transactionID as String);
    }

    @Override
    Set<String> getHistoricalIDs(final String key, final Long period) {
        Long now = DateTime.now().millis
        return redis.boundZSetOps(key).rangeByScore(now - period, now)
    }

    @Override
    Set<String> getHistoricalIDs(final String key, final Long startDateMillis, final Long endDateMillis) {
        return redis.boundZSetOps(key).rangeByScore(startDateMillis, endDateMillis)
    }

    @Override
    Set<String> getHistoricalIDs(final String key, final Long startDateMillis, final Long endDateMillis, final Long limit) {
        Set<String> tranIDs = redis.boundZSetOps(key).rangeByScore(startDateMillis, endDateMillis)
        return tranIDs?.size() > limit ? tranIDs?.toList()?.subList(0, limit as Integer) : tranIDs
        /*return redis.connectionFactory.connection.zRangeByScore(key.bytes, startDateMillis, endDateMillis, 0, limit)
                .collect { new String(it) }*/
    }

    @Override
    List<String> getHistoricalData(final String dataType, final Collection<String> transactionIDs) {
        List<String> history = []
        transactionIDs.each {
            history += redis.boundListOps("transaction:$it:data:$dataType" as String) range(0, -1);
        }
        return history
    }

    @Override
    List<Transaction> getHistoricalData(final Collection<String> transactionIDs) {
        Map<String, Transaction> transactions = [:]
        transactionIDs.each { id ->
            Set<String> keys = redis.keys("transaction:$id:data:*".toString())
            Long createDate = redis.opsForHash().get("transaction:$id:details" as String, 'createDate') as Long
            transactions[(id)] = new Transaction(id: Long.valueOf(id), data: [:], createDate: (createDate ? new Date(createDate) : null))
            keys?.each {
                transactions[(id)].data << [(it.tokenize(':')[-1]): redis.boundListOps(it).range(0, -1)]
            }
        }
        return transactions.values()?.toList() ?: []
    }

    @Override
    Long getTransactionCreateDateTime(final String transactionID) {
        return redis.opsForHash().get("transaction:$transactionID:details" as String, 'createDate') as Long
    }

    void saveMetric(String key, Aggregation type, Double aggregatedValue) {
        redis.opsForHash().put(key, type as String, aggregatedValue as String)
    }

    Map<Aggregation, Double> getMetrics(String key) {
        Map<Aggregation, Double> result = [:]
        redis.opsForHash().entries(key).each { aggregation, value ->
            result << [(aggregation as Aggregation): value as Double]
        }
        return result
    }
}
