package HW2;
// nab2992
// EID 2

//todo: need to make it so when add is called, LinkedList.length is incremented
//todo: need to make it so when getFirst is called, LinkedList.length is decremented
//todo: if dev gets overly tedious, make function for a blocking isFull/isEmpty
//todo: testing lol

public class PriorityQueue {
	private LinkedList queue;
	private int maxSize;

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
		this.queue = new LinkedList(maxSize);
		this.maxSize = maxSize;
	}

	public int add(String name, int priority) throws InterruptedException {
		// Adds the name with its priority to this queue.
		// Returns the current position in the list where the name was inserted;
		// otherwise, returns -1 if the name is already present in the list.
		// This method blocks when the list is full.

		// check Linked List to see if the queue is full (block)
		queue.monitorLock.lock();
		try {
			while (queue.isFull()) {
				queue.isFull.await();
			}
		} finally {
			queue.monitorLock.unlock();
			// check if name is already in the queue somewhere
			if (search(name) != -1) {
				return (-1);
			}
			LinkedListNode n = queue.getHead();
			LinkedListNode next;
			int idx = 0;
			// case where HEAD is null
			if (n == null) {
				queue.monitorLock.lock();
				boolean emptyQueueSet = false;
				try {
					if (queue.isEmpty()) {
						LinkedListNode newNode = new LinkedListNode(name, priority);
						queue.setHead(newNode);
						emptyQueueSet = true;
					}
				} finally {
					queue.monitorLock.unlock();
					if (emptyQueueSet) {
						return (0);
					}
				}
			}
			n.lock.lock();
			// case where priority[NEW] > priority[HEAD]
			try {
				if (priority > n.getPriority()) {
					// final check that queue is not full before adding new value
					queue.monitorLock.lock();
					boolean stillNotFull = true;
					try {
						if (queue.isFull()) {
							stillNotFull = false;
							throw (new InterruptedException());
						}
						else{
							queue.isEmpty.signal();
						}
					} finally {
						queue.monitorLock.unlock();
						if (stillNotFull) {
							LinkedListNode newNode = new LinkedListNode(name, priority);
							newNode.next = queue.getHead();
							queue.setHead(newNode);
							return (0);
						}

					}
				}
			} catch (InterruptedException e) {
				System.out.println("Interrupted Exception occurred.");
			} finally {
				n.lock.unlock();

			}

			while (n != null) {
				n.lock.lock();
				try {
					next = n.next;
					// case where priority[i] > priority[NEW] and next is null
					if (next == null) {
						// final check that queue is not full before adding new value
						queue.monitorLock.lock();
						boolean stillNotFull = true;
						try {
							if (queue.isFull()) {
								stillNotFull = false;
								throw (new InterruptedException());
							}
						} finally {
							queue.monitorLock.unlock();
							if (stillNotFull) {
								LinkedListNode newNode = new LinkedListNode(name, priority);
								n.next = newNode;
								return (idx);
							}
						}
					}
					// case where priority[i] > priority[NEW] > priority[j]
					if (priority > next.getPriority()) {
						// final check that queue is not full before adding new value
						queue.monitorLock.lock();
						boolean stillNotFull = true;
						try {
							if (queue.isFull()) {
								stillNotFull = false;
								throw (new InterruptedException());
							}
						} finally {
							queue.monitorLock.unlock();
							if (stillNotFull) {
								LinkedListNode newNode = new LinkedListNode(name, priority);
								n.next = newNode;
								newNode.next = next;
							}
						}
					}
					n.lock.unlock();
					return (idx);
				} catch (InterruptedException e) {
					System.out.println("Interrupted Exception occurred.");
				} finally {
					idx++;
				}
			}
			return(idx);
		}
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

	public String getFirst() throws InterruptedException {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
		LinkedListNode head;
		queue.monitorLock.lock();
		try {
			while (queue.isEmpty()) {
				queue.isEmpty.await();
			}
		} finally {
			head = queue.getHead();
			head.lock.lock();
			try{
				LinkedListNode next = head.next;
				if(next == null){
					queue.setHead(null);
				}
				else{
					queue.setHead(next);
				}
			}
			finally{
				return(head.getName());
			}
		}
	}
}