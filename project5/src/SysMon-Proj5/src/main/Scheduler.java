package main;

import java.util.ArrayList;

/**
 * A class that creates and starts the schedulers so that processes can begin; notifies the data extraction threads to collect information after 
 * the specified duration has passed
 * @author mdeslis
 *
 */

public class Scheduler extends Thread {

	/**
	 * Instantiates the delay variable
	 */
	private int delay;
	/**
	 * Instantiates an array list of type harvest called tasklist
	 */
	private ArrayList<Harvester> taskList;

	/**
	 * Sets the delay data
	 * @param delay passes in the value of delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
	 * Passes in the value of delay and 
	 * @param delay
	 * @param taskList
	 */
	public Scheduler(int delay, ArrayList<Harvester> taskList) {
		this.delay = delay;
		this.taskList = taskList;
	}

	/**
	 * This function does a wait but the wait only lasts for the time that delay is given
	 */
	private synchronized void delay() {
		// Here we acquire the lock for our current instance of our scheduler
		// and wait for the specified timeout
		try {
			wait(delay);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * This starts and notifies our threads so that they work together
	 */
	public synchronized void run() {
		for (Harvester h: taskList) {
			Thread t = new Thread(h);
			t.start();
		}
		
		while (true) {
			// Wait for the specified timeout
			delay();

			// Dispatch each harvester to collect data/update gui.

			// For those unfamiliar with Java, this is the equivalent of
			// a for-each loop... For each Harvester h in Tasks...do...
			for (Harvester h : taskList) {
				synchronized (h) {
					h.notifyAll();
				}
			}
		}
	}
}
