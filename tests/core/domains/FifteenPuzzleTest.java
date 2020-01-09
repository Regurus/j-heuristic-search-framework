package core.domains;

import core.State;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class FifteenPuzzleTest {
    FifteenPuzzle tested;
    @Before
    public void setUp() throws Exception {
        this.tested = new FifteenPuzzle();
    }

    @Test
    public void initialState() {
        State state = tested.initialState();
        assertTrue("default initial state is goal", tested.isGoal(state));
        State s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(this.tested.initialState(),0));
        assertFalse("new state is not goal",this.tested.isGoal(s));
        this.tested.setInitialState(s);
        assertFalse("state changed to custom",this.tested.isGoal(this.tested.initialState()));
    }

    @Test
    public void getNumOperators() {
        //testing amount of operators at any possible empty space position
        assertEquals("initial operators count OK",2,this.tested.getNumOperators(this.tested.initialState()));
        State s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(this.tested.initialState(),0));
        assertEquals("cell 2 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 3 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 4 OK",2,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 8 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 7 OK",4,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 6 OK",4,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 5 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,2));
        assertEquals("cell 9 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 10 OK",4,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,2));
        assertEquals("cell 11 OK",4,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,2));
        assertEquals("cell 12 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,2));
        assertEquals("cell 16 OK",2,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 15 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 14 OK",3,tested.getNumOperators(s));
        s = this.tested.applyOperator(s,this.tested.getOperator(s,1));
        assertEquals("cell 13 OK",2,tested.getNumOperators(s));
    }
    //operators disregard the values on tiles manipulated, so checking all operators on a  single specific case covers all cases
    @Test
    public void getOperator() {
        this.tested.getNumOperators(this.tested.initialState());
        FifteenPuzzle.FifteenPuzzleOperator operator0 = (FifteenPuzzle.FifteenPuzzleOperator)this.tested.getOperator(this.tested.initialState(),0);
        FifteenPuzzle.FifteenPuzzleOperator operator1 = (FifteenPuzzle.FifteenPuzzleOperator)this.tested.getOperator(this.tested.initialState(),1);
        FifteenPuzzle.FifteenPuzzleOperator operator2 = (FifteenPuzzle.FifteenPuzzleOperator)this.tested.getOperator(this.tested.initialState(),2);
        FifteenPuzzle.FifteenPuzzleOperator operator3 = (FifteenPuzzle.FifteenPuzzleOperator)this.tested.getOperator(this.tested.initialState(),3);
        assertEquals("operator0 value",1,operator0.value);
        assertEquals("operator1 value",4,operator1.value);
        assertNull(operator2);
        assertNull(operator3);

        State s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(this.tested.initialState(),0));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",0,operator0.value);
        assertEquals("operator1 value",2,operator1.value);
        assertEquals("operator2 value",5,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",1,operator0.value);
        assertEquals("operator1 value",3,operator1.value);
        assertEquals("operator2 value",6,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",2,operator0.value);
        assertEquals("operator1 value",7,operator1.value);
        assertNull(operator2);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",3,operator0.value);
        assertEquals("operator1 value",6,operator1.value);
        assertEquals("operator2 value",11,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",2,operator0.value);
        assertEquals("operator1 value",5,operator1.value);
        assertEquals("operator2 value",7,operator2.value);
        assertEquals("operator3 value",10,operator3.value);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",1,operator0.value);
        assertEquals("operator1 value",4,operator1.value);
        assertEquals("operator2 value",6,operator2.value);
        assertEquals("operator3 value",9,operator3.value);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",0,operator0.value);
        assertEquals("operator1 value",5,operator1.value);
        assertEquals("operator2 value",8,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",4,operator0.value);
        assertEquals("operator1 value",9,operator1.value);
        assertEquals("operator2 value",12,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",5,operator0.value);
        assertEquals("operator1 value",8,operator1.value);
        assertEquals("operator2 value",10,operator2.value);
        assertEquals("operator3 value",13,operator3.value);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",6,operator0.value);
        assertEquals("operator1 value",9,operator1.value);
        assertEquals("operator2 value",11,operator2.value);
        assertEquals("operator3 value",14,operator3.value);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",7,operator0.value);
        assertEquals("operator1 value",10,operator1.value);
        assertEquals("operator2 value",15,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",11,operator0.value);
        assertEquals("operator1 value",14,operator1.value);
        assertNull(operator2);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",10,operator0.value);
        assertEquals("operator1 value",13,operator1.value);
        assertEquals("operator2 value",15,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",9,operator0.value);
        assertEquals("operator1 value",12,operator1.value);
        assertEquals("operator2 value",14,operator2.value);
        assertNull(operator3);

        s = this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        operator0 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,0);
        operator1 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,1);
        operator2 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,2);
        operator3 = (FifteenPuzzle.FifteenPuzzleOperator) this.tested.getOperator(s,3);
        assertEquals("operator0 value",8,operator0.value);
        assertEquals("operator1 value",13,operator1.value);
        assertNull(operator2);
        assertNull(operator3);
    }

    @Test
    public void applyOperator() {
        assertEquals("initial position OK",0, ((FifteenPuzzle.TileState)this.tested.initialState()).blank);
        FifteenPuzzle.TileState s = (FifteenPuzzle.TileState) this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(this.tested.initialState(),0));
        assertEquals("1 step position OK",1,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("2 step position OK",2,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("3 step position OK",3,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("4 step position OK",7,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("5 step position OK",6,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("6 step position OK",5,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("7 step position OK",4,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        assertEquals("8 step position OK",8,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("9 step position OK",9,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        assertEquals("10 step position OK",10,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        assertEquals("11 step position OK",11,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,2));
        assertEquals("12 step position OK",15,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("13 step position OK",14,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("14 step position OK",13,s.blank);

        s = (FifteenPuzzle.TileState)this.tested.applyOperator(this.tested.initialState(),this.tested.getOperator(s,1));
        assertEquals("15 step position OK",12,s.blank);

    }
}