package core;

public interface State {

    /**
     * Returns the parent state which allows to reconstruct the found solution path
     *
     * @return The found parent state
     */
    State getParent();

    /**
     * Returns the heuristic estimate for the state.
     *
     * @return the heuristic estimate
     */
    double getH();

    /**
     * Returns the distance estimate for the state.
     *
     * @return the distance estimate
     */
    double getD();

    /**
     * Returns a string representation of the state
     *
     * @return The string representation of the state
     */
    String convertToString();

    /**
     * Returns an alternative SHORT string representation of the state
     *
     * @return A short representation of the state
     */
    String convertToStringShort();
}