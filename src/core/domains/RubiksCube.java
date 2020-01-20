package core.domains;

import core.Operator;
import core.SearchDomain;
import core.SearchResult;
import core.State;
import core.algorithms.IDAstar;
import core.collections.PackedElement;

import java.lang.reflect.Array;
import java.util.*;

import org.apache.log4j.Logger;
import sun.security.util.ArrayUtil;

/**
 *
 */

public class RubiksCube implements SearchDomain {

    public static HeuristicType activeHeuristic;
    public byte[][][] startingState;
    public static Logger log = Logger.getLogger(RubiksCube.class.getName());
    private final byte[][][] ORIGINAL_CUBE = {{{0,0,0},{0,0,0},{0,0,0}},{{1,1,1},{1,1,1},{1,1,1}},{{2,2,2},{2,2,2},{2,2,2}},
            {{3,3,3},{3,3,3},{3,3,3}},{{4,4,4},{4,4,4},{4,4,4}},{{5,5,5},{5,5,5},{5,5,5}}};
    static final HashMap<String,HashMap> database = new HashMap<>();
    static final HashMap<String,byte[][]> cubiesMap = new HashMap<>();
    private byte[][] cubieTarget = null;
    private byte[][] cubiePosition = null;
    byte[] order = null;
    public static final byte[][][] CUBIES_LIST = {
        {{0,0,0},{1,0,0},{4,0,2}},
        {{0,0,1},{4,0,1}},
        {{0,0,2},{3,0,2},{4,0,0}},
        {{0,1,0},{1,0,1}},
        {{3,0,1},{0,1,2}},
        {{0,2,2},{2,0,2},{3,0,0}},
        {{0,2,1},{2,0,1}},
        {{2,0,0},{0,2,0},{1,0,2}},
        {{1,2,2},{2,2,0},{5,0,0}},
        {{2,2,1},{5,0,1}},
        {{3,2,0},{2,2,2},{5,0,2}},
        {{3,2,1},{5,1,2}},
        {{1,2,1},{5,1,0}},
        {{4,2,2},{1,2,0},{5,2,0}},
        {{4,2,1},{5,2,1}},
        {{3,2,2},{4,2,0},{5,2,2}},
        {{4,1,2},{1,1,0}},
        {{3,1,2},{4,1,0}},
        {{2,1,2},{3,1,0}},
        {{1,1,2},{2,1,0}}};

    public RubiksCube(byte[][][] cube, HeuristicType active) {
        /*if(cubiesMap.isEmpty()) TODO: uncomment this when MD works
            initCubiesList();
        if(database.isEmpty())
            this.init3DMD();*/
        this.startingState = cube;
        activeHeuristic = active;
    }

    private RubiksCube(byte[][] cubieTarget, byte[][] cubiePosition, byte order){
        if(cubiePosition.length!=cubieTarget.length){
            throw new RuntimeException("cubies should be same size");
        }
        activeHeuristic = HeuristicType.NO_HEURISTIC;
        this.cubieTarget = cubieTarget;
        this.cubiePosition = cubiePosition;
        this.startingState = deepCopyCube(ORIGINAL_CUBE);
        if(cubiePosition.length==3){
            if(order<0 || order>5)
                throw new RuntimeException("order for 2 place cubies should be between 0 and 5");
            //covering all permutations for corners
            switch (order){
                case 0:
                    this.order = new byte[]{2, 1, 0};
                    break;
                case 1:
                    this.order = new byte[]{2, 0, 1};
                    break;
                case 2:
                    this.order = new byte[]{1, 2, 0};
                    break;
                case 3:
                    this.order = new byte[]{1, 0, 2};
                    break;
                case 4:
                    this.order = new byte[]{0, 1, 2};
                    break;
                case 5:
                    this.order = new byte[]{0, 2, 1};
                    break;
            }
        }
        else{
            if(order!=0 && order!=1)
                throw new RuntimeException("order for 2 place cubies should be either 0 or 1");
            switch (order){
                case 0:
                    this.order = new byte[]{1, 2};
                    break;
                case 1:
                    this.order = new byte[]{2, 1};
                    break;
            }
        }
    }

