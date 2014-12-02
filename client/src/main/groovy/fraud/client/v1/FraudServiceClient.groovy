package fraud.client.v1

import common.exceptions.ClientException
import common.pojo.StatusEntity
import common.rest.client.RestServiceClientSupport
import common.rest.client.transport.HttpClientSSLKeyStore
import common.rest.client.transport.ITransport
import common.rest.client.transport.support.ObjectMapperProvider
import fraud.api.v1.velocity.Velocity
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.http.client.methods.HttpPost

import javax.ws.rs.core.HttpHeaders

import static common.exceptions.HttpEvent.InvalidClientInput
import static common.web.RequestHelper.Param.CLIENT_ID
import static common.web.RequestHelper.Param.TRANSACTION_GUID

/**
 * Client service client implementation
 */
class FraudServiceClient extends RestServiceClientSupport implements IFraudServiceClient {

    private static final HttpClientSSLKeyStore TRUST_STORE

    String endpoint
    String clientID

    static {
        try {
            final InputStream trustStoreStream = FraudServiceClient.class.getResourceAsStream('/cert/oauthTrustStore.jks')
            final byte[] trustStoreBytes = IOUtils.toByteArray(trustStoreStream)
            TRUST_STORE = new HttpClientSSLKeyStore(new ByteArrayInputStream(trustStoreBytes), '5ecret0AUTHPa55word')
        } catch (Exception e) {
            throw new ClientException(new StatusEntity('400', e), e, InvalidClientInput)
        }
    }

    FraudServiceClient() {
    }

    FraudServiceClient(final String endpoint, final String clientID, final ITransport transport) {
        this.endpoint = endpoint
        this.clientID = clientID
        this.transport = transport

        mapper = ObjectMapperProvider.defaultMapper

    }

    private Map<String, String> buildHeaders(final String transactionGUID, final String url, final String method)
            throws ClientException {
        validateGUID(TRANSACTION_GUID.queryParamName, transactionGUID, true)

        final String txGUID = StringUtils.defaultIfEmpty(transactionGUID, UUID.randomUUID().toString())
        final Map<String, String> headers = new HashMap<>()
        headers.put(HttpHeaders.ACCEPT, JSON_CONTENT_TYPE)
        headers.put(HttpHeaders.CONTENT_TYPE, JSON_CONTENT_TYPE)
        headers.put(TRANSACTION_GUID.httpHeaderName, txGUID)
        headers.put(CLIENT_ID.httpHeaderName, clientID)
        return headers
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    List<Velocity> cassandraCheck(final String transactionGUID, final Map<String, String[]> velocityRQ) {
        final String confUrl = "${endpoint}/velocity/cassandra/check/"
        final ITransport.Response<List, StatusEntity> response =
                transport.performPost(confUrl, buildHeaders(transactionGUID, endpoint, HttpPost.METHOD_NAME),
                        marshal(velocityRQ), createResponseHandler(List.class))
        return checkForError(response)
    }

    @Override
    @SuppressWarnings(['rawtypes', 'unchecked'])
    List<Velocity> redisCheck(final String transactionGUID, final Map<String, String[]> velocityRQ) {
        final String confUrl = "${endpoint}/velocity/redis/check/"
        final ITransport.Response<List, StatusEntity> response =
                transport.performPost(confUrl, buildHeaders(transactionGUID, endpoint, HttpPost.METHOD_NAME),
                        marshal(velocityRQ), createResponseHandler(List.class))
        return checkForError(response)
    }
}
