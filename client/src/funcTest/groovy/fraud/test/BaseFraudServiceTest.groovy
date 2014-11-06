package fraud.test

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.GenericXmlContextLoader;
import fraud.client.v1.IFraudServiceClient;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ["classpath:spring-test-context.xml"], loader = GenericXmlContextLoader.class)
abstract class BaseFraudServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    protected IFraudServiceClient fraudServiceClient;
}
