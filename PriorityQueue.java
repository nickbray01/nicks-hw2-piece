package HW2;
// nab2992
// EID 2

//todo: need to make it so when getFirst is called, LinkedList.length is decremented\
//todo: testing lol

public class PriorityQueue {
	private LinkedList queue;
	private int maxSize;

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
		this.queue = new LinkedList(maxSize);
		this.maxSize = maxSize;
	}

	public int add(String name, int priority) {
		// Adds the name with its priority to this queue.
		// Returns the current position in the list where the name was inserted;
		// otherwise, returns -1 if the name is already present in the list.
		// This method blocks when the list is full.

		// check Linked List to see if the queue is full
		// (if full, thread blocked)
		if(!queue.isFull()){
			// check if name is already in the queue somewhere
			if (search(name) != -1) {
				return (-1);
			}
			LinkedListNode n = queue.getHead();
			LinkedListNode next;
			int idx = 0;
			// case where HEAD is null
			if (n == null) {
				LinkedListNode newNode = new LinkedListNode(name, priority);
				if(queue.setHead(newNode)){
					queue.isEmpty.signal();
					queue.length.incrementAndGet();
					return(0);
				}
			}
			n.lock.lock();
			// case where priority[NEW] > priority[HEAD]
			try {
				if (priority > n.getPriority()) {
					// final check that queue is not full before adding new value
					if(queue.isFull()){
						LinkedListNode newNode = new LinkedListNode(name, priority);
						newNode.next = queue.getHead();
						if(queue.setHead(newNode)){
							queue.length.incrementAndGet();
							return(0);
						}
					}
				}
			}
			finally {
				n.lock.unlock();
			}

			while (n != null) {
				n.lock.lock();
				try {
					next = n.next;
					// case where priority[i] > priority[NEW] and next is null
					if (next == null) {
						// final check that queue is not full before adding new value
						if(!queue.isFull()) {
							LinkedListNode newNode = new LinkedListNode(name, priority);
							n.next = newNode;
							queue.length.incrementAndGet();
							return (idx+1);
						}
					}
					next.lock.lock();
					try{
						// case where priority[i] > priority[NEW] > priority[j]
						if (priority > next.getPriority()) {
							// final check that queue is not full before adding new value
							if(!queue.isFull()) {
								LinkedListNode newNode = new LinkedListNode(name, priority);
								n.next = newNode;
								newNode.next = next;
								queue.length.incrementAndGet();
								return(idx+1);
							}
						}
					}
					finally{
						next.lock.unlock();
					}
				}
				finally {
					LinkedListNode nextN = n.next;
					n.lock.unlock();
					n = nextN;
					idx++;
				}
			}
			return(idx);
		}
		return(-1);
	}

	public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
		LinkedListNode n = queue.getHead();
		int idx = 0;
		while(n != null){
			n.lock.lock();
			try{
				if(n.getName().equals(name)){
					n.lock.unlock();
					return(idx);
				}
				idx++;
			}
			finally{
				LinkedListNode next = n.next;
				n.lock.unlock();
				n = next;
			}
		}
		return(-1);
	}

	public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
		LinkedListNode head;
		if(!queue.isEmpty()) {
			head = queue.getHead();
			head.lock.lock();
			try{
				LinkedListNode next = head.next;
				queue.setHead(next);
			}
			finally{
				return(head.getName());
			}
		}
		else{
			return("");
		}
	}

	public int getSize(){return(this.maxSize);}
}