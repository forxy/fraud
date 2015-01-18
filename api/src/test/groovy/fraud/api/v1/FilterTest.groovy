package fraud.api.v1

import fraud.api.v1.velocity.Filter
import org.junit.Test

/**
 * Unit test for Filtering operations
 */
class FilterTest {

    @Test
    void testAllGreaterThan() {
        assert !Filter.AllGreaterThan.apply(['100.1', '200.0', '300.00'], '200')
        assert Filter.AllGreaterThan.apply(['100.1', '200.0', '300.00'], '100')
    }

    @Test
    void testAnyGreaterThan() {
        assert Filter.AnyGreaterThan.apply(['100.1', '200.0', '300.00'], '200')
        assert !Filter.AnyGreaterThan.apply(['100.1', '200.0', '300.00'], '400')
    }

    @Test
    void testAllLessThan() {
        assert !Filter.AllLessThan.apply(['100.1', '200.0', '300.00'], '200')
        assert Filter.AllLessThan.apply(['100.1', '200.0', '300.00'], '400')
    }

    @Test
    void testAnyLessThan() {
        assert Filter.AnyLessThan.apply(['100.1', '200.0', '300.00'], '200')
        assert !Filter.AnyLessThan.apply(['100.1', '200.0', '300.00'], '100')
    }

    @Test
    void testAllEqualTo() {
        assert !Filter.AllEqualTo.apply(['aaa', 'aaa', 'bbb'], 'aaa')
        assert Filter.AllEqualTo.apply(['aaa', 'aaa', 'aaa'], 'aaa')
    }

    @Test
    void testAnyEqualTo() {
        assert Filter.AnyEqualTo.apply(['aaa', 'bbb', 'ccc'], 'aaa')
        assert !Filter.AnyEqualTo.apply(['bbb', 'ccc', 'ddd'], 'aaa')
    }
}
