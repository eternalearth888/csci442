package main;

import gui.SystemMonitorWindow;

import java.util.ArrayList;

/**
 * Starts the schedulers and runs the gui
 * 
 * @author Maria Deslis <mdeslis@mines.edu>
 * 
 * This project was accomplished with the help of Anastasia Shpurik and Taylor Sallee
 *
 *
 */

public class SystemMonitor {

	/**
	 * cpuTime first and foremost contains the update time of .5seconds for the CPU schedule by default
	 */
	private static int cpuTime = 500;
	/**
	 * Instantiates the CPU scheduler
	 */
	private static Scheduler cpuSched;
	/**
	 * Instantiates the Proces scheduler
	 */
	private static Scheduler processSched;
	
	/**
	 * 
	 * setUpdateInterval changes the time interval that the CPUHarvester and MemHarvester are updated
	 * when the user selects a new update interval from the GUI
	 * 
	 * @param updateInterval passes in the value of the users GUI menu choice for the new delay update time 
	 */
	public static void setUpdateInterval(int updateInterval) {
		cpuTime = updateInterval;
		cpuSched.setDelay(cpuTime);
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Instantiates an object for the SystemMonitorWindow
		 */
		SystemMonitorWindow mySysMon = new SystemMonitorWindow();
		/**
		 * Instantiates an array list called cpu and proc of type Harvester
		 */
		ArrayList<Harvester> cpu, proc;

		/**
		 * Populates the cpu array list with the CPUHarvester and MemHarvester and pass in the SystemMonitorWindow
		 * Populate the proc array list with the ProcessHarvester and pass in the SystemMonitorWindow
		 * 
		 * This allows for the schedulers to know which data they are extracting from
		 */
		cpu = new ArrayList<Harvester>();
		cpu.add(new CPUHarvester(mySysMon));
		cpu.add(new MemHarvester(mySysMon));

		proc = new ArrayList<Harvester>();
		proc.add(new ProcHarvester(mySysMon));

		/**
		 * Sets the cpu scheduler to the current value of cpuTime and tells it to grab information from the cpu array list
		 */
		cpuSched = new Scheduler(cpuTime, cpu);
		/**
		 * Sets the process scheduler to the default value of 5 seconds and tells it to grab information from the proc array list
		 */
		processSched = new Scheduler(5000, proc);

		/**
		 * Start cpu/proc schedulers
		 */
		cpuSched.start();
		processSched.start();
	}
}
