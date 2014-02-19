#Maria Deslis #
## Project 1 | csci442 | Fall 2013 ##

In this repository you will find the following:

1. Executables

	i. _alamode-fetch_

		This script connects to the machine(s) given and fetches the following information from them:
			* Hostname
			* # Users
			* Sleeping processes
			* Running processes
			* Stopped processes
			* Zombie processes
			* Idle processes
			* Uninterruptable processes
			* Avg. Load 1 min
			* Avg. Load 5 min
			* Avg. Load 15 min
		There are two ways to gather this information:
			1. Gather data from the machine given in the command line
				ex: ./alamode-fetch -n <machine>
			2. Gather date from multiple machiens given a file using the command line
				ex: ./alamode-fetch -f <file>

	ii. _alamode-publish_

		This script aggregates all the data that is piped from alamode-fetch and creates an html file in the current directory or the directory of your choice.
		There are two flags with alamode-publish
			1. -s <path to input directory>
				Specifies the directory to look in for the hostfiles containing the data saved by alamode-fetch
				ex: ./alamode-fetch <flag> <flag argument> | ./alamode-publish -s <directory>
			2. -d <path to output directory>
				Specifies the directory where the html file will be created. If the directory does not exist, then it will automatically create it in the current directory, provided you have the correct permissions.
				ex: ./alamode-fetch <flag> <flag argument> | ./alamode-publish -d <directory>
	iii. _alamode-invoke_

			This is a small executable that will only one run command:
			./alamode-fetch -f hosts.alamode | ./alamode-publish -d public_html
			Allowing the user to not have to constantly retype the command over and over and recieve quick feedback on all available machines

2. Files

	i. _README.md_

		This README file
	ii. _hosts.alamode_

		This is a file that contains all the machines in the alamode lab. It allows for _alamode-fetch_ to loop through all the machines when using -f command, rather than have the user type/search every single machine they wish to view

3. Directories

	i. *public_html*

		This is the default directory that alamode-invoke uses, as stated in the project1.pdf for storing the html file created by alamode-publish
