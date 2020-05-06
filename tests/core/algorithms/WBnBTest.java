package core.algorithms;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.domains.*;
import core.generators.UniversalGenerator;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static core.algorithms.Utils.createFifteenPuzzle;
import static core.algorithms.Utils.getFixedPancakes;
import static core.algorithms.Utils.testSearchAlgorithm;
import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

public class WBnBTest {

    private double weight;
    private double multiplierBound;
    private SearchAlgorithm wbnb;
    @Before
    public void setUp(){
        this.weight = 2;
        this.multiplierBound = 2;
        wbnb = new WBnB(weight, multiplierBound);
    }

    @Test
    public void getName() {
        assertEquals("name OK",wbnb.getName(),"WBnB");
    }

    @Test
    public void getPossibleParameters() {
        assertNull(wbnb.getPossibleParameters());
    }

    @Test(expected = NotImplementedException.class)
    public void setAdditionalParameterNotImplemented() {
        this.wbnb.setAdditionalParameter("someOtherParamenter","somevalue");
    }

    /**
     * test for weight = 2 , k = 2
     */
    @Test
    public void testWBNBPancakes() {
        final int SIZE = 300;
        SearchDomain pancakes = getFixedPancakes(SIZE);
        System.out.println("Pancakes problem: num pancakes:" + SIZE+" weight = "+ weight);
        System.out.println("WBNB");
        testSearchAlgorithm(pancakes, wbnb, 123075, 413, 413);
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
        System.out.println("WBNB");
        testSearchAlgorithm(domain, wbnb, 922346, 447488, 60);
        System.out.println("IDA*");
        SearchAlgorithm ida = new IDAstar(weight);
        testSearchAlgorithm(domain, ida, 1956869, 932535, 60);
        System.out.println("wrbfs");
        SearchAlgorithm wrbfs = new WRBFS(weight);
        testSearchAlgorithm(domain, wrbfs, 319630, 155498, 70);
    }

    @Test
    public void PancakesTest(){
        Pancakes domain = new Pancakes(25);
        UniversalGenerator universal = new UniversalGenerator();
        double avgDeltaExpanded = 0;
        double avgDeltaGenerated = 0;
        double avgDeltaSolutionLen = 0;
        double avgCost = 0;
        int runs = 100;
        for(int i=0;i<runs;i++){
            State newState = universal.generate(domain,50);
            domain.setInitialState(newState);

            System.out.println("*********ITERATION NUMBER " + i +"*********");

            System.out.println("WBNB-------------------------------------------");
            SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
            SearchResult wbnbRes = wbnb2.search(domain);
            System.out.println(wbnbRes);

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-wbnbRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-wbnbRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-wbnbRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-wbnbRes.getSolutions().get(0).getCost();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("AVG Cost Solution: "+avgCost/runs);

    }

    @Test
    public void PuzzleTest(){
        FifteenPuzzle domain = new FifteenPuzzle();
        UniversalGenerator universal = new UniversalGenerator();
        double avgDeltaExpanded = 0;
        double avgDeltaGenerated = 0;
        double avgDeltaSolutionLen = 0;
        double avgCost = 0;
        int runs = 100;
        for(int i=0;i<runs;i++){
            State newState = universal.generate(domain,20);
            domain.setInitialState(newState);

            System.out.println("*********ITERATION NUMBER " + i +"*********");

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);


            System.out.println("WBNB-------------------------------------------");
            SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
            SearchResult wbnbRes = wbnb2.search(domain);
            System.out.println(wbnbRes);

            avgDeltaExpanded += IDAstarRes.getExpanded()-wbnbRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-wbnbRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-wbnbRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-wbnbRes.getSolutions().get(0).getCost();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("AVG Cost Solution: "+avgCost/runs);

    }

    @Test
    public void DockyardTest(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\DockyardRobot";
        File dir = new File(PATH);
        File[] files = dir.listFiles();

        assertNotNull("Invalid directory path", files);
        assertTrue("Directory should not be empty", files.length != 0);

        double avgDeltaExpanded = 0;
        double avgDeltaGenerated = 0;
        double avgDeltaSolutionLen = 0;
        double avgCost = 0;

        int i = 1;
        try {
            for(File file : files){
                InputStream inStream = new FileInputStream(file);
                DockyardRobot domain = new DockyardRobot(inStream);
                System.out.println("*********ITERATION NUMBER " + i +"*********");

                System.out.println("WIDA*-------------------------------------------");
                SearchAlgorithm ida = new IDAstar(1.5);
                SearchResult IDAstarRes = ida.search(domain);
                System.out.println(IDAstarRes);

                System.out.println("WBNB-------------------------------------------");
                SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
                SearchResult wbnbRes = wbnb2.search(domain);
                System.out.println(wbnbRes);

                avgDeltaExpanded += IDAstarRes.getExpanded()-wbnbRes.getExpanded();
                avgDeltaGenerated += IDAstarRes.getGenerated()-wbnbRes.getGenerated();
                avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-wbnbRes.getSolutions().get(0).getLength();
                avgCost += IDAstarRes.getSolutions().get(0).getCost()-wbnbRes.getSolutions().get(0).getCost();

                i++;
                if(i==11)
                    continue;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/files.length);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/files.length);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/files.length);
        System.out.println("AVG Cost Solution: "+avgCost/files.length);
    }

