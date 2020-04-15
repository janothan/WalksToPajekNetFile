/**
 * Simple Tuple
 */
public class IntegerTuple {

    public int integer_1;
    public int integer_2;

    /**
     * Simple Constructor
     * @param integer_1 Integer 1 to set.
     * @param integer_2 Integer 2 to set.
     */
    public IntegerTuple(int integer_1, int integer_2){
        this.integer_1 = integer_1;
        this.integer_2 = integer_2;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (!(obj instanceof IntegerTuple)) return false;

        IntegerTuple that = (IntegerTuple)obj;
        return ((this.integer_1 == (that.integer_1)) && (this.integer_2 == (that.integer_2))) || ((this.integer_1 == (that.integer_2)) && (this.integer_2 == (that.integer_1)));
    }

    @Override
    public int hashCode(){
        return integer_1 + integer_2;
    }

}
