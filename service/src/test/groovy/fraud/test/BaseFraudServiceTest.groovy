package fraud.test

import org.junit.Ignore
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests

/**
 * Base class for all the FraudService unit test classes.
 * Has Spring context configured
 */
@Ignore
@ContextConfiguration(locations = ['classpath:/spring/spring-test-context.xml'])
abstract class BaseFraudServiceTest extends AbstractJUnit4SpringContextTests {}
