package core.algorithms;

public class NewWBnW extends NewBnB{

    /**
     * constructor
     * @param weight - how much the return solution can be far from the optimal length's path.
     *               should be more then 1 inorder to get suboptimal solutions.
     */
    public NewWBnW(double weight){
        super();
        this.weight= weight;
    }

    @Override
    public String getName() {
        return "WBnB";
    }

}
