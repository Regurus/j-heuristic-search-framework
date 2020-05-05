package core.algorithms;

import core.SearchAlgorithm;
import core.SearchResult;
import core.State;
import core.domains.*;
import core.generators.UniversalGenerator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

            System.out.println("IDDPS-------------------------------------------");
            SearchAlgorithm iddps = new IDDPS(1.5);
            SearchResult iddpsRes = iddps.search(domain);
            System.out.println(iddpsRes);

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-iddpsRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-iddpsRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-iddpsRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-iddpsRes.getSolutions().get(0).getCost();
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


            System.out.println("IDDPS-------------------------------------------");
            SearchAlgorithm iddps = new IDDPS(1.5);
            SearchResult iddpsRes = iddps.search(domain);
            System.out.println(iddpsRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-iddpsRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-iddpsRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-iddpsRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-iddpsRes.getSolutions().get(0).getCost();
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

                System.out.println("IDDPS-------------------------------------------");
                SearchAlgorithm iddps = new IDDPS(1.5);
                SearchResult iddpsRes = iddps.search(domain);
                System.out.println(iddpsRes);

                avgDeltaExpanded += IDAstarRes.getExpanded()-iddpsRes.getExpanded();
                avgDeltaGenerated += IDAstarRes.getGenerated()-iddpsRes.getGenerated();
                avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-iddpsRes.getSolutions().get(0).getLength();
                avgCost += IDAstarRes.getSolutions().get(0).getCost()-iddpsRes.getSolutions().get(0).getCost();

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


            System.out.println("IDDPS-------------------------------------------");
            SearchAlgorithm iddps = new IDEES(1.5);
            SearchResult iddpsRes = iddps.search(domain);
            System.out.println(iddpsRes);

            avgDeltaExpanded += IDAstarRes.getExpanded()-iddpsRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-iddpsRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-iddpsRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-iddpsRes.getSolutions().get(0).getCost();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("AVG Cost Solution: "+avgCost/runs);

    }

    @Test
    public void VacuumTet(){
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

                System.out.println("IDDPS-------------------------------------------");
                SearchAlgorithm iddps = new IDEES(1);
                SearchResult iddpsRes = iddps.search(domain);
                System.out.println(iddpsRes);

                avgDeltaExpanded += IDAstarRes.getExpanded()-iddpsRes.getExpanded();
                avgDeltaGenerated += IDAstarRes.getGenerated()-iddpsRes.getGenerated();
                avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-iddpsRes.getSolutions().get(0).getLength();
                avgCost += IDAstarRes.getSolutions().get(0).getCost()-iddpsRes.getSolutions().get(0).getCost();

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


            System.out.println("IDDPS-------------------------------------------");
            SearchAlgorithm iddps = new IDDPS(1.5);
            SearchResult iddpsRes = iddps.search(domain);
            System.out.println(iddpsRes);
        }
        catch(Exception e){

        }
    }

    @Test
    public void Instance99FifteenPuzzle() {
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\FifteenPuzzleInstances\\99.in";
        File file = new File(PATH);
        try {
            InputStream inStream = new FileInputStream(file);
            FifteenPuzzle domain = new FifteenPuzzle(inStream);


            SearchAlgorithm iddps = new IDDPS(1.5);
            SearchResult iddpsRes = iddps.search(domain);


            assertTrue("Nodes generated according to current implementation should be 77712", iddpsRes.getGenerated() == 77712);
            assertTrue("Nodes expanded according to current implementation should be 37109", iddpsRes.getExpanded() == 37109);
            assertTrue("Solution cost of weight 1.5 should be 36", iddpsRes.getSolutions().get(0).getCost() == 36.0);
        } catch (Exception e) {
        }
    }
}