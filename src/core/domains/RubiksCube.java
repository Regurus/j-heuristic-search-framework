package core.domains;

import core.*;
import core.algorithms.IDAstar;
import core.collections.PackedElement;

import java.io.*;
import java.util.*;

import core.data.TrueDistanceEstimator;
import org.apache.log4j.Logger;

public class RubiksCube implements SearchDomain {
    //<editor-fold desc=Constants>
    private final static byte[][][] ORIGINAL_CUBE = {{{0,0,0},{0,0,0},{0,0,0}},{{1,1,1},{1,1,1},{1,1,1}},{{2,2,2},{2,2,2},{2,2,2}},
            {{3,3,3},{3,3,3},{3,3,3}},{{4,4,4},{4,4,4},{4,4,4}},{{5,5,5},{5,5,5},{5,5,5}}};
    private final byte[] ANTOGONIST_SIDES = {5,3,4,1,2,0};
    public static final byte[][][] CUBIES_LIST = {
            {{0,0,0},{1,0,0},{4,2,0}},
            {{0,0,2},{1,2,0},{2,0,0}},
            {{0,2,2},{2,2,0},{3,0,0}},
            {{1,2,2},{2,0,2},{5,0,0}},
            {{3,2,0},{0,2,0},{4,0,0}},
            {{4,2,2},{1,0,2},{5,0,2}},
            {{3,2,2},{4,0,2},{5,2,2}},
            {{2,2,2},{3,0,2},{5,2,0}},
            {{0,0,1},{1,1,0}},
            {{0,1,0},{4,1,0}},
            {{3,0,1},{2,2,1}},
            {{0,2,1},{3,1,0}},
            {{2,0,1},{1,2,1}},
            {{1,0,1},{4,2,1}},
            {{1,1,2},{5,0,1}},
            {{5,1,2},{4,1,2}},
            {{3,1,2},{5,2,1}},
            {{0,1,2},{2,1,0}},
            {{2,1,2},{5,1,0}},
            {{3,2,1},{4,0,1}}};
    private static final byte[][] COLOR_COMBOS_1 = {
            {1,0,2},{0,2,1},{2,1,0},
            {2,0,3},{0,3,2},{3,2,0},
            {4,3,0},{3,0,4},{0,4,3},
            {4,0,1},{0,1,4},{1,4,0},
            {5,1,2},{1,2,5},{2,5,1},
            {5,2,3},{2,3,5},{3,5,2},
            {5,3,4},{3,4,5},{4,5,3},
            {5,4,1},{4,1,5},{1,5,4}
    };
    private static final byte[][] COLOR_COMBOS_2 = {
            {1,2,0},{2,0,1},{0,1,2},
            {2,3,0},{3,0,2},{0,2,3},
            {4,0,3},{0,3,4},{3,4,0},
            {4,1,0},{1,0,4},{0,4,1},
            {5,2,1},{2,1,5},{1,5,2},
            {5,3,2},{3,2,5},{2,5,3},
            {5,4,3},{4,3,5},{3,5,4},
            {5,1,4},{1,4,5},{4,5,1}
    };
    //</editor-fold>
    //<editor-fold desc=MD variables>
    static final HashMap<Integer,HashMap> database = new HashMap<>();
    private byte[] cubieTarget = null;
    private byte[][] cubiePosition = null;
    enum Operators{
        TOP_RIGHT_1234,
        TOP_LEFT_1234,
        BOT_RIGHT_1234,
        BOT_LEFT_1234,
        TOP_RIGHT_0351,
        TOP_LEFT_0351,
        BOT_RIGHT_0351,
        BOT_LEFT_0351,
        TOP_RIGHT_2540,
        TOP_LEFT_2540,
        BOT_RIGHT_2540,
        BOT_LEFT_2540,
    }

    //</editor-fold>
    public static HeuristicType activeHeuristic;
    public byte[][][] startingState;
    public static Logger log = Logger.getLogger(RubiksCube.class.getName());
    private static TrueDistanceEstimator estimator = new TrueDistanceEstimator(7,"resources/RubikTD.txt",new RubiksCube(ORIGINAL_CUBE, RubiksCube.HeuristicType.PARALLEL_LINES));


