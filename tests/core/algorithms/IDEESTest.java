package core.algorithms;

import core.*;
import core.domains.OverrideDomain;
import core.domains.Pancakes;
import core.generators.UniversalGenerator;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLOutput;
import java.util.List;

public class IDEESTest {

    /*
    public static void main(String[] args){
        String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\BugPancakes.txt";
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
}
