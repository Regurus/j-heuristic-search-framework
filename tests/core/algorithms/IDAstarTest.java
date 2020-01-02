package core.algorithms;

import core.Graphs;
import core.SearchResult;
import core.domains.OverrideDomain;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class IDAstarTest {
    private IDAstar tested;
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        tested = new IDAstar();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(originalOut);
    }

    @Test
    public void getName() {
        assertEquals("name OK",tested.getName(),"idastar");
    }

    @Test
    public void getPossibleParameters() {
        assertNull(tested.getPossibleParameters());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAdditionalParameter() {
        this.tested.setAdditionalParameter("weight","2.0");
        assertEquals("weight >1.0 OK","", outContent.toString());
        this.tested.setAdditionalParameter("weight", "0.1");
        assertEquals("weight <1.0 OK","[ERROR] The weight must be >= 1.0\r\n", outContent.toString());
        this.tested.setAdditionalParameter("weight","1.0");
        assertEquals("weight =1.0 OK","[WARNING] Weight of 1.0 is equivalent to A*\r\n", outContent.toString());
    }
    @Test(expected = NotImplementedException.class)
    public void setAdditionalParameterNotImplemented() {
        this.tested.setAdditionalParameter("someOtherParamenter","somevalue");
    }

    @Test
    public void search() {
        OverrideDomain graph1 = Graphs.graph1;
        SearchResult g1Result = this.tested.search(graph1);
        assertEquals("G1 solution cost OK", 10 ,(int)g1Result.getSolutions().get(0).getCost());
        assertEquals("G1 solution length OK", 4 ,g1Result.getSolutions().get(0).getLength());
        assertEquals("G1 expanded amount OK", 11 ,g1Result.getExpanded());
        assertEquals("G1 generated amount OK", 16 ,g1Result.getGenerated());
    }
}