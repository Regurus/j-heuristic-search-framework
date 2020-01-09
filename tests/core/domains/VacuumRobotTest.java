package core.domains;

import core.Operator;
import core.State;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class VacuumRobotTest {

    private static final String vacuumPath = System.getProperty("user.dir") + "\\testResources\\core\\domains\\vacuumRobotDomainTest.txt";

        /*
        Initialization of the file to test with should be as followed:
            Width Height - of map
            --Blank line / this line doesn't matter--
            Map drawn here, each char represents:
            ' ' , '.' , '_' - passage
            '*' - dirt
            '#' - blockage
            'V' / '@' - start point
     */

    /**
     * We're going to test the domain by creating an issue specified with params above and operate the robot,
     * see how it reacts, make sure the states and operators are defined well, heuristics, distance,
     * and etc.
     * e.g. - we can't clean a non-dirty spot or can't move into a blockage.
     */
    @Test
    public void testDomain() throws IOException {
        File domainTest = new File(vacuumPath);
        InputStream problemStream = new FileInputStream(domainTest);
        VacuumRobot vr = new VacuumRobot(problemStream);

        assertNotNull("VacuumRobot should initialize successfully", vr);

        State state = vr.initialState();

        assertNotNull("Initial state should initialize successfully", state);

        //Test the Distance to go (D) and Heuristic (H) which are identical -> Manhattan distance
        assertTrue("Distance to go should be 15.0", 15d == state.getD());
        assertTrue("Heuristic should be 15.0", 15d == state.getH());

        assertEquals("Possible moves - ", 2, vr.getNumOperators(state)); //UP, RIGHT
        assertTrue("Is initial state", state.equals(vr.initialState()));

//        System.out.println(vr.getOperator(state,1).toString());
        Operator op = vr.getOperator(state, 1);
        /** RIGHT **/
        state = vr.applyOperator(state, op);

        assertTrue("Distance should be 16.0", state.getD() == 16d);
        assertTrue("Heuristic should be 16.0", state.getH() == 16d);
        assertEquals("Number of moves- ", 2, vr.getNumOperators(state)); //LEFT, RIGHT
        assertFalse("Not initial state", state.equals(vr.initialState()));

//        System.out.println(vr.getOperator(state,0).toString());
        op = vr.getOperator(state, 0);
        /** LEFT **/
        state = vr.applyOperator(state, op);

        assertTrue("Distance to go should be 15.0", 15d == state.getD());
        assertTrue("Heuristic should be 15.0", 15d == state.getH());
        assertTrue("Is initial state again", state.equals(vr.initialState()));


//        System.out.println(vr.getOperator(state,0).toString());
        op = vr.getOperator(state, 0);
        /** UP **/
        state = vr.applyOperator(state, op);

        assertTrue("Distance to go should be 14.0", 14d == state.getD());
        assertTrue("Heuristic should be 14.0", 14d == state.getH());

        assertEquals("Possible moves - ", 1, vr.getNumOperators(state)); //SUCK
        assertTrue("When the ROBOT encounters a DIRTY SPOT it MUST clean it!" +
                " NO OTHER OPTION", vr.getOperator(state,0).toString() == "SUCK"); //Making sure its not DOWN


//        System.out.println(vr.getOperator(state,0).toString());
        op = vr.getOperator(state, 0);
        /** SUCK **/
        state = vr.applyOperator(state, op);

        assertTrue("Distance to go should be 13.0", 13d == state.getD());
        assertTrue("Heuristic should be 13.0", 13d == state.getH());

        assertEquals("Possible moves - ", 1, vr.getNumOperators(state));

        assertFalse("Not goal state", vr.isGoal(state));

//        System.out.println(vr.getOperator(state,0).toString());
        op = vr.getOperator(state, 0);
        /** DOWN **/
        state = vr.applyOperator(state, op);

        assertTrue("Distance to go should be 14.0", 14d == state.getD());
        assertTrue("Heuristic should be 14.0", 14d == state.getH());
        assertFalse("Making sure its not initial state, dirt spots changed!", state.equals(vr.initialState()));
        assertEquals("Number of moves should still be two at this spot", 2, vr.getNumOperators(state));

        //Now we will continue to solve the issue as we have tested corners & walls, we have 2 more walls to test

//        System.out.println(vr.getOperator(state,1).toString());
        op = vr.getOperator(state, 1);
        /** RIGHT **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 1);
        /** RIGHT **/
        state = vr.applyOperator(state, op);

        assertEquals("Number of moves should be 3", 3, vr.getNumOperators(state));
        assertTrue("Distance to go should be 12.0", 12d == state.getD());
        assertTrue("Heuristic should be 12.0", 12d == state.getH());

        op = vr.getOperator(state, 2);
        /** RIGHT **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** UP **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** SUCK **/
        state = vr.applyOperator(state, op);

        assertTrue("Distance to go should be 9.0", 9d == state.getD());
        assertTrue("Heuristic should be 9.0", 9d == state.getH());

        op = vr.getOperator(state, 1);
        /** LEFT **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** UP **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** SUCK **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** UP **/
        state = vr.applyOperator(state, op);

        assertTrue("Heuristic should be 5.0", 5d == state.getH());
        assertTrue("Distance should be 5.0", 5d == state.getD());

        op = vr.getOperator(state, 1);
        /** LEFT **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** SUCK **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** RIGHT **/
        state = vr.applyOperator(state, op);

        assertTrue("Heuristic should be 2.0", 2d == state.getH());
        assertTrue("Distance should be 2.0", 2d == state.getD());

        op = vr.getOperator(state, 2);
        /** RIGHT **/
        state = vr.applyOperator(state, op);

        op = vr.getOperator(state, 0);
        /** SUCK **/
        state = vr.applyOperator(state, op);

        assertTrue("Heuristic should be 0.0", 0d == state.getH());
        assertTrue("Distance should be 0.0", 0d == state.getD());
        assertTrue("We are done!",vr.isGoal(state));
    }
}