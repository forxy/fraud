package fraud.client.v1

import fraud.api.v1.velocity.Velocity

interface IFraudServiceClient {

    List<Velocity> cassandraCheck(final String transactionGUID, final Map<String, String[]> velocityRQ);

    List<Velocity> redisCheck(final String transactionGUID, final Map<String, String[]> velocityRQ);
}
