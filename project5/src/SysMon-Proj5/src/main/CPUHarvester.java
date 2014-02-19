package main;

import gui.SystemMonitorWindow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Extracts data about the cpus
 * @author Maria Deslis
 *
 */
public class CPUHarvester extends Harvester {

	/**
	 * Instantiates a BufferedReader called cpuReader for future use
	 */
	private BufferedReader cpuReader;

	/**
	 * Calls SystemMonitorWindow so that the information collected in CPUHarvester can be displayed in the GUI
	 * @param gui passes in the GUI
	 */
	public CPUHarvester(SystemMonitorWindow gui) {
		super(gui);
	}

	/**
	 *  Collects information on userMode/userIdle/systemMode/idleTask twice, with a short delay between the polling time of when the system is first booted and then after it sleeps for 250ms
	 *  It puts the information for the first polling time into an ArrayList of type integer, which then we put into another ArrayList of type integer. So that our first array list collects the four
	 *  pieces of information above, and then our second array list grabs all four pieces of information per valid /proc/stat file.
	 *  A similar process is repeated for the information that occurs after the thread sleeps for 250ms, however, it is stored in it's own seperate array.
	 *  
	 *  Then the two array lists of type array list of type integer are sent to another function for calculating the cpu usage percentage
	 */
	@Override
	public void collect() {
		/**
		 * Creates the two array list of array list of type integer where our final information will be stored and sent to be calculated for cpu usage percentage
		 */
		ArrayList<ArrayList<Integer>> firstRound = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> secondRound = new ArrayList<ArrayList<Integer>>();

		try {
			/**
			 * Instantiates a string called currentline which will be used to read the current line that the or loop is on for file /proc/stat
			 */
			String currentLine;

			/**
			 * the cpuReader opens the file /proc/stat
			 */
			cpuReader = new BufferedReader(new FileReader("/proc/stat"));

			// SKIPS FIRST LINE ON PURPOSE
			currentLine = cpuReader.readLine();

			/**
			 * Loops through /proc/stat and grabs the first four values of any cpu which are the information we are looking for
			 */
			while ((currentLine = cpuReader.readLine()) != null) {
				ArrayList<Integer> grabFour = new ArrayList<Integer>();
				if (currentLine.startsWith("cpu")) {
					String[] tokens = currentLine.split(" ");
					grabFour.add(Integer.parseInt(tokens[1]));
					grabFour.add(Integer.parseInt(tokens[2]));
					grabFour.add(Integer.parseInt(tokens[3]));
					grabFour.add(Integer.parseInt(tokens[4]));
					/**
					 * Now we add those four bits of information and add them to another array list so that we can call one element in that array list and immediately get all the information
					 * regarding one cpu
					 */
					firstRound.add(grabFour);
				}
			}
			// for (int i = 0; i < firstRound.size(); i++) {
			// System.out.println("First Round: " + firstRound.get(i));
			// }

			/**
			 * This is where we force the Thread to sleep for 250ms so that we can move on to grabbing our second bit of information
			 */
			try {
				// System.out.println("SLEEPING");
				// essentially a wait
				Thread.sleep(250);
				// System.out.println("DONE SLEEPING");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/**
			 * Open up a new /proc/stat since we have slept for 250ms
			 * repeat above process only changing which array list we put in our final information
			 */
			cpuReader = new BufferedReader(new FileReader("/proc/stat"));

			// SKIPS FIRST LINE ON PURPOSE
			currentLine = cpuReader.readLine();

			while ((currentLine = cpuReader.readLine()) != null) {
				ArrayList<Integer> grabFour = new ArrayList<Integer>();
				if (currentLine.startsWith("cpu")) {
					String[] tokens = currentLine.split(" ");
					grabFour.add(Integer.parseInt(tokens[1]));
					grabFour.add(Integer.parseInt(tokens[2]));
					grabFour.add(Integer.parseInt(tokens[3]));
					grabFour.add(Integer.parseInt(tokens[4]));
					secondRound.add(grabFour);
				}
			}
			// for (int i = 0; i < secondRound.size(); i++) {
			// System.out.println("Second Round: " + secondRound.get(i));
			// }
			// System.out.println("\n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/**
			 * Finally always close the file
			 */
			try {
				if (cpuReader != null) {
					cpuReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Pass our two array lists into this function so that we can caluclate our CPU Percentage
		 */
		CPUPercentage(firstRound, secondRound);
	}

	/**
	 * Calculates cpu usage percentage based on the information in the first and second array list 
	 * @param first
	 * @param second
	 */
	public void CPUPercentage(ArrayList<ArrayList<Integer>> first,
			ArrayList<ArrayList<Integer>> second) {
		int userTime1 = 0, userTime2 = 0, userIdleTime1 = 0, userIdleTime2 = 0, systemMode1 = 0, systemMode2 = 0, idleTask1 = 0, idleTask2 = 0;
		for (int i = 0; i < numCores; i++) {
			userTime1 = first.get(i).get(0);
			userIdleTime1 = first.get(i).get(1);
			systemMode1 = first.get(i).get(2);
			idleTask1 = first.get(i).get(3);

			userTime2 = second.get(i).get(0);
			userIdleTime2 = second.get(i).get(1);
			systemMode2 = second.get(i).get(2);
			idleTask2 = second.get(i).get(3);

			// System.out.println("UserTime1: " + userTime1);
			// System.out.println("UserTime2: " + userTime2);
			// System.out.println("UserIdleTime1: " + userIdleTime1);
			// System.out.println("UserIdleTime2: " + userIdleTime2);
			// System.out.println("SystemMode1: " + systemMode1);
			// System.out.println("SystemMode2: " + systemMode2);
			// System.out.println("IdleTask1: " + idleTask1);
			// System.out.println("IdleTask2: " + idleTask2);

			double upper = (userTime2 - userTime1)
					+ (userIdleTime2 - userIdleTime1)
					+ (systemMode2 - systemMode1);
			// System.out.println("Upper: " + upper);
			double lower = (upper + (idleTask2 - idleTask1));
			// System.out.println("Lower: " + lower);
			int calc = (int) ((upper / lower) * 100);
			// System.out.println("Calc: " + calc);

			/**
			 * Send our data to the GUI and repaint
			 */
			synchronized(gui) {
				gui.getCPUGraph().addDataPoint(i, calc);
				gui.getCPUGraph().repaint();
			}
		}
	}
}