    public RubiksCube(HeuristicType active) {
        /*if(cubiesMap.isEmpty())TODO: uncomment this when MD works
            initCubiesList();
        if(database.isEmpty())
            this.init3DMD();*/
        byte[][][] cube = deepCopyCube(ORIGINAL_CUBE);
        this.startingState = cube;
        activeHeuristic = active;
    }

    public void init3DMD(){
        byte[][][] stateList = RubiksCube.CUBIES_LIST;
        IDAstar algo = new IDAstar();
        for(byte[][] innerState: stateList){
            HashMap<String, List> destinationList = new HashMap<>();// destination -> permutation
            for(byte[][] state: stateList){
                ArrayList<Integer> permutations = new ArrayList<>();
                if (state.length!=innerState.length||innerState.equals(state))
                    continue;
                int maxIndex=6;
                if(innerState.length==2)
                    maxIndex=2;
                for(byte i=0;i<maxIndex;i++){
                    RubiksCube domain = new RubiksCube(state,innerState,i);
                    SearchResult res = algo.search(domain);
                    permutations.add(res.getSolutions().get(0).getLength());
                }
                destinationList.put(getCubieName(state),permutations);
            }
            database.put(getCubieName(innerState),destinationList);
        }
    }

    private void initCubiesList(){
        cubiesMap.put("410",new byte[][]{{0,0,0},{1,0,0},{4,0,2}});
        cubiesMap.put("430",new byte[][]{{0,0,2},{3,0,2},{4,0,0}});
        cubiesMap.put("320",new byte[][]{{0,2,2},{2,0,2},{3,0,0}});
        cubiesMap.put("210",new byte[][]{{2,0,0},{0,2,0},{1,0,2}});
        cubiesMap.put("521",new byte[][]{{1,2,2},{2,2,0},{5,0,0}});
        cubiesMap.put("532",new byte[][]{{3,2,0},{2,2,2},{5,0,2}});
        cubiesMap.put("541",new byte[][]{{4,2,2},{1,2,0},{5,2,0}});
        cubiesMap.put("543",new byte[][]{{3,2,2},{4,2,0},{5,2,2}});

        cubiesMap.put("40",new byte[][]{{0,0,1},{4,0,1}});
        cubiesMap.put("10",new byte[][]{{0,1,0},{1,0,1}});
        cubiesMap.put("30",new byte[][]{{3,0,1},{0,1,2}});
        cubiesMap.put("20",new byte[][]{{0,2,1},{2,0,1}});
        cubiesMap.put("52",new byte[][]{{2,2,1},{5,0,1}});
        cubiesMap.put("53",new byte[][]{{3,2,1},{5,1,2}});
        cubiesMap.put("51",new byte[][]{{1,2,1},{5,1,0}});
        cubiesMap.put("54",new byte[][]{{4,2,1},{5,2,1}});
        cubiesMap.put("41",new byte[][]{{4,1,2},{1,1,0}});
        cubiesMap.put("43",new byte[][]{{3,1,2},{4,1,0}});
        cubiesMap.put("32",new byte[][]{{2,1,2},{3,1,0}});
        cubiesMap.put("21",new byte[][]{{1,1,2},{2,1,0}});
    }

    public void setInitialState(State state){
        if(state instanceof RubiksState)
            this.startingState = ((RubiksState) state).cube;
    }

    RubiksOperator getTestOperator(){
        return new RubiksOperator(Operators.BOT_LEFT_1462);
    }

    enum Operators{
        TOP_RIGHT_2345,
        TOP_LEFT_2345,
        MID_RIGHT_2345,
        MID_LEFT_2345,
        BOT_RIGHT_2345,
        BOT_LEFT_2345,
        TOP_RIGHT_1462,
        TOP_LEFT_1462,
        MID_RIGHT_1462,
        MID_LEFT_1462,
        BOT_RIGHT_1462,
        BOT_LEFT_1462,
        TOP_RIGHT_3651,
        TOP_LEFT_3651,
        MID_RIGHT_3651,
        MID_LEFT_3651,
        BOT_RIGHT_3651,
        BOT_LEFT_3651,
    }

