package core.mains;

import core.SearchAlgorithm;
import core.SearchResult;
import core.State;
import core.algorithms.IDAstar;
import core.domains.FifteenPuzzle;
import core.domains.Pancakes;
import core.generators.UniversalGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WBNB_IDDPSProject {
    private static final int DUMP_PERIOD=20;
    private static final int iterations = 50;
    private static final int PAN_DEPTH = 50;
    private static final int FIFTEEN_DEPTH = 50;
    private static String VACUUM_FOLDER = "testResources/core/domains/VacuumRobotTestFiles";
    private static String[] VACCUM_FILES;

    public static void main(String[] args) {
        File f = new File(VACUUM_FOLDER);
        VACCUM_FILES = f.list();

        for(double i=1.0; i<3.0; i+=0.1){
            singleWeight(i,iterations);
        }

    }

    public static void singleWeight(double weight,int iterations){
        //TODO: plug multithreading here
        IDAstar idAstar = new IDAstar(weight);
        runAlgorithm(idAstar, iterations,weight);/*
        WBNB Wbnb = new WBNB(weight);
        runAlgorithm(Wbnb, iterations,weight);
        IDDPS iddps = new IDDPS(weight);
        runAlgorithm(iddps, iterations,weight);*/
    }

    private static void runAlgorithm(SearchAlgorithm algorithm,int iterations,double weight){
        Stack<List> lists = new Stack<>();
        Stack<String> fileNames = new Stack<>();
        //fifteen puzzle initiation
        FifteenPuzzle fifteenPuzzle = new FifteenPuzzle();
        String fifteeenName = algorithm.getClass().getSimpleName()+"_"+weight+"_FifteenPuzzle.csv";
        ArrayList<String> fifteenResults = new ArrayList<>();
        lists.push(fifteenResults);
        fileNames.push(fifteeenName);
        //pancakes initiation
        Pancakes pancakes = new Pancakes(50);
        String panName = algorithm.getClass().getSimpleName()+"_"+weight+"_Pancakes.csv";
        ArrayList<String> pancakesResults = new ArrayList<>();
        lists.push(pancakesResults);
        fileNames.push(panName);
        //optimal checker
        IDAstar idAstar = new IDAstar();
        final int maxDepth = 50;
        UniversalGenerator universalGenerator = new UniversalGenerator();

        for(int i=1;i<iterations;i++){
            //fifteen puzzle
            State initial = universalGenerator.generate(fifteenPuzzle,maxDepth);
            fifteenPuzzle.setInitialState(initial);
            SearchResult fifteenResult = algorithm.search(fifteenPuzzle);
            SearchResult optimal = idAstar.search(fifteenPuzzle);
            String line = ""+i+","+fifteenResult.getExpanded()+","+fifteenResult.getGenerated()+","
                    +fifteenResult.getCpuTimeMillis()+","+fifteenResult.getWallTimeMillis()+
                    ","+fifteenResult.getSolutions().get(0).getLength()+","+optimal.getSolutions().get(0).getLength()+","+initial.convertToStringShort();
            fifteenResults.add(line);
            System.out.println("FifteenPuzzle: "+line);
            //pancakes
            initial = universalGenerator.generate(pancakes,maxDepth);
            pancakes.setInitialState(initial);
            SearchResult pancakesResult = algorithm.search(pancakes);
            optimal = idAstar.search(pancakes);
            line = ""+i+","+pancakesResult.getExpanded()+","+pancakesResult.getGenerated()+","
                    +pancakesResult.getCpuTimeMillis()+","+pancakesResult.getWallTimeMillis()+
                    ","+pancakesResult.getSolutions().get(0).getLength()+","+optimal.getSolutions().get(0).getLength()+","+initial.convertToStringShort();
            pancakesResults.add(line);
            System.out.println("Pancakes: "+line);
            if(i%DUMP_PERIOD==0)
                dumpToFile(lists,fileNames);

        }
        dumpToFile(lists,fileNames);
    }

    private static void dumpToFile(Stack<List> results, Stack<String> filenames){
        System.out.println("Result dump in progress...");
        if(results.size()!=filenames.size()){
            System.out.println("filenames and results sizes incorrect! check the code!!!");
            return;
        }
        while (!results.empty()&&!filenames.empty()){
            String filename = filenames.pop();
            List<String> result = results.pop();
            BufferedWriter writer = null;
            try {
                FileWriter file = new FileWriter(filename, true); //true tells to append data.
                writer = new BufferedWriter(file);
                for(String line:result){
                    writer.write(line+'\n');
                }
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

            finally {
                if(writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
