package core;
public interface Iteration {

    /**
     * Returns the bound for this iteration.
     *
     * @return the bound
     */
    public double getBound();

    /**
     * Returns the number of nodes expanded.
     *
     * @return the number of nodes expanded
     */
    public long getExpanded();

    /**
     * Returns the number of nodes generated.
     *
     * @return the number of nodes generated
     */
    public long getGenerated();
}
