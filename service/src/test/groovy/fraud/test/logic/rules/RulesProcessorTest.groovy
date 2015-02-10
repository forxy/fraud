package fraud.test.logic.rules

import fraud.api.v1.check.Transaction
import fraud.db.dao.ICurrencyExchangeDAO
import fraud.test.BaseFraudServiceTest
import fraud.test.utils.data.TravelDataGenerator
import org.drools.runtime.StatelessKnowledgeSession
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

/**
 * Evaluates Drools against the transaction
 */
class RulesProcessorTest extends BaseFraudServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RulesProcessorTest.class)

    @Autowired
    StatelessKnowledgeSession session

    @Autowired
    ICurrencyExchangeDAO currencyExchangeDAO

    @Test
    void testRulesEvaluation() {
        Transaction transaction = TravelDataGenerator.generateTransaction()
        List<Object> arguments = new ArrayList<Object>(2)
        arguments << transaction
        arguments << currencyExchangeDAO
        session.execute(arguments)
        LOGGER.debug('Debug test')
    }
}
