package core.algorithms;

import core.SearchAlgorithm;
import core.SearchDomain;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static core.algorithms.Utils.createFifteenPuzzle;
import static core.algorithms.Utils.getFixedPancakes;
import static core.algorithms.Utils.testSearchAlgorithm;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WBnBTest {

    private double weight;
    private double multiplierBound;
    private SearchAlgorithm wbnb;
    @Before
    public void setUp(){
        this.weight = 2;
        this.multiplierBound = 2;
        wbnb = new WBnB(weight, multiplierBound);
    }

    @Test
    public void getName() {
        assertEquals("name OK",wbnb.getName(),"WBnB");
    }

    @Test
    public void getPossibleParameters() {
        assertNull(wbnb.getPossibleParameters());
    }

    @Test(expected = NotImplementedException.class)
    public void setAdditionalParameterNotImplemented() {
        this.wbnb.setAdditionalParameter("someOtherParamenter","somevalue");
    }

    /**
     * test for weight = 2 , multiplierBound = 2
     */
    @Test
    public void testWBNBPancakes() {
        final int SIZE = 300;
        SearchDomain pancakes = getFixedPancakes(SIZE);
        System.out.println("Pancakes problem: num pancakes:" + SIZE+" weight = "+ weight);
        System.out.println("WBNB");
        testSearchAlgorithm(pancakes, wbnb, 123075, 413, 413);
        System.out.println("IDA*");
        SearchAlgorithm ida = new IDAstar(weight);
        testSearchAlgorithm(pancakes, ida, 79688, 708, 590);
        System.out.println("WRBFS");
        SearchAlgorithm wrbfs = new WRBFS(weight);
        testSearchAlgorithm(pancakes, wrbfs, 133505, 448, 372);
    }

    @Test
    public void testWBNBFifteenPuzzle() {
        //weight = 2
        SearchDomain domain = createFifteenPuzzle();
        System.out.println("FifteenPuzzle: weight = "+ weight);
        System.out.println("WBNB");
        testSearchAlgorithm(domain, wbnb, 922346, 447488, 60);
        System.out.println("IDA*");
        SearchAlgorithm ida = new IDAstar(weight);
        testSearchAlgorithm(domain, ida, 1956869, 932535, 60);
        System.out.println("wrbfs");
        SearchAlgorithm wrbfs = new WRBFS(weight);
        testSearchAlgorithm(domain, wrbfs, 319630, 155498, 70);
    }

}