package core.algorithms;

import core.*;
import core.domains.DockyardRobot;
import core.domains.FifteenPuzzle;
import core.domains.OverrideDomain;
import core.domains.Pancakes;
import core.generators.UniversalGenerator;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;


public class BnBTest {

//    @Test
//    public void testDFS() throws FileNotFoundException {
//        //falls at 1035522 nodes
//        SearchDomain domain = createFifteenPuzzle("12");
//        SearchAlgorithm algo = new DFS();
//        testSearchAlgorithm(domain, algo, 546343, 269708, 45);
//    }
//
//    @Test
//    public void testIDAStarDockyard() throws FileNotFoundException {
//        String dockyardPath = System.getProperty("user.dir") + "\\testResources\\core\\domains\\dockyardDomainTest.txt";
//        File domainTest = new File(dockyardPath);
//        InputStream problemStream = new FileInputStream(domainTest);
//        DockyardRobot dr = new DockyardRobot(problemStream);
//        SearchDomain domain = dr;
//        SearchAlgorithm algo = new IDAstar(2);
//        testSearchAlgorithm(domain, algo, 130, 60, 74);
//    }
//
//    @Test
//    public void testWBNBDockyard() throws FileNotFoundException {
//        String dockyardPath = System.getProperty("user.dir") + "\\testResources\\core\\domains\\dockyardDomainTest.txt";
//        File domainTest = new File(dockyardPath);
//        InputStream problemStream = new FileInputStream(domainTest);
//        DockyardRobot dr = new DockyardRobot(problemStream);
//        SearchDomain domain = dr;
//        SearchAlgorithm algo = new BnB(2);
//        testSearchAlgorithm(domain, algo, 32, 23, 60);
//    }
//
//    @Test
//    public void testWBNBManyDockyard() throws FileNotFoundException {
//        String dockyardPath = "C:\\Users\\Ariel\\Desktop\\project\\dockyardRobot\\1.in";
//        File domainTest = new File(dockyardPath);
//        InputStream problemStream = new FileInputStream(domainTest);
//        DockyardRobot dr = new DockyardRobot(problemStream);
//        SearchDomain domain = dr;
//        SearchAlgorithm algo = new BnB(2);
//        testSearchAlgorithm(domain, algo, 32, 23, 60);
//    }
//    @Test
//    public void testBNBFifteenPuzzle() throws FileNotFoundException {
//        SearchDomain domain = createFifteenPuzzle("12");
//        SearchAlgorithm algo = new BnB(2);
//        testSearchAlgorithm(domain, algo, 546343, 269708, 45);
//    }

//    @Test
//    public void testBNBPancakes() throws FileNotFoundException {
//        final int SIZE = 30;
//        // create shuffled array of size
//        int[] array = new int[SIZE];
//        for (int i=0; i<SIZE; i++) {
//            array[i] = i;
//        }
//        // If running on Java 6 or older, use `new Random()` on RHS here
//        Random rnd = new Random();
//        rnd.setSeed(1);
//        for (int i = array.length - 1; i > 0; i--)
//        {
//            int index = rnd.nextInt(i + 1);
//            // Simple swap
//            int a = array[index];
//            array[index] = array[i];
//            array[i] = a;
//        }
//        Pancakes pancakes = new Pancakes(array);
//        //System.out.println(Arrays.toString(array));
//
//        SearchAlgorithm algo = new BnB(1);
//        SearchAlgorithm algo_wa_star = new WAStar();
//        SearchAlgorithm ida_star = new IDAstar();
//
//        System.out.println("WA STAR");
//        testSearchAlgorithm(pancakes, algo_wa_star, 546343, 269708, 45);
//        System.out.println("BNB");
//        testSearchAlgorithm(pancakes, algo, 546343, 269708, 45);
//        System.out.println("IDA");
//        //testSearchAlgorithm(pancakes, ida_star, 546343, 269708, 45);
//
//
//    }
//
//    public SearchDomain createFifteenPuzzle(String instance) throws FileNotFoundException {
//        InputStream is = getClass().getClassLoader().getResourceAsStream("tileFormatTest.pzl");
//        FifteenPuzzle puzzle = new FifteenPuzzle(is);
//        return puzzle;
//    }
//
//    @Test
//    public void testBnBBinaryTree() throws FileNotFoundException {
//        SearchDomain domain = getBinaryTree();
//        SearchAlgorithm algo = new BnB();
//        testSearchAlgorithm(domain, algo, 40, 20, 4);
//    }
//
//    private static SearchDomain getBinaryTree(){
//        OverrideDomain tested;
//        int[][] edges;
//        int[] vert;
//        //basic tree structure
//        vert = new int []{-1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2,-2};
//        edges = new int[][]{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
//                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
//        tested = new OverrideDomain(edges,vert);
//        return tested;
//    }
//
//    public static void testSearchAlgorithm(SearchDomain domain, SearchAlgorithm algo, long generated, long expanded, double cost) {
//        SearchResult result = algo.search(domain);
//        Solution sol = result.getSolutions().get(0);
//        showSolution(result,0);
//
//        //Assert.assertTrue(result.getWallTimeMillis() > 1);
//        // Assert.assertTrue(result.getWallTimeMillis() < 200);
//        // Assert.assertTrue(result.getCpuTimeMillis() > 1);
//        // Assert.assertTrue(result.getCpuTimeMillis() < 200);
//        // Assert.assertTrue(result.getGenerated() == generated);
//        // Assert.assertTrue(result.getExpanded() == expanded);
//        // Assert.assertTrue(sol.getCost() == cost);
//        // Assert.assertTrue(sol.getLength() == cost+1);
//    }
//
//    public static void showSolution(SearchResult searchResult,int solutionIndex){
//        Solution solution = searchResult.getSolutions().get(solutionIndex);
//		/*for(State state: solution.getStates()){
//			System.out.println(state.convertToString());
//		}*/
//        System.out.println("Cost: "+solution.getCost());
//        System.out.println("Time: "+(searchResult).getCpuTimeMillis()/1000+"s");
//        System.out.println("Expanded: "+(searchResult).getExpanded());
//        System.out.println("Generated: "+(searchResult).getGenerated());
//    }
//
//
//

