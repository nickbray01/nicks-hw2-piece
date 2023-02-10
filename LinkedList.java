package HW2;

import sun.awt.image.ImageWatched;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedList {

    private LinkedListNode head;
    private int maxLength;
    public AtomicInteger length = new AtomicInteger();
    final ReentrantLock monitorLock = new ReentrantLock();
    final Condition isFull = monitorLock.newCondition();
    final Condition isEmpty = monitorLock.newCondition();
    final Condition usingHead = monitorLock.newCondition();

    public LinkedList(int length){
        this.head = null;
        this.maxLength = length;
        this.length.set(0);
    }

    public LinkedListNode getHead(){
        // Locks the head of the node, need to unlock/handle in PQueue
        monitorLock.lock();
        try{
            while(head.lock.isLocked()){
                usingHead.await();
            }
        }
        catch(InterruptedException e){System.out.println("InterruptedException caught");}
        finally{
            if(head != null){
                head.lock.lock();
            }
            monitorLock.unlock();
            return(head);
        }
    }

    public boolean setHead(LinkedListNode newHead){
        boolean success = false;
        monitorLock.lock();
        try{
            if(this.head == null){
                this.head = newHead;
                success = true;
            }
        }
        finally {
            monitorLock.unlock();
            return(success);
        }
    }

    public boolean isFull() {
        // Block the isFull condition if queue is full
        monitorLock.lock();
        try {
            while (length.intValue() >= maxLength) {
                isFull.await();
            }
        }
        catch(InterruptedException e){
            System.out.println("Interrupted Exception Caught");
        }
        finally {
            monitorLock.unlock();
            return(length.intValue() >= maxLength);
        }
    }

    public boolean isEmpty(){
        //Block the isEmpty condition if queue is empty
        monitorLock.lock();
        try{
            while(head == null){
                isEmpty.await();
            }
        }
        catch(InterruptedException e){
            System.out.println("Interrupted Exception Caught");
        }
        finally{
            monitorLock.unlock();
            return(head == null);
        }
    }
}
