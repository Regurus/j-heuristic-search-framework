package core.generators;
import core.SearchDomain;
import core.State;
/***
 * Support class for generating random instances for problem spaces.
 * currently supports 15-puzzle,pancakes,rubik`s cube.
 */
import java.util.Random;

public class UniversalGenerator {
    Random generator = new Random();

    public UniversalGenerator() {
    }

    public State generate(SearchDomain solvedProblem, int maxSolutionDepth){
        State next = solvedProblem.initialState();
        for(int i=0;i<maxSolutionDepth;i++){
            int operatorsCount = solvedProblem.getNumOperators(next);
            int nextOperator = generator.nextInt(operatorsCount);
            next = solvedProblem.applyOperator(next, solvedProblem.getOperator(next,nextOperator));
        }
        return next;
    }
}
