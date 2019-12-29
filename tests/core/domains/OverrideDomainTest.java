package core.domains;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.algorithms.IDAstar;
import core.collections.Pair;
import org.junit.Before;
import org.junit.Test;

import static core.TestAllBasics.testSearchAlgorithm;
import static org.junit.Assert.*;

public class OverrideDomainTest {
    private OverrideDomain tested;
    @Before
    public void setUp() throws Exception {
        //basic tree structure
        int[] vert = {-1,3,4,-2};
        int[][] edges = {{ 0, 2,-1,-1},
                         {-1, 0, 5,-1 },
                         {-1, 0,-1, 2 },
                         {-1,-1,-1, 0}};
        tested = new OverrideDomain(edges,vert);
    }

    @Test
    public void main() {
        SearchDomain domain = tested;
        SearchAlgorithm algo = new IDAstar();
        testSearchAlgorithm(domain, algo, 546343, 269708, 45);
        tested.printDomain();
    }

    @Test
    public void checkIncorrectAdjacentMatrices(){

    }

    @Test
    public void checkIncorrectTransitionWeights(){

    }

    @Test
    public void checkTreeNavigation(){

    }
}