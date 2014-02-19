package main;

import gui.SystemMonitorWindow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * Extracts data about processes
 * @author mdeslis
 *
 */
public class ProcHarvester extends Harvester {

	/**
	 * Instantiates a File called rir
	 */
	private File dir;
	/**
	 * Instantiates a BufferedReader called proc
	 */
	private BufferedReader proc;
	/**
	 * Instantiates a String called name, pid, state, numThreads, vol and nonVol
	 */
	private String name;
	private String pid;
	private String state;
	private String numThreads;
	private String vol;
	private String nonVol;

	/**
	 * Calls SystemMonitorWindow so that the information collected in
	 * ProcHarvester can be displayed in the GUI
	 * 
	 * @param gui
	 *            passes in the GUI
	 */
	public ProcHarvester(SystemMonitorWindow gui) {
		super(gui);
	}

	/**
	 * Reads all the directories in /proc, if the directory name is a valid integer, it will go into the status file of that directory and grab
	 * the process name, pid, state, number of threads, voluntary and non volunatary context switches
	 * It will then store those values into a string array and send them to the gui to be displayed
	 * It will update the gui/list every 5 seconds
	 */
	@Override
	public void collect() {
		dir = new File("/proc");
		String[] names = dir.list();
		String[] procInfo = new String[6];
		String status = "";

		try {
			/**
			 * First remove all current rows from the list/gui
			 */
			synchronized (gui) {
				gui.removeAllRowsFromProcList();
			}
			/**
			 * Grab all the directories in /proc, if the directory name is a
			 * valid integer, then continue with grabbing information from the
			 * status file of that directory
			 */
			for (int i = 0; i < names.length; i++) {
				if (intDirectory(names[i])) {
					status = "/proc/" + names[i].toString() + "/status";
					// System.out.println(status);

					String currentLine;
					proc = new BufferedReader(new FileReader(status));
					while ((currentLine = proc.readLine()) != null) {
						/**
						 * Grab Name
						 */
						if (currentLine.startsWith("Name")) {
							// System.out.println("Current Line " +
							// currentLine);
							name = currentLine.substring("Name:".length() + 1,
									currentLine.length()).trim();
							// System.out.println("Name: " + name);
							procInfo[0] = name;
						}/**
						 * Grab Pid
						 */
						else if (currentLine.startsWith("Pid")) {
							// System.out.println("Current Line " +
							// currentLine);
							pid = currentLine.substring("Pid:".length() + 1,
									currentLine.length()).trim();
							// System.out.println("Pid: " + pid);
							procInfo[1] = pid;
						}/**
						 * Grab State
						 */
						else if (currentLine.startsWith("State")) {
							// System.out.println("Current Line " +
							// currentLine);
							state = currentLine
									.substring("State:".length() + 1,
											currentLine.length()).trim();
							// System.out.println("State: " + state);
							procInfo[2] = state;
						}/**
						 * Grab Number of Threads
						 */
						else if (currentLine.startsWith("Threads")) {
							// System.out.println("Current Line Threads " +
							// currentLine);
							numThreads = currentLine.substring(
									"Threads:".length() + 1,
									currentLine.length()).trim();
							// System.out.println("Threads: " + numThreads);
							procInfo[3] = numThreads;
						}/**
						 * Grab Voluntary Context Switches
						 */
						else if (currentLine
								.startsWith("voluntary_ctxt_switches")) {
							// System.out.println("Current Line " +
							// currentLine);
							vol = currentLine.substring(
									"voluntary_ctxt_switches:".length() + 1,
									currentLine.length()).trim();
							// System.out.println("Vol: " + vol);
							procInfo[4] = vol;
						}/**
						 * Grab Non Voluntary context switches
						 */
						else if (currentLine
								.startsWith("nonvoluntary_ctxt_switches")) {
							// System.out.println("Current Line " +
							// currentLine);
							nonVol = currentLine.substring(
									"nonvoluntary_ctxt_switches:".length() + 1,
									currentLine.length()).trim();
							// System.out.println("Non: " + nonVol);
							procInfo[5] = nonVol;
						}
					}
					// System.out.println(Arrays.toString(procInfo));
					//
					synchronized (gui) {
						gui.addRowToProcList(procInfo);
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (proc != null) {
				try {
					proc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Checks to see if the directory name is a valid integer value
	 * @param directory passes the directory name into the function
	 * @return
	 */
	public boolean intDirectory(String directory) {
		// Check that the directory is an int
		if (!isInteger(directory)) {
			return false;
		}
		return true;
	}
	/**
	 * Turns the directory name into an integer value
	 * @param s passes in the string to the function to be parsed into an integer value
	 * @return
	 */
	public boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
