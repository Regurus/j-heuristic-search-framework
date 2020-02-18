package core.algorithms;

import core.*;
import core.domains.DockyardRobot;
import core.domains.OverrideDomain;
import core.domains.Pancakes;
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
/*
    public static void main(String[] args){
        final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\BugPancakes.txt";
        try{
            InputStream inStream = new FileInputStream(new File(PATH));
            Pancakes domain = new Pancakes(inStream);

            System.out.println("WIDA*-------------------------------------------");
            SearchAlgorithm ida = new IDAstar(1.5);
            SearchResult IDAstarRes = ida.search(domain);
            System.out.println(IDAstarRes);

//            State init = domain.initialState();
//            Operator op = domain.getOperator(init, 4);
//            System.out.println(op.toString());
//            System.out.println(domain.applyOperator(init,op).convertToString());

            System.out.println("IDEES-------------------------------------------");
            SearchAlgorithm idees = new IDEES(1.5);
            SearchResult ideesRes = idees.search(domain);
            System.out.println(ideesRes);

//            for(State state : ideesRes.getSolutions().get(0).getStates()){
//                System.out.println(state.convertToString());
//            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
*/
    @Test
    public void PuzzleTest(){
        Pancakes domain = new Pancakes(25);
        UniversalGenerator universal = new UniversalGenerator();
        double avgDeltaExpanded = 0;
        double avgDeltaGenerated = 0;
        double avgDeltaSolutionLen = 0;
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
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/runs);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/runs);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/runs);

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

        try {
            for(File file : files){
                InputStream inStream = new FileInputStream(file);
                DockyardRobot domain = new DockyardRobot(inStream);

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
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("AVG Delta Expanded: "+avgDeltaExpanded/files.length);
        System.out.println("AVG Delta Generated: "+avgDeltaGenerated/files.length);
        System.out.println("AVG Delta Solution Length: "+avgDeltaSolutionLen/files.length);
    }
}
