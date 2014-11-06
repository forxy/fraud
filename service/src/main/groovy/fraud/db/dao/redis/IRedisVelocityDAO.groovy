package fraud.db.dao.redis

import fraud.api.v1.velocity.Aggregation
import fraud.api.v1.velocity.Transaction

/**
 * Velocity DAO vor redis DataSource
 */
interface IRedisVelocityDAO {

    void logData(Map<String, String[]> newData)

    Set<String> getHistoricalIDs(String key, Long period)

    Set<String> getHistoricalIDs(final String key, final Long startDateMillis, final Long endDateMillis)

    Set<String> getHistoricalIDs(final String key, final Long startDateMillis, final Long endDateMillis, final Long limit)

    List<String> getHistoricalData(String dataType, Collection<String> transactionIDs)

    List<Transaction> getHistoricalData(Collection<String> transactionIDs)

    Long getTransactionCreateDateTime(String transactionID)

    void saveMetric(String key, Aggregation type, Double aggregatedValue)

    Map<Aggregation, Double> getMetrics(String key)
}