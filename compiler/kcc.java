package compiler;

import java.io.IOException;
//ANTLR packages
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;
import lexparse.*;
import java.util.*;



/**
 * kcc.java
 * This is the front end compiler class that calls upon a listener file that does everything regarding the compiling
 * @author Emil Bjørlykke Berglund
 * @author Adam Fischer
 * @author Denys Ladden
 * @version 2.0
 * Programming project 4
 * CS322 - Compiler Construction
 * Fall 2021
 **/





public class kcc{


 /**
  * This is the main method where the different components of the antlr4 generated lexer and parsers are first made, 
  * then it creates a parse tree and calls upon the listener to insert the appropriate code as it enters and exits nodes.
  * The class is command-line based and should be input with the name of knightcode file and the name of the output file, 
  * however, it is made with a fail safe that will default to compile program1.kc and output a file called output1. 
  *
  **/

    public static void main(String[] args){
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

	String file;
	String output;
	if(args.length == 2){
	
		file = args[0];
		output = args[1];
		
	} else if(args.length == 1){ 
	
		file = args[0];
		output = "output/output1";
	
	} else {
	
		file = "tests/program1.kc";
		output = "output/output1";
	
	}
        try{
        
        
            input = CharStreams.fromFileName(file);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser
        
       
            //set the start location of the parser
            ParseTree tree = parser.file(); 
            
            
            //Walk the tree using the myListener2 class
            myListenerKccVersion listener = new myListenerKccVersion(output);
	    ParseTreeWalker walker = new ParseTreeWalker();
	    
	    walker.walk(listener, tree);
            
        
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }


    }

}//end class
