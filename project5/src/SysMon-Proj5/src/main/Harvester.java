package main;

import gui.SystemMonitorWindow;
/**
 * Extraction base class (abstract class) that uses polymorphism since all data extraction classes should have have the pause 
 * and collect methods in common for their data extraction threads
 * 
 * @author mdeslis
 *
 */
public abstract class Harvester implements Runnable {

	/**
	 * Instantiates a gui value to be used in all Harvester extended classes
	 */
	protected SystemMonitorWindow gui;
	/**
	 * Grabs the number of cores available at runtime so that the number of cores displayed is dynamic
	 */
	protected int numCores = Runtime.getRuntime().availableProcessors();

	/**
	 * Passes in the current gui from SystemMonitorWindow into Harvester so that the correct data can 
	 * be displayed on the gui
	 * @param gui passes in gui value
	 */
	public Harvester(SystemMonitorWindow gui) {
		this.gui = gui;
	}

	/**
	 * all Harvester extended classes will use to collect their data. But since all their data collect and processes are different,
	 * it is an abstract function
	 */
	public abstract void collect();

	/**
	 * all Harvester extended classes will use to call the collect method and wait until they are called again
	 */
	public synchronized void run() {
		while (true) {
			collect();
			pause();
		}
	}

	/**
	 * tells the program to wait until it is ready to notified (paused)
	 */
	protected void pause() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
