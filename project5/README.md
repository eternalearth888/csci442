#**csci442 | fall 2013 | camp | project05**

- Maria Deslis @eternalearth888
	- Approximate HOURS: 20 

---------------------------------------------------------------------------------
---------------------------------------------------------------------------------

#List of files & what each does
##(more information can be found in the JavaDocs of each file)
###/src/gui/

####SysGraph.java
SysGraph is a JComponent used to display a series of data points in a manner that closely represents a system monitor graph.

####SystemMonitorWindow.java
SystemMonitorWindow is a class that sets up and shows the GUI for the Project 5 system monitor

###/src/main

####SystemMonitor.java
Starts the schedulers and runs the gui

####Scheduler.java
A class that creates and starts the schedulers so that processes can begin; notifies the data extraction threads to collect information after the specified duration has passed

####Harvester.java
Extraction base class (abstract class) that uses polymorphism since all data extraction classes should have have the pause and collect methods in common for their data extraction threads

####MemHarvester.java
Extracts data from memory and sends it to the cpu

####CPU Harvester.java
Extracts data about the cpus and sends it to the gui

####ProcHarvester.java
Extracts data from the cpus sends it to the gui

---------------------------------------------------------------------------------
---------------------------------------------------------------------------------
#Any unusual/interesting features in your programs
N/A
