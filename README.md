# Natural-Language-Processing-Project-1

Very easy to run.
Specify “RUNALL” class as the real main class to run both first and second scripts in a row with the data passage handled.

Program will ask the directory in which authors and their files are located.

Expected directory structure is the following:

input_path/authorName/fileX

# Natural-Language-Processing-Project-2

This is the second project of the course Natural Language Processing in Bogazici University during the semester Spring 2016
Implementation of Viterbi Algorithm on the dataset Metubank in java

## How to run the code ##
Assuming all files are compiled using: 
    `javac <filename>.java`

### Main.java ###
--------------
This program expects your to send two parameters as specified in the project description.
First one is the path of the training data
Second one is the option of pos tags Either postag|cpostag
This program will train itself using the training data and will create 3 output files in the same directory that are  provided below

    posNamesToPosNamesProbablities.ser
    posNamesToWordPossibilities.ser
    posType.ser

If you dont provide any parameters then the program will crash.

###  Test.java  ###
--------------
This program expects your to send two parameters as specified in the project description.
First one is the path of the test data
Second one is the path of the output file
This program will test the test data using the pre-produced model by reading the .ser files explained above, afterwards it will produce an output file in which the guesses are written in a readable manner. 

