package core.algorithms;

import core.*;

import java.util.*;


public class BnB implements SearchAlgorithm {
    protected double weight;

    public BnB(){ this.weight = 1d; }

    public BnB(double weight){
        this.weight = weight;
    }


    @Override
    public String getName() {
        if(weight!= 1d)
            return "bnb";
        return "wbnb";
    }

    @Override
    public SearchResult search(SearchDomain domain) {
        State state = domain.initialState();
        Set<Node> open = new HashSet<>();

        return null;
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {

    }

    /**
     * Private Node class in use with BnB
     */
    private class Node{
        private Node parent;
        private Operator op;
        private State state;

        private double g;

        public Node(State state, Node parent, Operator op){
            this.state = state;
            this.parent = parent;
            this.op = op;
        }
    }
}