    public enum HeuristicType{
        BASE_RING, //has a bug still h!=0 on goal
        BASE_RING_COMPLEX,
        PARALLEL_LINES,
        PARALLEL_LINES_COMPLEX,
        BASELINE_HERISTIC, //manhattan distance equivalent
        NO_HEURISTIC
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
            // in coordinates from @cubiePosition should be located colors from @cubieTarget in order @order
            byte[] colors = new byte[this.cubiePosition.length];
            for(int i=0;i<this.cubieTarget.length;i++){
                colors[i] = cubieTarget[i][0];
            }
            boolean loc1 = ((RubiksState) state).cube[this.cubiePosition[0][0]][this.cubiePosition[0][1]][this.cubiePosition[0][2]]==colors[0];
            boolean loc2 = ((RubiksState) state).cube[this.cubiePosition[1][0]][this.cubiePosition[1][1]][this.cubiePosition[1][2]]==colors[1];
            boolean loc3 = true;
            if(this.cubiePosition.length==3)
                loc3 = ((RubiksState) state).cube[this.cubiePosition[2][0]][this.cubiePosition[2][1]][this.cubiePosition[2][2]]==colors[2];
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

    private String getCubieName(byte[][] cubie){
        ArrayList<Byte> res = new ArrayList<>();
        for(byte[] coordinate: cubie){
            res.add(coordinate[0]);
        }
        res.sort(new Comparator<Byte>() {
            @Override
            public int compare(Byte o1, Byte o2) {
                return  o2-o1;
            }
        });
        if(res.size()==2)
            return ""+res.get(0)+res.get(1);
        return ""+res.get(0)+res.get(1)+res.get(2);
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
                case BASELINE_HERISTIC:
                    return getBaselineHeuristic();
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

        @Override
        public String convertToString() {
            return "not implemented";
        }

        @Override
        public String convertToStringShort() {
            return "not implemented";
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
            return pair05 + pair13 + pair24;
        }

        private double getComplexParallelStripeHeuristic() {
            //TODO implement this
            return 0;
        }

        private double getBaselineHeuristic(){
            for(byte[][] cubie:CUBIES_LIST){

            }
            return 0;
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
            return 6;
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
                case TOP_LEFT_1462:
                    resultCube = this.applyTL1462(resultCube);
                    break;
                case TOP_RIGHT_1462:
                    resultCube = this.applyTR1462(resultCube);
                    break;
                case MID_LEFT_1462:
                    resultCube = this.applyML1462(resultCube);
                    break;
                case MID_RIGHT_1462:
                    resultCube = this.applyMR1462(resultCube);
                    break;
                case BOT_LEFT_1462:
                    resultCube = this.applyBL1462(resultCube);
                    break;
                case BOT_RIGHT_1462:
                    resultCube = this.applyBR1462(resultCube);
                    break;
                case TOP_LEFT_2345:
                    resultCube = this.applyTL2345(resultCube);
                    break;
                case TOP_RIGHT_2345:
                    resultCube = this.applyTR2345(resultCube);
                    break;
                case MID_LEFT_2345:
                    resultCube = this.applyML2345(resultCube);
                    break;
                case MID_RIGHT_2345:
                    resultCube = this.applyMR2345(resultCube);
                    break;
                case BOT_LEFT_2345:
                    resultCube = this.applyBL2345(resultCube);
                    break;
                case BOT_RIGHT_2345:
                    resultCube = this.applyBR2345(resultCube);
                    break;
                case TOP_LEFT_3651:
                    resultCube = this.applyTL3651(resultCube);
                    break;
                case TOP_RIGHT_3651:
                    resultCube = this.applyTR3651(resultCube);
                    break;
                case MID_LEFT_3651:
                    resultCube = this.applyML3651(resultCube);
                    break;
                case MID_RIGHT_3651:
                    resultCube = this.applyMR3651(resultCube);
                    break;
                case BOT_LEFT_3651:
                    resultCube = this.applyBL3651(resultCube);
                    break;
                case BOT_RIGHT_3651:
                    resultCube = this.applyBR3651(resultCube);
                    break;
                default:
                    break;}
            return new RubiksState(resultCube,prev,prev.currentCost+1);
        }


