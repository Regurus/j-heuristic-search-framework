package core.domains;

import core.SearchDomain;
import core.algorithms.IDAstar;
import core.algorithms.IDDPS;
import core.algorithms.IDEES;
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
        operator.applyTR0351(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.BASELINE_HEURISTIC);
        testSearchAlgorithm(domain, new IDAstar(3), 0, 0, 0);
    }
    @Test
    public void D2Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.COLORS);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D3Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D4Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.PARALLEL_LINES);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D5Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.HYBRID);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D6Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.BASELINE_HEURISTIC);
        testSearchAlgorithm(domain, new IDAstar(5), 0, 0, 0);
    }
    @Test
    public void D7Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.HYBRID);
        testSearchAlgorithm(domain, new IDDPS(), 0, 0, 0);
    }
    @Test
    public void D8Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.TRUE_DISTANCE);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void D9Test(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyTL1234(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.COLORS);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }

    @Test
    public void fullRunTest() {
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        operator.applyBL1234(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyBL1234(cubeForOperations);
        operator.applyBL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        operator.applyTL2540(cubeForOperations);
        operator.applyTL1234(cubeForOperations);
        operator.applyTR0351(cubeForOperations);
        SearchDomain domain = new RubiksCube(cubeForOperations, RubiksCube.HeuristicType.TRUE_DISTANCE);
        testSearchAlgorithm(domain, new IDAstar(), 0, 0, 0);
    }
    @Test
    public void testReverseOperator(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        RubiksCube.RubiksOperator operatorReversed = (RubiksCube.RubiksOperator) operator.reverse(null);
        assertEquals("operators reverse OK", RubiksCube.Operators.BOT_RIGHT_0351,operatorReversed.operator);
    }
    @Test
    public void testOperatorsReversable(){
        RubiksCube.RubiksOperator operator = test.getTestOperator();
        //TL1234-TR1234 PAIR
        byte[][][] tested = operator.applyTL1234(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyTR1234(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //BL1234-BR1234 PAIR
        tested = operator.applyBL1234(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyBR1234(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //TL0351-TR0351 PAIR
        tested = operator.applyTL0351(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyTR0351(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //BL0351-BR0351 PAIR
        tested = operator.applyBL0351(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyBR0351(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //TL2540-TR2540 PAIR
        tested = operator.applyTL2540(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyTR2540(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));

        //BL2540-BR2540 PAIR
        tested = operator.applyBL2540(cubeForOperations);
        //check if changed
        assertThat(tested,not(equalTo(ORIGINAL_CUBE)));
        tested = operator.applyBR2540(tested);
        //check if returned to original
        assertThat(tested,equalTo(ORIGINAL_CUBE));
    }

}