package fraud.client.v1

import common.exceptions.ClientException
import common.exceptions.HttpEvent
import common.pojo.StatusEntity
import common.rest.client.RestServiceClientSupport
import common.rest.client.transport.HttpClientSSLKeyStore
import common.rest.client.transport.ITransport
import common.rest.client.transport.support.ObjectMapperProvider
import fraud.api.v1.velocity.Velocity
import org.apache.commons.io.IOUtils

/**
 * Client service client implementation
 */
class FraudServiceClient extends RestServiceClientSupport implements IFraudServiceClient {

    private static final HttpClientSSLKeyStore TRUST_STORE

    static {
        try {
            final InputStream trustStoreStream =
                    FraudServiceClient.class.getResourceAsStream('/cert/oauthTrustStore.jks')
            final byte[] trustStoreBytes = IOUtils.toByteArray(trustStoreStream)
            TRUST_STORE = new HttpClientSSLKeyStore(new ByteArrayInputStream(trustStoreBytes), '5ecret0AUTHPa55word')
        } catch (Exception e) {
            TRUST_STORE = null
            throw new ClientException(new StatusEntity('400', e), e, HttpEvent.InvalidClientInput)
        }
    }

    FraudServiceClient(final String endpoint, final String clientID, final ITransport transport) {
        this.endpoint = endpoint
        this.clientID = clientID
        this.transport = transport

        mapper = ObjectMapperProvider.defaultMapper
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    List<Velocity> cassandraCheck(final String transactionGUID, final Map<String, String[]> velocityRQ) {
        final String confUrl = "${endpoint}/velocity/cassandra/check/"
        final ITransport.Response<List, StatusEntity> response =
                transport.performPost(confUrl, buildHeaders(transactionGUID),
                        marshal(velocityRQ), createResponseHandler(List.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    List<Velocity> redisCheck(final String transactionGUID, final Map<String, String[]> velocityRQ) {
        final String confUrl = "${endpoint}/velocity/redis/check/"
        final ITransport.Response<List, StatusEntity> response =
                transport.performPost(confUrl, buildHeaders(transactionGUID),
                        marshal(velocityRQ), createResponseHandler(List.class))
        return checkForError(response)
    }
}
