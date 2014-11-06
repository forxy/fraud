package fraud.test.dvt

import fraud.test.BaseFraudServiceTest
import org.apache.commons.lang.RandomStringUtils
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

/**
 * Test success path for AuthService client
 */
class FraudServiceClientSuccessPathTest extends BaseFraudServiceTest {

    private static final Random RAND = new Random()

    @Ignore
    @Test
    void testSimpleVelocityCassandraCheck() {
        Assert.assertNotNull(fraudServiceClient)
        String transactionGUID = UUID.randomUUID().toString()
        def testData = [
                'Email'     : ['aaa@mail.com'],
                'Amount'    : ['500.00'],
                'CreditCard': ['1111222233334444'],
        ] as Map<String, String[]>
        def metrics = fraudServiceClient.cassandraCheck(transactionGUID, testData)
        Assert.assertNotNull(metrics)
    }

    @Test
    void testSimpleVelocityRedisCheck() {
        Assert.assertNotNull(fraudServiceClient)
        String transactionGUID = UUID.randomUUID().toString()
        def testData = [
                'Email'     : ['aaa@mail.com'],
                'Amount'    : ['500.00'],
                'CreditCard': ['1111222233334444'],
        ] as Map<String, String[]>
        def metrics = fraudServiceClient.redisCheck(transactionGUID, testData)
        Assert.assertNotNull(metrics)
    }

    //@Ignore
    @Test
    void stressTest() {
        Assert.assertNotNull(fraudServiceClient)
        10000.times {

            String transactionGUID = UUID.randomUUID().toString()
            def testData = [
                    'BillAddress': [RandomStringUtils.randomAlphabetic(50)],
                    'Purchases'  : ["${RAND.nextInt(5000)}.00".toString()],
                    'CreditCard' : [generateCC(), generateCC()],
                    'TUID'       : [UUID.randomUUID().toString()],
                    'Email'      : ["aaa${RAND.nextInt(1000)}@gmail.com".toString()],
                    'DeviceID'   : [UUID.randomUUID().toString()],
                    'IPAddress'  : [generateIPAddress()],
                    'PhoneNumber': [generatePhoneNumber()],
            ] as Map<String, String[]>
            def redisMetrics = fraudServiceClient.redisCheck(transactionGUID, testData)
            Assert.assertNotNull(redisMetrics)
            //Assert.assertTrue(redisMetrics.size() > 0)
            //def cassandraMetrics = fraudServiceClient.cassandraCheck(transactionGUID, testData)
            //Assert.assertNotNull(cassandraMetrics)
            //Assert.assertTrue(cassandraMetrics.size() > 0)
            Thread.sleep(50)
        }
    }

    private static String generateIPAddress() {
        String ip = ''
        ip += RAND.nextInt(256)
        3.times {
            ip += '.'
            ip += RAND.nextInt(256) as String
        }
        ip
    }

    private static String generateCC() {
        String cc = ''
        16.times { cc += RAND.nextInt(10) }
        cc
    }

    private static String generatePhoneNumber() {
        String nbr = ''
        nbr += '+'
        12.times { nbr += RAND.nextInt(10) }
        nbr
    }
}
