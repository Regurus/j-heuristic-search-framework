package org.cs4j.core.collections;

import org.cs4j.core.SearchDomain;
import org.cs4j.core.algorithms.DP;

import java.util.*;

/**
 * Created by Daniel on 08/01/2016.
 */
public class BinHeapF<E extends SearchQueueElement> implements SearchQueue<E> {

    private final int key;
    private HashMap<Double, Integer> countF = new HashMap<>();
    private BinHeap<E> heap;
    private double fmin;
    private boolean withFComparator;

    public BinHeapF(int key, SearchDomain domain) {
        this.fmin = domain.initialState().getH();
        this.key = key;
        this.heap = new BinHeap<>(new FComparator(), this.key);
        this.withFComparator = true;
    }

    public BinHeapF(int key, SearchDomain domain, Comparator<E> Comparator) {
        this.fmin = domain.initialState().getH();
        this.key = key;
        this.heap = new BinHeap<>(Comparator, this.key);
        this.withFComparator = false;
    }

    public double getFmin(){
//        test();
        return fmin;
    }

    public void add(E e) {
        countF_add(e.getF());
        heap.add(e);
//        test();
    }

    private void countF_add(double Val){
        if(countF.containsKey(Val))
            countF.put(Val,countF.get(Val)+1);
        else {
            countF.put(Val, 1);
            if(Val < fmin || heap.size() == 0){//heap is empty or new lowest fmin
                fmin = Val;
            }
        }
    }

    @Override
    public E poll() {
        E e = heap.peek();
        return remove(e);
    }

    @Override
    public E peek() {
        return heap.peek();
    }

    public void heapify(E e) {
        heap.update(e);
    }

    @Override
    public void update(E e) {
        throw new UnsupportedOperationException("Invalid operation for BinheapF, use updateF or heapify instead.");
    }

    public void updateF(E updatedNode, double oldF) {
        countF_add(updatedNode.getF());
        countF_remove(oldF);
        heap.update(updatedNode);
    }

    public boolean isEmpty() {
        return this.heap.peek() == null;
    }

    @Override
    public int size() {
        return heap.size();
    }

    @Override
    public void clear() {
        heap.clear();
        countF.clear();
    }

    @Override
    public E remove(E e) {
//        test();
        heap.remove(e);
        countF_remove(e.getF());
//        test();
        return e;
    }

    private void countF_remove(double Val){
        if(!countF.containsKey(Val)){
            countF.put(Val,0);
        }
        countF.put(Val,countF.get(Val)-1);
        if(countF.get(Val)==0){
            countF.remove(Val);
            if(Val==fmin && heap.size()>0){//find next lowest fmin
                if(withFComparator){//is sorted by F value
                    fmin = heap.peek().getF();
                }
                else{
                    Iterator it = countF.entrySet().iterator();
                    fmin = Integer.MAX_VALUE;
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        double key = (double) pair.getKey();
                        if(fmin >= key){
                            fmin = key;
                        }
//                        System.out.println(pair.getKey() + " = " + pair.getValue());
//                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
//                System.out.println(fmin);
            }
        }
    }

    @Override
    public int getKey() {
        return this.key;
    }

    public int getFminCount(){
        return countF.get(fmin);
    }

    public E getElementAt(int i) {
        return heap.getElementAt(i);
    }

    protected final class FComparator implements Comparator<E> {
        @Override
        public int compare(E o1, E o2) {
            if(o1.getF() < o2.getF()) return -1;
            if(o1.getF() > o2.getF()) return 1;
            return 0;
        }
    }

    public String getCountFString(){
/*        String toPrint="";
        SortedSet<Double> keys = new TreeSet<>(countF.keySet());
        for (Double key : keys) {
            int value = countF.get(key);
            toPrint += key +"->"+value+", ";
            // do something
        }*/
        return countF.toString();
//        System.out.println(toPrint);
    }

    /*public void test(){
        BinHeap list = heap;
//        System.out.println(heap.size());
        Iterator it = countF.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
//            System.out.println(pair.getKey() + " = " + pair.getValue());
        }

        for(int i=0; i < list.size(); i++){
            E e = (E) list.getElementAt(i);
            double Val = e.getF();
            if(Val < fmin){
                System.out.println("test Failed!");
            }
            countF.put(Val,countF.get(Val)-1);
            if(countF.get(Val)<0){
                System.out.println("test failed");
            }
        }

        it = countF.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
//           System.out.println(pair.getKey() + " = " + pair.getValue());
        }

        for(int i=0; i < list.size(); i++){
            E e = (E) list.getElementAt(i);
            double Val = e.getF();
            if(countF.containsKey(Val))
                countF.put(Val,countF.get(Val)+1);
            else countF.put(Val,1);
        }
    }*/

}
