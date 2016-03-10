package org.cs4j.core.collections;

import org.cs4j.core.SearchDomain;
import org.cs4j.core.algorithms.DP;

import java.util.*;

/**
 * Created by Daniel on 08/01/2016.
 */
public class GH_heap<E extends SearchQueueElement> implements SearchQueue<E> {

    private final int key;
    private HashMap<Double, Integer> countF = new HashMap<>();
//    private BinHeap<E> heap;
    private TreeMap<gh_node,ArrayList<E>> tree;
    private double fmin;
    private double w;
    private gh_node bestNode;
    private ArrayList<E> bestList;
    private ghNodeComparator comparator;
    private int GH_heapSize;
    private SearchDomain domain;
    private Comparator<E> NodePackedComparator;
//    private boolean withFComparator;

    public GH_heap(int key, SearchDomain domain, double w, Comparator<E> NodePackedComparator) {
        this.domain = domain;
        this.fmin = domain.initialState().getH();
        this.key = key;
        this.comparator = new ghNodeComparator();
        this.tree = new TreeMap<>(this.comparator);
        this.w = w;
        this.NodePackedComparator = NodePackedComparator;
//        this.heap = new BinHeap<>(new FComparator(), this.key);
//        this.withFComparator = true;
    }

/*    public GH_heap(int key, SearchDomain domain, Comparator<E> Comparator) {
        this.fmin = domain.initialState().getH();
        this.key = key;
        this.heap = new BinHeap<>(Comparator, this.key);
        this.withFComparator = false;
    }*/

    public double getFmin(){
//        test();
        return fmin;
    }

    public void add(E e) {
//    test();
        countF_add(e.getF());

        gh_node node = new gh_node(e.getG(),e.getH());
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
//    test();
    }

    private void countF_add(double Val){
        GH_heapSize++;
        if(countF.containsKey(Val))
            countF.put(Val,countF.get(Val)+1);
        else {
            countF.put(Val, 1);
            if(tree.size() == 0){//tree is empty
                fmin = Val;
            }
            if(Val < fmin){//new lowest fmin ???
                fmin = Val;
                reorder();
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
        TreeMap<gh_node,ArrayList<E>> tempTree = new TreeMap<>(comparator);

        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            gh_node node = entry.getKey();
            ArrayList<E> list = entry.getValue();
            it.remove();
            node.calcPotential();
            tempTree.put(node,list);
        }
        tree = tempTree;
    }

    @Override
    public int size() {
        return GH_heapSize;
    }

    @Override
    public void clear() {
        countF.clear();
        tree.clear();
        bestNode = null;
        bestList.clear();
    }

    @Override
    public E remove(E e) {
        gh_node node = new gh_node(e.getG(),e.getH());
        ArrayList<E> list = tree.get(node);

        if(list.isEmpty()){
            System.out.println("Empty list, can not remove");
        }
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

        countF_remove(e.getF());
        return e;
    }

    private void countF_remove(double Val){
        GH_heapSize--;
        if(!countF.containsKey(Val)){
            countF.put(Val,0);
        }
        countF.put(Val,countF.get(Val)-1);
        if(countF.get(Val)==0){
            countF.remove(Val);
            if(Val==fmin && tree.size()>0){//find next lowest fmin
                fmin = Integer.MAX_VALUE;
                Iterator it = countF.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    double key = (double) pair.getKey();
                    if(fmin >= key){
                        fmin = key;
                    }
//                        System.out.println(pair.getKey() + " = " + pair.getValue());
//                        it.remove(); // avoids a ConcurrentModificationException
                }
                reorder();
//             System.out.println(fmin);
            }
        }
    }

    @Override
    public int getKey() {
        return this.key;
    }

/*    public void test(){
        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            ArrayList<E> list = entry.getValue();
            double Val = list.get(0).getF();
            if(Val < fmin){
                System.out.println("test Failed!");
            }
            countF.put(Val,countF.get(Val)-list.size());
            if(countF.get(Val)<0){
                System.out.println("test failed");
            }
        }

        for(Iterator<Map.Entry<gh_node,ArrayList<E>>> it = tree.entrySet().iterator(); it.hasNext();){
            Map.Entry<gh_node,ArrayList<E>> entry = it.next();
            ArrayList<E> list = entry.getValue();
            double Val = list.get(0).getF();
            if(Val < fmin){
                System.out.println("test Failed!");
            }
            countF.put(Val,countF.get(Val)+list.size());
        }
    }*/


    private final class gh_node{
        double g;
        double h;
        double potential;

        public gh_node(double g, double h) {
            this.g = g;
            this.h = h;
            calcPotential();
        }

        public void calcPotential(){
            this.potential = (w*fmin-this.g)/this.h;
        }

    }

    /**
     * The nodes comparator class
     */
    protected final class ghNodeComparator implements Comparator<gh_node> {

        @Override
        public int compare(final gh_node a, final gh_node b) {
            // First compare by potential (bigger is preferred), then by f (smaller is preferred), then by g (bigger is preferred)
            if (a.potential > b.potential) return -1;
            if (a.potential < b.potential) return 1;

            if (a.h < b.h) return -1;
            if (a.h > b.h) return 1;

            if (a.g > b.g) return -1;
            if (a.g < b.g) return 1;
            return 0;
        }
    }

}
