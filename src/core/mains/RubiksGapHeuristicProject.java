package core.mains;

import core.SearchResult;
import core.Solution;
import core.State;
import core.algorithms.IDAstar;
import core.domains.RubiksCube;
import core.generators.UniversalGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;

import static org.junit.Assert.assertTrue;

public class RubiksGapHeuristicProject {
    public static void main(String[] args) {
        ArrayList<String> gapResults = new ArrayList<>();
        gapResults.add("Run,Expanded,Generated,Solution Depth,Time");//header line
        ArrayList<String> baseResults = new ArrayList<>();
        baseResults.add("Run,Expanded,Generated,Solution Depth,Time");//header line
        IDAstar solver = new IDAstar();
        UniversalGenerator universalGenerator = new UniversalGenerator();
        int depth = 10;
        long start = 0;
        long finish = 0;
        long timeElapsed = 0;
        final int RUNS = 10000;
        for(int i=0; i<RUNS; i++){
            //initializing
            RubiksCube gapCube = new RubiksCube(RubiksCube.HeuristicType.GAP);
            RubiksCube baselineCube = new RubiksCube(RubiksCube.HeuristicType.BASELINE_HERISTIC);
            State newState = universalGenerator.generate(gapCube,depth);
            gapCube.setInitialState(newState);
            baselineCube.setInitialState(newState);
            //GAP run
            start = System.currentTimeMillis();
            SearchResult gapResult = solver.search(gapCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution gapSolution = gapResult.getSolutions().get(0);
            gapResults.add(""+i+","+gapResult.getExpanded()+","+gapResult.getGenerated()+","+gapSolution.getLength()+","+timeElapsed);

            //3DMH run
            start = System.currentTimeMillis();
            SearchResult baseResult = solver.search(gapCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution baseSolution = baseResult.getSolutions().get(0);
            gapResults.add(""+i+","+baseResult.getExpanded()+","+baseResult.getGenerated()+","+baseSolution.getLength()+","+timeElapsed);
        }

        //save to files
        File file = new File("GAPResults.csv");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            for(String line :gapResults){
                writer.write(line+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        file = new File("BaseResults.csv");
        try {
            writer = new FileWriter(file);
            for(String line :baseResults){
                writer.write(line+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
