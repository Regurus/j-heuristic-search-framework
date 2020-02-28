package core.generators;

import core.State;
import core.domains.FifteenPuzzle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

public class FifteenPuzzleGenerator extends GeneralInstancesGenerator {
    public static void main(String args[]) throws IOException {
        int depth =  100;
        int numProblems = 150;

        UniversalGenerator universalGenerator = new UniversalGenerator();
        FifteenPuzzle puzzle = new FifteenPuzzle();
        String start = "4,4\n\n";
        String end = "0\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15";
        HashSet<String> problems = new HashSet<>();
        String outputDirectory = "testResources/core/domains/FifteenPuzzleInstances";
        for(int i=0; i<numProblems; i++){
            State initial = universalGenerator.generate(puzzle, depth);
            String str = initial.convertToStringShort();
            String [] array = str.split(" ");
            String result = start;

            for(int j=0; j<array.length; j++){
                result+=array[j]+"\n";
            }
            result += end;
            if(!problems.add(result)){
                i--;
                continue;
            }

            FileWriter fw = new FileWriter(new File(outputDirectory, i + ".in"));
            fw.write(result);
            fw.close();
        }
    }
}