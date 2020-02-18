package core.algorithms;

import core.*;
import core.domains.FifteenPuzzle;
import core.domains.OverrideDomain;
import core.domains.Pancakes;
import core.generators.UniversalGenerator;
import org.junit.Test;

public class BnBTest {

    @Test
    public void PuzzleTest(){
        Pancakes domain = new Pancakes(25);
        UniversalGenerator universal = new UniversalGenerator();
        long avgDeltaExpanded = 0;
        long avgDeltaGenerated = 0;
        long avgDeltaSolutionLen = 0;
        int runs = 100;
        for(int i=0;i<runs;i++){
            State newState = universal.generate(domain,50);
            domain.setInitialState(newState);

            System.out.println("wBnB-------------------------------------------");
            SearchAlgorithm bnb = new ImprovingBnB(1.5);
            SearchResult bnbRes = bnb.search(domain);
            System.out.println(bnbRes);

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-bnbRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-bnbRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-bnbRes.getSolutions().get(0).getLength();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);

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
        SearchAlgorithm solver = new ImprovingBnB(2);
        SearchResult result = solver.search(domain);
        Solution sol = result.getSolutions().get(0);
        TestAllBasics.showSolution(result,0);
    }
    @Test
    public void G3Test(){
        OverrideDomain domain = Graphs.graph3;
        SearchAlgorithm solver = new ImprovingBnB(2);
        SearchResult result = solver.search(domain);
        Solution sol = result.getSolutions().get(0);
        TestAllBasics.showSolution(result,0);
    }
}