package core;
public interface Operator {

    /**
     * Finds the cost for this operator as it applied on the specified state and goes to the specified parent
     *
     * @param state The state that is reached
     * @param parent The parent from where we come (OPTIONAL and can be null - the domain should deal with it!)
     *
     * @return the cost
     */
    double getCost(State state, State parent);

    /**
     * Returns the operator that would reverse the operation.
     *
     * @param state the state
     * @return the reverse operator
     */
    Operator reverse(State state);

}