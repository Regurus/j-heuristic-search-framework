package core.domains;

import core.Operator;
import core.State;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class PancakesTest {
    Pancakes tested;
    final int SIZE=20;
    @Before
    public void setUp() throws Exception {
        tested = new Pancakes(SIZE);
    }

    @Test
    public void initialState() {
        for(int i=5;i<100;i++){
            Pancakes pan = new Pancakes(i);
            int[] compared = new int[i];
            for(int j=0;j<i;j++){
                compared[j] = j;
            }
            assertArrayEquals("initial array OK, size: "+i,compared, pan.initialState().cakes);
        }
        int[] randomArray = new int[20];
        Random generator = new Random();
        for(int i=0;i<SIZE;i++){
            randomArray[i] = generator.nextInt(SIZE);
        }
        this.tested = new Pancakes(randomArray);
        assertArrayEquals("random init array OK",randomArray, this.tested.initialState().cakes);
    }

    @Test
    public void getNumOperators() {
        for(int i=5;i<100;i++){
            Pancakes pan = new Pancakes(i);
            assertEquals("initial operator count OK, size: "+i,i-1, pan.getNumOperators(pan.initialState()));
        }
    }

    @Test
    public void applyOperator() {
        for(int i=0;i<SIZE-1;i++){
            Pancakes pan = new Pancakes(SIZE);
            Operator op = pan.getOperator(pan.initialState(),i);
            Pancakes.PancakeState state = (Pancakes.PancakeState) pan.applyOperator(pan.initialState(),op);
            i+=2;//no need for another variable
            int[] reversed = new int[i];
            System.arraycopy(pan.initialState().cakes,0,reversed,0, i);
            for(int j=0;j<reversed.length;j++){
                assertEquals("reverse OK @iteration #"+i,reversed[reversed.length-1-j],state.cakes[j]);
            }
            i-=2;
        }
    }

    @Test
    public void fullInstanceRun(){
        Pancakes domain = new Pancakes(6);
        State initState = domain.initialState();

        assertEquals("Initial order should be sorted", "0 1 2 3 4 5", initState.convertToStringShort());
        assertTrue("Distance to go should be 0", 0.0 == initState.getD());
        assertTrue("Cost to go should be 0", 0.0 == initState.getH());
        assertTrue("This is the goal state", domain.isGoal(initState));

        State curState = initState;

        assertEquals("Amount of operators:", 5, domain.getNumOperators(curState));
        /*
            Generally there's no point to check Num of operators further as
            the number of operators should ALWAYS be equal to the number of gaps (cakes size-1)
            However - we will check it once more later on to make sure it doesn't change.
         */

        //Flip the first and the second places - this creates a gap of 1, therefore D and H should be 1.
        Operator op = domain.getOperator(curState, 0);
        curState = domain.applyOperator(curState, op);


        assertTrue("Distance to go should be 1", 1.0 == curState.getD());
        assertTrue("Cost to go should be 1", 1.0 == curState.getH());

        //Flip all the pancakes up until the 4th gap.

        op = domain.getOperator(curState, 3);
        curState = domain.applyOperator(curState, op);

        assertTrue("Distance to go should be 2", 2.0 == curState.getD());
        assertTrue("Cost to go should be 2", 2.0 == curState.getH());
        assertEquals("Amount of operators:", 5, domain.getNumOperators(curState));
        assertFalse("Not a goal state", domain.isGoal(curState));


        //Reverse the flip
        op = domain.getOperator(curState, 3);
        curState = domain.applyOperator(curState, op);

        //Flip the entire pile, still only 1 gap in the pile.
        op = domain.getOperator(curState, 4);
        curState = domain.applyOperator(curState, op);

        //Distance is now 2 as the gaps are 1 and they are sorted in reverse
        assertTrue("Distance to go should be 2", 2.0 == curState.getD());
        assertTrue("Cost to go should be 2", 2.0 == curState.getH());

        //We will flip the 3rd pancake, now gaps should be 3.
        op = domain.getOperator(curState, 3);
        curState = domain.applyOperator(curState, op);

        assertTrue("Distance to go should be 3", 3.0 == curState.getD());
        assertTrue("Cost to go should be 3", 3.0 == curState.getH());

        domain.setInitialState(curState);
        initState = domain.initialState();

        assertTrue("States should be equal", initState.equals(curState));

        //Solve the instance.

        op = domain.getOperator(curState,3);
        curState = domain.applyOperator(curState, op);

        op = domain.getOperator(curState, 4);
        curState = domain.applyOperator(curState, op);

        assertFalse("States shouldn't be equal", curState.equals(initState));
        assertTrue("Distance to go should be 1", 1.0 == curState.getD());
        assertTrue("Cost to go should be 1", 1.0 == curState.getH());
        assertFalse("Not a goal state", domain.isGoal(curState));

        op = domain.getOperator(curState, 0);
        curState = domain.applyOperator(curState, op);

        assertTrue("Distance to go should be 0", 0.0 == curState.getD());
        assertTrue("Cost to go should be 0", 0.0 == curState.getH());
        assertTrue("This is the goal state", domain.isGoal(curState));
    }
}