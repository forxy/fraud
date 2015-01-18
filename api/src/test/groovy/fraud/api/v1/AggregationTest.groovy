package fraud.api.v1

import fraud.api.v1.velocity.Aggregation
import org.junit.Test

/**
 * Created by Tiger on 14.01.15.
 */
class AggregationTest {

    @Test
    void testMax() {
        assert Aggregation.Max.apply(['100.1','200.0', '300.00']) == 300.0
    }

    @Test
    void testMin() {
        assert Aggregation.Min.apply(['100.1','200.0', '300.00']) == 100.1
    }

    @Test
    void testAvg() {
        assert Aggregation.Avg.apply(['100.0','200.0', '300.00']) == 200.0
    }

    @Test
    void testSum() {
        assert Aggregation.Sum.apply(['100.1','200.0', '300.00']) == 600.1
    }

    @Test
    void testCount() {
        assert Aggregation.Count.apply(['aaa','aaa', 'bbb']) == 3.0
    }

    @Test
    void testUniqueCount() {
        assert Aggregation.UniqueCount.apply(['aaa','aaa', 'bbb']) == 2.0
    }
}
