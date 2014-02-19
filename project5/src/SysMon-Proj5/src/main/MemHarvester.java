package main;

import gui.SystemMonitorWindow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Extracts data about memory
 * @author mdeslis
 *
 */
public class MemHarvester extends Harvester {


	private BufferedReader memReader;
	private Pattern nums = Pattern.compile("[0-9][0-9]*");

	private int memTotal, memFree, memAct, memInAct, swapTotal, swapFree, dirtyPage,
			writeBack;

	/**
	 * Calls SystemMonitorWindow so that the information collected in ProcHarvester can be displayed in the GUI
	 * @param gui passes in the GUI
	 */
	public MemHarvester(SystemMonitorWindow gui) {
		super(gui);
	}


	/**
	 * This collect function goes into the /proc/meminfo file and grabs the information for total memory, free memory, active memory, inactive memory, swap total, swap free, dirty pages,
	 * and write back
	 * 
	 * It then calculates the current RAM
	 * 
	 * And sends the information of the above values and RAM to the GUI to be displayed
	 */
	@Override
	public void collect() {
		try {
			String currentLine;

			memReader = new BufferedReader(new FileReader("/proc/meminfo"));

			while ((currentLine = memReader.readLine()) != null) {

				// System.out.println("CurrentLine: " + currentLine);
				/**
				 * Grab Memory Total
				 */
				Pattern memTotalP = Pattern.compile("MemTotal: *[0-9]*");
				Matcher memTotalM = memTotalP.matcher(currentLine);

				if (memTotalM.find()) {
					Matcher numM = nums.matcher(memTotalM.group());
					if (numM.find()) {
						// System.out.println("1 " + numM.group());
						memTotal = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Memory Free
				 */
				Pattern memFreeP = Pattern.compile("MemFree: *[0-9]*");
				Matcher memFreeM = memFreeP.matcher(currentLine);

				if (memFreeM.find()) {
					Matcher numM = nums.matcher(memFreeM.group());
					if (numM.find()) {
						// System.out.println("2 " + numM.group());
						memFree = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Memory Active
				 */
				Pattern memActP = Pattern.compile("Active: *[0-9]*");
				Matcher memActM = memActP.matcher(currentLine);

				if (memActM.find()) {
					Matcher numM = nums.matcher(memActM.group());
					if (numM.find()) {
						// System.out.println("3 " + numM.group());
						memAct = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Memory Inactive
				 */
				Pattern memInActP = Pattern.compile("Inactive: *[0-9]*");
				Matcher memInActM = memInActP.matcher(currentLine);

				if (memInActM.find()) {
					Matcher numM = nums.matcher(memInActM.group());
					if (numM.find()) {
						// System.out.println("4 " + numM.group());
						memInAct = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Swap Total
				 * 
				 */
				Pattern swapTotalP = Pattern.compile("SwapTotal: *[0-9]*");
				Matcher swapTotalM = swapTotalP.matcher(currentLine);

				if (swapTotalM.find()) {
					Matcher numM = nums.matcher(swapTotalM.group());
					if (numM.find()) {
						// System.out.println("5 " + numM.group());
						swapTotal = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Swap Free
				 */
				Pattern swapFreeP = Pattern.compile("SwapFree: *[0-9]*");
				Matcher swapFreeM = swapFreeP.matcher(currentLine);

				if (swapFreeM.find()) {
					Matcher numM = nums.matcher(swapFreeM.group());
					if (numM.find()) {
						// System.out.println("6 " + numM.group());
						swapFree = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Dirty Pages
				 */
				Pattern dirtyPageP = Pattern.compile("Dirty: *[0-9]*");
				Matcher dirtyPageM = dirtyPageP.matcher(currentLine);

				if (dirtyPageM.find()) {
					Matcher numM = nums.matcher(dirtyPageM.group());
					if (numM.find()) {
						// System.out.println("7 " + numM.group());
						dirtyPage = Integer.parseInt(numM.group());
					}
				}

				/**
				 * Grab Writeback
				 */
				Pattern writeBackP = Pattern.compile("Writeback: *[0-9*]");
				Matcher writeBackM = writeBackP.matcher(currentLine);

				if (writeBackM.find()) {
					Matcher numM = nums.matcher(writeBackM.group());
					if (numM.find()) {
						// System.out.println("8 " + numM.group());
						writeBack = Integer.parseInt(numM.group());
					}
				}
//				System.out.println("MEMTOTAL: " + memTotal);
//				System.out.println("MEMFREE: " + memFree);
//				
				/**
				 * Calculates RAM based on current values of memTotal and memFree
				 */
				double RAM = (((double) memTotal - (double) memFree)/(double)memTotal)*100;
//				System.out.println("RAM: " + RAM);
				
				/**
				 * Sends value of RAM to be displayed in the GUI/Graph
				 * 
				 * Sends the values of memTotal, memFree, memAct, memInAct, swapTotal, swapFree, dirtyPage, writeBack to the GUI
				 */
				synchronized(gui) {
					gui.getCPUGraph().addDataPoint(numCores, (int) RAM);
					gui.updateMemoryInfo(memTotal, memFree, memAct, memInAct,
						swapTotal, swapFree, dirtyPage, writeBack);
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (memReader != null) {
					memReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
