#include <cstdlib>
#include <fstream>
#include <iostream>
#include <map>
#include <sstream>
#include <string>
#include <vector>

#include <readline/readline.h>
#include <readline/history.h>

#include "builtins.h"

#include <sys/wait.h>
#include <unistd.h>

// Potentially useful #includes (either here or in builtins.h):
//   #include <dirent.h>
//   #include <errno.h>
   #include <fcntl.h>
//   #include <signal.h>
//   #include <sys/errno.h>
   #include <sys/param.h>
//   #include <sys/types.h>
   #include <sys/stat.h>

using namespace std;


// The characters that readline will use to delimit words
const char* const WORD_DELIMITERS = " \t\n\"\\'`@><=;|&{(";

// An external reference to the execution environment
extern char** environ;

// Define 'command' as a type for built-in commands
typedef int (*command)(vector<string>&);

// A mapping of internal commands to their corresponding functions
map<string, command> builtins;

// Variables local to the shell
map<string, string> localvars;

int execute_line(vector<string>& tokens, map<string, command>& builtins);

//count for background tasks
int backgroundCount;

//Grabs vector and turns it into an array of char
//Help form Matt Buland
char** vectChar(vector<string> vector_list) {
	char** cArray = (char **) malloc(sizeof(char*)*vector_list.size()+1);
	for (int i=0; i < vector_list.size(); i++) {
		cArray[i]=strdup(vector_list[i].c_str());
	}
	cArray[vector_list.size()]=(char*)0;
	return cArray;
}


int file_redirect(vector<string> tokens) {
	vector<string> command;
 	int return_value=0;	
	const char* fileName;
	int status;
	for (int i=1; i < tokens.size(); ++i) {
		if (tokens.at(i) == ">") {
			for(int j=0; j < i; ++j) {
				command.push_back(tokens.at(j));
			}

				fileName = (tokens.at(i+1).c_str());
				
				FILE * createCat = freopen(fileName,"w",stdout);
				
				return_value = execute_line(command, builtins);

				fclose(stdout);

		} else if (tokens.at(i) == ">>") {
			for(int j=0; j < i; ++j) {
				command.push_back(tokens.at(j));
			}

			fileName = (tokens.at(i+1).c_str());
			
			FILE * createCat = freopen(fileName,"a",stdout);
			
			return_value = execute_line(command, builtins);

			fclose(stdout);

		} else if (tokens.at(i) == "<") {
			for(int j=0; j < i; ++j) {
				command.push_back(tokens.at(j));
			}

			fileName = (tokens.at(i+1).c_str());
			
			FILE * createCat = freopen(fileName,"r",stdin);
			
			return_value = execute_line(command, builtins);

			fclose(stdin);
		}
	}

		return return_value;
}


int pipes(vector<string> tokens) {
	vector<string> tokensLeft;
	vector<string> tokensRight;
 	int return_value=-1;	
	int status;

	for (int i=0; i < tokens.size(); ++i) {
		if (tokens.at(i) == "|") {
			for (int j=0; j < i; ++j) {
				tokensLeft.push_back(tokens.at(j));
			}
			for (int j=i+1; j < tokens.size(); ++j) {
				tokensRight.push_back(tokens.at(j));
			}

			int pipefd[2];
			int child;

			pipe(pipefd);
			child = fork();

			if(child < 0) {
				perror("pipe fork");
				exit(1);
			}

			if (child == 0) {
				dup2(pipefd[1],1);
				close(pipefd[0]);
				
				return_value=execute_line(tokensLeft, builtins);
			} else {
				wait(&status);

				dup2(pipefd[0],0);
				close(pipefd[1]);
				
				return_value = execute_line(tokensRight, builtins);
			}
			exit(return_value);
		}
	}
	
	return return_value;
}


int background_command(vector<string>& tokens) {
	int return_value = -1;
	pid_t child = fork();

	if (child < 0) {
		perror("background command");
		exit(1);
	}
	
	if (child == 0) {
		setpgid(0,0);
		tokens.pop_back();
		return_value = execute_line(tokens, builtins);
	} else {
		backgroundCount++;
		cout << "[" << backgroundCount << "] " << child <<  endl;
	}
	return return_value;
}


