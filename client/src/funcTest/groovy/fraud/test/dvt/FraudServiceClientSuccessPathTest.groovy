package fraud.test.dvt

import fraud.test.BaseFraudServiceTest
import org.apache.commons.lang.RandomStringUtils
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertNotNull

/**
 * Test success path for AuthService client
 */
class FraudServiceClientSuccessPathTest extends BaseFraudServiceTest {

    private static final Random RAND = new Random()

    @Ignore
    @Test
    void testSimpleVelocityCassandraCheck() {
        assertNotNull fraudServiceClient
        def testData = [
                'Email'     : ['aaa@mail.com'],
                'Amount'    : ['500.00'],
                'CreditCard': ['1111222233334444'],
        ] as Map<String, String[]>
        def metrics = fraudServiceClient.cassandraCheck(transactionGUID, testData)
        assertNotNull(metrics)
    }

    @Test
    void testSimpleVelocityRedisCheck() {
        assertNotNull fraudServiceClient
        def testData = [
                'Email'     : ['aaa@mail.com'],
                'Amount'    : ['500.00'],
                'CreditCard': ['1111222233334444'],
        ] as Map<String, String[]>
        def metrics = fraudServiceClient.redisCheck(transactionGUID, testData)
        assertNotNull metrics
    }

    @Ignore
    @Test
    void stressTest() {
        assertNotNull fraudServiceClient
        10000.times {
            def testData = [
                    'BillAddress': [RandomStringUtils.randomAlphabetic(50)],
                    'Purchases'  : ["${RAND.nextInt(5000)}.00".toString()],
                    'CreditCard' : [CC, CC],
                    'TUID'       : [GUID],
                    'Email'      : ["aaa${RAND.nextInt(1000)}@gmail.com".toString()],
                    'DeviceID'   : [GUID],
                    'IPAddress'  : [IPAddress],
                    'PhoneNumber': [phoneNumber],
            ] as Map<String, String[]>
            def redisMetrics = fraudServiceClient.redisCheck(transactionGUID, testData)
            assertNotNull redisMetrics
            //Assert.assertTrue(redisMetrics.size() > 0)
            //def cassandraMetrics = fraudServiceClient.cassandraCheck(transactionGUID, testData)
            //Assert.assertNotNull(cassandraMetrics)
            //Assert.assertTrue(cassandraMetrics.size() > 0)
            Thread.sleep(50)
        }
    }

    private static String getIPAddress() {
        String ip = ''
        ip += RAND.nextInt(256)
        3.times {
            ip += '.'
            ip += RAND.nextInt(256) as String
        }
        return ip
    }

    private static String getCC() {
        String cc = ''
        16.times { cc += RAND.nextInt(10) }
        return cc
    }

    private static String getPhoneNumber() {
        String nbr = ''
        nbr += '+'
        12.times { nbr += RAND.nextInt(10) }
        return nbr
    }

    private static String getGUID() {
        UUID.randomUUID() as String
    }
}
