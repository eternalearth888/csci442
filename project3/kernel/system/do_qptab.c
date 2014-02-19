#include "kernel/system.h"
#include <minix/endpoint.h>
#include "kernel/proc.h"
#include <minix/syslib.h>

int do_qptab(struct proc * caller, message *m_ptr){
struct proc * process;
int i;

for(i=0;i<NR_PROCS+NR_TASKS;i++){
	if((proc[i].p_endpoint == m_ptr->m1_i1) && proc_is_runnable((struct proc *) &proc[i])) {
		process = &proc[i];
		dequeue(process);
		enqueue(process);
	}
}
return(OK);
}
