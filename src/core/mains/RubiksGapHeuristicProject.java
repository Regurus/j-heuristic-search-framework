package core.mains;

import core.SearchResult;
import core.Solution;
import core.State;
import core.algorithms.IDAstar;
import core.domains.RubiksCube;
import core.generators.UniversalGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        final int RUNS = 2500;
        final int DUMP_PERIOD = 99;
        for(int i=1; i<RUNS+1; i++){
            //initializing
            RubiksCube gapCube = new RubiksCube(RubiksCube.HeuristicType.GAP);
            RubiksCube baselineCube = new RubiksCube(RubiksCube.HeuristicType.BASELINE_HEURISTIC);
            State newState = universalGenerator.generate(gapCube,depth);
            gapCube.setInitialState(newState);
            baselineCube.setInitialState(newState);
            //GAP run
            RubiksCube.activeHeuristic = RubiksCube.HeuristicType.GAP;
            start = System.currentTimeMillis();
            SearchResult gapResult = solver.search(gapCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution gapSolution = gapResult.getSolutions().get(0);
            gapResults.add(""+i+","+gapResult.getExpanded()+","+gapResult.getGenerated()+","+gapSolution.getLength()+","+timeElapsed);
            System.out.println(""+i+","+gapResult.getExpanded()+","+gapResult.getGenerated()+","+gapSolution.getLength()+","+timeElapsed);
            //3DMH run
            RubiksCube.activeHeuristic = RubiksCube.HeuristicType.BASELINE_HEURISTIC;
            start = System.currentTimeMillis();
            SearchResult baseResult = solver.search(baselineCube);
            finish = System.currentTimeMillis();
            timeElapsed = finish - start;
            Solution baseSolution = baseResult.getSolutions().get(0);
            baseResults.add(""+i+","+baseResult.getExpanded()+","+baseResult.getGenerated()+","+baseSolution.getLength()+","+timeElapsed);
            System.out.println(""+i+","+baseResult.getExpanded()+","+baseResult.getGenerated()+","+baseSolution.getLength()+","+timeElapsed);
            if(i%DUMP_PERIOD==0){
                dumpToFile(gapResults,baseResults);
                gapResults = new ArrayList<>();
                baseResults = new ArrayList<>();
            }
        }
        dumpToFile(gapResults,baseResults);
    }
    private static void dumpToFile(List<String> gaps,List<String> base){
        System.out.println("Result dump in progress...");
        //save to files
        BufferedWriter gapWriter = null;
        BufferedWriter baseWriter = null;
        try {
            FileWriter gapFile = new FileWriter("GAPResults.csv", true); //true tells to append data.
            FileWriter baseFile = new FileWriter("3DMHResults.csv", true); //true tells to append data.
            gapWriter = new BufferedWriter(gapFile);
            baseWriter = new BufferedWriter(baseFile);
            for(String gapLine:gaps){
                gapWriter.write(gapLine+'\n');

            }
            for(String baseLine:base){
                baseWriter.write(baseLine+'\n');
            }
        }
        catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        finally {
            if(gapWriter != null) {
                try {
                    gapWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(baseWriter != null) {
                try {
                    baseWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
