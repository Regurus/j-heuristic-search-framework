package core.algorithms;

import core.*;
import core.domains.*;
import core.generators.UniversalGenerator;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IDEESTest {

    @Test
    public void bugFix(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\BugPancakes.txt";
        try{
            InputStream inStream = new FileInputStream(new File(PATH));
            Pancakes domain = new Pancakes(inStream);

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);

//            for(State state : IDAstarRes.getSolutions().get(0).getStates())
//                System.out.println(state.convertToStringShort());

//            State init = domain.initialState();
//            Operator op = domain.getOperator(init, 4);
//            System.out.println(op.toString());
//            System.out.println(domain.applyOperator(init,op).convertToString());

            System.out.println("IDEES-------------------------------------------");
            SearchAlgorithm idees = new IDEES(1.5);
            SearchResult ideesRes = idees.search(domain);
            System.out.println(ideesRes);
        }
        catch(Exception e){
            e.printStackTrace();
        }
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

            System.out.println("IDEES-------------------------------------------");
            SearchAlgorithm idees = new IDEES(1.5);
            SearchResult ideesRes = idees.search(domain);
            System.out.println(ideesRes);

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-ideesRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-ideesRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-ideesRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-ideesRes.getSolutions().get(0).getCost();
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


            System.out.println("IDEES-------------------------------------------");
            SearchAlgorithm idees = new IDEES(1.5);
            SearchResult ideesRes = idees.search(domain);
            System.out.println(ideesRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-ideesRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-ideesRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-ideesRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-ideesRes.getSolutions().get(0).getCost();
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

                System.out.println("IDEES-------------------------------------------");
                SearchAlgorithm idees = new IDEES(1.5);
                SearchResult ideesRes = idees.search(domain);
                System.out.println(ideesRes);

                avgDeltaExpanded += IDAstarRes.getExpanded()-ideesRes.getExpanded();
                avgDeltaGenerated += IDAstarRes.getGenerated()-ideesRes.getGenerated();
                avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-ideesRes.getSolutions().get(0).getLength();
                avgCost += IDAstarRes.getSolutions().get(0).getCost()-ideesRes.getSolutions().get(0).getCost();

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


            System.out.println("IDEES-------------------------------------------");
            SearchAlgorithm idees = new IDEES(1.5);
            SearchResult ideesRes = idees.search(domain);
            System.out.println(ideesRes);

            /*System.out.println("IDPS-------------------------------------------");
            SearchAlgorithm idps = new ImprovingPS(1.5);
            SearchResult IDPSRes = idps.search(domain);
            System.out.println(IDPSRes);*/

            avgDeltaExpanded += IDAstarRes.getExpanded()-ideesRes.getExpanded();
            avgDeltaGenerated += IDAstarRes.getGenerated()-ideesRes.getGenerated();
            avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-ideesRes.getSolutions().get(0).getLength();
            avgCost += IDAstarRes.getSolutions().get(0).getCost()-ideesRes.getSolutions().get(0).getCost();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);
        System.out.println("AVG Cost Solution: "+avgCost/runs);

    }

    @Test
    public void VacuumTet(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\FifteenPuzzleInstances";
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
                FifteenPuzzle domain = new FifteenPuzzle(inStream);
                System.out.println("*********ITERATION NUMBER " + i +"*********");

                System.out.println("WIDA*-------------------------------------------");
                SearchAlgorithm ida = new IDAstar(1);
                SearchResult IDAstarRes = ida.search(domain);
                System.out.println(IDAstarRes);

                System.out.println("IDEES-------------------------------------------");
                SearchAlgorithm idees = new IDEES(1);
                SearchResult ideesRes = idees.search(domain);
                System.out.println(ideesRes);

                avgDeltaExpanded += IDAstarRes.getExpanded()-ideesRes.getExpanded();
                avgDeltaGenerated += IDAstarRes.getGenerated()-ideesRes.getGenerated();
                avgDeltaSolutionLen += IDAstarRes.getSolutions().get(0).getLength()-ideesRes.getSolutions().get(0).getLength();
                avgCost += IDAstarRes.getSolutions().get(0).getCost()-ideesRes.getSolutions().get(0).getCost();

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
        FifteenPuzzle domain = null;
        try{
            InputStream inStream = new FileInputStream(file);
            domain = new FifteenPuzzle(inStream);



        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("WIDA*-------------------------------------------");
        SearchAlgorithm ida = new IDAstar(1.5);
        SearchResult IDAstarRes = ida.search(domain);
        System.out.println(IDAstarRes);


        System.out.println("IDEES-------------------------------------------");
        SearchAlgorithm idees = new IDEES(1.5);
        SearchResult ideesRes = idees.search(domain);
        System.out.println(ideesRes);
    }

    @Test
    public void Instance99FifteenPuzzle(){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\FifteenPuzzleInstances\\99.in";
        File file = new File(PATH);
        try{
            InputStream inStream = new FileInputStream(file);
            FifteenPuzzle domain = new FifteenPuzzle(inStream);


            SearchAlgorithm idees = new IDEES(1.5);
            SearchResult ideesRes = idees.search(domain);

            assertTrue("Nodes generated according to current implementation should be 22081", ideesRes.getGenerated()==22081);
            assertTrue("Nodes expanded according to current implementation should be 10723", ideesRes.getExpanded()==10723);
            assertTrue("Solution cost of weight 1.5 should be 42", ideesRes.getSolutions().get(0).getCost()==42.0);
        }
        catch(Exception e){
        }
    }
}
