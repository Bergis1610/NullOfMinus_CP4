# NullOfMinus_CP4

This project encapsulates the directory structure required for the final project in CS322.
The goal of this project was to use ANTLR to create a lexer and parser for a grammar file containing the language 'KnightCode.' From there, we received a parse tree, for which we created a Listener in order to use ASM to translate the parse tree into java bytecode. As a result, the project allows for KnightCode to compile to the JVM.

In order to operate the compiler, you must first refer to the build.xml file which contains the commands to use ANTLR. Specifically, you must run "ant build-grammar", "ant compile-grammar", and "ant compile" in order to prepare the compiler for operation. From there, another command must be run in order to use the file kcc.java from the compiler directory to compile a specifc KnightCode program from the tests directory and send the .class file with its bytecode to the output directory.

(This particular setup requires the user either to have ant downloaded on their computer, or to manually compile all the grammar files and the compiler files. It also requires that the user has the appropriate antlr4 files necessary to build up the lexparse folder.)

In summary, an example of a command to run program1 from the tests directory would be as follows: "java compiler/kcc tests/program1.kc output/ouput1". This must be run from the NullOfMinus_CP4 folder, meaning your directory must just be the main folder.
From there, the command "java output/Program1" would run the java bytecode created by kcc.java. 

Emil Bjørlykke Berglund  
Denys Ladden   
Adam Fischer   
