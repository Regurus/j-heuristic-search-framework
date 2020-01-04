package core.domains;

import core.SearchAlgorithm;
import core.SearchDomain;
import core.algorithms.IDAstar;
import core.algorithms.IDDPS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static core.TestAllBasics.testSearchAlgorithm;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

public class RubiksCubeTest {
    private RubiksCube test;
    private final byte[][][] ORIGINAL_CUBE = {{{0,0,0},{0,0,0},{0,0,0}},{{1,1,1},{1,1,1},{1,1,1}},{{2,2,2},{2,2,2},{2,2,2}},
            {{3,3,3},{3,3,3},{3,3,3}},{{4,4,4},{4,4,4},{4,4,4}},{{5,5,5},{5,5,5},{5,5,5}}};
    private byte[][][] cubeForOperations = {{{0,0,0},{0,0,0},{0,0,0}},{{1,1,1},{1,1,1},{1,1,1}},{{2,2,2},{2,2,2},{2,2,2}},
            {{3,3,3},{3,3,3},{3,3,3}},{{4,4,4},{4,4,4},{4,4,4}},{{5,5,5},{5,5,5},{5,5,5}}};
    @Before
    public void setUp() {
        test = new RubiksCube(ORIGINAL_CUBE, RubiksCube.HeuristicType.PARALLEL_LINES);
    }

    @After
    public void tearDown() throws Exception {
    }



    @Test
    public void D1Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D2Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D3Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D4Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D5Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.NO_HEURISTIC);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D6Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D7Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D8Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D9Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyML2345(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }

    @Test
    public void fullRunTest() {
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyML2345(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyBL2345(cubeForOperations);
        operator.applyBL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        operator.applyTL3651(cubeForOperations);
        operator.applyML2345(cubeForOperations);
        operator.applyTR1462(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void operatorRotateTest(){
        byte[][] basecase = {{0,1,2},{3,4,5},{6,7,8}};
        byte[][] XRotatedLeft = {{2,5,8},{1,4,7},{0,3,6}};
        byte[][] XRotatedRight = {{6,3,0},{7,4,1},{8,5,2}};
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        byte[][] ARotatedLeft = operator.rotateLeft(basecase);
        assertArrayEquals(XRotatedLeft[0],ARotatedLeft[0]);
        assertArrayEquals(XRotatedLeft[1],ARotatedLeft[1]);
        assertArrayEquals(XRotatedLeft[2],ARotatedLeft[2]);
        byte[][] ARotatedRight = operator.rotateRight(basecase);
        assertArrayEquals(XRotatedRight[0],ARotatedRight[0]);
        assertArrayEquals(XRotatedRight[1],ARotatedRight[1]);
        assertArrayEquals(XRotatedRight[2],ARotatedRight[2]);
    }
    @Test
    public void testOperatorsReversable(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        //TL2345-TR2345 PAIR
        byte[][][] tested = operator.applyTL2345(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyTR2345(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //ML2345-MR2345 PAIR
        tested = operator.applyML2345(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyMR2345(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //BL2345-BR2345 PAIR
        tested = operator.applyBL2345(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyBR2345(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //TL1462-TR1462 PAIR
        tested = operator.applyTL1462(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyTR1462(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //ML1462-MR1462 PAIR
        tested = operator.applyML1462(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyMR1462(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //BL1462-BR1462 PAIR
        tested = operator.applyBL1462(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyBR1462(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //TL3651-TR3651 PAIR
        tested = operator.applyTL3651(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyTR3651(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //ML2345-MR2345 PAIR
        tested = operator.applyML3651(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyMR3651(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //BL2345-BR2345 PAIR
        tested = operator.applyBL3651(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyBR3651(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));
    }

}