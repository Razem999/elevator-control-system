# SYSC3303

## File Names
The main package contains the source code for running the main program.
The main package consists of:
 - common package: classes that are commonly used between all other packages
 - elevator package: elevator related classes
 - floor package: floor related classes
 - scheduler package: scheduler related classes
The test package contains all the unit tests for the 4 packages in main

## Set up Instructions
### Source
For running the program using Eclipse, there are three files to run: Scheduler.java, Elevator.java and FloorManager.java  
You'll want to right click each file in the file explorer > "Run As" > "Java Application"  
Note: You need to run the files in this order: Scheduler -> Elevator -> FloorManager

### Unit Tests
For running all the JUnit Tests using Eclipse, right click the "src" folder and select "Run As" > "JUnit Test"