#**csci442 | fall 2013 | camp | project04**
##**TEAM**

- Maria Deslis @eternalearth888
- Mykala Miller @mymiller

###What is the purpose of the page cache?
	the page cache (free_page_cache) is used for processes that require only 1 page of memory and have no memory restrictions. It is treated like a stack, and the most recently freed page is given to the process, making the allocation of this type of process much quicker than iterating through the bitmap for the first available page.

###Provide a narrative description of the findbit() function
	findbit() is a function that takes 5 integers.
		one represents the lower-bound page number the process can be allocated to (low)
		one representing where the code should start scanning memory (startscan)
		one representing the number of pages the process needs (pages)
		one representing the memory flags associated with the process (memflags) - this variable is unused in the function
		one representing the length of the hole that was found
	
		it iterates through all the possible pages bound by startscan and the lower bound, and:
			If the current page being looked at is not free, it will adjust the current page accordingly
			Otherwise, it will find the next-largest hole and return its starting address
			

###Provide pseudo-code of how you would implement worst-fit memory allocation (where worst-fit is putting the process into the largest hole possible)
	for all entries in free_pages_bitmap:
		if the last page was free:
			if the current page is not free:
				if the length of the most recent hole > the maximum length found so far:
				initial page index of largest hole = initial page index of the most recent hole
			else:
				increment the length of the current hole
		else if the current page is free:
			indicate that the last page is free for future iterations
			length of the current hole = 1
			total_free_pages++;
			starting index of this hole = current index
	
	ensure that the length of the largest hole is greater than the number of pages being requested
	
	initial page for the process = initial page of largest hole

###Hardest part of the assignment
	The hardest part of the assignment was proving that our change to first fir worked. It was tedious to look at all of our printout statements

###Additional comments
	N/A
