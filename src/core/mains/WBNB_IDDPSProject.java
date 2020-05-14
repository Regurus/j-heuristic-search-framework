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
        for(double i=9; i<=10.0; i+=0.1){
            singleWeight(i,iterations);
        }

    }

    public static void singleWeight(double weight,int iterations){
        Runnable task1 = () -> {
            System.out.println("Thread 1 started IDA* run on weight: "+weight);
            IDAstar idAstar = new IDAstar(weight,5000000);
            try {
                runAlgorithm(idAstar, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("IDA* run finished on weight: "+weight);
        };
        Thread t1 = new Thread(task1);
        t1.start();
        Runnable task2 = () -> {
            System.out.println("Thread 2 started WBnB run on weight: "+weight);
            WBnB Wbnb = new WBnB(weight,2,5000000);
            try {

                runAlgorithm(Wbnb, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("WBnB run finished on weight: "+weight);
        };
        Thread t2 = new Thread(task2);
        t2.start();
        Runnable task3 = () -> {
            System.out.println("Thread 3 started IDDPS run on weight: "+weight);
            IDDPS iddps = new IDDPS(weight,5000000);
            try {
                runAlgorithm(iddps, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("IDDPS run finished on weight: "+weight);
        };
        Thread t3 = new Thread(task3);
        t3.start();
        Runnable task4 = () -> {
            System.out.println("Thread 4 started WRBFS run on weight: "+weight);
            WRBFS wrbfs = new WRBFS(weight,5000000);
            try {
                runAlgorithm(wrbfs, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("WRBFS run finished on weight: "+weight);
        };
        Thread t4 = new Thread(task4);
        t4.start();
        Runnable task5 = () -> {
            System.out.println("Thread 5 started IDEES run on weight: "+weight);
            IDEES idees = new IDEES(weight);
            try {
                runAlgorithm(idees, weight);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("IDEES run finished on  weight: "+weight);
        };
        Thread t5 = new Thread(task5);
        t5.start();
        try {
            /*t1.join();
            t2.join();
            t3.join();
            t4.join();*/
            t5.join();
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
            System.out.println(algorithm.getClass().getSimpleName()+" Fifteen Puzzle: "+line);
            fifteenResults.add(line);
            //Pancake puzzle
            domain = new Pancakes(new FileInputStream(PANCAKES_FOLDER+"/"+fileName));
            line = runOnDomain(domain, algorithm, fileName);
            System.out.println(algorithm.getClass().getSimpleName()+" Pancake Puzzle: "+line);
            pancakesResults.add(line);
            //Vacuum*/
            domain = new VacuumRobot(new FileInputStream(VACUUM_FOLDER+"/"+fileName));
            line = runOnDomain(domain, algorithm,fileName);
            System.out.println(algorithm.getClass().getSimpleName()+" Vacuum Robot: "+line);
            vacuumResults.add(line);
        }
        dumpToFile(lists,fileNames);
    }

    private static String runOnDomain(SearchDomain domain, SearchAlgorithm algorithm,String fileName){
        SearchResult vacuumResult = algorithm.search(domain);
        String solutionLen;
        if(vacuumResult.getSolutions().size()==0)
            solutionLen = "N/A";
        else
            solutionLen=""+vacuumResult.getSolutions().get(0).getCost();
        return ""+fileName+","+vacuumResult.getExpanded()+","+vacuumResult.getGenerated()+","
                +vacuumResult.getCpuTimeMillis()+","+vacuumResult.getWallTimeMillis()+
                ","+solutionLen;//+","+optimal.getSolutions().get(0).getLength();
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
