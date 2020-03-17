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

public class WBnBTest {

    private double weight;
    private double multiplierBound;
    @Before
    public void setUp(){
        this.weight = 2;
        this.multiplierBound = 2;
    }

    public SearchDomain getFixedPancakes(int size){
        final int SIZE = 300;
        // create shuffled array of size
        int[] array = new int[SIZE];
        for (int i=0; i<SIZE; i++) {
            array[i] = i;
        }
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = new Random();
        rnd.setSeed(1);
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
        Pancakes pancakes = new Pancakes(array);
        return pancakes;
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

    public SearchDomain createFifteenPuzzle(String instance) throws FileNotFoundException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("tileFormatTest.pzl");
        FifteenPuzzle puzzle = new FifteenPuzzle(is);
        return puzzle;
    }

    @Test
    public void testWBNBFifteenPuzzle() throws FileNotFoundException {
        //weight = 2
        SearchDomain domain = createFifteenPuzzle("12");
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

    public static void testSearchAlgorithm(SearchDomain domain, SearchAlgorithm algo, long generated, long expanded, double cost) {
        SearchResult result = algo.search(domain);
        if (result.getSolutions().size() > 0) {
            Solution sol = result.getSolutions().get(0);
            showSolution(result, 0);
        }
        else {
            System.out.println("no solution found");
        }
        Assert.assertTrue(result.getGenerated() == generated);
        Assert.assertTrue(result.getExpanded() == expanded);
    }

    public static void showSolution(SearchResult searchResult,int solutionIndex){
        Solution solution = searchResult.getSolutions().get(solutionIndex);
		/*for(State state: solution.getStates()){
			System.out.println(state.convertToString());
		}*/
        System.out.println("Cost: "+solution.getCost());
        System.out.println("Time: "+(searchResult).getCpuTimeMillis()/1000+"s");
        System.out.println("Expanded: "+(searchResult).getExpanded());
        System.out.println("Generated: "+(searchResult).getGenerated());
    }



}