    //<editor-fold desc=MD functions>
    private byte[][] getCubieByIndex(int index){
        return CUBIES_LIST[index];
    }
    /**
     * encode coordinate to single digit representation, ignoring permutation
     * @param coordinate
     * @return single digit representation of a coordinate or -1 if incorrect coordinate given
     */
    public int getCubieIndex(byte[][] coordinate){
        if(coordinate.length==3){
            if(Arrays.equals(coordinate[0], CUBIES_LIST[0][0])||Arrays.equals(coordinate[1], CUBIES_LIST[0][0])||Arrays.equals(coordinate[2], CUBIES_LIST[0][0])){
                return 0;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[1][0])||Arrays.equals(coordinate[1], CUBIES_LIST[1][0])||Arrays.equals(coordinate[2], CUBIES_LIST[1][0])){
                return 1;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[2][0])||Arrays.equals(coordinate[1], CUBIES_LIST[2][0])||Arrays.equals(coordinate[2], CUBIES_LIST[2][0])){
                return 2;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[3][0])||Arrays.equals(coordinate[1], CUBIES_LIST[3][0])||Arrays.equals(coordinate[2], CUBIES_LIST[3][0])){
                return 3;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[4][0])||Arrays.equals(coordinate[1], CUBIES_LIST[4][0])||Arrays.equals(coordinate[2], CUBIES_LIST[4][0])){
                return 4;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[5][0])||Arrays.equals(coordinate[1], CUBIES_LIST[5][0])||Arrays.equals(coordinate[2], CUBIES_LIST[5][0])){
                return 5;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[6][0])||Arrays.equals(coordinate[1], CUBIES_LIST[6][0])||Arrays.equals(coordinate[2], CUBIES_LIST[6][0])){
                return 6;
            }
            else if(Arrays.equals(coordinate[0], CUBIES_LIST[7][0])||Arrays.equals(coordinate[1], CUBIES_LIST[7][0])||Arrays.equals(coordinate[2], CUBIES_LIST[7][0])){
                return 7;
            }
        }
        else if(coordinate.length==2){
            if(Arrays.equals(coordinate[0], CUBIES_LIST[8][0])||Arrays.equals(coordinate[1], CUBIES_LIST[8][0])){
                return 8;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[9][0])||Arrays.equals(coordinate[1], CUBIES_LIST[9][0])){
                return 9;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[10][0])||Arrays.equals(coordinate[1], CUBIES_LIST[10][0])){
                return 10;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[11][0])||Arrays.equals(coordinate[1], CUBIES_LIST[11][0])){
                return 11;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[12][0])||Arrays.equals(coordinate[1], CUBIES_LIST[12][0])){
                return 12;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[13][0])||Arrays.equals(coordinate[1], CUBIES_LIST[13][0])){
                return 13;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[14][0])||Arrays.equals(coordinate[1], CUBIES_LIST[14][0])){
                return 14;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[15][0])||Arrays.equals(coordinate[1], CUBIES_LIST[15][0])){
                return 15;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[16][0])||Arrays.equals(coordinate[1], CUBIES_LIST[16][0])){
                return 16;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[17][0])||Arrays.equals(coordinate[1], CUBIES_LIST[17][0])){
                return 17;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[18][0])||Arrays.equals(coordinate[1], CUBIES_LIST[18][0])){
                return 18;
            }
            if(Arrays.equals(coordinate[0], CUBIES_LIST[19][0])||Arrays.equals(coordinate[1], CUBIES_LIST[19][0])){
                return 19;
            }
        }
        return -1;
    }

    public void build3DMD() {
        if(database.size()==0)
            this.initDatabaseStructure();
        IDAstar algo = new IDAstar();
        HashSet<Byte> colors = new HashSet<>(Arrays.asList(new Byte[]{0, 1, 2, 3, 4, 5}));
        //3 color cubies
        for (int cubieIndex = 0; cubieIndex < 8; cubieIndex++){
            byte[][] currentCoordinates = this.getCubieByIndex(cubieIndex);
            byte[][] colorCombos = COLOR_COMBOS_1;
            if(cubieIndex==1||cubieIndex==2){
                colorCombos = COLOR_COMBOS_2;
            }
            for(byte[] combo: colorCombos){
                byte color1 = combo[0];
                byte color2 = combo[1];
                byte color3 = combo[2];
                RubiksCube domain = new RubiksCube(new byte[]{color1,color2,color3},currentCoordinates);
                SearchResult res = algo.search(domain);
                System.out.println(color1 + " " + color2 + " " + color3 + " | cost: "+res.getSolutions().get(0).getLength());
                database.get(cubieIndex).put(""+color1+color2+color3,res.getSolutions().get(0).getLength());
            }
            System.out.println("End cycle: "+cubieIndex);

        }
        //2 position cubies
        for (int cubieIndex = 8; cubieIndex < 20; cubieIndex++) {
            byte[][] currentCoordinates = this.getCubieByIndex(cubieIndex);
            for (byte color1 : colors) {
                HashSet<Byte> colors1 = new HashSet<>(colors);
                colors1.remove(color1);//removing same color
                for (byte color2 : colors1) {
                    if((color1==5&&color2==0)||(color1==0&&color2==5))
                        continue;
                    if((color1==3&&color2==1)||(color1==1&&color2==3))
                        continue;
                    if((color1==4&&color2==2)||(color1==2&&color2==4))
                        continue;
                    System.out.println(color1 + " " + color2);
                    RubiksCube domain = new RubiksCube(new byte[]{color1,color2},currentCoordinates);
                    SearchResult res = algo.search(domain);
                    System.out.println(color1 + " " + color2 + " | cost: "+res.getSolutions().get(0).getLength());
                    database.get(cubieIndex).put(""+color1+color2,res.getSolutions().get(0).getLength());
                }
            }
        }
        try {
            this.save3DMDToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done!");
    }

    //TODO remove this
    private boolean validateCube(byte[][][] cube){
        for(byte[][] positions: CUBIES_LIST){
            String repr = "";
            for (byte[] pos:positions){
                repr+=cube[pos[0]][pos[1]][pos[2]];
            }
            if(repr.contains("0")&&repr.contains("5")){
                System.out.print("FAIL @"+positions[0][0]+positions[0][1]+positions[0][2]+":"+positions[1][0]+positions[1][1]+positions[1][2]);
                if(positions.length==3)
                    System.out.println(":"+positions[2][0]+positions[2][1]+positions[2][2]);
                else System.out.println(" ");
                System.out.println(repr);
                return false;
            }
            if(repr.contains("3")&&repr.contains("1")){
                System.out.print("FAIL @"+positions[0][0]+positions[0][1]+positions[0][2]+":"+positions[1][0]+positions[1][1]+positions[1][2]);
                if(positions.length==3)
                    System.out.println(":"+positions[2][0]+positions[2][1]+positions[2][2]);
                else System.out.println(" ");
                System.out.println(repr);
                return false;
            }
            if(repr.contains("4")&&repr.contains("2")){
                System.out.print("FAIL @"+positions[0][0]+positions[0][1]+positions[0][2]+":"+positions[1][0]+positions[1][1]+positions[1][2]);
                if(positions.length==3)
                    System.out.println(":"+positions[2][0]+positions[2][1]+positions[2][2]);
                else System.out.println(" ");

                System.out.println(repr);
                return false;
            }
        }
        return true;
    }

    private void save3DMDToFile() throws IOException {
        if(database.size()==0)
            return;
        File file = new File("resources/3DMDData.data");
        FileWriter writer = null;
        writer = new FileWriter(file);
        for(int position:database.keySet()){
            for(Object arrangement:database.get(position).keySet()){
                String actualArrangement = (String)arrangement;
                writer.write(""+position+";"+arrangement+";"+database.get(position).get(actualArrangement)+"\n");
            }
        }
        writer.close();
    }

    private void load3DMDFromFile() throws IOException {
        File file = new File("resources/3DMDData.data");
        FileReader reader = new FileReader(file);
        BufferedReader bufferreader = new BufferedReader(reader);
        this.initDatabaseStructure();
        String line;
        while((line = bufferreader.readLine()) != null){
            String[] linecontents = line.split(";");
            database.get(Integer.parseInt(linecontents[0])).put(linecontents[1],Integer.parseInt(linecontents[2]));
        }
    }

    private void initDatabaseStructure(){
        //corner cubies (3 colors)
        database.put(0,new HashMap<String,Integer>());
        database.put(1,new HashMap<String,Integer>());
        database.put(2,new HashMap<String,Integer>());
        database.put(3,new HashMap<String,Integer>());
        database.put(4,new HashMap<String,Integer>());
        database.put(5,new HashMap<String,Integer>());
        database.put(6,new HashMap<String,Integer>());
        database.put(7,new HashMap<String,Integer>());
        //regular cubies (2 colors)
        database.put(8,new HashMap<String,Integer>());
        database.put(9,new HashMap<String,Integer>());
        database.put(10,new HashMap<String,Integer>());
        database.put(11,new HashMap<String,Integer>());
        database.put(12,new HashMap<String,Integer>());
        database.put(13,new HashMap<String,Integer>());
        database.put(14,new HashMap<String,Integer>());
        database.put(15,new HashMap<String,Integer>());
        database.put(16,new HashMap<String,Integer>());
        database.put(17,new HashMap<String,Integer>());
        database.put(18,new HashMap<String,Integer>());
        database.put(19,new HashMap<String,Integer>());
    }

    private RubiksCube(byte[] cubieTarget, byte[][] cubiePosition){
        if(cubiePosition.length!=cubieTarget.length){
            throw new RuntimeException("cubies should be same size");
        }
        activeHeuristic = HeuristicType.NO_HEURISTIC;
        this.cubieTarget = cubieTarget;
        this.cubiePosition = cubiePosition;
        this.startingState = deepCopyCube(ORIGINAL_CUBE);
    }
    //</editor-fold>

    public RubiksCube(byte[][][] cube, HeuristicType active) {
        //this.build3DMD();
        if(database.size()==0){
            try {
                this.load3DMDFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.startingState = cube;
        activeHeuristic = active;
    }

    public RubiksCube() {
        if(database.size()==0){
            try {
                this.load3DMDFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[][][] cube = deepCopyCube(ORIGINAL_CUBE);
        this.startingState = cube;
        activeHeuristic = HeuristicType.GAP;
    }

    public RubiksCube(HeuristicType active) {
        if(database.size()==0){
            try {
                this.load3DMDFromFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[][][] cube = deepCopyCube(ORIGINAL_CUBE);
        this.startingState = cube;
        activeHeuristic = active;
    }

    public void setInitialState(State state){
        if(state instanceof RubiksState)
            this.startingState = ((RubiksState) state).cube;
    }

    RubiksOperator getTestOperator(){
        return new RubiksOperator(Operators.BOT_LEFT_0351);
    }

    public enum HeuristicType{
        BASE_RING, //has a bug still h!=0 on goal
        BASE_RING_COMPLEX,
        PARALLEL_LINES,
        PARALLEL_LINES_COMPLEX,
        BASELINE_HEURISTIC, //manhattan distance equivalent
        NO_HEURISTIC,
        COLORS,
        GAP,
        HYBRID,
        BORDER_PASSING,
        TRUE_DISTANCE
    }

    static byte[][][] deepCopyCube(byte[][][] cube){
        byte[][][] result = new byte[cube.length][][];
        for(int i=0;i<cube.length;i++){
            result[i] = new byte[cube[i].length][];
            for(int j=0;j<cube[i].length;j++){
                result[i][j] = new byte[cube[i][j].length];
                for(int k=0;k<cube[i][j].length;k++){
                    result[i][j][k] = cube[i][j][k];
                }
            }
        }
        return result;
    }

    @Override
    public State initialState() {
        return new RubiksState(this.startingState);
    }

    @Override
    public boolean isGoal(State state) {
        if(!(state instanceof RubiksState)){
            if(debugMode)
                log.debug("Goal: False");
            return false;
        }
        //for 3DMD building
        if(this.cubieTarget !=null){
            /*if(!this.validateCube(((RubiksState) state).cube))
                System.out.println(111);*/
            // in coordinates from @cubiePosition should be located colors from @cubieTarget in order @order
            boolean loc1 = ((RubiksState) state).cube[this.cubiePosition[0][0]][this.cubiePosition[0][1]][this.cubiePosition[0][2]]==cubieTarget[0];
            boolean loc2 = ((RubiksState) state).cube[this.cubiePosition[1][0]][this.cubiePosition[1][1]][this.cubiePosition[1][2]]==cubieTarget[1];
            boolean loc3 = true;
            if(this.cubiePosition.length==3)
                loc3 = ((RubiksState) state).cube[this.cubiePosition[2][0]][this.cubiePosition[2][1]][this.cubiePosition[2][2]]==cubieTarget[2];
            return loc1 && loc2 && loc3;
        }
        for(int i=0;i<6;i++){
            for(byte[] line : ((RubiksState) state).getCube()[i]){
                for(byte cell : line){
                    if(cell != i){
                        if(debugMode)
                            log.debug("Goal: False");
                        return false;
                    }
                }
            }
        }
        if(debugMode)
            log.debug("Goal: True");
        return true;
    }

    @Override
    public int getNumOperators(State state) {
        return Operators.values().length;
    }

    @Override
    public Operator getOperator(State state, int index) {
        if(debugMode)
            log.debug("Returning operator: "+Operators.values()[index]);
        return new RubiksOperator(Operators.values()[index]);
    }

    @Override
    public State applyOperator(State state, Operator op) {
        if(!(op instanceof RubiksOperator) || !(state instanceof RubiksState))
            return null;
        return ((RubiksOperator) op).apply(state);
    }

    @Override
    public State copy(State state) {
        return null;
    }

    @Override
    public PackedElement pack(State state) {
        if(!(state instanceof RubiksState))
            return null;
        return (RubiksState)state;
    }

    @Override
    public State unpack(PackedElement packed) {
        if(!(packed instanceof RubiksState))
            return null;
        return (RubiksState)packed;
    }

    @Override
    public String dumpStatesCollection(State[] states) {
        return null;
    }

    @Override
    public boolean isCurrentHeuristicConsistent() {
        return false;
    }

    @Override
    public void setOptimalSolutionCost(double cost) {

    }

    @Override
    public double getOptimalSolutionCost() {
        return 0;
    }

    @Override
    public int maxGeneratedSize() {
        return 0;
    }

    @Override
    public Map<String, Class> getPossibleParameters() {
        return null;
    }

    @Override
    public void setAdditionalParameter(String parameterName, String value) {

    }

    protected class RubiksState extends PackedElement implements State {
        private byte[][][] cube;
        private RubiksState previous;
        private int currentCost = 0;

        public RubiksState(byte[][][] cube) {
            super(1);
            this.cube = cube;
        }

        public RubiksState(byte[][][] cube,RubiksState previous,int currentCost) {
            super(1);
            this.cube = cube;
            this.previous = previous;
            this.currentCost = currentCost;
        }

        @Override
        public State getParent() {
            if(previous!=null)
                return previous;
            return this;
        }

        @Override
        public double getH() {
            if(this.previous==null)
                return 0;// check if necessary
            double res = 0;
            switch (RubiksCube.activeHeuristic){
                case NO_HEURISTIC:
                    return 0;
                case BASE_RING:
                    return getRingHeuristic();
                case BASE_RING_COMPLEX:
                    return getComplexRingHeuristic();
                case PARALLEL_LINES:
                    return getParallelStripeHeuristic();
                case PARALLEL_LINES_COMPLEX:
                    return getComplexParallelStripeHeuristic();
                case BASELINE_HEURISTIC:
                    return getBaselineHeuristic();
                case COLORS:
                    return getColorsHeuristic();
                case GAP:
                    return getGapHeuristic();
                case BORDER_PASSING:
                    return getBorderPassingHeuristics();
                case TRUE_DISTANCE:
                    return estimator.getTrueDistance(this);
                case HYBRID:
                    double gap = getGapHeuristic();
                    double mh = getBaselineHeuristic();
                    return Math.max(mh,gap);
            }
            if(debugMode)
                log.debug("H requested: "+res+" returned");
            return res;
        }

        @Override
        public double getD() {
            if(debugMode)
                log.debug("D requested: "+currentCost+" returned");
            return currentCost;
        }
        private int getColorsHeuristic(){
            int result = 0;
            int[] colorsCounter = {0, 0, 0, 0, 0, 0};
            int movesCounter = 0;
            for (int face = 0; face < this.cube.length; face++){
                for (int i = 0; i < this.cube[face].length; i++) {
                    for (int j = 0; j < this.cube[face][i].length; j++) {
                        colorsCounter[this.cube[face][i][j]] = 1;
                    }
                }
                for (int i = 0; i < colorsCounter.length; i++) {
                    movesCounter += colorsCounter[i];
                }
                result = Math.max(result, movesCounter-1);
            }
            return result;
        }

        private int getGapHeuristic(){
            int gaps = 0;
            for (int face=0; face<cube.length; face++){
                for (int i=0; i<cube[face].length; i++){
                    for (int j=0; j<cube[face][i].length-1; j++){
                        if (cube[face][i][j] != cube[face][i][j+1]){
                            gaps += 1;
                            if (this.isOpposite(cube[face][i][j],cube[face][i][j+1])){
                                gaps+=1;
                            }
                        }
                    }
                }
                for (int i = 0; i < cube[face].length-1; i++) {
                    for (int j = 0; j < cube[face][i].length; j++) {
                        if(cube[face][i][j] != cube[face][i+1][j]){
                            gaps += 1;
                            if(this.isOpposite(cube[face][i][j],cube[face][i+1][j])){
                                gaps += 1;
                            }
                        }
                    }
                }
            }
            return gaps/12;
        }
        private boolean isOpposite(byte one,byte two){
            if(one==0&&two==5||two==0&&one==5){
                return true;
            }
            if(one==1&&two==3||two==1&&one==3){
                return true;
            }
            if(one==1&&two==4||two==1&&one==4){
                return true;
            }
            return false;
        }
        @Override
        public String convertToString() {
            return "not implemented";
        }

        @Override
        public String convertToStringShort() {
            return "not implemented";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i=0;i<this.cube.length;i++){
                for (int j=0;j<this.cube[i].length;j++){
                    for(int k=0;k<this.cube[i][j].length;k++){
                        sb.append(this.cube[i][j][k]);
                    }
                }
            }
            return sb.toString();
        }

        public byte[][][] getCube() {
            return cube;
        }

        private double getRingHeuristic() {
            int result = 9;
            //operator 2345 TOP
            if(cube[1][0][0]==cube[1][0][1]&&cube[1][0][1]==cube[1][0][2]){
                if(cube[2][0][0]==cube[2][0][1]&&cube[2][0][1]==cube[2][0][2]){
                    if(cube[3][0][0]==cube[3][0][1]&&cube[3][0][1]==cube[3][0][2]){
                        if(cube[4][0][0]==cube[4][0][1]&&cube[4][0][1]==cube[4][0][2]){
                            result--;
                        }
                    }
                }
            }
            //operator 2345 MID
            if(cube[1][1][0]==cube[1][1][1]&&cube[1][1][1]==cube[1][1][2]){
                if(cube[2][1][0]==cube[2][1][1]&&cube[2][1][1]==cube[2][1][2]){
                    if(cube[3][1][0]==cube[3][1][1]&&cube[3][1][1]==cube[3][1][2]){
                        if(cube[4][1][0]==cube[4][1][1]&&cube[4][1][1]==cube[4][1][2]){
                            result--;
                        }
                    }
                }
            }
            //operator 2345 BOT
            if(cube[1][2][0]==cube[1][2][1]&&cube[1][2][1]==cube[1][2][2]){
                if(cube[2][2][0]==cube[2][2][1]&&cube[2][2][1]==cube[2][2][2]){
                    if(cube[3][2][0]==cube[3][2][1]&&cube[3][2][1]==cube[3][2][2]){
                        if(cube[4][2][0]==cube[4][2][1]&&cube[4][2][1]==cube[4][2][2]){
                            result--;
                        }
                    }
                }
            }
            //operator 1462 TOP
            if(cube[0][0][0]==cube[0][0][1]&&cube[0][0][1]==cube[0][0][2]){
                if(cube[3][0][2]==cube[3][1][2]&&cube[3][1][2]==cube[3][2][2]){
                    if(cube[5][2][0]==cube[5][2][1]&&cube[5][2][1]==cube[5][2][2]){
                        if(cube[1][0][0]==cube[1][1][0]&&cube[1][1][0]==cube[1][2][0]){
                            result--;
                        }
                    }
                }
            }
            //operator 1462 MID
            if(cube[0][1][0]==cube[0][1][1]&&cube[0][1][1]==cube[0][1][2]){
                if(cube[3][0][1]==cube[3][1][1]&&cube[3][1][1]==cube[3][2][1]){
                    if(cube[5][1][0]==cube[5][1][1]&&cube[5][1][1]==cube[5][1][2]){
                        if(cube[1][0][1]==cube[1][1][1]&&cube[1][1][1]==cube[1][2][1]){
                            result--;
                        }
                    }
                }
            }
            //operator 1462 BOT
            if(cube[0][2][0]==cube[0][2][1]&&cube[0][2][1]==cube[0][2][2]){
                if(cube[3][0][0]==cube[3][1][0]&&cube[3][1][0]==cube[3][2][0]){
                    if(cube[5][0][0]==cube[5][0][1]&&cube[5][0][1]==cube[5][0][2]){
                        if(cube[1][0][2]==cube[1][1][2]&&cube[1][1][2]==cube[1][2][2]){
                            result--;
                        }
                    }
                }
            }
            //operator 3651 TOP
            if(cube[0][0][0]==cube[0][1][0]&&cube[0][1][0]==cube[0][2][0]){
                if(cube[2][0][0]==cube[3][1][0]&&cube[3][1][0]==cube[3][2][0]){
                    if(cube[5][0][0]==cube[5][1][0]&&cube[5][1][0]==cube[5][2][0]){
                        if(cube[4][2][0]==cube[4][2][1]&&cube[4][0][1]==cube[4][2][2]){
                            result--;
                        }
                    }
                }
            }
            //operator 3651 MID
            if(cube[0][0][1]==cube[0][1][1]&&cube[0][1][1]==cube[0][2][1]){
                if(cube[2][0][1]==cube[3][1][1]&&cube[3][1][1]==cube[3][2][1]){
                    if(cube[5][0][1]==cube[5][1][1]&&cube[5][1][1]==cube[5][2][1]){
                        if(cube[4][0][1]==cube[4][1][1]&&cube[4][1][1]==cube[4][2][1]){
                            result--;
                        }
                    }
                }
            }
            //operator 3651 BOT
            if(cube[0][2][0]==cube[0][2][1]&&cube[0][2][1]==cube[0][2][2]){
                if(cube[2][2][0]==cube[3][2][1]&&cube[3][2][1]==cube[3][2][2]){
                    if(cube[5][2][0]==cube[5][2][1]&&cube[5][2][1]==cube[5][2][2]){
                        if(cube[4][0][0]==cube[4][1][0]&&cube[4][1][0]==cube[4][2][0]){
                            result--;
                        }
                    }
                }
            }

            return result;
        }

        private double getComplexRingHeuristic(){
            //TODO implement this
            return 0;
        }

        private double getParallelStripeHeuristic() {
            int pair05 = getHorizontalStripes(this.cube[0], this.cube[5]) + getVerticalStripes(this.cube[0], this.cube[5]);
            int pair13 = getHorizontalStripes(this.cube[1], this.cube[3]) + getVerticalStripes(this.cube[1], this.cube[3]);
            int pair24 = getHorizontalStripes(this.cube[2], this.cube[4]) + getVerticalStripes(this.cube[2], this.cube[4]);
            return (double)(pair05 + pair13 + pair24)/2;
        }

        private double getComplexParallelStripeHeuristic() {
            //TODO implement this
            return 0;
        }

        private double getBaselineHeuristic(){
            double result = 0;
            for(int i=0;i<20;i++){
                byte[][] currentPosition = RubiksCube.CUBIES_LIST[i];
                String coordinateName = "";
                for(byte[] singleCoordinate:currentPosition){
                    coordinateName = coordinateName+this.cube[singleCoordinate[0]][singleCoordinate[1]][singleCoordinate[2]];

                }
                Integer res = 0;
                try {
                    res = (int)RubiksCube.database.get(i).get(coordinateName);
                }
                catch (Exception e){
                    System.out.println(e.getMessage());
                }
                result = result + res;

            }
            return result/8.0;
        }

        private double getBorderPassingHeuristics(){
            double result=0;
            for(int i=0;i<this.cube.length;i++){
                for (int j=0;j<this.cube[i].length;j++){
                    for(int k=0;k<this.cube[i][j].length;k++){
                        if(this.cube[i][j][k]==ANTOGONIST_SIDES[i])
                            result+=2;
                        else if(this.cube[i][j][k]==i)
                            result+=0;
                        else
                            result+=1;
                    }
                }
            }
            return result/12.0;
        }

        private int getHorizontalStripes(byte[][] sideA, byte[][] sideB) {
            int result = 0;
            for (int i = 0; i < 3; i++) {
                boolean aTheSame = sideA[i][0] == sideA[i][1] && sideA[i][1] == sideA[i][2];
                boolean bTheSame = sideB[i][0] == sideB[i][1] && sideB[i][1] == sideB[i][2];
                if (!aTheSame || !bTheSame)
                    result++;
            }
            return result;
        }

        private int getVerticalStripes(byte[][] sideA, byte[][] sideB) {
            int result = 0;
            boolean aTheSame = sideA[0][0] == sideA[1][0] && sideA[1][0] == sideA[2][0];
            boolean bTheSame = sideB[0][0] == sideB[1][0] && sideB[1][0] == sideB[2][0];
            if (!aTheSame || !bTheSame)
                result++;
            aTheSame = sideA[0][1] == sideA[1][1] && sideA[1][1] == sideA[2][1];
            bTheSame = sideB[0][1] == sideB[1][1] && sideB[1][1] == sideB[2][1];
            if (!aTheSame || !bTheSame)
                result++;
            aTheSame = sideA[0][2] == sideA[1][2] && sideA[1][2] == sideA[2][2];
            bTheSame = sideB[0][2] == sideB[1][2] && sideB[1][2] == sideB[2][2];
            if (!aTheSame || !bTheSame)
                result++;
            return result;
        }
    }

    protected class RubiksOperator implements Operator{
        Operators operator;

        public RubiksOperator(Operators operator) {
            this.operator = operator;
        }

        @Override
        public double getCost(State state, State parent) {
            return 1;
        }

        @Override
        public Operator reverse(State state) {
            int value = this.operator.ordinal();
            if(value%2==0){
                return new RubiksOperator(Operators.values()[value+1]);
            }
            else{
                return new RubiksOperator(Operators.values()[value-1]);
            }
        }

        public RubiksState apply(State state){
            if(debugMode)
                log.debug("Applying Operator: "+this.operator);
            if(!(state instanceof RubiksState))
                return null;
            RubiksState prev = (RubiksState) state;
            byte[][][] resultCube = RubiksCube.deepCopyCube(prev.cube);
            switch (this.operator){
                case TOP_LEFT_0351:
                    resultCube = this.applyTL0351(resultCube);
                    break;
                case TOP_RIGHT_0351:
                    resultCube = this.applyTR0351(resultCube);
                    break;
                case BOT_LEFT_0351:
                    resultCube = this.applyBL0351(resultCube);
                    break;
                case BOT_RIGHT_0351:
                    resultCube = this.applyBR0351(resultCube);
                    break;
                case TOP_LEFT_1234:
                    resultCube = this.applyTL1234(resultCube);
                    break;
                case TOP_RIGHT_1234:
                    resultCube = this.applyTR1234(resultCube);
                    break;
                case BOT_LEFT_1234:
                    resultCube = this.applyBL1234(resultCube);
                    break;
                case BOT_RIGHT_1234:
                    resultCube = this.applyBR1234(resultCube);
                    break;
                case TOP_LEFT_2540:
                    resultCube = this.applyTL2540(resultCube);
                    break;
                case TOP_RIGHT_2540:
                    resultCube = this.applyTR2540(resultCube);
                    break;
                case BOT_LEFT_2540:
                    resultCube = this.applyBL2540(resultCube);
                    break;
                case BOT_RIGHT_2540:
                    resultCube = this.applyBR2540(resultCube);
                    break;
                default:
                    break;}
            return new RubiksState(resultCube,prev,prev.currentCost+1);
        }

        byte[][][] applyTR1234(byte[][][] cube){
            byte[] savedValues = {cube[1][0][0],cube[1][1][0],cube[1][2][0]};

            cube[1][0][0] = cube[4][0][0];
            cube[1][1][0] = cube[4][1][0];
            cube[1][2][0] = cube[4][2][0];

            cube[4][0][0] = cube[3][0][0];
            cube[4][1][0] = cube[3][1][0];
            cube[4][2][0] = cube[3][2][0];

            cube[3][0][0] = cube[2][0][0];
            cube[3][1][0] = cube[2][1][0];
            cube[3][2][0] = cube[2][2][0];

            cube[2][0][0] = savedValues[0];
            cube[2][1][0] = savedValues[1];
            cube[2][2][0] = savedValues[2];

            cube[0] = rotateCounterClock(cube[0]);
            return cube;
        }
        byte[][][] applyTL1234(byte[][][] cube){
            byte[] savedValues = {cube[1][0][0],cube[1][1][0],cube[1][2][0]};

            cube[1][0][0] = cube[2][0][0];
            cube[1][1][0] = cube[2][1][0];
            cube[1][2][0] = cube[2][2][0];

            cube[2][0][0] = cube[3][0][0];
            cube[2][1][0] = cube[3][1][0];
            cube[2][2][0] = cube[3][2][0];

            cube[3][0][0] = cube[4][0][0];
            cube[3][1][0] = cube[4][1][0];
            cube[3][2][0] = cube[4][2][0];

            cube[4][0][0] = savedValues[0];
            cube[4][1][0] = savedValues[1];
            cube[4][2][0] = savedValues[2];

            cube[0] = rotateClock(cube[0]);
            return cube;
        }
        byte[][][] applyBR1234(byte[][][] cube){
            byte[] savedValues = {cube[1][0][2],cube[1][1][2],cube[1][2][2]};

            cube[1][0][2] = cube[4][0][2];
            cube[1][1][2] = cube[4][1][2];
            cube[1][2][2] = cube[4][2][2];

            cube[4][0][2] = cube[3][0][2];
            cube[4][1][2] = cube[3][1][2];
            cube[4][2][2] = cube[3][2][2];

            cube[3][0][2] = cube[2][0][2];
            cube[3][1][2] = cube[2][1][2];
            cube[3][2][2] = cube[2][2][2];

            cube[2][0][2] = savedValues[0];
            cube[2][1][2] = savedValues[1];
            cube[2][2][2] = savedValues[2];

            cube[5] = rotateClock(cube[5]);

            return cube;
        }
        byte[][][] applyBL1234(byte[][][] cube){
            byte[] savedValues = {cube[1][0][2],cube[1][1][2],cube[1][2][2]};

            cube[1][0][2] = cube[2][0][2];
            cube[1][1][2] = cube[2][1][2];
            cube[1][2][2] = cube[2][2][2];

            cube[2][0][2] = cube[3][0][2];
            cube[2][1][2] = cube[3][1][2];
            cube[2][2][2] = cube[3][2][2];

            cube[3][0][2] = cube[4][0][2];
            cube[3][1][2] = cube[4][1][2];
            cube[3][2][2] = cube[4][2][2];

            cube[4][0][2] = savedValues[0];
            cube[4][1][2] = savedValues[1];
            cube[4][2][2] = savedValues[2];

            cube[5] = rotateCounterClock(cube[5]);

            return cube;
        }
        byte[][][] applyTR0351(byte[][][] cube){
            byte[] savedValues = {cube[0][0][2],cube[0][1][2],cube[0][2][2]};

            cube[0][0][2] = cube[1][2][2];
            cube[0][1][2] = cube[1][2][1];
            cube[0][2][2] = cube[1][2][0];

            cube[1][2][0] = cube[5][0][0];
            cube[1][2][1] = cube[5][1][0];
            cube[1][2][2] = cube[5][2][0];

            cube[5][0][0] = cube[3][0][2];
            cube[5][1][0] = cube[3][0][1];
            cube[5][2][0] = cube[3][0][0];

            cube[3][0][0] = savedValues[0];
            cube[3][0][1] = savedValues[1];
            cube[3][0][2] = savedValues[2];

            cube[2] = rotateClock(cube[2]);
            return cube;
        }
        byte[][][] applyTL0351(byte[][][] cube){
            byte[] savedValues = {cube[0][0][2],cube[0][1][2],cube[0][2][2]};

            cube[0][0][2] = cube[3][0][0];
            cube[0][1][2] = cube[3][0][1];
            cube[0][2][2] = cube[3][0][2];

            cube[3][0][0] = cube[5][2][0];
            cube[3][0][1] = cube[5][1][0];
            cube[3][0][2] = cube[5][0][0];

            cube[5][0][0] = cube[1][2][0];
            cube[5][1][0] = cube[1][2][1];
            cube[5][2][0] = cube[1][2][2];

            cube[1][2][0] = savedValues[2];
            cube[1][2][1] = savedValues[1];
            cube[1][2][2] = savedValues[0];

            cube[2] = rotateCounterClock(cube[2]);
            return cube;
        }
        byte[][][] applyBR0351(byte[][][] cube){
            byte[] savedValues = {cube[0][0][0],cube[0][1][0],cube[0][2][0]};

            cube[0][0][0] = cube[1][0][2];
            cube[0][1][0] = cube[1][0][1];
            cube[0][2][0] = cube[1][0][0];

            cube[1][0][0] = cube[5][0][2];
            cube[1][0][1] = cube[5][1][2];
            cube[1][0][2] = cube[5][2][2];

            cube[5][0][2] = cube[3][2][2];
            cube[5][1][2] = cube[3][2][1];
            cube[5][2][2] = cube[3][2][0];

            cube[3][2][0] = savedValues[0];
            cube[3][2][1] = savedValues[1];
            cube[3][2][2] = savedValues[2];

            cube[4] = rotateCounterClock(cube[4]);
            return cube;
        }
        byte[][][] applyBL0351(byte[][][] cube){
            byte[] savedValues = {cube[0][0][0],cube[0][1][0],cube[0][2][0]};

            cube[0][0][0] = cube[3][2][0];
            cube[0][1][0] = cube[3][2][1];
            cube[0][2][0] = cube[3][2][2];

            cube[3][2][0] = cube[5][2][2];
            cube[3][2][1] = cube[5][1][2];
            cube[3][2][2] = cube[5][0][2];

            cube[5][0][2] = cube[1][0][0];
            cube[5][1][2] = cube[1][0][1];
            cube[5][2][2] = cube[1][0][2];

            cube[1][0][0] = savedValues[2];
            cube[1][0][1] = savedValues[1];
            cube[1][0][2] = savedValues[0];

            cube[4] = rotateClock(cube[4]);
            return cube;
        }
        byte[][][] applyTR2540(byte[][][] cube){
            byte[] savedValues = {cube[0][0][0],cube[0][0][1],cube[0][0][2]};

            cube[0][0][0] = cube[2][0][0];
            cube[0][0][1] = cube[2][0][1];
            cube[0][0][2] = cube[2][0][2];

            cube[2][0][0] = cube[5][0][0];
            cube[2][0][1] = cube[5][0][1];
            cube[2][0][2] = cube[5][0][2];

            cube[5][0][0] = cube[4][2][2];
            cube[5][0][1] = cube[4][2][1];
            cube[5][0][2] = cube[4][2][0];

            cube[4][2][0] = savedValues[2];
            cube[4][2][1] = savedValues[1];
            cube[4][2][2] = savedValues[0];

            cube[1] = rotateCounterClock(cube[1]);
            return cube;
        }
        byte[][][] applyTL2540(byte[][][] cube){
            byte[] savedValues = {cube[0][0][0],cube[0][0][1],cube[0][0][2]};

            cube[0][0][0] = cube[4][2][2];
            cube[0][0][1] = cube[4][2][1];
            cube[0][0][2] = cube[4][2][0];

            cube[4][2][0] = cube[5][0][2];
            cube[4][2][1] = cube[5][0][1];
            cube[4][2][2] = cube[5][0][0];

            cube[5][0][0] = cube[2][0][0];
            cube[5][0][1] = cube[2][0][1];
            cube[5][0][2] = cube[2][0][2];

            cube[2][0][0] = savedValues[0];
            cube[2][0][1] = savedValues[1];
            cube[2][0][2] = savedValues[2];

            cube[1] = rotateClock(cube[1]);
            return cube;
        }
        byte[][][] applyBR2540(byte[][][] cube){
            byte[] savedValues = {cube[0][2][0],cube[0][2][1],cube[0][2][2]};

            cube[0][2][0] = cube[2][2][0];
            cube[0][2][1] = cube[2][2][1];
            cube[0][2][2] = cube[2][2][2];

            cube[2][2][0] = cube[5][2][0];
            cube[2][2][1] = cube[5][2][1];
            cube[2][2][2] = cube[5][2][2];

            cube[5][2][0] = cube[4][0][2];
            cube[5][2][1] = cube[4][0][1];
            cube[5][2][2] = cube[4][0][0];

            cube[4][0][0] = savedValues[2];
            cube[4][0][1] = savedValues[1];
            cube[4][0][2] = savedValues[0];

            cube[3] = rotateClock(cube[3]);
            return cube;
        }
        byte[][][] applyBL2540(byte[][][] cube){
            byte[] savedValues = {cube[0][2][0],cube[0][2][1],cube[0][2][2]};

            cube[0][2][0] = cube[4][0][2];
            cube[0][2][1] = cube[4][0][1];
            cube[0][2][2] = cube[4][0][0];

            cube[4][0][0] = cube[5][2][2];
            cube[4][0][1] = cube[5][2][1];
            cube[4][0][2] = cube[5][2][0];

            cube[5][2][0] = cube[2][2][0];
            cube[5][2][1] = cube[2][2][1];
            cube[5][2][2] = cube[2][2][2];

            cube[2][2][0] = savedValues[0];
            cube[2][2][1] = savedValues[1];
            cube[2][2][2] = savedValues[2];

            cube[3] = rotateCounterClock(cube[3]);
            return cube;
        }
        private byte[][] getNewSide(){
            byte[][] result = new byte[3][];
            result[0] = new byte[3];
            result[1] = new byte[3];
            result[2] = new byte[3];
            return result;
        }
        public byte[][] rotateCounterClock(byte[][] side){
            byte[][] result = this.getNewSide();
            result[0][0] = side[2][0];
            result[0][1] = side[1][0];
            result[0][2] = side[0][0];
            result[1][0] = side[2][1];
            result[1][1] = side[1][1];
            result[1][2] = side[0][1];
            result[2][0] = side[2][2];
            result[2][1] = side[1][2];
            result[2][2] = side[0][2];
            return result;
        }
        public byte[][] rotateClock(byte[][] side){
            byte[][] result = this.getNewSide();
            result[0][0] = side[0][2];
            result[0][1] = side[1][2];
            result[0][2] = side[2][2];
            result[1][0] = side[0][1];
            result[1][1] = side[1][1];
            result[1][2] = side[2][1];
            result[2][0] = side[0][0];
            result[2][1] = side[1][0];
            result[2][2] = side[2][0];
            return result;
        }
    }
}