// Handles external commands, redirects, and pipes.
int execute_external_command(vector<string> tokens) {
	vector<string> command;
	int status;
	int return_value = -1;
	
	for(int i=0; i < tokens.size(); i++) {
		command.push_back(tokens.at(i));
	}

	pid_t child = fork();	
	if (child == 0) {
		char** args = vectChar(tokens);
		return_value = execvp(tokens[0].c_str(), args);

		if(return_value != 0) {
			perror("execute_external_command");
			exit(1);
		}
		exit(child);
	} else {
		pid_t done = wait(&status);
	}

	return return_value;
}


// Return a string representing the prompt to display to the user. It needs to
// include the current working directory and should also use the return value to
// indicate the result (success or failure) of the last command.
string get_prompt(int return_value) {
	string prompt = pwd();
	if (return_value != -1) {
		return prompt + " :) $ ";
	} else {
		return prompt + " :( $ ";
	}
}

// Return one of the matches, or NULL if there are no more.
char* pop_match(vector<string>& matches) {
  if (matches.size() > 0) {
    const char* match = matches.back().c_str();

    // Delete the last element
    matches.pop_back();

    // We need to return a copy, because readline deallocates when done
   char* copy = (char*) malloc(strlen(match) + 1);
    strcpy(copy, match);

    return copy;
  }

  // No more matches
  return NULL;
}


// Generates environment variables for readline completion. This function will
// be called multiple times by readline and will return a single cstring each
// time.
char* environment_completion_generator(const char* text, int state) {
  // A list of all the matches;
  // Must be static because this function is called repeatedly
  static vector<string> matches;
  // If this is the first time called, construct the matches list with
  // all possible matches
	if (state == 0) {
		string str(text);
		for(int i = 0; environ[i] != NULL; i++) {
			string fullPath = "$"+((string)environ[i]).substr(0, ((string)environ[i]).find("="));

			if (fullPath.find(text) == 0) {
				matches.push_back(fullPath);
			}
		}
	}	

  // Return a single match (one for each time the function is called)
  return pop_match(matches);
}


// Generates commands for readline completion. This function will be called
// multiple times by readline and will return a single cstring each time.
char* command_completion_generator(const char* text, int state) {
  // A list of all the matches;
  // Must be static because this function is called repeatedly
  static vector<string> matches;

  // If this is the first time called, construct the matches list with
  // all possible matches
  if (state == 0) {
	  map<string, command>::iterator itr;
	  string str(text);

		for(itr = builtins.begin(); itr != builtins.end(); itr++) {
			if (itr->first.find(text) == 0)	{
				matches.push_back(itr->first);
			}
		}
	  
  }

  // Return a single match (one for each time the function is called)
  return pop_match(matches);
}


// This is the function we registered as rl_attempted_completion_function. It
// attempts to complete with a command, variable name, or filename.
char** word_completion(const char* text, int start, int end) {
  char** matches = NULL;

  if (start == 0) {
    rl_completion_append_character = ' ';
    matches = rl_completion_matches(text, command_completion_generator);
  } else if (text[0] == '$') {
    rl_completion_append_character = ' ';
    matches = rl_completion_matches(text, environment_completion_generator);
  } else {
    rl_completion_append_character = '\0';
    // We get directory matches for free (thanks, readline!)
  }

  return matches;
}


// Transform a C-style string into a C++ vector of string tokens, delimited by
// whitespace.
vector<string> tokenize(const char* line) {
  vector<string> tokens;
  string token;

  // istringstream allows us to treat the string like a stream
  istringstream token_stream(line);

  while (token_stream >> token) {
    tokens.push_back(token);
  }

  // Search for quotation marks, which are explicitly disallowed
  for (size_t i = 0; i < tokens.size(); i++) {

    if (tokens[i].find_first_of("\"'`") != string::npos) {
      cerr << "\", ', and ` characters are not allowed." << endl;

      tokens.clear();
    }
  }

  return tokens;
}

