package core.mains;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.algorithms.IDAstar;
import core.domains.DockyardRobot;
import core.domains.FifteenPuzzle;
import core.generators.UniversalGenerator;

import java.io.*;

public class ExportDataLior {
    public static void main(String[] args) throws IOException {
        String OutputPath = System.getProperty("user.dir") + "\\resources\\FPuzzle.csv";
        FileWriter writer = new FileWriter(OutputPath);

        SearchAlgorithm solver = new IDAstar(2);


        writer.append("Algorithm,Generated,Expanded,Runtime\n");

        String PATH = System.getProperty("user.dir") + "\\testResources\\core\\algorithms\\DockyardRobot";

        File dir = new File(PATH);
        File[] files = dir.listFiles();

        //DOCKYARD or VACUUMROBOT ENVIRONMENT
//        int pNum=1;
//        for(File problemFile : files){
//            System.out.println("Problem number: " + pNum++);
//            InputStream problemStream = new FileInputStream(problemFile);
//            SearchDomain domain = new DockyardRobot(problemStream);
//            SearchResult result = solver.search(domain);
//            writer.append("WIDA*," + result.getExpanded() + "," + result.getGenerated() + "," + result.getCpuTimeMillis() + "\n");
//        }

        int depth = 50;
        UniversalGenerator ug = new UniversalGenerator();
        for(int i=1; i<=100; i++){
            FifteenPuzzle domain = new FifteenPuzzle();
            State newState = ug.generate(domain,depth);
            domain.setInitialState(newState);

            SearchResult result = solver.search(domain);
            writer.append("WIDA*," + result.getExpanded() + "," + result.getGenerated() + "," + result.getCpuTimeMillis() + "\n");

        }

        writer.flush();
        writer.close();
    }
}
