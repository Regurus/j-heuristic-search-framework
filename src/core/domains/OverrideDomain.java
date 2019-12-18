package core.domains;

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.PackedElement;
import core.collections.Pair;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * class for building a graph containing arbitrary search graph not related to any actual domain
 * terms; index: row number in adj. matrix
 */
public class OverrideDomain implements SearchDomain {

    private DefaultUndirectedGraph tree = new DefaultUndirectedGraph(DefaultEdge.class);
    private Node[] nodes;

    /**
     * Basic constructor class using in-memory data
     * !!!same order in transition matrix as in vertex list!!!
     * @param transitionMatrix gValue[i][j] cost for transitioning from i=>j, -1 for no edge there
     * @param vertices list of vertex hValues, -1 for starting node, -2 for goal node
     *
     */
    public OverrideDomain(int[][] transitionMatrix,int[] vertices) {
        //basic input checks
        if(!this.sanityChecks(transitionMatrix, vertices))
            System.out.println("Input Error @constructor");
        nodes = new Node[vertices.length];
        this.addVertices(vertices);
        this.addEdges(transitionMatrix);
    }

    private void addVertices(int[] vertices) {
        for(int i=0;i<vertices.length;i++){
            Node next = new Node(vertices[i]==-2,vertices[i]==-1,vertices[i]);
            tree.addVertex(next);
            nodes[i]=next;
        }
    }

    private void addEdges(int[][] adjacencyMatrix){
        for(int i=0;i<adjacencyMatrix.length;i++){
            for(int j=0;j<adjacencyMatrix[0].length;j++){
                if(adjacencyMatrix[i][j] > -1 && i != j)//no self edges
                    tree.addEdge(nodes[i],nodes[j], new Cost(adjacencyMatrix[i][j]));
            }
        }
    }
    public void printDomain(){
        System.out.println(this.tree.toString());
    }
    private boolean sanityChecks(int[][] transitionMatrix, int[] vertices){
        //sizes fit
        if(transitionMatrix==null || transitionMatrix.length!=transitionMatrix[0].length || transitionMatrix.length!=vertices.length)
        {
            return false;
        }
        //no more than one start node
        int starts = 0;
        for(int cell:vertices) {
                if(cell<-2)
                    return false;
                if(cell==-1)
                    starts++;
            if(starts>1)
                return false;
        }
        return true;
    }
    @Override
    public State initialState() {
        return null;
    }

    @Override
    public boolean isGoal(State state) {
        return false;
    }

    @Override
    public int getNumOperators(State state) {
        return 0;
    }

    @Override
    public Operator getOperator(State state, int index) {
        return null;
    }

    @Override
    public State applyOperator(State state, Operator op) {
        return null;
    }

    @Override
    public State copy(State state) {
        return null;
    }

    @Override
    public PackedElement pack(State state) {
        return null;
    }

    @Override
    public State unpack(PackedElement packed) {
        return null;
    }

    @Override
    public String dumpStatesCollection(State[] states) {
        return null;
    }

    @Override
    public boolean isCurrentHeuristicConsistent() {
        return false;
    }

    @Override
    public void setOptimalSolutionCost(double cost) {

    }

    @Override
    public double getOptimalSolutionCost() {
        return 0;
    }

    @Override
    public int maxGeneratedSize() {
        return 0;
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {

    }
    private class Node{
        boolean isGoal;
        boolean isRoot;
        int hValue;

        public Node(boolean isGoal, boolean isRoot, int hValue) {
            this.isGoal = isGoal;
            this.isRoot = isRoot;
            this.hValue = hValue;
        }

        public boolean isGoal() {
            return isGoal;
        }

        public boolean isRoot() {
            return isRoot;
        }

        public int gethValue() {
            return hValue;
        }

        @Override
        public String toString() {
            if(isRoot)
                return "Root";
            else if(isGoal)
                return "Goal";
            else
                return ""+hValue;
        }
    }
    //jgrapht disregards same objects bound to edged so there is a need for container for edge-cost value
    private class Cost{
        int cost;

        public Cost(int cost) {
            this.cost = cost;
        }

        @Override
        public String toString() {
            return "Edge cost:"+cost;
        }
    }
}
