#pragma once
#include <cstdlib>
#include <iostream>
#include <string>
#include <vector>
#include <dirent.h>
#include <stdio.h>
#include <readline/history.h>
#include <readline/readline.h>

using std::vector;
using std::string;


// Lists all the files in the specified directory. If not given an argument,
// the current working directory is used instead.
int com_ls(vector<string>& tokens);


// Changes the current working directory to that specified by the given
// argument.
int com_cd(vector<string>& tokens);


// Displays the current working directory.
int com_pwd(vector<string>& tokens);

// Prints all arguments to the terminal.
int com_echo(vector<string>& tokens);


// Exits the program.
int com_exit(vector<string>& tokens);


// Displays all previously entered commands, as well as their associated line
// numbers in history.
int com_history(vector<string>& tokens);


// Returns the current working directory.
string pwd();