    @Test
    public void RubikCubeTest(){
        RubiksCube domain = new RubiksCube(RubiksCube.HeuristicType.GAP);
        UniversalGenerator universal = new UniversalGenerator();
        double avgDeltaExpanded = 0;
        double avgDeltaGenerated = 0;
        double avgDeltaSolutionLen = 0;
        double avgCost = 0;
        int runs = 100;
        for(int i=0;i<runs;i++){
            State newState = universal.generate(domain,5);
            domain.setInitialState(newState);

            System.out.println("*********ITERATION NUMBER " + i +"*********");

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);


            System.out.println("WBNB-------------------------------------------");
            SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
            SearchResult wbnbRes = wbnb2.search(domain);
            System.out.println(wbnbRes);

            avgDeltaExpanded += IDAstarRes.getExpanded()-wbnbRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-wbnbRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-wbnbRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-wbnbRes.getSolutions().get(0).getCost();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("AVG Cost Solution: "+avgCost/runs);

    }

    @Test
    public void VacuumTest(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\VacuumRobotTestFiles";
        File dir = new File(PATH);
        File[] files = dir.listFiles();

        assertNotNull("Invalid directory path", files);
        assertTrue("Directory should not be empty", files.length != 0);

        double avgDeltaExpanded = 0;
        double avgDeltaGenerated = 0;
        double avgDeltaSolutionLen = 0;
        double avgCost = 0;

        int i = 1;
        try {
            for(File file : files){
                InputStream inStream = new FileInputStream(file);
                VacuumRobot domain = new VacuumRobot(inStream);
                System.out.println("*********ITERATION NUMBER " + i +"*********");

                System.out.println("WIDA*-------------------------------------------");
                SearchAlgorithm ida = new IDAstar(1);
                SearchResult IDAstarRes = ida.search(domain);
                System.out.println(IDAstarRes);

                System.out.println("WBNB-------------------------------------------");
                SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
                SearchResult wbnbRes = wbnb2.search(domain);
                System.out.println(wbnbRes);

                avgDeltaExpanded += IDAstarRes.getExpanded()-wbnbRes.getExpanded();
                avgDeltaGenerated += IDAstarRes.getGenerated()-wbnbRes.getGenerated();
                avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-wbnbRes.getSolutions().get(0).getLength();
                avgCost += IDAstarRes.getSolutions().get(0).getCost()-wbnbRes.getSolutions().get(0).getCost();

                i++;
                if(i==4)
                    continue;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/files.length);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/files.length);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/files.length);
        System.out.println("AVG Cost Solution: "+avgCost/files.length);
    }

    @Test
    public void SingleInstanceTest(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\FifteenPuzzleInstances\\122.in";
        File file = new File(PATH);
        try{
            InputStream inStream = new FileInputStream(file);
            FifteenPuzzle domain = new FifteenPuzzle(inStream);


            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);


            System.out.println("WBNB-------------------------------------------");
            SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
            SearchResult wbnbRes = wbnb2.search(domain);
            System.out.println(wbnbRes);
        }
        catch(Exception e){

        }
    }

    @Test
    public void Instance99FifteenPuzzle(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\FifteenPuzzleInstances\\99.in";
        File file = new File(PATH);
        try{
            InputStream inStream = new FileInputStream(file);
            FifteenPuzzle domain = new FifteenPuzzle(inStream);

            SearchAlgorithm wbnb2 = new WBnB(1.5, 2);
            SearchResult wbnbRes = wbnb2.search(domain);

            assertTrue("Nodes generated according to current implementation should be 3284676", wbnbRes.getGenerated()==3284676);
            assertTrue("Nodes expanded according to current implementation should be 1580222", wbnbRes.getExpanded()==1580222);
            assertTrue("Solution cost of weight 1.5 should be 36", wbnbRes.getSolutions().get(0).getCost()==36.0);
        }
        catch(Exception e){
        }
    }

}