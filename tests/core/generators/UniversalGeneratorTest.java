package core.generators;

import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.algorithms.IDAstar;
import core.domains.FifteenPuzzle;
import core.domains.Pancakes;
import core.domains.RubiksCube;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UniversalGeneratorTest {
    UniversalGenerator tested = new UniversalGenerator();

    @Test
    public void generateRubiks() {
        IDAstar solver = new IDAstar();
        int depth = 10;
        for(int i=0; i<100; i++){
            RubiksCube domain = new RubiksCube(RubiksCube.HeuristicType.GAP);
            State newState = this.tested.generate(domain,depth);
            domain.setInitialState(newState);
            SearchResult result = solver.search(domain);
            System.out.println("actual lenght: " +result.getSolutions().get(0).getLength());
            assertTrue(result.getSolutions().get(0).getLength()<=depth);
        }
    }
    @Test
    public void generate15Puzzle() {
        IDAstar solver = new IDAstar();
        int depth = 50;
        for(int i=0; i<100; i++){
            FifteenPuzzle domain = new FifteenPuzzle();
            State newState = this.tested.generate(domain,depth);
            domain.setInitialState(newState);
            SearchResult result = solver.search(domain);
            System.out.println(result.toString());
            System.out.println("actual lenght: " +result.getSolutions().get(0).getLength());
            assertTrue(result.getSolutions().get(0).getLength()<=depth);
        }
    }
    @Test
    public void generatePancakes() {
        IDAstar solver = new IDAstar();
        int depth = 25;
        for(int i=0; i<100; i++){
            Pancakes domain = new Pancakes(50);
            State newState = this.tested.generate(domain,depth);
            domain.setInitialState(newState);
            SearchResult result = solver.search(domain);
            System.out.println("actual lenght: " +result.getSolutions().get(0).getLength());
            assertTrue(result.getSolutions().get(0).getLength()<=depth);
        }
    }
}