package core.domains;

import core.collections.Pair;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OverrideDomainTest {
    private OverrideDomain tested;
    @Before
    public void setUp() throws Exception {
        //basic tree structure
        int[] vert = {-1,3,-2};
        int[][] edges = {{0 ,2 ,-1},
                         {-1,0 ,2 },
                         {-1,-1,0}};
        tested = new OverrideDomain(edges,vert);
    }

    @Test
    public void findRootIndex() {
        tested.printDomain();
    }

    @Test
    public void sanityChecks() {
    }

    @Test
    public void initialState() {
    }

    @Test
    public void isGoal() {
    }

    @Test
    public void getNumOperators() {
    }

    @Test
    public void getOperator() {
    }

    @Test
    public void applyOperator() {
    }

    @Test
    public void copy() {
    }

    @Test
    public void pack() {
    }

    @Test
    public void unpack() {
    }

    @Test
    public void dumpStatesCollection() {
    }

    @Test
    public void isCurrentHeuristicConsistent() {
    }

    @Test
    public void setOptimalSolutionCost() {
    }

    @Test
    public void getOptimalSolutionCost() {
    }

    @Test
    public void maxGeneratedSize() {
    }

    @Test
    public void getPossibleParameters() {
    }

    @Test
    public void setAdditionalParameter() {
    }
}