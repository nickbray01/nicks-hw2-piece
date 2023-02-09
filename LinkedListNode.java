package HW2;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LinkedListNode {
    private int priority;
    private String name;
    public LinkedListNode next;
    public ReentrantLock lock;

    public LinkedListNode(String name, int priority){
        this.priority = priority;
        this.name = name;
        this.next = null;
    }

    public String getName(){
        return(this.name);
    }

    public int getPriority(){
        return(this.priority);
    }

}
