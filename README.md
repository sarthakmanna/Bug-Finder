# Bug-Finder



*This simple, easy-to-use GUI application takes two source codes, executes them against a common input and compares their stdout output.  
This project is mainly targeted for the competitive programmers, who find it boring and irritating to hunt down a small bug which stands as an obstruction between a WA and an AC.*  

**Prerequisite** : Java 8 or later must be preinstalled.  

**To run this project,**  
1. Download the jar file (JinglingJava.jar) from */dist/*
2. Open the folder containing the jar file from the terminal.
3. Type the following command :
    *java -jar "JinglingJava.jar"*
4. A GUI window should open. Select the source codes and the input file and hit "Run".  


**Features :**  
1. Simple and clean. Easy to use.
2. Currently supported languages : C++, Java and Python (2 & 3).
3. You can select a test case generating code to run your code(s) against random test cases.
4. You can test your code(s) against custom test cases.
5. You can also select an existing test file.
6. The generated output file can be directly submitted when required. (Several competitions like Facebook Hackercup, Google Kickstart etc. require you to submit the output file along with the source code).  


The Source Code can be found at */src/Bug_Finder.java*  


**1. When adding a C++ file,**  
a. The file extension must be '.cpp'. Failing this will result in a Compilation Error.  
b. Make sure your PC has 'g++' compiler preinstalled and recognises the command "g++ filename.cpp".  

**2. When adding a Java file,**  
a. The file extension must be '.java'.  
b. The filename must be same as the class name containing the 'main' method.  
Failing to follow any of these will result in a Compilation Error or a Runtime Error.  
c. Make sure your PC has 'JDK' compiler preinstalled and recognises the commands "javac" and "java".  

**3. When adding a Python 2 file,**  
a. The file extension should be '.py' or '.py2' for auto language detection. However, the language can be explicitly selected and hence, there is no hard-and-fast rule for naming the file.  
b. Make sure your PC has Python 2 preinstalled and recognises the command "python2".  

**4. When adding a Python 3 file,**  
a. The file extension should be '.py3' for auto language detection. However, the language can be explicitly selected and hence, there is no hard-and-fast rule for naming the file.  
b. Make sure your PC has Python 3 preinstalled and recognises the command "python3".  


**Bug Report :**  
If you find any bug, please report it at sarthakmannaofficial@gmail.com