int check_command(vector<string>& tokens) {
	int command = 0;
	for (int i=1; i < tokens.size(); ++i) {
		if (tokens.at(i) == ">" || tokens.at(i) == ">>" || tokens.at(i) == "<") {
			command = 1;
		} else if (tokens.at(i) == "|") {
			command = 2;
		}
	}
	return command;
}


// Executes a line of input by either calling execute_external_command or
// directly invoking the built-in command.
int execute_line(vector<string>& tokens, map<string, command>& builtins) {
  int return_value = 0;
	int status;
	
	if (tokens.at(tokens.size()-1) == "&") {
		return background_command(tokens);
	}

	if (tokens.size() !=0) {
    	map<string, command>::iterator cmd = builtins.find(tokens[0]);

		if(check_command(tokens) == 0) {
				if (cmd == builtins.end()) {
					return_value = execute_external_command(tokens);
				} else {
					return_value = ((*cmd->second)(tokens));
				}
		} else if (check_command(tokens) == 1){
			pid_t child = fork();
			
			if (child==0) {
				return_value = file_redirect(tokens);
				exit(child);
			} else {
				pid_t done = wait(&status);
			}
		} else if (check_command(tokens) == 2) {
			pid_t child = fork();	
			if (child==0) {
				return_value = pipes(tokens);
				exit(child);
			} else {
				pid_t done = wait(&status);
			}

		}
	}
  return return_value;
}
// Substitutes any tokens that start with a $ with their appropriate value, or
// with an empty string if no match is found.
void variable_substitution(vector<string>& tokens) {
  vector<string>::iterator token;

  for (token = tokens.begin(); token != tokens.end(); ++token) {

    if (token->at(0) == '$') {
      string var_name = token->substr(1);

      if (getenv(var_name.c_str()) != NULL) {
        *token = getenv(var_name.c_str());
      } else if (localvars.find(var_name) != localvars.end()) {
        *token = localvars.find(var_name)->second;
      } else {
        *token = "";
      }
    }
  }
}


// Examines each token and sets an env variable for any that are in the form
// of key=value.
void local_variable_assignment(vector<string>& tokens) {
  vector<string>::iterator token = tokens.begin();

  while (token != tokens.end()) {
    string::size_type eq_pos = token->find("=");

    // If there is an equal sign in the token, assume the token is var=value
    if (eq_pos != string::npos) {
      string name = token->substr(0, eq_pos);
      string value = token->substr(eq_pos + 1);

      localvars[name] = value;

      token = tokens.erase(token);
    } else {
      ++token;
    }
  }
}


// The main program
int main() {
  // Populate the map of available built-in functions
  builtins["ls"] = &com_ls;
  builtins["cd"] = &com_cd;
  builtins["pwd"] = &com_pwd;
  builtins["echo"] = &com_echo;
  builtins["exit"] = &com_exit;
  builtins["history"] = &com_history;

  // Specify the characters that readline uses to delimit words
  rl_basic_word_break_characters = (char *) WORD_DELIMITERS;

  // Tell the completer that we want to try completion first
  rl_attempted_completion_function = word_completion;

  // The return value of the last command executed
  int return_value = 0;

  // Loop for multiple successive commands 
  while (true) {

    // Get the prompt to show, based on the return value of the last command
    string prompt = get_prompt(return_value);

    // Read a line of input from the user
    char* line = readline(prompt.c_str());

    // If the pointer is null, then an EOF has been received (ctrl-d)
    if (!line) {
      break;
    }

    // If the command is non-empty, attempt to execute it
    if (line[0]) {
		// !! commands
		char *expansion;
		int result;

		result = history_expand(line, &expansion);

		if (result == 1) {
			free (line);
			line = expansion;
		}

      // Add this command to readline's history
      add_history(line);

      // Break the raw input line into tokens
      vector<string> tokens = tokenize(line);

      // Handle local variable declarations
      local_variable_assignment(tokens);

      // Substitute variable references
      variable_substitution(tokens);

      // Execute the line
      return_value = execute_line(tokens, builtins);
    }

    // Free the memory for the input string
    free(line);
  }

	//!!

  return 0;
}
