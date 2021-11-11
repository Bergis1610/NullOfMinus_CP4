package compiler;
/**
 * This class encapsulates a basic grammar test.
 */

import java.io.IOException;
//ANTLR packages
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;

import lexparse.*;

import java.util.*;

public class CompilerTest{


    public static void main(String[] args){
        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

	int a;// this is a change to make sure I made the branch correctly
	String file;
	if(args.length == 0)
		file = "Program1.kc";
	else 
		file = args[0];
	
	file = "PROGRAM Program1 DECLARE INTEGER x INTEGER y INTEGER z BEGIN SET x := 10 SET y := 12 SET z := x + y PRINT z END";

        try{
        /*
            input = CharStreams.fromFileName(file);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser
       
            ParseTree tree = parser.file();  //set the start location of the parser
            //Trees.inspect(tree, parser);
            
            System.out.println(tree.toStringTree(parser));
            
            */
            
            input = CharStreams.fromFileName(args[0]);  //get the input
            lexer = new KnightCodeLexer(input); //create the lexer
            tokens = new CommonTokenStream(lexer); //create the token stream
            parser = new KnightCodeParser(tokens); //create the parser

	    //adding custom error listener
	    //parser.removeErrorListeners();
	    //parser.addErrorListener(new VerboseListener());

       
            ParseTree tree = parser.file();  //set the start location of the parser
             
           // System.out.println(tree.toStringTree(parser));
           //Trees.inspect(tree, parser);
            
        
	    Scanner scan = new Scanner(System.in);
	    System.out.print("Enter name for output class file: ");
	    String classFile = scan.next();
	    
	    System.out.print("Debug?  Y/N: ");
	    String debug = scan.next();
	    boolean debugFlag = false;
            if(debug.equals("Y"))
		debugFlag = true;		
	
	/*
	    myListenerTest listener = new myListenerTest(classFile, debugFlag);
	    ParseTreeWalker walker = new ParseTreeWalker();
	    walker.walk(listener, tree);
	    */
	    
	  myVisitor visitor = new myVisitor();
	   visitor.visit(tree);  
	  
	  
            
            
            
            
        
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }


    }




}//end class
