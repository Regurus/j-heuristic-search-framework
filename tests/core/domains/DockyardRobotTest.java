package core.domains;

import core.Operator;
import core.State;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DockyardRobotTest {
    private static final String dockyardPath = System.getProperty("user.dir") + "\\testResources\\core\\domains\\dockyardDomainTest.txt";

    /*
        Initialization of the file to test with should be as followed:
            Locations to define
            Cranes available
            Boxes count
            Piles count
            Robots available - MUST BE 1
            Defining locations and their properties: loc-> adjacent/cranes/piles/pile
            Defining containers (goal for each and every box with a location)
     */

    /**
     * We're going to test the domain by creating an issue specified with params above and play with moving the boxes
     * making sure every move turns out and impacts as expected.
     * e.g. - while using a single crane we cant pick up more than 1 box simultaneously.
     */
    @Test
    public void testDomain() throws IOException {
        File domainTest = new File(dockyardPath);
        InputStream problemStream = new FileInputStream(domainTest);
        DockyardRobot dr = new DockyardRobot(problemStream);
        assertNotNull("DockyardRobot should initialize successfully", dr);

        State state = dr.initialState();
        assertNotNull("InitialState should not be null", state);

        assertNull("There is no parent for the initialState", state.getParent());

        assertTrue("Distance of InitialState should be 11.0 assuming D is the amount of OPERATORS" +
                "that needs to be activated to reach goal state", 11d == state.getD());
        assertTrue("Heuristic from this specific state to the goal should be 38.0 assuming OPERATORS" +
                "have cost", 38d == state.getH());


        assertEquals("Number of possible states from initialization-", 2, dr.getNumOperators(state));
        //{POP B3, MOVE 0->1}

        //We will now try to solve the issue while making sure the heuristic and distance changes correctly
//        System.out.println(dr.getOperator(state, 0).toString());
        Operator op = dr.getOperator(state,0);
        /**POP B0**/
        state = dr.applyOperator(state, op);

        assertTrue("Distance should be 10.0", 10d == state.getD());
        assertTrue("Distance should be 23.0", 23d == state.getH());
        assertEquals("Number of operators available to us now-", 3, dr.getNumOperators(state));
        //{LOAD B0, PUSH B0, MOVE 0->1}



        assertFalse("Current state is not goal state", dr.isGoal(state));
//        System.out.println(dr.getOperator(state, 0).toString());
        op = dr.getOperator(state, 0);
        assertTrue("Same state therefore should be equal", state.getParent().equals(dr.applyOperator(state, op))); //Reverse operator functional
        assertTrue("Make sure it's also the initialState we declared", dr.applyOperator(state, op).equals(dr.initialState()));


//        System.out.println(dr.getOperator(state, 1).toString());
        op = dr.getOperator(state, 1);
        /**LOAD B0**/
        state = dr.applyOperator(state, op);

        assertTrue("Distance should be 9.0", 9d == state.getD());
        assertTrue("Distance should be 22.0", 22d == state.getH());

        assertEquals("Number of operators available to us now-", 3, dr.getNumOperators(state));
        //{POP B1, UNLOAD B0, MOVE 0->1}

//        System.out.println(dr.getOperator(state, 2).toString());
        op = dr.getOperator(state, 2);
        /**MOVE 0->1**/
        state = dr.applyOperator(state, op);

        assertFalse("Is not goal state", dr.isGoal(state));

        assertEquals("Number of operators available to us now-", 2, dr.getNumOperators(state));
        //{PUSH B3, MOVE 1->0}

//        System.out.println(dr.getOperator(state, 0).toString());
        op = dr.getOperator(state, 0);
        /**UNLOAD B0**/
        state = dr.applyOperator(state, op);

        assertEquals("Number of operators available to us now-", 3, dr.getNumOperators(state));
        //{PUSH B0, UNLOAD B0, MOVE 1->0}
//        System.out.println(dr.getOperator(state, 0).toString());

        assertTrue("Distance should be 10.0", 7d == state.getD());
        assertTrue("Distance should be 20.0", 20d == state.getH());

//        System.out.println(dr.getOperator(state, 0).toString());
        op = dr.getOperator(state, 0);
        /**PUSH B0**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 1).toString());
        op = dr.getOperator(state, 1);
        /**MOVE 1->0**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 0).toString());
        op = dr.getOperator(state, 0);
        /**POP B1**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 1).toString());
        op = dr.getOperator(state, 1);
        /**LOAD B1**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 2).toString());
        op = dr.getOperator(state, 2);
        /**MOVE 0->1**/
        state = dr.applyOperator(state, op);

        assertTrue("Operators should now be equal to 3", 3 == dr.getNumOperators(state));
        //POP B0, UNLOAD B1, MOVE 1->0

//        System.out.println(dr.getOperator(state, 1).toString());
        op = dr.getOperator(state, 1);
        /**UNLOAD B1**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 2).toString());
        op = dr.getOperator(state, 2);
        /**MOVE 1->0**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 0).toString());
        op = dr.getOperator(state, 0);
        /**POP B2**/
        state = dr.applyOperator(state, op);

//        System.out.println(dr.getOperator(state, 1).toString());
        op = dr.getOperator(state, 1);
        /**LOAD B2**/
        state = dr.applyOperator(state, op);

        assertFalse("Not goal state!", dr.isGoal(state));
        assertTrue("Distance should be 1", 1d == state.getD());
        assertTrue("Heuristic should be 1", 1d == state.getH());

        assertTrue("Only 2 operators are available", 2 == dr.getNumOperators(state));

//        System.out.println(dr.getOperator(state, 1).toString());
        op = dr.getOperator(state, 1);
        /**MOVE 0->1**/
        state = dr.applyOperator(state, op);

        assertTrue("Goal state!", dr.isGoal(state));
        assertTrue("Distance should be 0", 0d == state.getD());
        assertTrue("Heuristic should be 0", 0d == state.getH());
    }
}