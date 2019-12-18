package core.mains;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import core.*;
import core.algorithms.DP;
import core.algorithms.EES;
import core.algorithms.IDAstar;
import core.algorithms.WAStar;
import core.collections.PackedElement;
import core.data.Weights;

import java.io.*;
import java.lang.Boolean;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainDaniel {

    private static String fileEnd;
    private static String filePrefix;
    private static SearchAlgorithm alg;
    private static String[] relPathes = {"C:/Users/Daniel/Documents/gilond/Master/ResearchData/","C:/Users/gilond/Google Drive/gilond/Master/ResearchData/"};
    private static String relPath;
    private static String inputPath;
    private static String outputPath;
    private static String globalPrefix;
    private static String summarySheetName;
    private static String summaryName;
    private static Weights.SingleWeight w;
    private static String domainName;
    private static Weights weights = new Weights();
    private static boolean overwriteSummary;
    private static boolean overwriteFile;
    private static boolean appendToFile;
    private static boolean useBestFR;
    private static boolean useOracle;
    private static double totalWeight;
    private static int startInstance;
    private static int stopInstance;
    private static boolean saveSolutionPath;
    private static boolean[][] solvableInstances;
    private static int solvableNum;
    private static SearchAlgorithm[] SearchAlgorithmArr;
    private static HashMap<String,String> domainParams;
    private static String[] DPextraHeaders;

    private static double[] searchSave100FR(boolean save, int i, int algPos){
//        boolean reopen = true;
        double retArray[] = {0,0,0};//solved,generated,expanded
        String[] resultColumnNames = {"InstanceID", "Found", "Depth", "Cost" , "Generated", "Expanded", "Cpu Time", "Wall Time"};
        if(alg.getName().equals("DP")){
            int aLen = resultColumnNames.length;
            int bLen = DPextraHeaders.length;
            String [] temp = new String[aLen+bLen];
            System.arraycopy(resultColumnNames, 0, temp, 0, aLen);
            System.arraycopy(DPextraHeaders, 0, temp, aLen, bLen);
            resultColumnNames = temp;
        }
        OutputResult output = null;
        Constructor<?> cons = null;
        StringBuilder sb = new StringBuilder();
        try {
            Class<?> cl = Class.forName("org.cs4j.core.domains."+domainName);
            cons = cl.getConstructor(InputStream.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String str;
            String[] lines = new String[0];
            String fname = outputPath+alg.getName()+"_"+(int)w.wg+"_"+(int)w.wh+"_"+fileEnd+".csv";
            File file = new File(fname);
            int found = 0;
            double d[] = new double[resultColumnNames.length];
            if(file.exists()){
                FileInputStream fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                str = new String(data, "UTF-8");
                lines = str.split("\n");
            }
            if(save && !appendToFile){
                String toPrint = String.join(",", resultColumnNames);
                sb.append(toPrint+"\n");
            }
            boolean[] alreadyPrinted = new boolean[stopInstance+1];
            if(appendToFile){
                String toPrint = String.join(",", resultColumnNames);
                sb.append(toPrint+"\n");
                int count = 0;
                for(String line : lines) {
                    // results without header
                    if(count > 0) {
                        String[] lineSplit = line.split(",");

                        String iId = lineSplit[0];
                        double iIdD = Double.parseDouble(iId);
                        int instanceId = (int)iIdD;
                        if(!alreadyPrinted[instanceId]){
                            sb.append(line+"\n");
                            alreadyPrinted[instanceId] = true;

                            String f = lineSplit[1];
                            if (Double.parseDouble(f) == 1.0) {
                                found++;
                            } else if (solvableInstances[instanceId - startInstance][algPos]) {
                                solvableNum--;
                                solvableInstances[instanceId - startInstance][algPos] = false;
                            }
                        }
                    }
                    count++;
                }
            }
            if(alreadyPrinted[i]){
                return retArray;
            }
//            System.out.println("Solving "+domainName + "\tAlg: " + alg.getName() + "_" + fileEnd + "\tweight: wg : " + w.wg + " wh: " + w.wh);
            BufferedReader optimalReader = null;
            if(useOracle) {
                InputStream optimalIS = new FileInputStream(new File(inputPath + "/optimalSolutions.in"));
                optimalReader = new BufferedReader(new InputStreamReader(optimalIS));
            }
            //set algo total weight
            alg.setAdditionalParameter("weight", totalWeight + "");
            //search on this domain and algo and weight the 100 instances
            try {
                if(useOracle) {
                    String optimalLine = optimalReader.readLine();
                    String[] optArr = optimalLine.split(",");
                    int optInstance = Integer.parseInt(optArr[0]);
                    int optimalSolution = Integer.parseInt(optArr[1]);
                    double optimalBounded = optimalSolution * totalWeight;
                    if (optInstance != i) System.out.println("[WARNING] Wrong optimal solution set");
                    else alg.setAdditionalParameter("max-cost", optimalBounded + "");
//                        else alg.setAdditionalParameter("optimalSolution", optimalSolution + "");
                }
                InputStream is = new FileInputStream(new File(inputPath + "/" + i + ".in"));
                SearchDomain domain;
                domain = (SearchDomain) cons.newInstance(is);
                for(Map.Entry<String, String> entry : domainParams.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    domain.setAdditionalParameter(key,value);
                }
                System.out.print("\rSolving " + domainName + " instance " + (found+1) +"/"+ i +" ("+solvableNum+")\tAlg: " + alg.getName() + "_" + fileEnd + "\tweight: wg : " + w.wg + " wh: " + w.wh);
                SearchResult result = null;
                if(solvableInstances[i-startInstance][algPos])
                    result = alg.search(domain);
                if (result != null && result.hasSolution()) {
                    if(totalWeight == 1)
                        saveOptimalSolution(result,i,domain);
//                            saveSolutionPathAsInstances(result,i);
                    found++;
                    d[1] = 1;
                    d[2] = result.getSolutions().get(0).getLength();
                    d[3] = result.getSolutions().get(0).getCost();
                    d[4] = result.getGenerated();
                    d[5] = result.getExpanded();
                    d[6] = result.getCpuTimeMillis();
                    d[7] = result.getWallTimeMillis();
//                            d[8] = domain.initialState().getH();
                    if(alg.getName().equals("DP")){
                        TreeMap extras = result.getExtras();
/*                                d[8] = extras.size();
                        if(d[8] != 0){
                            int maxBuckets = Integer.parseInt(extras.lastKey().toString());
                            d[9] = (double) maxBuckets;
                            d[10] = Double.parseDouble(extras.get(maxBuckets+"").toString());
                        }*/
                        Object generatedFirst = extras.get("generatedFirst");
                        if(generatedFirst != null){
                            d[8] = Double.parseDouble(generatedFirst.toString());
                        }
                        d[9] = Double.parseDouble(extras.get("numOfGoalsFound").toString());
                        d[10] = Double.parseDouble(extras.get("fmin").toString());
                    }

/*                            retArray[0] += 1;
                    retArray[1] += result.getGenerated();
                    retArray[2] += result.getSolutions().get(0).getLength();*/
                }
                else if(solvableInstances[i-startInstance][algPos]){
                    solvableInstances[i-startInstance][algPos] = false;
                    solvableNum--;

                    int sul = 0;
                    for (boolean[] solvableInstance : solvableInstances) {
                        if (solvableInstance[algPos])
                            sul++;
                    }
                    if(sul!= solvableNum)
                        System.out.println("[WARNING] solvable num incorrect i:"+i);
                }
                if(save) {
                    d[0] = i;
                    for (double temp : d) {
                        sb.append(temp);
                        sb.append(",");
                    }
                    sb.append("\n");

                    output = new OutputResult(outputPath+alg.getName()+"_"+(int)w.wg+"_"+(int)w.wh+"_"+fileEnd, null, -1, -1, null, false, overwriteFile);
                    output.writeln(sb.toString());
                    output.close();
                }
            } catch (OutOfMemoryError e) {
                System.out.println("[INFO] MainDaniel OutOfMemory :-( "+e);
                System.out.println("[INFO] OutOfMemory in:"+alg.getName()+" on:"+ domainName);
            }
            catch (FileNotFoundException e) {
                System.out.println("[INFO] FileNotFoundException At inputPath:"+inputPath);
//                    e.printStackTrace();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }  catch (IOException e) {
            System.out.println("[INFO] IOException At outputPath:"+outputPath);
            e.printStackTrace();
        }
        finally {

        }
        return retArray;
    }

    private static void saveOptimalSolution(SearchResult result, int instance, SearchDomain domain){
//        InputStream is = new FileInputStream(new File(inputPath + "/" + i + ".in"));
        String alpha = domainParams.get("cost-function");
        String optimalSolutionParam = "FIF"+alpha;
        String savePath = inputPath+"/optimalSolutions_"+optimalSolutionParam+".in";
        File saveFile = new File(savePath);
        StringBuilder sb = new StringBuilder();
        int lineCounter = 0;
        if(saveFile.exists()){
            try {
                InputStream stream = new FileInputStream(saveFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line = reader.readLine();
                while(line != null){
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));

                    line = reader.readLine();
                    lineCounter++;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(lineCounter < instance){
            int dOpt = result.getSolutions().get(0).getLength();
            double hOpt = result.getSolutions().get(0).getCost();
            sb.append(instance + "," + dOpt + "," + hOpt);
//            System.out.println("[INFO] Optimal solution found:\tinstance: "+instance+"\tdepth: "+dOpt);
            sb.append(System.getProperty("line.separator"));

            try {
                List<Operator> operators = result.getSolutions().get(0).getOperators();
                PrintWriter writer = new PrintWriter(inputPath + "/optimalOperators_"+optimalSolutionParam+"_" + instance + ".in", "UTF-8");
                State parentState = domain.initialState();
                State childState = null;
                for (int i = 0 ; i <=operators.size()-1; i++) {
                    boolean appended=false;
                    Operator iop = operators.get(i);
                    childState = domain.applyOperator(parentState,iop);
                    PackedElement childPacked = domain.pack(childState);
                    int operatorsNum = domain.getNumOperators(parentState);
                    for(int j=0 ; j < operatorsNum;j++){
                        Operator op = domain.getOperator(parentState,j);
                        State stateJ = domain.applyOperator(parentState,op);
                        PackedElement packedJ = domain.pack(stateJ);
                        if(childPacked.equals(packedJ) || stateJ.equals(childState)){
                            appended=true;
//                            System.out.println(parentState.dumpStateShort()+" ->op("+j+")-> "+childState.dumpStateShort());
                            parentState = childState;
                            String toSave = j+"";
                            writer.println(toSave);
//                            System.out.print(toSave+" ");
                            break;
                        }
                    }
                    if(!appended){
                        System.out.println("[WARNING] state not added");
                    }
                }
                if(!domain.isGoal(childState)){
                    System.out.println("[WARNING] Goal NOT Reached");
                }
                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try {
            PrintWriter writer = new PrintWriter(savePath, "UTF-8");
            writer.print(sb);
//            System.out.println(sb);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void saveSolutionPathAsInstances(SearchResult result, int instance){
/*           String gridName = "brc202d.map";
            String path = relPath + "input/GridPathFinding/"+gridName;
        for(int i=1;i<=100;i++){
                InputStream stream = new FileInputStream(new File(path+"/"+i+".in"));
               BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
               String line = reader.readLine();
               String sz[] = line.trim().split(" ");
               System.out.println(sz[1]);
               sz[1] = path;
               String joined = String.join(" ", sz);
               System.out.println(joined);

               Path path = Paths.get(relPath + "input/GridPathFinding/"+gridName+"/"+i+".in");
               Charset charset = StandardCharsets.UTF_8;
               String toReplace = "input/gridpathfinding/raw/maps/brc202d.map";
               String replaceWith = relPath + "input/GridPathFinding/"+gridName;
               String content = new String(Files.readAllBytes(path), charset);
               System.out.println(content);
               content = content.replaceAll(toReplace, replaceWith);
               System.out.println(content);
               Files.write(path, content.getBytes(charset));
               String line = reader.readLine();
               String[] ary = line.split(" ");
               PrintWriter writer = new PrintWriter("C:/Users/Daniel/Documents/gilond/Master/Research Data/input/fifteenpuzzle/states15/"+i+".in", "UTF-8");
               writer.println("15");
               writer.println();
               for(int j=0;j<16;j++){
                   writer.println(ary[j]);
               }
               writer.println();
               for(int j=0;j<16;j++){
                   writer.println(j);
               }
               writer.close();
        }*/

/*        try {
            InputStream stream = new FileInputStream(new File(path+"/.txt"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();
            int counter = 1;
            while(line != null){
                String[] ary = line.split(" ");
                PrintWriter writer = new PrintWriter(path+"/"+counter+".in", "UTF-8");
                writer.println("15");
                writer.println();
                for(int j=0;j<16;j++){
                    writer.println(ary[j]);
                }
                writer.println();
                for(int j=0;j<16;j++){
                    writer.println(j);
                }
                writer.close();
                counter++;
                line = reader.readLine();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        String path = relPath + "input/pancakes/generated-40byInstanceStep/Instance"+instance;

        File theDir = new File(path);
        boolean dirExist = theDir.exists();
        // if the directory does not exist, create it
        if (!dirExist) {
//            System.out.println("creating directory: " + path);
            try{
                theDir.mkdir();
                dirExist = true;
            }
            catch(SecurityException se){
                //handle it
            }
        }
        if(dirExist) {
            List<Solution> solutions = result.getSolutions();
            Solution solution = solutions.get(0);
            List<State> states = solution.getStates();
            List<Operator> operators = solution.getOperators();

            try {
                PrintWriter writer = new PrintWriter(path+"/operators.in", "UTF-8");
                for (Operator operator : operators) {
                    String toSave = operator.toString();
                    writer.println(toSave);
                    System.out.println(toSave);
                }
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

/*            for(int i = states.size()-1 ; i > 0 ; i--){
                String toSave = "40\n"+states.get(i).dumpStateShort();
                System.out.println(toSave);
                int pos = states.size() - i;
                try {
                    PrintWriter writer = new PrintWriter(path+"/"+pos+".in", "UTF-8");
                    writer.println(toSave);
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }

    private static void createSummary()throws IOException{
//        int algoNum = SearchAlgorithmArr.length;
        try {
            int num = 0;
            String path = relPath +"results/summary/"+summaryName+".xls";
            File summaryFile = new File(path);
            WritableWorkbook writableWorkbook;
            NumberFormat numberFormat1000 = new NumberFormat("#,###");
            WritableCellFormat cellFormat1000 = new WritableCellFormat(numberFormat1000);
            WritableCellFormat cellFormatDecimal = new WritableCellFormat(new NumberFormat("#,###.00"));
            if(summaryFile.exists()){
                Workbook existingWorkbook = Workbook.getWorkbook(summaryFile);
                num = existingWorkbook.getNumberOfSheets();
                writableWorkbook = Workbook.createWorkbook(summaryFile,existingWorkbook);
            }
            else{
                writableWorkbook = Workbook.createWorkbook(summaryFile);
            }

            WritableSheet writableSheet;
            WritableSheet existingSheet = writableWorkbook.getSheet(summarySheetName);
            if(existingSheet != null){
                writableSheet = existingSheet;
            }
            else{
                writableSheet = writableWorkbook.createSheet(summarySheetName,num);
            }

            Label label;
            int currentCol = 0;
            int currentRow = 0;

            String[] headers = {"Weight","Alpha","Prefix","Alg Name","Success Rate","Depth","Cost","Generated","Expanded","Cpu Time","Wall Time"};
            if(alg.getName().equals("DP")){
                int aLen = headers.length;
                int bLen = DPextraHeaders.length;
                String [] temp = new String[aLen+bLen];
                System.arraycopy(headers, 0, temp, 0, aLen);
                System.arraycopy(DPextraHeaders, 0, temp, aLen, bLen);
                headers = temp;
            }
            int indent = 4;//skip first XXX columns

            for (String header : headers) {
                label = new Label(currentCol++, currentRow, header);
                writableSheet.addCell(label);
            }

            for ( Weights.SingleWeight ws :weights.NATURAL_WEIGHTS) {
                w = ws;
                totalWeight = w.wh / w.wg;

                System.out.println("Summary "+domainName + "\tweight: wg : " + w.wg + " wh: " + w.wh);
                for (SearchAlgorithm aSearchAlgorithmArr : SearchAlgorithmArr) {
                    alg = aSearchAlgorithmArr;
                    currentRow++;
                    String fileName = outputPath + alg.getName() + "_" + (int) w.wg + "_" + (int) w.wh + "_" + fileEnd + ".csv";
                    File file = new File(fileName);
                    if (file.exists()) {
                        byte[] data = new byte[(int) file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(data);
                        fis.close();
                        String str = new String(data, "UTF-8");
                        String resultsLine[] = str.split("\n");

                        writableSheet.addCell(new Number(0, currentRow, totalWeight, cellFormatDecimal));
                        writableSheet.addCell(new Number(1, currentRow, Double.parseDouble(domainParams.get("cost-function")), cellFormatDecimal));
                        writableSheet.addCell(new Label(2, currentRow, globalPrefix));
                        writableSheet.addCell(new Label(3, currentRow, alg.getName()));
                        double[] solD = new double[headers.length - indent];
                        for (int j = 1; j <= stopInstance; j++) {
                            String[] sol = resultsLine[j].split(",");
                            for (int k = 0; k < headers.length - indent; k++) {
                                solD[k] += Double.parseDouble(sol[k + 1]);
/*                                if(k==8 && Double.parseDouble(sol[k]) == 0.0){
                                    solD[7] += Double.parseDouble(sol[4]);
                                }*/
                            }

                        }
                        // calculate Average
                        for (int k = 1; k < solD.length; k++) {//skip Success rate
                            solD[k] = solD[k] / solD[0];// value/found
                        }
                        for (int k = 0; k < solD.length; k++) {
//                            WritableCellFormat format = cellFormat1000;
//                                if(k==0) format = cellFormatDecimal;//weight
                            writableSheet.addCell(new Number(k + indent, currentRow, solD[k], cellFormat1000));
                        }

                    }
                }
            }

            writableWorkbook.write();
            writableWorkbook.close();
        } catch (IOException e) {
            throw new IOException("File " + summarySheetName + " already Exist, or Folder is missing");
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    private static void WalkPath(String savename){
        System.out.println("WalkPath " + filePrefix);
        String alpha = domainParams.get("cost-function");
        String optimalSolutionParam = "FIF"+alpha;
        savename += alpha;
//        String optimalFileEnd = "_alpha1";
        OutputResult output=null;
        try {
            output = new OutputResult(relPath + "results/"+savename + fileEnd, null, -1, -1, null, false, true);
            String headers = "Instance,d,dHat,h,hHat,d*,h*";
            output.writeln(headers);
            System.out.println(headers);

            String optimalSolutionsName = inputPath + "/optimalSolutions_"+optimalSolutionParam+".in";
            File optimalSolutionsFile = new File(optimalSolutionsName);
            if(!optimalSolutionsFile.exists()) {
                System.out.println("[WARNING] optimalSolutions.in not not found!");
            }

            FileInputStream optimalSolutionsStream = new FileInputStream(new File(optimalSolutionsName));
            BufferedReader optimalSolutionsReader = new BufferedReader(new InputStreamReader(optimalSolutionsStream));

/*            String alpha = outputPath.split("alpha")[1];
            alpha = alpha.split("_")[0];*/

            for ( Weights.SingleWeight ws :weights.NATURAL_WEIGHTS) {
                w = ws;
/*                String path = outputPath+alg.getName()+"_"+(int)w.wg+"_"+(int)w.wh+"_"+fileEnd+".csv";
                InputStream Stream = new FileInputStream(new File(path));
                BufferedReader Reader = new BufferedReader(new InputStreamReader(Stream));
                String Line = Reader.readLine();*/

                for (int instance = startInstance; instance <= stopInstance; instance++) {
                    String optimalSolutionsLine = optimalSolutionsReader.readLine();
                    if(optimalSolutionsLine == null)
                        continue;
                    String[]optimalSolutionsLineArr = optimalSolutionsLine.split(",");
                    instance = Integer.parseInt(optimalSolutionsLineArr[0]);
                    System.out.println("Walk path instance "+instance);

                    String instancePath = inputPath + "/optimalOperators_"+optimalSolutionParam+"_"+instance+".in";
                    File optFile = new File(instancePath);
                    if(!optFile.exists()) {
                        System.out.println("[INFO] optimal operator not not found for instance "+instance);
                        continue;
                    }
                    FileInputStream optimalOperatorsStream = new FileInputStream(new File(instancePath));
                    BufferedReader optimalOperatorsReader;

                    double solD = Double.parseDouble(optimalSolutionsLineArr[1]);
                    double solH = Double.parseDouble(optimalSolutionsLineArr[2]);

                    InputStream is = new FileInputStream(new File(inputPath + "/" + instance + ".in"));

                    Class<?> cl = Class.forName("org.cs4j.core.domains."+domainName);
                    Constructor<?> cons = cl.getConstructor(InputStream.class);
                    SearchDomain domain = (SearchDomain) cons.newInstance(is);
                    for(Map.Entry<String, String> entry : domainParams.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        domain.setAdditionalParameter(key,value);
                    }

//                    SearchDomain domain = new Pancakes(is);
                    EES ees = new EES(domain);

                    State parentState = domain.initialState();
                    State childState;
                    EES.Node parentNode = ees.createNode(parentState, null, null, null, null);;

                    optimalOperatorsStream.getChannel().position(0);
                    optimalOperatorsReader = new BufferedReader(new InputStreamReader(optimalOperatorsStream));
                    String optimalOperatorsLine;
                    boolean onlyInitialState = true;
                    boolean printed = false;
                    while (solD > 1) {
//                    System.out.println(parentState.dumpStateShort());
                    optimalOperatorsLine = optimalOperatorsReader.readLine();
                        int opPos = Integer.parseInt(optimalOperatorsLine);
                        Operator op = domain.getOperator(parentState, opPos);
                        childState = domain.applyOperator(parentState, op);
                        solH -= op.getCost(childState,parentState);
                        solD--;
                        EES.Node childNode = ees.createNode(childState, parentNode, parentState, op, op.reverse(parentState));

                        StringBuilder sb = new StringBuilder();
                        sb.append(instance);
                        sb.append(",");
                        sb.append(childNode.d);
                        sb.append(",");
                        sb.append(childNode.dHat);
                        sb.append(",");
                        sb.append(childNode.h);
                        sb.append(",");
                        sb.append(childNode.hHat);
                        sb.append(",");
                        sb.append(solD);
                        sb.append(",");
                        sb.append(solH);
                        sb.append(",");
/*                        sb.append(w.wh/w.wg);
                        sb.append(",");*/

                        String toPrint = String.valueOf(sb);
                        if((onlyInitialState && !printed) || !onlyInitialState) {
                            output.writeln(toPrint);
                            System.out.println(toPrint);
                            printed = true;
                        }

//                        System.out.println(parentState.dumpStateShort()+" ("+opPos+") "+childState.dumpStateShort());

                        parentState = childState;
                        parentNode = childNode;
                    }

                        optimalOperatorsLine = optimalOperatorsReader.readLine();
                    int opPos = Integer.parseInt(optimalOperatorsLine);
                    Operator op = domain.getOperator(parentState, opPos);
                    childState = domain.applyOperator(parentState, op);
                    if(!domain.isGoal(childState)){
                        System.out.println("[WARNING] Last state is NOT Goal");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        finally {
            output.close();
        }

    }

    private static void afterSetDomain() throws IOException{
/*        WalkPath("FIF1000_");
        if(true) return;*/

        //search over algo and weight
        solvableNum = stopInstance-startInstance+1;
        solvableInstances = new boolean[solvableNum][SearchAlgorithmArr.length];
        // Fill each row with 1.0
        for (boolean[] algo: solvableInstances)
            Arrays.fill(algo, true);
        for (int i = startInstance; i <= stopInstance; ++i) {
            int algPos = 0;
            for (SearchAlgorithm sa : SearchAlgorithmArr) {
                alg = sa;
                for ( Weights.SingleWeight ws :weights.NATURAL_WEIGHTS) {
                    w = ws;
                    totalWeight = w.wh / w.wg;
                    searchSave100FR(true,i,algPos);
                }
                algPos++;
            }
        }

        //create summary over algo and weight
//        createSummary();
    }

    public static void main(String[] args) throws IOException, WriteException, BiffException {
//        saveSolutionPathAsInstances();
//        domainName = "GridPathFinding";

/*        String ANSI_RESET = "\u001B[0m";
        String ANSI_BLACK = "\u001B[30m";
        String ANSI_RED = "\u001B[31m";
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_BLUE = "\u001B[34m";
        String ANSI_PURPLE = "\u001B[35m";
        String ANSI_CYAN = "\u001B[36m";
        String ANSI_WHITE = "\u001B[37m";
        System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);
        System.out.println("\u001B[31m" + "This text is red!" + "\u001B[0m");
        System.out.println("\u001B[32m" + "This text is green!" + "\u001B[0m");
*/


        for(int i=0; i<relPathes.length; i++) {
            Path path = Paths.get(relPathes[i]);
            if (Files.exists(path)) {
                relPath = relPathes[i];
                System.out.println("path:"+relPath);
            }
        }

        overwriteFile = true;//if false throws error if file exists already
        overwriteSummary = true;
        appendToFile = true;//true: do not calculate instances again if already exist
        useBestFR = false;
        useOracle = false;
        saveSolutionPath = false;
        startInstance = 1;
        stopInstance = 100;

        DPextraHeaders = new String[]{"Generated until 1st goal", "number of times goal was found","fmin"};

        summaryName = "NO-SUMMARY-SET";

        if(useOracle) globalPrefix = "ORACLE_";
        else globalPrefix = "";

//        globalPrefix = "DiagonalHeavy";
//        globalPrefix = "DiagonalInverse";
//        globalPrefix = "D-1";
//        globalPrefix = "FlipedDD_D";

        if(useBestFR)fileEnd = "bestFR";
        else fileEnd = "NoFr";

        SearchAlgorithm[] AlgoArr = {
//                new IDAstar(),
//                new BEES(),
//                new WAStar(),
                new DP("RDPSU",true,true,false),
//                new DP("DPSU",true,false,false),
//                new DP("DPS",false,false,false),
//                new DP("RWAU",true,true,true),
//                new DP("WAU",true,false,true),
                new EES(1),
        };
//        AlgoArr[0].setAdditionalParameter("FR","2500");

        SearchAlgorithmArr = AlgoArr;

        String[] domains = {
//            "FifteenPuzzle",
            "Pancakes",
//            "VacuumRobot",
//            "DockyardRobot",
//            "GridPathFinding"
        };

        for (String dN : domains) {
            domainName = dN;
            domainParams = new HashMap<>();
            switch (domainName) {
                case "FifteenPuzzle": {
                    summaryName = "15DP-ALL";
                    for(int i = 1 ; i <= 2 ; i++) {
                        int resolution = 1;
                        double alpha;
                        if(i%2 == 1)alpha = (double) ((i+1)/2) / resolution;
                        else        alpha = (double) (-i/2) / resolution;
                        domainParams.put("cost-function", alpha+"");
                        filePrefix = globalPrefix+"alpha" + alpha + "_";  //for cost-function
//                    filePrefix = "";  //for unit costs
                        System.out.println("Solving FifteenPuzzle " + filePrefix);
                        inputPath = relPath + "input/FifteenPuzzle/states15";
//                        inputPath = relPath + "input/FifteenPuzzle/fif1000";
//                    inputPath = relPath + "input/FifteenPuzzle/states15InstanceByStep/43";
                        outputPath = relPath + "results/FifteenPuzzle/" + filePrefix;
//                        outputPath = relPath + "results/tests/"+filePrefix;
                        summarySheetName = filePrefix + "FifteenPuzzle";
                        afterSetDomain();
                    }
                    break;
                }
                case "Pancakes": {
                    int[] pancakesNum;
                    summaryName = "Pancakes";
//                    pancakesNum = new int[]{10, 12, 16, 20, 40};
//                    pancakesNum = new int[]{10, 12, 16};
//                    pancakesNum = new int[]{16,20,40};
//                    pancakesNum = new int[]{40};
                    pancakesNum = new int[]{101};
//                    pancakesNum = new int[]{20};
//                    pancakesNum = new int[]{16};
//                    pancakesNum = new int[]{12};
//                    pancakesNum = new int[]{10};
                    for(int gap = 0 ; gap <= 0  ; gap+=1) {
//                        double GAPK = ((double)gap/2);
                        double GAPK = (double)gap;
                        for (int j = 0; j < pancakesNum.length; j++) {
                            int num = pancakesNum[j];
                            for(int i = 4 ; i <= 4 ; i+=1) {
                                int resolution = 2;
                                double alpha;
                                if(i%2 == 1)alpha = (double) ((i+1)/2) / resolution;
                                else        alpha = (double) (-i/2) / resolution;
                                domainParams.put("cost-function", alpha+"");
                                filePrefix = globalPrefix + num + "_alpha" + alpha + "_";  //for cost-function

                                domainParams.put("GAP-k", GAPK + "");
                                filePrefix += "GAP-" + GAPK + "_";

                                System.out.println("\rSolving Pancakes " + filePrefix);
                                inputPath = relPath + "input/pancakes/generated-" + num;
                                outputPath = relPath + "results/pancakes/" + num + "/" + filePrefix;
                                summarySheetName = filePrefix + "pancakes";
                                afterSetDomain();
                            }
                        }
                    }
                    break;
                }
                case "VacuumRobot": {
                    int[] dirts;
                    dirts = new int[]{10};
//                    dirts = new int[]{5};
//                    int[] dirts = new int[]{5, 10};
                    for(int i=1 ; i <= 1 ; i+=1) {
                        int resolution = 2;
                        double alpha;
                        if(i%2 == 1)alpha = (double) ((i+1)/2) / resolution;
                        else        alpha = (double) (-i/2) / resolution;
                        for (int j = 0; j < dirts.length; j++) {
                            for(int shrinkTo = dirts[j] ; shrinkTo <= dirts[j] ; shrinkTo+=1){
                                filePrefix = globalPrefix + "";

                                domainParams.put("cost-function", alpha+"");
                                filePrefix += "alpha"+alpha+"_";

                                domainParams.put("shrinkTo", shrinkTo+"");
                                filePrefix += "shrinkTo"+shrinkTo+"_";

                                System.out.println("\rSolving VacuumRobot "+filePrefix);
                                inputPath = relPath + "input/VacuumRobot/generated-" + dirts[j] + "-dirt";
                                outputPath = relPath + "results/VacuumRobot/" + dirts[j] + "-dirt/" + filePrefix;
                                summarySheetName = "Vacuum-" + dirts[j] + "-a"+alpha+"-s"+shrinkTo;
                                afterSetDomain();
                            }
                        }
                    }
                    break;
                }
                case "DockyardRobot": {
                    filePrefix = "";
                    System.out.println("Solving DockyardRobot "+filePrefix);
                    inputPath = relPath + "input/dockyard-robot-max-edge-2-out-of-place-30/";
                    outputPath = relPath + "results/dockyard-robot-max-edge-2-out-of-place-30/"+filePrefix;
                    summarySheetName = "dockyard-robot";
                    afterSetDomain();
                    break;
                }
                case "GridPathFinding": {
                    String gridName = "brc202d.map";
                    System.out.println("Solving VacuumRobot");
                    inputPath = relPath + "input/GridPathFinding/" + gridName;
                    outputPath = relPath + "results/GridPathFinding/" + gridName;
                    summarySheetName = gridName;
                    filePrefix = globalPrefix+"";
                    afterSetDomain();
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
    }
}
