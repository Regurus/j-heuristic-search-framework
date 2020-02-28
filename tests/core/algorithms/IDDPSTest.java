package core.algorithms;

import core.SearchResult;
import core.State;
import core.domains.FifteenPuzzle;
import core.domains.Pancakes;
import core.generators.UniversalGenerator;
import org.junit.Before;
import org.junit.Test;

public class IDDPSTest {
    Pancakes domain;
    @Before
    public void setUp() throws Exception {
        this.domain = new Pancakes();
    }

    @Test
    public void debugTest() {
        int depth = 50;
        UniversalGenerator generator = new UniversalGenerator();
        long avgDeltaGenerated = 0;
        long avgDeltaGeneratedPercent = 0;
        long avgDeltaExpanded = 0;
        long avgDeltaExpandedPercent = 0;
        long avgDeltaSolutionLen = 0;
        long avgDeltaSolutionLenPercent = 0;

        int runs = 1000;
        for(int i=0;i<runs;i++){
            State newState = generator.generate(domain,depth);
            domain.setInitialState(newState);
            IDAstar idAstar = new IDAstar(1.4);
            SearchResult IDAstarRes = idAstar.search(this.domain);
            System.out.println("IDA*--------------------------------------------");
            System.out.println(IDAstarRes);
            IDDPS idps = new IDDPS(1.4);
            SearchResult IDPSres = idps.search(this.domain);
            System.out.println("IDDPS--------------------------------------------");
            System.out.println(IDPSres);

            avgDeltaExpanded += IDAstarRes.getExpanded()-IDPSres.getExpanded();
            avgDeltaExpandedPercent += (IDAstarRes.getExpanded()-IDPSres.getExpanded())/IDAstarRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-IDPSres.getGenerated();
            avgDeltaGeneratedPercent += (IDAstarRes.getGenerated()-IDPSres.getGenerated())/IDAstarRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-IDPSres.getSolutions().get(0).getLength();
            avgDeltaSolutionLenPercent += IDAstarRes.getSolutions().get(0).getLength()-IDPSres.getSolutions().get(0).getLength()/IDAstarRes.getSolutions().get(0).getLength();

        }
        System.out.println("Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("Delta % Expanded: "+avgDeltaExpandedPercent/runs+"%");
        System.out.println("Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("Delta % Generated: "+avgDeltaGeneratedPercent/runs+"%");
        System.out.println("Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("Delta Solution % Length: "+avgDeltaSolutionLenPercent/runs+"%");
        System.out.println("Positive value for IDDPS, Negative for IDA*");
    }

    @Test
    public void test() {
        ImprovingBnB solver = new ImprovingBnB(2);
        UniversalGenerator generator = new UniversalGenerator();
        State newState = generator.generate(domain,10);
        domain.setInitialState(newState);
        solver.search(domain);
    }
}