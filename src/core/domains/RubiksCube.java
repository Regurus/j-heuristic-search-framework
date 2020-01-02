package core.domains;

import core.Operator;
import core.SearchDomain;
import core.State;
import core.collections.PackedElement;

import java.io.*;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 *
 */

public class RubiksCube implements SearchDomain {

    public static HeuristicType activeHeuristic;
    public byte[][][] startingState;
    public static Logger log = Logger.getLogger(RubiksCube.class.getName());
    private final byte[][][] ORIGINAL_CUBE = {{{0,0,0},{0,0,0},{0,0,0}},{{1,1,1},{1,1,1},{1,1,1}},{{2,2,2},{2,2,2},{2,2,2}},
            {{3,3,3},{3,3,3},{3,3,3}},{{4,4,4},{4,4,4},{4,4,4}},{{5,5,5},{5,5,5},{5,5,5}}};

    public RubiksCube(byte[][][] cube, HeuristicType active) {
        this.startingState = cube;
        activeHeuristic = active;
    }

    public RubiksCube(HeuristicType active) {
        byte[][][] cube = deepCopyCube(ORIGINAL_CUBE);
        this.startingState = cube;
        activeHeuristic = active;
    }

    public void setInitialState(State state){
        if(state instanceof RubiksState)
            this.startingState = ((RubiksState) state).cube;
    }

    RubiksOperator getTestOperator(){
        return new RubiksOperator(Operators.BOT_LEFT_1462);
    }

    private enum Operators{
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
        BASE_RING,//has a bug still h!=0 on goal
        PARALLEL_LINES,
        PARALLEL_LINES_COMPLEX,
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
        //no backtracking possible
        @Override
        public State getParent() {
            if(previous!=null)
                return previous;
            return this;
        }

        @Override
        public double getH() {
            if(this.previous==null)
                return 0;
            double res = 0;
            if(RubiksCube.activeHeuristic == HeuristicType.NO_HEURISTIC){
                return 0;
            }
            else if (RubiksCube.activeHeuristic == HeuristicType.PARALLEL_LINES){
                res = this.getParallelStripeHeuristic();
            }
            else if(RubiksCube.activeHeuristic == HeuristicType.BASE_RING){
                res = this.getRingHeuristic();
            }
            else{
                res = this.getComplexParallelStripeHeuristic();
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

        private double getParallelStripeHeuristic() {
            int pair05 = getHorizontalStripes(this.cube[0], this.cube[5]) + getVerticalStripes(this.cube[0], this.cube[5]);
            int pair13 = getHorizontalStripes(this.cube[1], this.cube[3]) + getVerticalStripes(this.cube[1], this.cube[3]);
            int pair24 = getHorizontalStripes(this.cube[2], this.cube[4]) + getVerticalStripes(this.cube[2], this.cube[4]);
            return pair05 + pair13 + pair24;
        }

        private double getComplexParallelStripeHeuristic() {
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
            return null;
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
