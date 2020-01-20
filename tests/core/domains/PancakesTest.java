package core.domains;

import core.Operator;
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
}