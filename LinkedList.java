package HW2;

import sun.awt.image.ImageWatched;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList {

    private LinkedListNode head;
    private int maxLength;
    public AtomicInteger length;
    final ReentrantLock monitorLock = new ReentrantLock();
    final Condition isFull = monitorLock.newCondition();
    final Condition isEmpty = monitorLock.newCondition();

    public LinkedList(int length){
        this.head = null;
        this.maxLength = length;
        this.length.set(0);
    }

    public LinkedListNode getHead(){
        return(head);
    }

    public void setHead(LinkedListNode newHead){
        this.head = newHead;
    }

    public boolean isFull(){
        return(length.intValue() < maxLength);
    }

    public boolean isEmpty(){
        if(head == null){
            return(true);
        }
        return(false);
    }
}
