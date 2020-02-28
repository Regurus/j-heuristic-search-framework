package core.mains;

import core.SearchResult;
import core.Solution;
import core.State;
import core.algorithms.IDAstar;
import core.data.TrueDistanceEstimator;
import core.domains.RubiksCube;
import core.generators.UniversalGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RubiksGapHeuristicProject {
    public static void main(String[] args) {

        ArrayList<String> gapResults = new ArrayList<>();
        gapResults.add("Run,Expanded,Generated,Solution Depth,Time");//header line
        ArrayList<String> baseResults = new ArrayList<>();
        baseResults.add("Run,Expanded,Generated,Solution Depth,Time");//header line
        IDAstar solver = new IDAstar();
        UniversalGenerator universalGenerator = new UniversalGenerator();
        int depth = 16;
        long start = 0;
        long finish = 0;
        long timeElapsed = 0;
        final int RUNS = 100;
        for(int i=0; i<RUNS; i++){
            //initializing
            RubiksCube gapCube = new RubiksCube(RubiksCube.HeuristicType.GAP);
            RubiksCube baselineCube = new RubiksCube(RubiksCube.HeuristicType.BASELINE_HEURISTIC);
            State newState = universalGenerator.generate(gapCube,depth);
            gapCube.setInitialState(newState);
            baselineCube.setInitialState(newState);
            /*//GAP run
            RubiksCube.activeHeuristic = RubiksCube.HeuristicType.GAP;
            start = System.currentTimeMillis();
            SearchResult gapResult = solver.search(gapCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution gapSolution = gapResult.getSolutions().get(0);
            gapResults.add(""+i+","+gapResult.getExpanded()+","+gapResult.getGenerated()+","+gapSolution.getLength()+","+timeElapsed);
            System.out.println("GAP: "+i+","+gapResult.getExpanded()+","+gapResult.getGenerated()+","+gapSolution.getLength()+","+timeElapsed);*/
            //TD
            RubiksCube.activeHeuristic = RubiksCube.HeuristicType.TRUE_DISTANCE;
            start = System.currentTimeMillis();
            SearchResult hybridResult = solver.search(baselineCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution hybridSolution = hybridResult.getSolutions().get(0);
            //baseResults.add(""+i+","+baseResult.getExpanded()+","+baseResult.getGenerated()+","+baseSolution.getLength()+","+timeElapsed);
            System.out.println("TD: "+i+","+hybridResult.getExpanded()+","+hybridResult.getGenerated()+","+hybridSolution.getLength()+","+timeElapsed);
            //3DMH run
            /*RubiksCube.activeHeuristic = RubiksCube.HeuristicType.BASELINE_HEURISTIC;
            start = System.currentTimeMillis();
            SearchResult baseResult = solver.search(baselineCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution baseSolution = baseResult.getSolutions().get(0);
            baseResults.add(""+i+","+baseResult.getExpanded()+","+baseResult.getGenerated()+","+baseSolution.getLength()+","+timeElapsed);
            System.out.println("MHD: "+i+","+baseResult.getExpanded()+","+baseResult.getGenerated()+","+baseSolution.getLength()+","+timeElapsed);*/
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
