package core.algorithms;

import core.SearchResult;
import core.State;
import core.domains.FifteenPuzzle;
import core.generators.UniversalGenerator;
import org.junit.Before;
import org.junit.Test;

public class IDDPSTest {
    FifteenPuzzle domain;
    @Before
    public void setUp() throws Exception {
        this.domain = new FifteenPuzzle();
    }

    @Test
    public void debugTest() {
        int depth = 50;
        UniversalGenerator generator = new UniversalGenerator();
        long avgDeltaGenerated = 0;
        long avgDeltaExpanded = 0;
        long avgDeltaSolutionLen = 0;
        int runs = 10;
        for(int i=0;i<runs;i++){
            State newState = generator.generate(domain,depth);
            domain.setInitialState(newState);
            IDAstar idAstar = new IDAstar(2);
            SearchResult IDAstarRes = idAstar.search(this.domain);
            System.out.println("IDA*--------------------------------------------");
            System.out.println(IDAstarRes);
            IDDPS idps = new IDDPS(2);
            SearchResult IDPSres = idps.search(this.domain);
            System.out.println("IDDPS*--------------------------------------------");
            System.out.println(IDPSres);

            avgDeltaExpanded += IDAstarRes.getExpanded()-IDPSres.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-IDPSres.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-IDPSres.getSolutions().get(0).getLength();

        }
        System.out.println("Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("Delta Solution Length: "+avgDeltaSolutionLen/runs);
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