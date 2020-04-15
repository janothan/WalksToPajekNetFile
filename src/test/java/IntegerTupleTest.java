import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class IntegerTupleTest {

    @org.junit.jupiter.api.Test
    void testEquals() {
        assertEquals(new IntegerTuple(1, 1), new IntegerTuple(1, 1));
        assertEquals(new IntegerTuple(1, 2), new IntegerTuple(2, 1));
        assertNotEquals(new IntegerTuple(1, 3), new IntegerTuple(2, 1));

        HashSet<IntegerTuple> testSet = new HashSet<>();
        testSet.add(new IntegerTuple(1,2));
        testSet.add(new IntegerTuple(2,1));
        testSet.add(new IntegerTuple(Integer.MAX_VALUE, 1));
        testSet.add(new IntegerTuple(1, Integer.MAX_VALUE));
        assertTrue(testSet.size() == 2, "Expected size: 1 | Actual size: " + testSet.size());

    }
}