package core.mains;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.algorithms.*;
import core.domains.FifteenPuzzle;
import core.domains.Pancakes;
import core.domains.VacuumRobot;
import core.generators.UniversalGenerator;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class WBNB_IDDPSProject {
    private static final int DUMP_PERIOD=20;
    private static final int iterations = 50;
    private static final String VACUUM_FOLDER = "testResources/core/domains/VacuumRobotTestFiles";
    private static final String PANCAKES_FOLDER = "testResources/core/domains/PancakeTestFiles";
    private static final String FIFTEEN_FOLDER = "testResources/core/domains/FifteenPuzzleInstances";
    private static String[] VACCUM_FILES;
    private static String[] PANCAKE_FILES;
    private static String[] FIFTEEN_FILES;

    public static void main(String[] args) {
        File f = new File(VACUUM_FOLDER);
        VACCUM_FILES = f.list();
        f = new File(PANCAKES_FOLDER);
        PANCAKE_FILES = f.list();
        f = new File(FIFTEEN_FOLDER);
        FIFTEEN_FILES = f.list();
        for(double i=1.1; i<3.0; i+=0.1){
            singleWeight(i,iterations);
        }

    }

    public static void singleWeight(double weight,int iterations){
        Runnable task1 = () -> {
            IDAstar idAstar = new IDAstar(weight,5000000);
            try {
                runAlgorithm(idAstar, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
        Thread t1 = new Thread(task1);
        System.out.println("Thread 1 started IDA* run on weight: "+weight);
        t1.start();
        Runnable task2 = () -> {
            WBnB Wbnb = new WBnB(weight,2);
            try {

                runAlgorithm(Wbnb, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
        Thread t2 = new Thread(task2);
        System.out.println("Thread 2 started WBnB run on weight: "+weight);
        t2.start();
        Runnable task3 = () -> {
            IDDPS iddps = new IDDPS(weight,5000000);
            try {
                runAlgorithm(iddps, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
        Thread t3 = new Thread(task3);
        t3.start();
        System.out.println("Thread 3 started WBnB run on weight: "+weight);
        Runnable task4 = () -> {
            WRBFS wrbfs = new WRBFS(weight);
            try {
                runAlgorithm(wrbfs, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
        Thread t4 = new Thread(task4);
        System.out.println("Thread 4 started RBFS run on weight: "+weight);
        Runnable task5 = () -> {
            IDEES idees = new IDEES(weight);
            try {
                runAlgorithm(idees, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        };
        Thread t5 = new Thread(task5);
        t5.start();
        System.out.println("Thread 5 started IDEES run on weight: "+weight);
        try {
            t1.join();
            System.out.println("IDA* run finished on weight: "+weight);
            t2.join();
            System.out.println("WBnB run finished on weight: "+weight);
            t3.join();
            System.out.println("IDDPS run finished on weight: "+weight);
            t4.join();
            System.out.println("RBFS run finished on weight: "+weight);
            t5.join();
            System.out.println("IDEES run finished on weight: "+weight);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void runAlgorithm(SearchAlgorithm algorithm,double weight) throws FileNotFoundException {
        Stack<List> lists = new Stack<>();
        Stack<String> fileNames = new Stack<>();
        //fifteen puzzle initiation
        String fifteeenName = algorithm.getClass().getSimpleName()+"_"+new DecimalFormat("##.##").format(weight)+"_FifteenPuzzle.csv";
        ArrayList<String> fifteenResults = new ArrayList<>();
        lists.push(fifteenResults);
        fileNames.push(fifteeenName);
        //pancakes initiation
        String panName = algorithm.getClass().getSimpleName()+"_"+new DecimalFormat("##.##").format(weight)+"_Pancakes.csv";
        ArrayList<String> pancakesResults = new ArrayList<>();
        lists.push(pancakesResults);
        fileNames.push(panName);
        //vacuum initiation
        String vacuumName = algorithm.getClass().getSimpleName()+"_"+new DecimalFormat("##.##").format(weight)+"_Vacuum.csv";
        ArrayList<String> vacuumResults = new ArrayList<>();
        lists.push(vacuumResults);
        fileNames.push(vacuumName);
        //optimal checker
        if(VACCUM_FILES.length!=FIFTEEN_FILES.length||VACCUM_FILES.length!=PANCAKE_FILES.length){
            System.out.println("Same amount of instances required!!!");
            return;
        }
        for(String fileName:VACCUM_FILES){
            //Fifteen puzzle
            SearchDomain domain = new FifteenPuzzle(new FileInputStream(FIFTEEN_FOLDER+"/"+fileName));
            String line = runOnDomain(domain, algorithm, fileName);
            System.out.println("Fifteen Puzzle: "+line);
            fifteenResults.add(line);
            //Pancake puzzle
            domain = new Pancakes(new FileInputStream(PANCAKES_FOLDER+"/"+fileName));
            line = runOnDomain(domain, algorithm, fileName);
            System.out.println("Pancake Puzzle: "+line);
            pancakesResults.add(line);
            //Vacuum

            domain = new VacuumRobot(new FileInputStream(VACUUM_FOLDER+"/"+fileName));
            line = runOnDomain(domain, algorithm,fileName);
            System.out.println("Vacuum Robot: "+line);
            vacuumResults.add(line);
        }
        dumpToFile(lists,fileNames);
    }

    private static String runOnDomain(SearchDomain domain, SearchAlgorithm algorithm,String fileName){
        SearchResult vacuumResult = algorithm.search(domain);
        return ""+fileName+","+vacuumResult.getExpanded()+","+vacuumResult.getGenerated()+","
                +vacuumResult.getCpuTimeMillis()+","+vacuumResult.getWallTimeMillis()+
                ","+(int)vacuumResult.getSolutions().get(0).getCost();//+","+optimal.getSolutions().get(0).getLength();
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
                writer.write("Filename,Expanded,Generated,CPU Time,Wall Time,Solution Length\n");
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
