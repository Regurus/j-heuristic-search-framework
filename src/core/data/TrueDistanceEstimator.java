package core.data;

import core.Operator;
import core.SearchDomain;
import core.State;
import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class TrueDistanceEstimator {
    private int maxDepth;
    private SearchDomain domain;
    private String savePath;
    private HashMap<String,Double> distances;
    private long skipped=0;

    /**
     *
     * @param maxDepth maximum depth to be indexed (consider memory constrains)
     * @param savePath path to save indexed data (size warning)
     * @param domain domain to be looked
     */
    public TrueDistanceEstimator(int maxDepth, String savePath, SearchDomain domain) {
        this.maxDepth = maxDepth;
        this.savePath = savePath;
        this.domain = domain;
        this.distances = new HashMap<>();
    }

    private void buildTrueDistancesDatabase(){
        State init = this.domain.initialState();
        if(!this.domain.isGoal(init)){
            System.out.println("Not solved problem given initial state should be solved");
            return;
        }
        LinkedList<Pair<State,Integer>> queue = new LinkedList<>();
        queue.add(new Pair<>(init,0));
        while (!queue.isEmpty()){
            Pair<State,Integer> nextPair = queue.poll();
            int currentDepth = nextPair.getValue();
            State currentState = nextPair.getKey();
            String representation = currentState.toString();
            if(!this.distances.containsKey(representation)){
                this.distances.put(representation,(double)currentDepth);
                System.out.println("Hash size: "+this.distances.size());
            }
            else{
                System.out.println("Skipped: "+skipped);
                skipped++;
            }
            if(currentDepth==this.maxDepth){
                continue;
            }
            for(int i=0;i<this.domain.getNumOperators(currentState);i++){
                Operator op = this.domain.getOperator(currentState,i);
                State next = domain.applyOperator(currentState,op);
                if(currentDepth+1==maxDepth){
                    representation = next.toString();
                    if(!this.distances.containsKey(representation)){
                        this.distances.put(representation,(double)currentDepth+1);
                        System.out.println("Hash size: "+this.distances.size());
                    }
                    else
                        skipped++;
                }
                else
                    queue.add(new Pair<>(next,currentDepth+1));
            }
            System.out.println("Current Depth: "+currentDepth+" Queue Size:"+queue.size());
        }
        System.out.println("Done!");
    }

    /**
     * load existing database if exists
     */
    private void loadTrueDistanceDatabase(){
        File file = new File(this.savePath);
        if(!file.exists()){
            this.buildTrueDistancesDatabase();
            this.saveTrueDistanceDatabase();
            return;
        }
        FileReader reader=null;
        try {
            reader = new FileReader(file);
            BufferedReader bufferreader = new BufferedReader(reader);
            String line = bufferreader.readLine();
            String[] metadata = line.split(",");
            if(!this.domain.getClass().getName().equalsIgnoreCase(metadata[0]))
                System.out.println("File is NOT for this domain");
            this.maxDepth = Integer.parseInt(metadata[1]);
            while((line = bufferreader.readLine()) != null) {
                String[] dataLine = line.split(",");
                this.distances.put(dataLine[0],Double.parseDouble(dataLine[1]));
            }
            }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * save built database to file
     */
    private void saveTrueDistanceDatabase(){
        if(distances.size()==0)
            return;
        File file = new File(this.savePath);
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            //METADATA line
            writer.write(this.domain.getClass().getName()+","+this.maxDepth+'\n');
            for(String state:distances.keySet()){
                writer.write(state+","+this.distances.get(state)+'\n');
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("File writing error! Cannot save!!");
        }
    }

    public Double getTrueDistance(State state){
        if(distances.size()==0){
            this.loadTrueDistanceDatabase();
        }
        String representation = state.toString();
        if(!this.distances.containsKey(representation))
            //in case the case is not covered it`s at least one further(from goal) than calculated maximum
            return (double)this.maxDepth+1;
        return this.distances.get(representation);
    }
}
