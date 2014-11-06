package fraud.test.logic

import org.apache.cxf.jaxrs.impl.HttpHeadersImpl
import org.apache.cxf.jaxrs.impl.UriInfoImpl
import org.apache.cxf.message.Message
import org.apache.cxf.message.MessageImpl
import org.junit.Assert
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import fraud.db.dao.ITransactionDAO
import fraud.controller.v1.FraudCheckController
import fraud.api.v1.check.Transaction
import fraud.test.BaseFraudServiceTest

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

class FraudServiceImplTest extends BaseFraudServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudServiceImplTest.class)

    @Autowired
    FraudCheckController fraudService

    @Autowired
    @Qualifier('transactionDAO.mongo.perf')
    ITransactionDAO fraudDAOMock

    @Test
    void testFraudCheck() {
        Transaction testTransaction = new Transaction()
        Message m = new MessageImpl()
        UriInfo uriInfo = new UriInfoImpl(m)
        HttpHeaders headers = new HttpHeadersImpl(m)

        // Mock configuration

        // Fraud check
        Response response = fraudService.check(testTransaction, uriInfo, headers)
        Assert.assertNotNull(response)
        Assert.assertNotNull(response.getEntity())
    }
}
