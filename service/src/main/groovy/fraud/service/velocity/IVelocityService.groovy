package fraud.service.velocity

import org.joda.time.DateTime

/**
 * Black lists manipulation logic API
 */
interface IVelocityService {

    def cassandraGetMetrics(final Map<String, String[]> velocityRQ, final boolean asyncUpdate)

    def redisGetMetrics(final Map<String, String[]> velocityRQ, final boolean asyncUpdate)

    def cassandraGetHistory(final Map<String, String> filter, final DateTime startDate, final DateTime endDate,
                            final UUID startID, final UUID endID)

    def redisGetHistory(final Map<String, String> filter, final DateTime startDate, final DateTime endDate,
                        final String startID, final String endID)
}
