package core.algorithms;

import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.domains.FifteenPuzzle;
import core.domains.Pancakes;
import core.domains.RubiksCube;
import core.generators.UniversalGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IDDPSTest {
    Pancakes domain;
    @Before
    public void setUp() throws Exception {
        this.domain = new Pancakes(100);
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
}