        byte[][][] applyTR2345(byte[][][] cube){
            byte[][] savedValues = {cube[1][0],cube[2][0],cube[3][0],cube[4][0]};
            cube[2][0] = savedValues[0];
            cube[3][0] = savedValues[1];
            cube[4][0] = savedValues[2];
            cube[1][0] = savedValues[3];
            cube[0] = rotateLeft(cube[0]);
            return cube;
        }
        byte[][][] applyTL2345(byte[][][] cube){
            byte[][] savedValues = {cube[1][0],cube[2][0],cube[3][0],cube[4][0]};
            cube[2][0] = savedValues[2];
            cube[3][0] = savedValues[3];
            cube[4][0] = savedValues[0];
            cube[1][0] = savedValues[1];
            cube[0] = rotateRight(cube[0]);
            return cube;
        }
        byte[][][] applyMR2345(byte[][][] cube){
            byte[][] savedValues = {cube[1][1],cube[2][1],cube[3][1],cube[4][1]};
            cube[2][1] = savedValues[0];
            cube[3][1] = savedValues[1];
            cube[4][1] = savedValues[2];
            cube[1][1] = savedValues[3];
            return cube;
        }
        byte[][][] applyML2345(byte[][][] cube){
            byte[][] savedValues = {cube[1][1],cube[2][1],cube[3][1],cube[4][1]};
            cube[2][1] = savedValues[2];
            cube[3][1] = savedValues[3];
            cube[4][1] = savedValues[0];
            cube[1][1] = savedValues[1];
            return cube;
        }
        byte[][][] applyBR2345(byte[][][] cube){
            byte[][] savedValues = {cube[1][2],cube[2][2],cube[3][2],cube[4][2]};
            cube[2][2] = savedValues[0];
            cube[3][2] = savedValues[1];
            cube[4][2] = savedValues[2];
            cube[1][2] = savedValues[3];
            cube[5] = rotateLeft(cube[5]);
            return cube;
        }
        byte[][][] applyBL2345(byte[][][] cube){
            byte[][] savedValues = {cube[1][2],cube[2][2],cube[3][2],cube[4][2]};
            cube[2][2] = savedValues[2];
            cube[3][2] = savedValues[3];
            cube[4][2] = savedValues[0];
            cube[1][2] = savedValues[1];
            cube[5] = rotateRight(cube[5]);
            return cube;
        }
        byte[][][] applyTR1462(byte[][][] cube){
            byte[][] savedValues = {{cube[3][0][2],cube[3][1][2],cube[3][2][2]},
                    cube[5][2],
                    {cube[1][0][0],cube[1][1][0],cube[1][2][0]},
                    cube[0][0]};
            //[0] 0,1,2 -> [3] 2,5,8
            cube[3][0][2] = savedValues[3][0];
            cube[3][1][2] = savedValues[3][1];
            cube[3][2][2] = savedValues[3][2];
            //[3] 2,5,8 -> [5] 6,7,8
            cube[5][2] = savedValues[0];
            //[5] 6,7,8 -> [1] 0,3,6
            cube[1][0][0] = savedValues[1][0];
            cube[1][1][0] = savedValues[1][1];
            cube[1][2][0] = savedValues[1][2];
            //[1] 0,3,6 -> [0] 0,1,2
            cube[0][0] = savedValues[2];

            cube[4] = rotateLeft(cube[4]);
            return cube;
        }
        byte[][][] applyTL1462(byte[][][] cube){
            byte[][] savedValues = {{cube[3][0][2],cube[3][1][2],cube[3][2][2]},
                    cube[5][2],
                    {cube[1][0][0],cube[1][1][0],cube[1][2][0]},
                    cube[0][0]};
            //[3] 2,5,8 -> [0] 0,1,2
            cube[0][0] = savedValues[0];
            //[5] 6,7,8 -> [3] 2,5,8
            cube[3][0][2] = savedValues[1][0];
            cube[3][1][2] = savedValues[1][1];
            cube[3][2][2] = savedValues[1][2];
            //[1] 0,3,6 -> [5] 6,7,8
            cube[5][2] = savedValues[2];
            //[0] 0,1,2 -> [1] 0,3,6
            cube[1][0][0] = savedValues[3][0];
            cube[1][1][0] = savedValues[3][1];
            cube[1][2][0] = savedValues[3][2];

            cube[4] = rotateRight(cube[4]);
            return cube;
        }
        byte[][][] applyMR1462(byte[][][] cube){
            byte[][] savedValues = {{cube[3][0][1],cube[3][1][1],cube[3][2][1]},
                    cube[5][1],
                    {cube[1][0][1],cube[1][1][1],cube[1][2][1]},
                    cube[0][1]};
            //[0] 3,4,5 -> [3] 1,4,7
            cube[3][0][1] = savedValues[3][0];
            cube[3][1][1] = savedValues[3][1];
            cube[3][2][1] = savedValues[3][2];
            //[3] 1,4,7 -> [5] 3,4,5
            cube[5][1] = savedValues[0];
            //[5] 3,4,5 -> [1] 1,4,7
            cube[1][0][1] = savedValues[1][0];
            cube[1][1][1] = savedValues[1][1];
            cube[1][2][1] = savedValues[1][2];
            //[1] 1,4,7 -> [0] 3,4,5
            cube[0][1] = savedValues[2];

            return cube;
        }
        byte[][][] applyML1462(byte[][][] cube){
            byte[][] savedValues = {{cube[3][0][1],cube[3][1][1],cube[3][2][1]},
                    cube[5][1],
                    {cube[1][0][1],cube[1][1][1],cube[1][2][1]},
                    cube[0][1]};
            //[3] 1,4,7 -> [0] 3,4,5
            cube[0][1] = savedValues[0];
            //[5] 3,4,5 -> [3] 1,4,7
            cube[3][0][1] = savedValues[1][0];
            cube[3][1][1] = savedValues[1][1];
            cube[3][2][1] = savedValues[1][2];
            //[1] 1,4,7 -> [5] 3,4,5
            cube[5][1] = savedValues[2];
            //[0] 3,4,5 -> [1] 1,4,7
            cube[1][0][1] = savedValues[3][0];
            cube[1][1][1] = savedValues[3][1];
            cube[1][2][1] = savedValues[3][2];

            return cube;
        }
        byte[][][] applyBR1462(byte[][][] cube){
            byte[][] savedValues = {{cube[3][0][0],cube[3][1][0],cube[3][2][0]},
                    cube[5][0],
                    {cube[1][0][2],cube[1][1][2],cube[1][2][2]},
                    cube[0][2]};

            //[0] 6,7,8 -> [3] 0,3,6
            cube[3][0][0] = savedValues[3][0];
            cube[3][1][0] = savedValues[3][1];
            cube[3][2][0] = savedValues[3][2];
            //[3] 0,3,6 -> [5] 0,1,2
            cube[5][0] = savedValues[0];
            //[5] 0,1,2 -> [1] 2,5,8
            cube[1][0][2] = savedValues[1][0];
            cube[1][1][2] = savedValues[1][1];
            cube[1][2][2] = savedValues[1][2];
            //[1] 2,5,8 -> [0] 6,7,8
            cube[0][2] = savedValues[2];

            cube[2] = rotateRight(cube[2]);
            return cube;
        }
        byte[][][] applyBL1462(byte[][][] cube){
            byte[][] savedValues = {{cube[3][0][0],cube[3][1][0],cube[3][2][0]},
                    cube[5][0],
                    {cube[1][0][2],cube[1][1][2],cube[1][2][2]},
                    cube[0][2]};

            //[3] 0,3,6 -> [0] 6,7,8
            cube[0][2] = savedValues[0];
            //[5] 0,1,2 -> [3] 0,3,6
            cube[3][0][0] = savedValues[1][0];
            cube[3][1][0] = savedValues[1][1];
            cube[3][2][0] = savedValues[1][2];
            //[1] 2,5,8 -> [5] 0,1,2
            cube[5][0] = savedValues[2];
            //[0] 6,7,8 -> [1] 2,5,8
            cube[1][0][2] = savedValues[3][0];
            cube[1][1][2] = savedValues[3][1];
            cube[1][2][2] = savedValues[3][2];

            cube[2] = rotateLeft(cube[2]);
            return cube;
        }
        byte[][][] applyTR3651(byte[][][] cube){
            byte[][] savedValues = {{cube[5][0][2],cube[5][1][2],cube[5][2][2]},
                    {cube[4][0][0],cube[4][1][0],cube[4][2][0]},
                    {cube[0][0][2],cube[0][1][2],cube[0][2][2]},
                    {cube[2][0][2],cube[2][1][2],cube[2][2][2]}};
            //[2] 2,5,8 -> [5] 2,5,8
            cube[5][0][2] = savedValues[3][0];
            cube[5][1][2] = savedValues[3][1];
            cube[5][2][2] = savedValues[3][2];
            //[5] 2,5,8 -> [4] 0,3,6
            cube[4][0][0] = savedValues[0][0];
            cube[4][1][0] = savedValues[0][1];
            cube[4][2][0] = savedValues[0][2];
            //[4] 0,3,6 -> [0] 2,5,8
            cube[0][0][2] = savedValues[1][0];
            cube[0][1][2] = savedValues[1][1];
            cube[0][2][2] = savedValues[1][2];
            //[0] 2,5,8 -> [2] 2,5,8
            cube[2][0][2] = savedValues[2][0];
            cube[2][1][2] = savedValues[2][1];
            cube[2][2][2] = savedValues[2][2];

            //TODO verify this
            cube[3] = rotateLeft(cube[3]);
            return cube;
        }
        byte[][][] applyTL3651(byte[][][] cube){
            byte[][] savedValues = {{cube[5][0][2],cube[5][1][2],cube[5][2][2]},
                    {cube[4][0][0],cube[4][1][0],cube[4][2][0]},
                    {cube[0][0][2],cube[0][1][2],cube[0][2][2]},
                    {cube[2][0][2],cube[2][1][2],cube[2][2][2]}};
            //[5] 2,5,8 -> [2] 2,5,8
            cube[2][0][2] = savedValues[0][0];
            cube[2][1][2] = savedValues[0][1];
            cube[2][2][2] = savedValues[0][2];
            //[4] 0,3,6 -> [5] 2,5,8
            cube[5][0][2] = savedValues[1][0];
            cube[5][1][2] = savedValues[1][1];
            cube[5][2][2] = savedValues[1][2];
            //[0] 2,5,8 -> [4] 0,3,6
            cube[4][0][0] = savedValues[2][0];
            cube[4][1][0] = savedValues[2][1];
            cube[4][2][0] = savedValues[2][2];
            //[2] 2,5,8 -> [0] 2,5,8
            cube[0][0][2] = savedValues[3][0];
            cube[0][1][2] = savedValues[3][1];
            cube[0][2][2] = savedValues[3][2];

            //TODO verify this
            cube[3] = rotateRight(cube[3]);
            return cube;
        }
        byte[][][] applyMR3651(byte[][][] cube){
            byte[][] savedValues = {{cube[5][0][1],cube[5][1][1],cube[5][2][1]},
                    {cube[4][0][1],cube[4][1][1],cube[4][2][1]},
                    {cube[0][0][1],cube[0][1][1],cube[0][2][1]},
                    {cube[2][0][1],cube[2][1][1],cube[2][2][1]}};
            //[2] 1,4,7 -> [5] 1,4,7
            cube[5][0][1] = savedValues[3][0];
            cube[5][1][1] = savedValues[3][1];
            cube[5][2][1] = savedValues[3][2];
            //[5] 1,4,7 -> [4] 1,4,7
            cube[4][0][1] = savedValues[0][0];
            cube[4][1][1] = savedValues[0][1];
            cube[4][2][1] = savedValues[0][2];
            //[4] 1,4,7 -> [0] 1,4,7
            cube[0][0][1] = savedValues[1][0];
            cube[0][1][1] = savedValues[1][1];
            cube[0][2][1] = savedValues[1][2];
            //[0] 1,4,7 -> [2] 1,4,7
            cube[2][0][1] = savedValues[2][0];
            cube[2][1][1] = savedValues[2][1];
            cube[2][2][1] = savedValues[2][2];

            return cube;
        }
        byte[][][] applyML3651(byte[][][] cube){
            byte[][] savedValues = {{cube[2][0][1],cube[2][1][1],cube[2][2][1]},
                    {cube[5][0][1],cube[5][1][1],cube[5][2][1]},
                    {cube[4][0][1],cube[4][1][1],cube[4][2][1]},
                    {cube[0][0][1],cube[0][1][1],cube[0][2][1]}};
            //[5] 1,4,7 -> [2] 1,4,7
            cube[2][0][1] = savedValues[1][0];
            cube[2][1][1] = savedValues[1][1];
            cube[2][2][1] = savedValues[1][2];
            //[4] 1,4,7 -> [5] 1,4,7
            cube[5][0][1] = savedValues[2][0];
            cube[5][1][1] = savedValues[2][1];
            cube[5][2][1] = savedValues[2][2];
            //[0] 1,4,7 -> [4] 1,4,7
            cube[4][0][1] = savedValues[3][0];
            cube[4][1][1] = savedValues[3][1];
            cube[4][2][1] = savedValues[3][2];
            //[2] 1,4,7 -> [0] 1,4,7
            cube[0][0][1] = savedValues[0][0];
            cube[0][1][1] = savedValues[0][1];
            cube[0][2][1] = savedValues[0][2];

            return cube;
        }
        byte[][][] applyBR3651(byte[][][] cube){
            byte[][] savedValues = {{cube[5][0][0],cube[5][1][0],cube[5][2][0]},
                    {cube[4][0][2],cube[4][1][2],cube[4][2][2]},
                    {cube[0][0][0],cube[0][1][0],cube[0][2][0]},
                    {cube[2][0][0],cube[2][1][0],cube[2][2][0]}};
            //[2] 0,3,6 -> [5] 0,3,6
            cube[5][0][0] = savedValues[3][0];
            cube[5][1][0] = savedValues[3][1];
            cube[5][2][0] = savedValues[3][2];
            //[5] 0,3,6 -> [4] 2,5,8
            cube[4][0][2] = savedValues[0][0];
            cube[4][1][2] = savedValues[0][1];
            cube[4][2][2] = savedValues[0][2];
            //[4] 2,5,8 -> [0] 0,3,6
            cube[0][0][0] = savedValues[1][0];
            cube[0][1][0] = savedValues[1][1];
            cube[0][2][0] = savedValues[1][2];
            //[0] 0,3,6 -> [2] 0,3,6
            cube[2][0][0] = savedValues[2][0];
            cube[2][1][0] = savedValues[2][1];
            cube[2][2][0] = savedValues[2][2];

            //TODO verify this
            cube[1] = rotateRight(cube[1]);
            return cube;
        }
        byte[][][] applyBL3651(byte[][][] cube){
            byte[][] savedValues = {{cube[5][0][0],cube[5][1][0],cube[5][2][0]},
                    {cube[4][0][2],cube[4][1][2],cube[4][2][2]},
                    {cube[0][0][0],cube[0][1][0],cube[0][2][0]},
                    {cube[2][0][0],cube[2][1][0],cube[2][2][0]}};
            //[5] 0,3,6 -> [2] 0,3,6
            cube[2][0][0] = savedValues[0][0];
            cube[2][1][0] = savedValues[0][1];
            cube[2][2][0] = savedValues[0][2];
            //[4] 2,5,8 -> [5] 0,3,6
            cube[5][0][0] = savedValues[1][0];
            cube[5][1][0] = savedValues[1][1];
            cube[5][2][0] = savedValues[1][2];
            //[0] 0,3,6 -> [4] 2,5,8
            cube[4][0][2] = savedValues[2][0];
            cube[4][1][2] = savedValues[2][1];
            cube[4][2][2] = savedValues[2][2];
            //[2] 0,3,6 -> [0] 0,3,6
            cube[0][0][0] = savedValues[3][0];
            cube[0][1][0] = savedValues[3][1];
            cube[0][2][0] = savedValues[3][2];

            //TODO verify this
            cube[1] = rotateLeft(cube[1]);
            return cube;
        }
        private byte[][] getNewSide(){
            byte[][] result = new byte[3][];
            result[0] = new byte[3];
            result[1] = new byte[3];
            result[2] = new byte[3];
            return result;
        }
        public byte[][] rotateLeft(byte[][] side){
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
        public byte[][] rotateRight(byte[][] side){
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
    }
}
