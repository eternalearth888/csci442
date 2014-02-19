#include "builtins.h"

using namespace std;


int com_ls(vector<string>& tokens) {
  // if no directory is given, use the local directory
  if (tokens.size() < 2) {
    tokens.push_back(".");
  }

  // open the directory
  DIR* dir = opendir(tokens[1].c_str());

  // catch an errors opening the directory
  if (!dir) {
    // print the error from the last system call with the given prefix
    perror("ls error: ");

    // return error
    return 1;
  }

  // output each entry in the directory
  for (dirent* current = readdir(dir); current; current = readdir(dir)) {
    cout << current->d_name << endl;
  }

  // return success
  return 0;
}


int com_cd(vector<string>& tokens) {
  // TODO: YOUR CODE GOES HERE
  	const char *pathname;
  	for (int i=1; i < tokens.size(); i++) {
		pathname=tokens[i].c_str();
	}
	
	if (!pathname) {
		perror("cd error: ");
	} else {
		chdir(pathname);
	}
 	return 0;
}


int com_pwd(vector<string>& tokens) {
  // TODO: YOUR CODE GOES HERE
  // HINT: you should implement the actual fetching of the current directory in
  // pwd(), since this information is also used for your prompt
	cout << pwd() << endl; 
	return 0;
}

int com_echo(vector<string>& tokens) {
  // TODO: YOUR CODE GOES HERE
	for(int i=1; i < tokens.size(); i++) {
		cout << tokens[i].c_str();
		if (i < tokens.size()-1) {
			cout << " ";
		}
	}
	cout << " " << endl;
	return 0;
}


int com_exit(vector<string>& tokens) {
  // TODO: YOUR CODE GOES HERE
	exit(0);
	return 0;
}


int com_history(vector<string>& tokens) {
  // TODO: YOUR CODE GOES HERE
  // HIST_ENTRY** returns a list of pointers, not a list of listsi
	HIST_ENTRY **the_list;
	
	the_list=history_list();

	for (int i=0; the_list[i]; i++) {
		cout << i+history_base << " " << the_list[i]->line << endl;
	}
	return 0;
}

string pwd() {
  // TODO: YOUR CODE GOES HERE
	size_t size;
	char *buff;
	char *ptr;
	if ((buff = (char *)malloc((size_t)size)) != NULL) {
		ptr=getcwd(buff, (size_t)size);
	} else {
		perror ("pwd error: ");
	}
	return ptr;
}
