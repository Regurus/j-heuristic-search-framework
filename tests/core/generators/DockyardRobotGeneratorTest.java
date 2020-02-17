package core.generators;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class DockyardRobotGeneratorTest {
    private static final String PATH = System.getProperty("user.dir") + "\\testResources\\core\\domains\\DockyardRobot";
    private static final int LOCATIONS = 0, CRANES = 1, BOXES = 2, PILES = 3, ROBOTS = 4;

    /**
     * The assumption we make : Locations Count = Cranes Count = Piles Count - 1 crane and pile per location.
     */

    @Test
    public void TestFilesGenerated() throws Exception {
        File dir = new File(PATH);
        File[] files = dir.listFiles();

        assertNotNull("Invalid directory path", files);
        assertTrue("Directory should not be empty", files.length != 0);
        for(File file : files){
            //Variables needed
            BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
            String line;
            int curLoc = -1;

            HashMap<Integer, Integer> locationsToPiles = new HashMap<>();
            HashMap<Integer, Integer> pilesToLocations = new HashMap<>();

            HashMap<Integer, List<Integer>> pilesToBoxes = new HashMap<>();
            HashMap<Integer, Integer> boxesToPiles = new HashMap<>();

            HashMap<Integer, Integer> containersToLocations = new HashMap<>();

            //Read arguments given
            int[] args = new int[5];
            String tmp;

            for(int i=0;i<5;i++){
                tmp = reader.readLine().split(" ")[1];
                if(isInt(tmp))
                    args[i] = Integer.parseInt(tmp);
                else
                    assertFalse("Not an Integer in arguments initialization!", true);
            }

            assertEquals("Only one robot worker can be initiated", 1, args[ROBOTS]);

            while((line = reader.readLine()) != null){
                try{
                    if(line.length() == 0)
                        continue;
                    String[] splitLine = line.split(": ");
                    if(splitLine.length>1){
                        switch(splitLine[0]){
                            case "adjacent":
                                assertEquals("Incorrect number of arguments given- ", args[LOCATIONS], splitLine[1].split(" ").length);
                                continue;
                            case "cranes":
                                if(isInt(splitLine[1]))
                                    assertEquals("Number of cranes at the location-", 1, Integer.parseInt(splitLine[1]));
                                else
                                    assertFalse("Cranes must be an int", true);
                                continue;
                            case "piles":
                                if(isInt(splitLine[1])){
                                    if(locationsToPiles.containsKey(curLoc) || pilesToLocations.containsKey(Integer.parseInt(splitLine[1])))
                                        assertFalse("double entry - fault in file", true);
                                    locationsToPiles.put(curLoc,Integer.parseInt(splitLine[1]));
                                    pilesToLocations.put(Integer.parseInt(splitLine[1]), curLoc);
                                }
                                continue;
                            default:
                                assertFalse("Unknown parameter input", true);
                                continue;
                        }
                    }
                    else{
                        splitLine = line.split(" ");
                        if(!isInt(splitLine[1]))
                            assertFalse("Parameter must be followed by an int", true);
                        int input = Integer.parseInt(splitLine[1]);
                        switch(splitLine[0]){
                            case "location":
                                curLoc = input;
                                continue;
                            case "pile":
                                line = reader.readLine();
                                List<Integer> boxes = new ArrayList<>();

                                for(String s : line.split(" ")){
                                    if(!isInt(s))
                                        assertFalse("box indexes must be an int", true);
                                    int index = Integer.parseInt(s);
                                    boxes.add(index);

                                    if(boxesToPiles.containsKey(index))
                                        assertFalse("double entry - fault in file", true);
                                    boxesToPiles.put(index, input);
                                }
                                if(pilesToBoxes.containsKey(input))
                                    assertFalse("double entry - fault in file", true);
                                pilesToBoxes.put(input, boxes);
                                continue;
                            case "container":
                                if(containersToLocations.containsKey(input))
                                    assertFalse("double entry - fault in file", true);
                                if(splitLine.length != 3 || !isInt(splitLine[2]) ||
                                            (Integer.parseInt(splitLine[2]) < 0) || Integer.parseInt(splitLine[2])>= args[LOCATIONS])
                                    assertFalse("Invalid container input", true);
                                containersToLocations.put(input, 0);
                                continue;
                            default:
                                assertFalse("Unknown parameter input", true);
                                continue;
                        }
                    }
                }catch(Exception e){
                    assertFalse("No exception should be thrown!\n" + line, true);
                }
            }
        }
    }

    private boolean isInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}