package org.cs4j.core.collections;

import org.cs4j.core.algorithms.SearchResultImpl;

import java.util.*;

/**
 * Created by Daniel on 08/01/2016.
 */
public class GH_heap<E extends SearchQueueElement> implements SearchQueue<E> {

    private final int key;
    private HashMap<Double, Double> countF = new HashMap<>();
    private HashMap<Double, Double> countD = new HashMap<>();
    //    private BinHeap<E> heap;
    private TreeMap<gh_node,ArrayList<E>> tree;
    private double fmin;
    private double dmin;
    private boolean isOptimal;
    private double w;
    private gh_node bestNode;
    private ArrayList<E> bestList;
    private ghNodeComparator comparator;
    private int GH_heapSize;
    private Comparator<E> NodePackedComparator;
    private SearchResultImpl result;
    private double hCoefficient;
    private double hHatCoefficient;
    private double dCoefficient;
    private double dHatCoefficient;

    private double fminCoefficient;
    private double dminCoefficient;
    private double gCostCoefficient;
    private double dCostCoefficient;

    private double lowerBound;
//    private boolean withFComparator;

    public GH_heap(double w, int key, double fmin, Comparator<E> NodePackedComparator,SearchResultImpl result, HashMap<String,Double> coefficients) {
        this.w = w;
        this.key = key;
        this.fmin = fmin;
        this.comparator = new ghNodeComparator();
        this.tree = new TreeMap<>(this.comparator);
        this.NodePackedComparator = NodePackedComparator;
        this.result =result;
        this.hCoefficient = coefficients.get("h");
        this.hHatCoefficient = coefficients.get("hHat");
        this.dCoefficient = coefficients.get("d");
        this.dHatCoefficient = coefficients.get("dHat");
        this.fminCoefficient = coefficients.get("fmin");
        this.dminCoefficient = coefficients.get("dmin");
        this.gCostCoefficient = coefficients.get("gCost");
        this.dCostCoefficient = coefficients.get("dCost");
//        this.heap = new BinHeap<>(new FComparator(), this.key);
//        this.withFComparator = true;
    }

    public void setOptimal(double fmin){
        this.isOptimal = true;
        this.fmin = fmin;
    }

    public double getFmin(){
//        testHat();
        return fmin;
    }

    public void add(E e) {
//    testHat();
        count_add(e);

        gh_node node = new gh_node(e);
        ArrayList<E> list;

        if(tree.containsKey(node)){
            list = tree.get(node);
        }
        else{
            list = new ArrayList<>();
        }
        e.setIndex(this.key,list.size());
        list.add(e);
        tree.put(node,list);

        if(this.bestNode != null){
            if(this.comparator.compare(bestNode,node) > 0){
                bestNode = node;
                bestList = list;
            }
        }
        else{
            bestNode = node;
            bestList = list;
        }
//    testHat();
    }

    private void count_add(E e){
        double f = e.getF();
        GH_heapSize++;
        if(countF.containsKey(f))
            countF.put(f,countF.get(f)+1);
        else {
            countF.put(f, 1.0);
            if(!isOptimal) {//fmin might change/decrease
                if (tree.size() == 0) {//tree is empty
                    fmin = f;
                }
                if (fmin - f > 0) {//might occur due to rounding of f;
                    fmin = f;
                    reorder();
                }
            }
        }
        double fd = e.getD()+e.getDepth();
        if(countD.containsKey(fd))
            countD.put(fd,countD.get(fd)+1);
        else {
            countD.put(fd, 1.0);
            if(!isOptimal) {//fmin might change/decrease
                if (tree.size() == 0) {//tree is empty
                    dmin = fd;
                }
                if (dmin - fd > 0) {//might occur due to rounding of d??? - this should never happen;
                    System.out.println("[WARNING] dmin-d>0");
                    dmin = fd;
                    reorder();
                }
            }
        }
    }

    @Override
    public E poll() {
        E e = bestList.get(0);
        return remove(e);
    }

    @Override
    public E peek() {
        if(bestList == null){
            System.out.println("GH_heap peek error");
        }
        if(bestList.get(0) == null){
            System.out.println("GH_heap peek error");
        }
        return bestList.get(0);
    }

    @Override
    public void update(E e) {
        throw new UnsupportedOperationException("Invalid operation for GH_heap, use remove and add instead");
    }

    @Override
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    public void updateF(E oldNode, E newNode) {
//        test();
/*        gh_node oldPos = new gh_node(oldG,oldH);
        ArrayList<E> oldList = tree.get(oldPos);
        E toRemove = null;
        for (E e:oldList) {
            if(NodePackedComparator.compare(updatedNode,e)==0){
                toRemove = e;
            }
        }
        if(toRemove != null){
            remove(toRemove);
        }
        else{
            System.out.println("can not remove");
        }
        add(updatedNode);*/
//        test();

    }



