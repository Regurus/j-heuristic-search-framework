package core.algorithms;

import core.SearchAlgorithm;
import core.SearchDomain;
import org.junit.Test;
import java.io.FileNotFoundException;
import static core.algorithms.Utils.*;

public class RBFSTest {
    @Test
    public void testBinaryTree(){
        SearchDomain domain = getBinaryTree();
        SearchAlgorithm algo = new WRBFS(1);
        testSearchAlgorithm(domain, algo, 40, 20, 4);
    }

    @Test
    public void testFifteenPuzzle() throws FileNotFoundException {
        SearchDomain domain = createFifteenPuzzle();
        SearchAlgorithm algo = new WRBFS(2);
        testSearchAlgorithm(domain, algo, 319630, 155498, 70);
    }

    @Test
    public void testPancakes(){
        SearchDomain domain = getFixedPancakes(200);
        SearchAlgorithm algo = new WRBFS(2);
        testSearchAlgorithm(domain, algo, 61777, 312, 248);
    }





}