package core.algorithms;

import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.domains.FifteenPuzzle;
import core.domains.RubiksCube;
import core.generators.UniversalGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IDDPSTest {
    FifteenPuzzle domain;
    @Before
    public void setUp() throws Exception {
        this.domain = new FifteenPuzzle();
    }

    @Test
    public void debugTest() {
        int depth = 20;
        UniversalGenerator generator = new UniversalGenerator();
        State newState = generator.generate(domain,depth);
        domain.setInitialState(newState);
        IDDPS solver = new IDDPS(5);
        SearchResult res = solver.search(this.domain);
        System.out.println(res);
    }
}