    private void reorder(){
        lowerBound = fmin * fminCoefficient + dmin * dminCoefficient;
        int buckets = tree.size();//for paper debug
        int nodes = 0;//for paper debug
        TreeMap<gh_node,ArrayList<E>> tempTree = new TreeMap<>(comparator);

        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            gh_node node = entry.getKey();
            ArrayList<E> list = entry.getValue();
            nodes +=list.size();
            it.remove();
            node.calcPotential();
            tempTree.put(node,list);
        }
        result.setExtras(buckets+"",nodes+"");//for paper debug
        tree = tempTree;
    }

    @Override
    public int size() {
        return GH_heapSize;
    }

    @Override
    public void clear() {
        countF.clear();
        countD.clear();
        tree.clear();
        bestNode = null;
        bestList.clear();
    }

    @Override
    public E remove(E e) {
//        testHat();
/*        if(e.getH() == 4.0 && e.getG() == 54 && e.getD() == 2){
            System.out.println("++++++++++++++++++++++");
            System.out.println(e);
            System.out.println(result.generated);
            System.out.println("++++++++++++++++++++++");
        }*/
        gh_node node = new gh_node(e);
        ArrayList<E> list = tree.get(node);

        if(list == null) {
            System.out.println("\ne:"+e);
            System.out.println("tree size:"+tree.size());
            System.out.println("list == null, can not remove");
        }
        if(list.isEmpty())
            System.out.println("list is empty, can not remove");
        list.remove(e);
        e.setIndex(this.key,-1);

        if(list.isEmpty()){
            tree.remove(node);
            if(this.comparator.compare(node,bestNode) <= 0){
                if(tree.isEmpty()){
                    bestNode = null;
                    bestList = null;
                }
                else {
                    bestNode = tree.firstKey();
                    bestList = tree.get(bestNode);
                }
            }
        }
        else{
            tree.put(node,list);
        }

        count_remove(e);
//        testHat();
        return e;
    }

    private void count_remove(E e){
        double f = e.getF();
        double fd = e.getD()+e.getDepth();
        GH_heapSize--;
        if(!countF.containsKey(f)){
            countF.put(f,0.0);
        }
        countF.put(f,countF.get(f)-1);
        if(countF.get(f)==0){
            countF.remove(f);
            if(!isOptimal) {//fmin might change/increase
                if (f == fmin && tree.size() > 0) {//find next lowest
                    fmin = Integer.MAX_VALUE;
                    Iterator it = countF.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        double key = (double) pair.getKey();
                        if (fmin >= key) {
                            fmin = key;
                        }
                    }
                    reorder();
                }
            }
        }
        if(!countD.containsKey(fd)){
            countD.put(fd,0.0);
        }
        countD.put(fd,countD.get(fd)-1);
        if(countD.get(fd)==0){
            countD.remove(fd);
            if(!isOptimal) {//fmin might change/increase
                if (fd == dmin && tree.size() > 0) {//find next lowest
                    dmin = Integer.MAX_VALUE;
                    Iterator it = countD.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        double key = (double) pair.getKey();
                        if (dmin >= key) {
                            dmin = key;
                        }
                    }
                    reorder();
                }
            }
        }
    }

    @Override
    public int getKey() {
        return this.key;
    }

    public void testHat(){
        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            ArrayList<E> list = entry.getValue();
            for(int i=list.size()-1 ; i>=0 ; i--){
                double Val = list.get(i).getF();
                if(Val < fmin){
                    System.out.println("test Failed! Val < fmin");
                }
                countF.put(Val,countF.get(Val)-1);
                if(countF.get(Val)<0){
                    System.out.println("test failed! countF.get("+Val+")<0");
                }
            }
        }

        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            ArrayList<E> list = entry.getValue();
            for(int i=list.size()-1 ; i>=0 ; i--){
                double Val = list.get(i).getF();
                if(Val < fmin){
                    System.out.println("test Failed! Val < fmin");
                }
                countF.put(Val,countF.get(Val)+list.size());
            }
        }
    }

/*    public void test(){
        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            ArrayList<E> list = entry.getValue();
            double Val = list.get(0).getF();
            if(Val < fmin){
                System.out.println("test Failed! Val < fmin");
            }
            countF.put(Val,countF.get(Val)-list.size());
            if(countF.get(Val)<0){
                System.out.println("test failed! countF.get("+Val+")<0");
            }
        }

        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            ArrayList<E> list = entry.getValue();
            double Val = list.get(0).getF();
            if(Val < fmin){
                System.out.println("test Failed! Val < fmin");
            }
            countF.put(Val,countF.get(Val)+list.size());
        }
    }*/


    private final class gh_node{
        double g;
        double h;
        double potential;

        double depth;
        double hHat;
        double d;
        double dHat;

        double cost;
        double dividor;

        public gh_node(E e) {
            this.g = e.getG();
            this.h = e.getH();
            this.depth = e.getDepth();
            this.hHat = e.getHhat();
            this.d = e.getD();
            this.dHat = e.getDhat();
            calcPotential();
        }

        public void calcPotential(){
            dividor = this.h * hCoefficient +
                    this.hHat * hHatCoefficient +
                    this.d * dCoefficient +
                    this.dHat * dHatCoefficient;

            lowerBound = fmin * fminCoefficient + dmin * dminCoefficient;
            cost = this.g * gCostCoefficient + this.depth * dCostCoefficient;
            if(dividor == 0){
                this.potential = Double.MAX_VALUE;
            }
            else{
                this.potential = (w*lowerBound-cost)/dividor;
                //this.potential = Math.pow(this.potential,1);
            }
        }

    }

    /**
     * The nodes comparator class
     */
    protected final class ghNodeComparator implements Comparator<gh_node> {

        @Override
        public int compare(final gh_node a, final gh_node b) {
            // First compare by potential (bigger is preferred), then by f (smaller is preferred), then by g (smaller is preferred)
            if (a.potential > b.potential) return -1;
            if (a.potential < b.potential) return 1;

            if (a.cost < b.cost) return -1;
            if (a.cost > b.cost) return 1;

            if (a.dividor < b.dividor) return -1;
            if (a.dividor > b.dividor) return 1;

            return 0;
        }
    }

}
