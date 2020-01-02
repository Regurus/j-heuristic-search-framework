package core.domains;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.SearchResult;
import core.algorithms.IDAstar;
import core.collections.Pair;
import org.junit.Before;
import org.junit.Test;

import static core.TestAllBasics.testSearchAlgorithm;
import static org.junit.Assert.*;

public class OverrideDomainTest {
    private OverrideDomain tested;
    private int[][] edges;
    private int[] vert;
    @Before
    public void setUp() throws Exception {
        //basic tree structure
        vert = new int[]{-1,3,4,-2};
        edges = new int[][]{{0,2,0,0},
                            {0,0,5,0},
                            {0,0,0,2},
                            {0,0,0,0}};
        tested = new OverrideDomain(edges,vert);
    }

    @Test
    public void runSearch() {
        SearchAlgorithm algo = new IDAstar();
        SearchResult g1Result = algo.search(tested);
        assertEquals("G1 solution cost OK", 9 ,(int)g1Result.getSolutions().get(0).getCost());
        assertEquals("G1 solution length OK", 3 ,g1Result.getSolutions().get(0).getLength());
        assertEquals("G1 expanded amount OK", 6 ,g1Result.getExpanded());
        assertEquals("G1 generated amount OK", 6 ,g1Result.getGenerated());
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkNoStart(){
        int[] noStart = {0,3,4,-2};
        tested = new OverrideDomain(edges,noStart);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkTwoStarts(){
        int[] twoStarts = {-1,-1,4,-2};
        tested = new OverrideDomain(edges,twoStarts);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkNoGoal(){
        int[] noGoal = {-1,3,4,1};
        tested = new OverrideDomain(edges,noGoal);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkIncorrectTransitionWeights(){
        int[][] negativeWeights = {{0,2,0,0},
                {0,0,-5,0},
                {0,0,0,2},
                {0,0,0,0}};
        tested = new OverrideDomain(negativeWeights,vert);
    }

}