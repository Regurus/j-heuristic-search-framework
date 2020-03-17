package core.algorithms;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.SearchResult;
import core.Solution;
import core.domains.FifteenPuzzle;
import core.domains.Pancakes;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

import static core.algorithms.Utils.getFixedPancakes;
import static core.algorithms.Utils.testSearchAlgorithm;

public class WBnBTest {

    private double weight;
    private double multiplierBound;
    @Before
    public void setUp(){
        this.weight = 2;
        this.multiplierBound = 2;
    }

    /**
     * test for weight = 2 , multiplierBound = 2
     */
    @Test
    public void testWBNBPancakes() {
        final int SIZE = 300;
        SearchDomain pancakes = getFixedPancakes(SIZE);
        System.out.println("Pancakes problem: num pancakes:" + SIZE+" weight = "+ weight);
        System.out.println("WBNB");
        SearchAlgorithm bnb = new WBnB(weight, multiplierBound);
        testSearchAlgorithm(pancakes, bnb, 123075, 413, 413);
        System.out.println("IDA*");
        SearchAlgorithm ida = new IDAstar(weight);
        testSearchAlgorithm(pancakes, ida, 79688, 708, 590);
        System.out.println("WRBFS");
        SearchAlgorithm wrbfs = new WRBFS(weight);
        testSearchAlgorithm(pancakes, wrbfs, 133505, 448, 372);
    }

    @Test
    public void testWBNBFifteenPuzzle() {
        //weight = 2
        SearchDomain domain = createFifteenPuzzle();
        System.out.println("FifteenPuzzle: weight = "+ weight);
        SearchAlgorithm bnb = new WBnB(weight, multiplierBound);
        System.out.println("WBNB");
        testSearchAlgorithm(domain, bnb, 922346, 447488, 60);
        System.out.println("IDA*");
        SearchAlgorithm ida = new IDAstar(weight);
        testSearchAlgorithm(domain, ida, 1956869, 932535, 60);
        System.out.println("wrbfs");
        SearchAlgorithm wrbfs = new WRBFS(weight);
        testSearchAlgorithm(domain, wrbfs, 319630, 155498, 70);
    }

    public SearchDomain createFifteenPuzzle() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("tileFormatTest.pzl");
        FifteenPuzzle puzzle = new FifteenPuzzle(is);
        return puzzle;
    }



}