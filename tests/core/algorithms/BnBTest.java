package core.algorithms;

import core.*;
import core.domains.FifteenPuzzle;
import core.domains.OverrideDomain;
import core.generators.UniversalGenerator;
import org.junit.Test;

import static org.junit.Assert.*;

public class BnBTest {

    @Test
    public void PuzzleTest(){
        FifteenPuzzle domain = new FifteenPuzzle();
        UniversalGenerator universal = new UniversalGenerator();
        State newState = universal.generate(domain,50);
        domain.setInitialState(newState);

        SearchAlgorithm solver = new DFS();
        SearchResult result = solver.search(domain);
        TestAllBasics.showSolution(result,0);
    }
    @Test
    public void G1Test(){
        OverrideDomain domain = Graphs.graph1;
        SearchAlgorithm solver = new NewBnB();
        SearchResult result = solver.search(domain);
        Solution sol = result.getSolutions().get(0);
        TestAllBasics.showSolution(result,0);
    }
    @Test
    public void G2Test(){
        OverrideDomain domain = Graphs.graph2;
        SearchAlgorithm solver = new BnB();
        SearchResult result = solver.search(domain);
        Solution sol = result.getSolutions().get(0);
        TestAllBasics.showSolution(result,0);
    }
    @Test
    public void G3Test(){
        OverrideDomain domain = Graphs.graph3;
        SearchAlgorithm solver = new BnB();
        SearchResult result = solver.search(domain);
        Solution sol = result.getSolutions().get(0);
        TestAllBasics.showSolution(result,0);
    }
}