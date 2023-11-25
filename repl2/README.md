# Project Details:

## Name: Repl

## Repo Link: https://github.com/cs0320-f23/repl-dhan25-acodjoe.git

## Contributors: David Han (dhan25) and Ariana Codjoe (acodjoe)

## Time speant: 30 hours combined 

# Design Choices / Overview:

At a high level, this project integrated our front end applications from previous 
projects and our backend applications to produce a web page that had the following 
functionalities: loading csv files, viewing csv files, searching csv files, 
providing requests to the census API, registering new commands through a front end 
webpage that is designed to be accessible. For the backend components, we used Java 
classes to build a server unit that takes in requests from the user (provided by the
frontend), and handles the requests according to functionality. For broadband 
requests, the backend will connect to the census API and will serialize the results
in a JSON format. For csv related commands, the backend will look in a data folder 
and call specific handlers to perform different requests.


For the frontend we used CSS and HTML for the basic structure and
TypeScript with React to organize components and process events (the commands being
entered). In simple words, our REPL program reads in the user's commands and this 
is passed in the REPLInput class, where specific functions are called via a commandMap
in order to execute the inpoutted comamnd with its specified arguments.

More specifically, we identified 7 components that would be useful for our purposes.
Below, we will give a brief explanation of each component and into function to the
overall code.

App.tsx: acts as the highest level component, defining the header of the project.

ControlledInput.tsx: handles user inputs into our website as well as sets up the “Enter
a Command” box

REPL.tsx: defines the function REPL and all of the relevant useState hooks/ shared states
    that we will need to access during the processing of commands (i.e history list, parsing
    information, the data source used).

REPLHistory: 1.) defines mostly the HTML return type for the history functionality such 
    that on verbose, the entire map appears and in brief only the most recent command appears 
    2.) was modified to make accessible such that upon using chrome's screen reader, a user can have commands read

REPLInput.tsx:  1.) harbors the majority of functionality for this sprint, where each repl
    input is handled with their corresponding behavior 2.) harbors the register function that 
    allows users to register new commands
 
enums.ts: contains the enums for modes verbose and brief

FunctionList.tsx: defines functions that can be accessed through the repl 
    (i.e harbor functionality for view, load, search, broadband)

MockedData.tsx: contains mocked functions mainly used for the purpose of functionality
Keyboard.tsx: contains logic relevant to keyboard shortcuts

Main.tsx: creates the root

# Error / Bugs:
During testing, we noticed that sometimes tests would pass and sometimes 
tests would fail. In order to circumvent this, we created multiple testing files 
(as described below), which fixed the majority of our issues. However, within 
the end2end suite (designed to test full integration) we were unable to fully 
resolve this issue. We believe it has to do with the server needing to restart 
and the testing suite executing too quickly in order to keep up (as the tests 
status changes despite the code remaining intact).

We found that the backend would not reset when we refreshed the front-end to start
a new session. This would lead to files being loaded before we specifically loaded 
files in each new session. We tried to implement some kind of useState to specify
when a user starts a new session to refresh the backend as well, but our current
implementation limited our use of useStates to do that.

# Testing
Per the guidelines in the sprint, we expect that certain functionalities in 
the frontend and backend have been previously tested. Thus, we are testing 
features that are relevant to this sprint specifically.
Testing the addition of the register command located in /tests/register.spec.ts:
[X] command not in list -> returns an error in history
[X] successful register of load
[X] registering 2 commands back to back + calling first command
[X] registering same command twice -> still registered
[X] register a command + call it -> works 
[X] don't register a command + call -> doesn't work
[X] don't register a command + call + register + call-> doesn't work then works 
[X] checks to see if all commands 

Testing the addition of keyboard shortcuts located in /tests/keyboard-commands.spec.ts:
[X] key up functionality
[X] key down functionality
[X] key up and key down tested together after back to back calls
[X] enter functionality
[X] enter functionality using arrow up and arrow down (no clicks accessibility test)

Testing functionality end2end (backend successfully integrates with 
    frontend) located in tests/end2end.spec.ts: 
[X] load one file + search + load a new file + search
[X] load one file + view
[X] broadband

# How To:

In order to run our program, you must cd into the repl2 directory. Then,type 
npm start and click on the link outputted in the terminal. Upon clicking the link,
 you should be greeted with a screen that invites you to enter a command. If you 
 would like backend integration, that is the abilities with csv files or the census api, 
 then you must also navigate to our server class (found in the backend folder under 
 src/main/server) and run that class. This command will set up the local server 
 port needed in order to successfully run our program. Here are the following 
 command options. If you choose to enter something that is not listed below, 
 you will receive an error.

1.. register: this command takes in one argument and it must be a command name. 
You call this command to register the command into our platform. You must register 
commands before calling them or you will receive an error. Valuable commands are 
as follows: load, view, search, broadband (note: there are also mocked version 
of these but these are not recommended for usage as they were only established 
for testing purposes)

2.. load: this command takes in one argument and it is the file that you would 
    like to load. The file must be present in the data folder or it will return in error

3.. View: this command takes in no arguments and allow you to view the currently 
    loaded table

4.. search: this command takes in one argument and an optional second 
    argument. The first argument indicates that value you'd like to 
    search and the optional second argument
    contains the column you would like to search in

5.. broadband: this takes in two arguments a state and a county where there 
    is an underscore in between any instances of multiple words (i.e to search 
    Orange County you must provide Orange_County)

6.. mode: this command take in one argument and it must be either verbose or brief

7.. registry: this command takes in no arguments and returns all the registered commands 


In addition to the commands, there are also a couple of relevant keyboard shortcuts! See below:

ArrowUp: will give you the previous command run

ArrowDown: if used ArrowUp, this will return you from the previous command

Tab: allows you to access different components of the webpage without use of a mousepad

Enter: allows you to enter what is currently typed in the submit screen.