    @Test
    public void debugTest() {
        Pancakes domain = new Pancakes(100);
        int depth = 100;
        UniversalGenerator generator = new UniversalGenerator();
        long avgDeltaGenerated = 0;
        long avgDeltaGeneratedPercent = 0;
        long avgDeltaExpanded = 0;
        long avgDeltaExpandedPercent = 0;
        long avgDeltaSolutionLen = 0;
        long avgDeltaSolutionLenPercent = 0;

        long curDeltaExpanded = 0;
        long curDeltaGenerated = 0;
        long curDeltaSolution = 0;

        int runs = 100;
        for(int i=0;i<runs;i++){
            State newState = generator.generate(domain,depth);
            domain.setInitialState(newState);
            IDAstar idAstar = new IDAstar(4);
            SearchResult IDAstarRes = idAstar.search(domain);
            System.out.println("IDA*--------------------------------------------");
            System.out.println(IDAstarRes);
            WBnB wbnb = new WBnB(4,2);
            SearchResult WBnBSres = wbnb.search(domain);
            System.out.println("WBnB--------------------------------------------");
            System.out.println(WBnBSres);

            curDeltaExpanded = IDAstarRes.getExpanded()-WBnBSres.getExpanded();
            avgDeltaExpanded += curDeltaExpanded;
            avgDeltaExpandedPercent += (IDAstarRes.getExpanded()-WBnBSres.getExpanded())/IDAstarRes.getExpanded();
            curDeltaGenerated = IDAstarRes.getGenerated()-WBnBSres.getGenerated();
            avgDeltaGenerated += curDeltaGenerated;
            avgDeltaGeneratedPercent += (IDAstarRes.getGenerated()-WBnBSres.getGenerated())/IDAstarRes.getGenerated();
            curDeltaSolution = IDAstarRes.getSolutions().get(0).getLength()-WBnBSres.getSolutions().get(0).getLength();
            avgDeltaSolutionLen += curDeltaSolution;
            avgDeltaSolutionLenPercent += (IDAstarRes.getSolutions().get(0).getLength()-WBnBSres.getSolutions().get(0).getLength())/IDAstarRes.getSolutions().get(0).getLength();

            System.out.println("expanded " + curDeltaExpanded);
            System.out.println("generated " + curDeltaGenerated);
            System.out.println("solution " + curDeltaSolution);
        }
        System.out.println("Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("Positive value for WBnB, Negative for IDA*");
    }
    @Test
    public void G1Test(){
        OverrideDomain domain = Graphs.graph1;
        SearchAlgorithm solver = new BnB();
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