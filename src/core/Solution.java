package core;

import java.util.List;

public interface Solution {

    /**
     * Returns a list of operators used to construct this solution.
     *
     * @return list of operators
     */
    public List<Operator> getOperators();

    /**
     * Returns a list of states used to construct this solution
     *
     * @return list of states
     */
    public List<State> getStates();

    /**
     * Returns a string representation of the solution
     *
     * @return A string that represents the solution
     */
    public String dumpSolution();

    /**
     * Returns the cost of the solution.
     *
     * @return the cost of the solution
     */
    public double getCost();

    /**
     * Returns the length of the solution.
     */
    public int getLength();

}

