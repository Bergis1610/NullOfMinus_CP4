package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants
import org.objectweb.asm.Label;
import static org.objectweb.asm.Opcodes.*;
import lexparse.*; //classes for lexer parser
import java.util.*;


/**
 * myListenerKccVersion.java
 * This is the main class that runs all the bytecode operations, hashmap manipulation as well as writing the output file
 * @author Emil Bjørlykke Berglund
 * @author Adam Fischer
 * @author Denys Ladden
 * @version 10.2
 * Programming project 4
 * CS322 - Compiler Construction
 * Fall 2021
 **/



public class myListenerKccVersion extends KnightCodeBaseListener{

	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status
	
	
//Here are general stuff that I made

/*
 *
 * The variable object is the second component to the SymbolTable hash map. It contains strings to determine the datatype and value of variables in KnightCode as well as an integer 
 * to determine the variable's location in memory and a boolean to keep track of whether or not athe valurable has been set by the user.
 *
*/
public class variable{

	public String variableType = "";
	public String value = "";
	public int memLoc = -1;
	public boolean valueSet = false;

	/*
	 *This is the constructor for the variable object
	*/
	public variable(String variableType, String value){
		this.variableType = variableType;
		this.value = value;
	
	}// end variable constructor
	

	/*
	 * This is the empty argument constructor
	*/
	public variable(){
		variableType = "";
		value = "";
	}// end empty argument constructor


}// end class variable

/*
 * The stacker class is simply a custom made int stack
 */ 

public class stacker{

	public int[]stack = new int[30];
	public int head = 0;


	public stacker(){
		
	}
	
	

}


	public HashMap<String, variable> SymbolTable = new HashMap<String, variable>();// the SymbolTable hash map is vital to the compiler as it keeps track of all variables
	public static final String INT = "INTEGER";
	public static final String STR = "STRING";
	
	
	
	public variable currvar;
	public variable extravar;
	public variable var1;
	public variable var2;
	
	public String outputString;
	public String key;
	public String key2;
	public String keyID;
	public String id;
	public String genNum;
	public String genIntStr = "  ";
	public String genString;
	public String op1 = "";
	public String op2 = "";
	public String operation = "  ";
	public String arithmeticOperation = "  ";
	public String compString;
	public String decOp1;
	public String decOp2;
	public String decCompSymbol;
	public String prev;
	public String then1;
	public String then2;
	public String else1;
	public String else2;
	
	public int decComparison;
	public int decOperator1;
	public int decOperator2;
	public int operator1;
	public int operator2;
	public int num;
	public int outputInt;
	public int count;
	public int decCount;
	public int skipCount = 0;
	public int tempInt;
	
	
	public boolean exit = false;
	public boolean printString;
	public boolean operationDone;
	public boolean genBool;
	public boolean genPrint;
	public boolean expre;
	public boolean printTwice;	
	public int memoryCounter = 1;
	
	
	/*
	 * This method prints the symbol table
	 */
	public void printHashMap(HashMap<String,variable> map){
	
		Object[]keys = map.keySet().toArray();
		String val;
		int mem;
		boolean set;
		
		for(int i = 0; i < keys.length; i++){
			System.out.print(keys[i]);
			System.out.print(": " + map.get(keys[i]).variableType); 
			val = map.get(keys[i]).value;
			mem = map.get(keys[i]).memLoc;
			set = map.get(keys[i]).valueSet;
			System.out.println(", " + val + ", " + mem + ", " + set);
			
		} 	
		
	}// end printHashMap
	
	/*
	 * This method determines whether or not a variable is a string, making this process faster with no duplicate code when this is required
	 */
	public boolean isString(variable var){
		
		if(var.variableType.equals(STR))
			return true;
		return false;
	}// end isString
	
	/*
	 * The following methods are stack methods, that print, push, pop or peek the stack
	 */
	public void printStack(stacker s){
	
		for(int i = s.head;i>= 0;i--){
			System.out.print(s.stack[i]+",");
		}	
	
	}// end printStack
	
	public int push(stacker s, int value){
	
		if(s.head == 29){
			return -1;
		} else {	
			s.head++;
			s.stack[s.head] = value;
			return value;
		}
	
	}//end push
	
	public int pop(stacker s){
	
		int value = 0;
		if(s.head == 0){
			return value;
		} else {	
			
			value = s.stack[s.head];
			s.stack[s.head] = 0;
			s.head--;
			return value;
		}
	
	}//end pop
	
	
	public int peek(stacker s){
	
		return s.stack[s.head];
		
	
	}//end peek

	
//End general stuff	
	
	
	
	
	
	//constructor 
	public myListenerKccVersion(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor
	
	
	//Constructor
	public myListenerKccVersion(String programName){
	       
		this.programName = programName;
		debug = false;

	}//end constructor


	

	/*
	 * The setupClass method creates the class writer for all of the ASM in the listener so that the program can write to java bytecode
	 */
	public void setupClass(){
	
		if(exit)
			return;
		
		//Set up the classwriter
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        	cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,this.programName, null, "java/lang/Object",null);
	
		//Use local MethodVisitor to create the constructor for the object
		MethodVisitor mv=cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
       	mv.visitCode();
        	mv.visitVarInsn(Opcodes.ALOAD, 0); //load the first local variable: this
        	mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        	mv.visitInsn(Opcodes.RETURN);
        	mv.visitMaxs(1,1);
        	mv.visitEnd();
       	
		//Use global MethodVisitor to write bytecode according to entries in the parsetree	
	 	mainVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,  "main", "([Ljava/lang/String;)V", null, null);
        	mainVisitor.visitCode(); 
        	
        	
        	
        	//mainVisitor.visitJumpInsn(GOTO, returnl);
        	
        	
        	

	}//end setupClass
	
		
	/*
	 * This method is the counterpart to setupClass. It closes the class writer and outputs the resulting bytecode to the specified class file
	 */
	public void closeClass(){
	
		if(exit)
			return;
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		
		//mainVisitor.visitLabel(returnl);
		
		
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(0, 0);
		mainVisitor.visitEnd();

		cw.visitEnd();

        	byte[] b = cw.toByteArray();



        	Utilities.writeFile(b,this.programName+".class");
        
        	System.out.println("Successfully wrote to file!");

	}//end closeClass





/**
 * Enter different rules context
 *
 */


	/*
	 * Enters the file and sets up the standard stuff for the byte code class
	 */
	@Override
	public void enterFile(KnightCodeParser.FileContext ctx){
	
		System.out.println("Enter program rule for first time");
		setupClass();
	}
	
	/*
	 * Exits the file and wraps up the standard stuff for the byte code class
	 */
	@Override
	public void exitFile(KnightCodeParser.FileContext ctx){
		if(exit)
			return;
			
			
		System.out.println("Attempting to exit file");	

		
		closeClass();
		System.out.println("Leaving program rule. . .");

	}


	/*
	 * Enters the declare node
	 */
	@Override 
	public void enterDeclare(KnightCodeParser.DeclareContext ctx){
		if(exit)
			return;
			
		System.out.println("Enter declare");
		
		//enter = false;
		count = ctx.getChildCount();
		
	}// end enterDeclare
	
	/*
	 * Exits the declare node
	 */
	@Override 
	public void exitDeclare(KnightCodeParser.DeclareContext ctx){
		if(exit)
			return;

		//enter = true;
		System.out.println("Exit declare");
	}// end exitDeclare

	/*
	 * Enters the variable node
	 * This method is what adds entries to the SymbolTable by entering the datatype into a variable object and adding that value to the hash map with the variable's id as the key
	 */
	@Override 
	public void enterVariable(KnightCodeParser.VariableContext ctx){
		if(exit)
			return;
		
		
		System.out.println("Enter variable");
		
		variable var = new variable();
		
		String identifier = ctx.getChild(1).getText();
		var.variableType = ctx.getChild(0).getText();
		var.memLoc = memoryCounter;
		
		
		
		SymbolTable.put(identifier, var);

		memoryCounter++;
		
	
	}// end enterVariable

	/*
	 * Exits the variable node
	 */
	@Override 
	public void exitVariable(KnightCodeParser.VariableContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Exit variable");
	}// end exitVariable
	
	/* 
	 * Enters identifier
	 */
	@Override 
	public void enterIdentifier(KnightCodeParser.IdentifierContext ctx){
		if(exit)
			return;
	
	}// end enterIdentifier
	
	/*
	 * Exits identifier
	 */
	@Override 
	public void exitIdentifier(KnightCodeParser.IdentifierContext ctx){ 
		if(exit)
			return;
	
	}// end exitIdentifier
	
	/* 
	 * Enters and exits varType, no real functionality here, the vartype is instead checked in the setVar or declareVariable methods
	 */
	@Override public void enterVartype(KnightCodeParser.VartypeContext ctx) { }
	@Override public void exitVartype(KnightCodeParser.VartypeContext ctx) { }
	
	
	
	
	/*
	 * Enters body and counts the statements
	 */
	@Override 
	public void enterBody(KnightCodeParser.BodyContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter body!");
		
		count = ctx.getChildCount();

	}// end enterBody

	/*
	 * Exits the body
	 */ 
	@Override 
	public void exitBody(KnightCodeParser.BodyContext ctx){ 
		if(exit)
			return;
	
		printHashMap(SymbolTable);
	
		System.out.println("Exit body!");
	}// end exitBody
	
	
	/*
	 * Enters and exits stats, this method is always followed by a more specific enter and exit which contains all the functionality
	 */
	@Override public void enterStat(KnightCodeParser.StatContext ctx) { }
	@Override public void exitStat(KnightCodeParser.StatContext ctx) { }
	
	
	public int operationCount = 0;


	/*
	 * While enterVariable creates entries in the SymbolTable, Setvar actually defines the values of these entries. 
	 * Additionally, this checks for errors when the datatype is incorrect or when the variable was not declared earlier
	 */
	@Override 
	public void enterSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter setvar");
		operationCount = 0;
		genIntStr = "";
		
		if(ctx.getChild(1) != null)
			key = ctx.getChild(1).getText();
			

		if(SymbolTable.containsKey(key)){
			currvar = SymbolTable.get(key);
		} else {
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Identifier: " + key + " was not declared");
			exit = true;
			return;
		
		}
		
		if(isString(currvar)){
		
			if(ctx.getChild(3).getChildCount() != 0){
			
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				
				System.out.println("Variable being set to " + key + " is not a string!");
				exit = true;
				return;
			
			}
		
			
			genIntStr = ctx.getChild(3).getText();

		}
	
	}// end enterSetvar
	

	/*
	 * This method sends the actual value of the variable to a specific location in memory with ASM and keeps track of this location within the SymbolTable
	 */
	@Override 
	public void exitSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
	

		currvar.value = genIntStr;
		
		
		
		//ASM bytecode stuff
		int store = currvar.memLoc;
		
		genBool = isString(currvar);
			
		if(genBool){
	
			mainVisitor.visitLdcInsn(currvar.value);
			mainVisitor.visitVarInsn(ASTORE,store);
		} else {
			
			mainVisitor.visitVarInsn(ISTORE,store);
		}
		currvar.valueSet = true;
		SymbolTable.put(key, currvar);
		
    	    
    	    	operation = "";
    	    	genIntStr = "";
    	    	
    	    	int tempBoolElse = peek(decElseStacker);
		if(tempBoolElse > 0){
			int newUsage = peek(decIfStacker);			
				if(newUsage == 1){

					Label temper;
					Label tempEnd;
			 		int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
					switch(currentUsage){
						case 1: {
							tempEnd = endDecLab0;
							temper = startOfElse0;						
							break;
						}
						case 2: {
							tempEnd = endDecLab1;
							temper = startOfElse1;
							break;
						}
						case 3: {
							tempEnd = endDecLab2;
							temper = startOfElse2;						
							break;
						}
						case 4: {
							tempEnd = endDecLab3;
							temper = startOfElse3;
							break;
						}
						case 5: {
							tempEnd = endDecLab4;
							temper = startOfElse4;
							break;
						}
						case 6: {						
							tempEnd = endDecLab5;
							temper = startOfElse5;
							break;
						}
						case 7: { 						
							tempEnd = endDecLab6;
							temper = startOfElse6;
							break;
					
						}
						case 8: {
							tempEnd = endDecLab7;
							temper = startOfElse7;
							break;
					
						}
						case 9: { 
							tempEnd = endDecLab8;
							temper = startOfElse8;
							break;
						
						}
						case 10: { 
							tempEnd = endDecLab9;
							temper = startOfElse9;
							break;
					
						}
						default: {
						
							System.out.println("\n\n------------------------------------------");
							System.out.println("COMPILER ERROR");
							System.out.println("------------------------------------------");
						
							System.out.println("jump label failure for if-else statement in print!");
							
							exit = true;
							return;
						}	
							
					}
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					pop(decElseStacker);
					pop(decIfStacker);	
				} else if(newUsage > 1){

					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
				}
		}
		System.out.println("Exit setvar");
	}// end exitSetvar
	
	public String enterAndExitNumber;
	
	@Override 
	public void enterNumber(KnightCodeParser.NumberContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter Number");
		enterAndExitNumber = ctx.getText();
		genIntStr += enterAndExitNumber;			
	}//End enterNumber
	
	@Override 
	public void exitNumber(KnightCodeParser.NumberContext ctx){ 
		if(exit)
			return;
	
		num = Integer.valueOf(enterAndExitNumber);	
		mainVisitor.visitIntInsn(SIPUSH, num);
		System.out.println("Exit Number");
	}//end exitNumber
	
	
	
	
	@Override 
	public void enterId(KnightCodeParser.IdContext ctx){ 
		if(exit)
			return;
		System.out.println("enter ID");
		keyID = ctx.getText();
		
		if(SymbolTable.containsKey(keyID)){
			var1 = SymbolTable.get(keyID);
			op1 = keyID;
	
			operator1 = var1.memLoc;
					
			if(var1.variableType.equalsIgnoreCase(STR) && operationCount > 0){
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				
				System.out.println("Cannot perform arithmetic operations on a String");

				exit = true;
				return;
			
			}
			
			if(isString(var1)){
				mainVisitor.visitIntInsn(ALOAD, operator1);
			} else {
				mainVisitor.visitIntInsn(ILOAD, operator1);
			}
		} else {
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("ID: " + keyID + " does not exist!");
			exit = true;
			return;
		
		}
		
		genIntStr += op1;
		
	}// end enterId

	@Override 
	public void exitId(KnightCodeParser.IdContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Exit ID");
		
		genIntStr += arithmeticOperation.charAt(0);
		if(arithmeticOperation.length() != 0)
			arithmeticOperation = arithmeticOperation.substring(1);

		if(printTwice){
			genIntStr += arithmeticOperation.charAt(0);
			if(arithmeticOperation.length() != 0)
				arithmeticOperation = arithmeticOperation.substring(1);
			printTwice = false;	
		}	
	}// end exitId
	
	
	
	
	/**
	 * Parenthesis
	 *
	 */
	@Override 
	public void enterParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter parenthesis");
		
		
	
		genIntStr += "(";
		arithmeticOperation = ")" + arithmeticOperation;
		
	
	}// end enterParenthesis

	@Override 
	public void exitParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		//skipCount = 1;
		
		genIntStr += arithmeticOperation.charAt(0);
		if(arithmeticOperation.length() != 0)
			arithmeticOperation = arithmeticOperation.substring(1);
		
		System.out.println("Exit parenthesis");
	}// end exitParenthesis
	
	
	/**
	 * Addition
	 *
	 */
	@Override 
	public void enterAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter addition");
		operationCount++;
		
		arithmeticOperation = "+" + arithmeticOperation;
	}// end enterAddition
	
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;

			
		operationCount--;	

		//ASM stuff
		mainVisitor.visitInsn(IADD);
                      	
		System.out.println("Exit addition");
	}// end exitAddition
	
	/**
	 * Multiplication
	 *
	 */
	@Override 
	public void enterMultiplication(KnightCodeParser.MultiplicationContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Enter multiplication");
		operationCount++;
		arithmeticOperation = "*" + arithmeticOperation;
	
	}// end enterMultiplication

	@Override 
	public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	
		
		//ASM stuff
		mainVisitor.visitInsn(IMUL);
            		
		System.out.println("Exit multiplication");
	
	}// end exitMultiplication
	
	/**
	 * Division
	 *
	 */
	@Override 
	public void enterDivision(KnightCodeParser.DivisionContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Enter division");
		operationCount++;
		arithmeticOperation = "/"+arithmeticOperation;
	
	}// end enterDivision

	@Override 
	public void exitDivision(KnightCodeParser.DivisionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	
		
		//ASM stuff
		mainVisitor.visitInsn(IDIV);
            		
		System.out.println("Exit division");
	
	}// end exitDivision
	
	
	/**
	 * Subtraction
	 *
	 */
	@Override 
	public void enterSubtraction(KnightCodeParser.SubtractionContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter subtraction");
		operationCount++;
		arithmeticOperation = "-"+ arithmeticOperation;
	
	}// end enterSubtraction

	@Override 
	public void exitSubtraction(KnightCodeParser.SubtractionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;
			
		//ASM stuff
		mainVisitor.visitInsn(ISUB);
            	           	
		System.out.println("Exit subtraction");

	}// end exitSubtraction

	/**
	 * Comparison
	 *
	 */
	@Override 
	public void enterComparison(KnightCodeParser.ComparisonContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter Comparison");
		
		if(ctx.getChildCount() != 0){
			compString = ctx.getChild(1).getChild(0).getText();	
			operation = compString + operation;
			if(compString.equals("<>"))
				printTwice = true;
			//System.out.println(compString);
		}	
	
	}// end enterComparison

	@Override 
	public void exitComparison(KnightCodeParser.ComparisonContext ctx){ 
		if(exit)
			return;		       
		Label label1 = new Label();
        	Label label2 = new Label();
   
        		if(compString.equals(">")){
				
				mainVisitor.visitJumpInsn(IF_ICMPLE, label1);
				arithmeticOperation = ">"+arithmeticOperation;
				
			} else if(compString.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE, label1);
				arithmeticOperation = "<"+arithmeticOperation;
			
			} else if(compString.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, label1);
				arithmeticOperation = "<>"+arithmeticOperation;
			
			} else if(compString.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, label1);
				arithmeticOperation = "="+arithmeticOperation;
			
			}
			

		mainVisitor.visitInsn(ICONST_1);
		mainVisitor.visitJumpInsn(GOTO, label2);
		mainVisitor.visitLabel(label1);
		mainVisitor.visitInsn(ICONST_0);
		mainVisitor.visitLabel(label2);
		
			    	
		System.out.println("Exit Comparison");

	}// end exitComparison
	 
	/**
	 * Comp: GT | LT | EQ | NEQQ"
	 *
	 */ 
	@Override 
	public void enterComp(KnightCodeParser.CompContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter Comp");	
			
	}// end enterComp

	@Override 
	public void exitComp(KnightCodeParser.CompContext ctx){ 
		if(exit)
			return;
					
		System.out.println("Exit Comp");
	}// end exitComp
	
	Label endDecLab0 = new Label();
	Label endDecLab1 = new Label();
	Label endDecLab2 = new Label();
	Label endDecLab3 = new Label();
	Label endDecLab4 = new Label();
	Label endDecLab5 = new Label();
	Label endDecLab6 = new Label();
	Label endDecLab7 = new Label();
	Label endDecLab8 = new Label();
	Label endDecLab9 = new Label();
	
	Label startOfElse0 = new Label();
	Label startOfElse1 = new Label();
	Label startOfElse2 = new Label();
	Label startOfElse3 = new Label();
	Label startOfElse4 = new Label();
	Label startOfElse5 = new Label();
	Label startOfElse6 = new Label();
	Label startOfElse7 = new Label();
	Label startOfElse8 = new Label();
	Label startOfElse9 = new Label();
	
	
	public static int ifCount1 = 0;
	public static int elseCount1 = 0;
	public static int decLabCount = 0;
	public int decCount2 = 0;

	public String decNestStack = "000";	
	public String decElseStack = "000";	
	public String decIfStack = "000";
	
	public stacker decIfStacker = new stacker();
	public stacker decElseStacker = new stacker();

	public boolean firstNestedDec = false;

	/**
	 * Decision
	 * The most complicated of the methods, it first checks that the number of decision statements are less than 10 because that would cause a label error.
	 * Then it checks for syntax errors and adds then counts the number of If-statements and else-statements so that the rest of the program knows where to put which label where.
	 * This design supports having nested If-Then-Else statements in a KnightCode program.
	 *
	 */
	@Override 
	public void enterDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;

		System.out.println("Enter Decision");
		if(decLabCount > 9){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Too many If-Else statements, compiler can only handle 10 or less!");
			exit = true;
			return;

		} else {
			decLabCount++;	
			decCount2++;		
		}
		decNestStack = decLabCount + decNestStack;

		decCount = ctx.getChildCount();

		if(decCount < 7){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;

		}
		
		//IF
		
		if(!ctx.getChild(0).getText().equalsIgnoreCase("IF")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		
		}
		if(!ctx.getChild(4).getText().equalsIgnoreCase("THEN")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement! If statement must be followed by then");
			
			exit = true;
			return;
		
		
		}
		if(ctx.getChild(decCount-2).getText().equalsIgnoreCase("ELSE")||ctx.getChild(5).getText().equalsIgnoreCase("ELSE")){
		
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				System.out.println("Syntax is wrong for If-Else statement at ELSE");
			
				exit = true;
				return;	
		}	
		if(!ctx.getChild(decCount-1).getText().equalsIgnoreCase("ENDIF")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			

			System.out.println("Syntax is wrong for If-Else statement at ENDIF");
			exit = true;
			return;
		}

		decOp1 = ctx.getChild(1).getText();
		if(SymbolTable.containsKey(decOp1)){
			var1 = SymbolTable.get(decOp1);
			
			if(!var1.valueSet||var1.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;
        			
				
			}
			
			operator1 = var1.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator1);
		} else {
			try{
            			
            			decOperator1 = Integer.valueOf(decOp1);
            			mainVisitor.visitIntInsn(SIPUSH, decOperator1);
            			
            			
       		     } catch(NumberFormatException e){
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp1 + " does not exist, is not assigned a value or is not a valid integer.");
				
				exit = true;
				return;
        			
        		     }
		
		}
		
		decOp2 = ctx.getChild(3).getText();
		if(SymbolTable.containsKey(decOp2)){
			var2 = SymbolTable.get(decOp2);

			if(!var2.valueSet || var2.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;	
			}
			
			operator2 = var2.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator2);
		} else {
		
			  try{
            			
            			decOperator2 = Integer.valueOf(decOp2);
            			mainVisitor.visitIntInsn(SIPUSH, decOperator2);	
       		     } catch(NumberFormatException e){
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + decOp2 + " does not exist, is not assigned a value or is not a valid integer.");
				
				exit = true;
				return;
        			
        		     }	
		}		
			
		decCompSymbol = ctx.getChild(2).getChild(0).getText();

		
		String temporaryCounterString;
		
		int temporaryIfCounter = 0;
		int temporaryElseCounter = 0;
		int tempI = 5;
		int elseNodeNum = -1;
		boolean elseFound = false;
		
		while(tempI < decCount && !elseFound){
	
			if(ctx.getChild(tempI).getText().equalsIgnoreCase("ELSE")){
					prev = "ELSE";

					elseNodeNum = tempI;
					elseFound = true;
			} else {
			
				if(!ctx.getChild(tempI).getText().equalsIgnoreCase("ENDIF")){
					temporaryIfCounter++;
					

		
					if(ctx.getChild(tempI).getText().substring(0,5).equalsIgnoreCase("WHILE")){
				

							int t = ctx.getChild(tempI).getChild(0).getChildCount();

							int f = ctx.getChild(tempI).getChild(0).getChildCount()-6;

							String tempoStringer = "";							
							int i = 5;
							while(i<t-1){								

								
								tempoStringer = ctx.getChild(tempI).getChild(0).getChild(i).getChild(0).getChild(0).getText();
								if(tempoStringer.length()>=5){	
								if(ctx.getChild(tempI).getChild(0).getChild(i).getChild(0).getChild(0).getText().substring(0,5).equalsIgnoreCase("WHILE")){

									
									int cll = ctx.getChild(tempI).getChild(0).getChild(i).getChild(0).getChildCount();
									int j = 5;
									while(j<cll-1){
										String tempnextstringer = ctx.getChild(tempI).getChild(0).getChild(i).getChild(0).getChild(0).getText();
										if(tempnextstringer.length()>=5){				
							if(ctx.getChild(tempI).getChild(0).getChild(i).getChild(0).getChild(j).getChild(0).getText().substring(0,5).equalsIgnoreCase("WHILE")){
									
									
												System.out.println("\n\n------------------------------------------");
												System.out.println("COMPILER ERROR");
												System.out.println("------------------------------------------");
			
												System.out.println("While-loop overflow");
												System.out.println("Compiler cannot handle more 3 nested while-loops inside an if-bracket");
												System.out.println("A label error would occur.");
				
												exit = true;
												return;
									
										}	
										
										
										
										}
										j++;
									
									}
								

									f+= cll-6;	
								}
								}
								i++;
							}
							
							
							temporaryIfCounter += f;
					
					}
			
			
				}
			}
				
			tempI++;
		}	
		

		if(elseFound){
			

			while(tempI < decCount){
			
			
			
				if(!ctx.getChild(tempI).getText().equalsIgnoreCase("ENDIF")){
					temporaryElseCounter++;
									
				}
			
				tempI++;
			
			}
		
		} 	

		int brukOgKast = pop(decIfStacker);
		
		brukOgKast += temporaryElseCounter;
		push(decIfStacker, brukOgKast);

		push(decIfStacker, temporaryIfCounter);


		
		push(decElseStacker, temporaryElseCounter);

		int tempElse = elseCount1;
		elseCount1 += temporaryElseCounter;
		ifCount1 +=temporaryIfCounter;	
				
//---------------------------------------------------------------------------------------------------			
	
		Label temp; 
		Label tempEnd;
		
			
			int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
			switch(currentUsage){
				case 1: {
					temp = startOfElse0;
					tempEnd = endDecLab0;
					break;
				}
				case 2: {
					temp = startOfElse1;
					tempEnd = endDecLab1;
					break;
				}
				case 3: {
					temp = startOfElse2;
					tempEnd = endDecLab2;
					break;
				}
				case 4: {
					temp = startOfElse3;
					tempEnd = endDecLab3;
					break;
				}
				case 5: {
					temp = startOfElse4;
					tempEnd = endDecLab4;
					break;
				}
				case 6: {
					temp = startOfElse5;
					tempEnd = endDecLab5;
					break;
				}
				case 7: { 
					temp = startOfElse6;
					tempEnd = endDecLab6;
					break;
				
				}
				case 8: {
					temp = startOfElse7;
					tempEnd = endDecLab7;
					break;
				
				}
				case 9: { 
					temp = startOfElse8;
					tempEnd = endDecLab8;
					break;
				
				}
				case 10: { 
					temp = startOfElse9;
					tempEnd = endDecLab9;
					break;
				
				}
				default: {
				
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for if-else statement at enter!");
					
					exit = true;
					return;
				}		
			}
			

		String tempStringDecBla = "ifComp... ";
		if(elseCount1 > tempElse){
		} else {

			temp = tempEnd;		
		}	
		

			if(decCompSymbol.equals(">")){
				mainVisitor.visitJumpInsn(IF_ICMPLE, temp);
			} else if(decCompSymbol.equals("<")){
				mainVisitor.visitJumpInsn(IF_ICMPGE,temp);
			} else if(decCompSymbol.equals("<>")){
				mainVisitor.visitJumpInsn(IF_ICMPEQ, temp);
			} else if(decCompSymbol.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, temp);			
			}
			
	

	}// end enterDecision
	@Override 
	public void exitDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;
		

		
			Label temper; 	
			Label temp;
			int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
			
			switch(currentUsage) {
		
				case 1: {
					temp = endDecLab0;
	temper = startOfElse0;	
					break;
				}
				case 2: {
					temp = endDecLab1;
	temper = startOfElse1;	
					break;
				}
				case 3: {
					temp = endDecLab2;
	temper = startOfElse1;	
					break;
				}
				case 4: {
					temp = endDecLab3;
	temper = startOfElse1;	
					break;
				}
				case 5: {
					temp = endDecLab4;
	temper = startOfElse1;	
					break;
				}
				case 6: {
	temper = startOfElse5;
					temp = endDecLab5;
					break;
				}
				case 7: { 
	temper = startOfElse6;
					temp = endDecLab6;
					break;
				
				}
				case 8: {
	temper = startOfElse7;
					temp = endDecLab7;
					break;
				
				}
				case 9: { 
	temper = startOfElse8;
					temp = endDecLab8;
					break;
				
				}
				case 10: { 
	temper = startOfElse9;
					temp = endDecLab9;
					break;
				
				}

				default: {
	temper = startOfElse1;	
			
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("Case 1: jump label failure for if-else statement at exit");
					
					exit = true;
					return;
				}	
					
			}
			decCount2--;

		
		//ASM
		mainVisitor.visitLabel(temp);
		
		if(decNestStack.length() != 0)
			decNestStack = decNestStack.substring(1);
		int tempBoolElse = peek(decElseStacker);
		int newUsage;
		if(tempBoolElse > 0){
			newUsage = peek(decIfStacker);
				if(newUsage == 1){;
			
					Label tempEnd;
			 		currentUsage = Character.getNumericValue(decNestStack.charAt(0));
					switch(currentUsage){
						case 1: {
							tempEnd = endDecLab0;
							temper = startOfElse0;						
							break;
						}
						case 2: {
							tempEnd = endDecLab1;
							temper = startOfElse1;
							break;
						}
						case 3: {
							tempEnd = endDecLab2;
							temper = startOfElse2;						
							break;
						}
						case 4: {
							tempEnd = endDecLab3;
							temper = startOfElse3;
							break;
						}
						case 5: {
							tempEnd = endDecLab4;
							temper = startOfElse4;
							break;
						}
						case 6: {						
							tempEnd = endDecLab5;
							temper = startOfElse5;
							break;
						}
						case 7: { 						
							tempEnd = endDecLab6;
							temper = startOfElse6;
							break;
					
						}
						case 8: {
							tempEnd = endDecLab7;
							temper = startOfElse7;
							break;
					
						}
						case 9: { 
							tempEnd = endDecLab8;
							temper = startOfElse8;
							break;
						
						}
						case 10: { 
							tempEnd = endDecLab9;
							temper = startOfElse9;
							break;
					
						}
						default: {
						
							System.out.println("\n\n------------------------------------------");
							System.out.println("COMPILER ERROR");
							System.out.println("------------------------------------------");
						
							System.out.println("jump label failure for if-else statement in print!");
							
							exit = true;
							return;
						}	
							
					}
					
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
				

				} else if(newUsage > 1){

					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);	
				}
		} else {
		

			pop(decIfStacker);
			pop(decElseStacker);
			newUsage = pop(decIfStacker);
			newUsage--;
			push(decIfStacker,newUsage);
		}

		System.out.println("Exit Decision");


	}// end exitDecision
	
	
	/**
	 * Print
	 * This method determines whether the data to be printed is an Identifier or a string, 
	 * and then either loads the memory location of that identifier from the symbol table, or prints the string from the constant pool.
	 *
	 */
	@Override
	public void enterPrint(KnightCodeParser.PrintContext ctx){
		if(exit)
			return;

		System.out.println("Enter print");

		

		key2 = ctx.getChild(1).getText();

		//ASM bytecode	
		mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
		
		if(SymbolTable.containsKey(key2)){
			genPrint = false;
			extravar = SymbolTable.get(key2);
			
			outputInt = extravar.memLoc;
			if(isString(extravar)){
				printString = true;
			} else { 	
				printString = false;
			}
			
		} else {
			genPrint = true;
			outputString = key2; 
		}
		
	
	}//end enterPrint
	@Override 
	public void exitPrint(KnightCodeParser.PrintContext ctx){ 
		if(exit)
			return;

	
		
		if(genPrint){

			
			
			mainVisitor.visitLdcInsn(outputString);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
		} else {
			if(printString){

				
				
				mainVisitor.visitVarInsn(ALOAD, outputInt);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
			} else {

				
				
				mainVisitor.visitVarInsn(Opcodes.ILOAD, outputInt);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
			}
		}
		
		
		int tempBoolElse = peek(decElseStacker);

		
		if(tempBoolElse > 0){

		
			int newUsage = peek(decIfStacker);

		

			
				if(newUsage == 1){


					
					Label temper;
					Label tempEnd;
			 		int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
					switch(currentUsage){
						case 1: {
							tempEnd = endDecLab0;
							temper = startOfElse0;						
							break;
						}
						case 2: {
							tempEnd = endDecLab1;
							temper = startOfElse1;
							break;
						}
						case 3: {
							tempEnd = endDecLab2;
							temper = startOfElse2;						
							break;
						}
						case 4: {
							tempEnd = endDecLab3;
							temper = startOfElse3;
							break;
						}
						case 5: {
							tempEnd = endDecLab4;
							temper = startOfElse4;
							break;
						}
						case 6: {						
							tempEnd = endDecLab5;
							temper = startOfElse5;
							break;
						}
						case 7: { 						
							tempEnd = endDecLab6;
							temper = startOfElse6;
							break;
					
						}
						case 8: {
							tempEnd = endDecLab7;
							temper = startOfElse7;
							break;
					
						}
						case 9: { 
							tempEnd = endDecLab8;
							temper = startOfElse8;
							break;
						
						}
						case 10: { 
							tempEnd = endDecLab9;
							temper = startOfElse9;
							break;
					
						}
						default: {
						
							System.out.println("\n\n------------------------------------------");
							System.out.println("COMPILER ERROR");
							System.out.println("------------------------------------------");
						
							System.out.println("jump label failure for if-else statement in print!");
							
							exit = true;
							return;
						}	
							
					}
				
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
				
				
				} else if(newUsage > 1){

					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
					
				}
			
			
		}

		System.out.println("Exit print");
	}// end exitPrint

	
	public boolean alreadyRead = false;
	public int readStoredLocation;
	/**
	 * Read
	 * Creates and initializes a scanner instance if not already created and then reads the input and stores it in the appropriate memory location using the symbol table.
	 *
	 *
	 */
	@Override 
	public void enterRead(KnightCodeParser.ReadContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter read\n");
		
		
		if(ctx.getChild(1) != null)
			key = ctx.getChild(1).getText();
			

		if(SymbolTable.containsKey(key)){
			currvar = SymbolTable.get(key);
		} else {
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Identifier: " + key + " was not declared");
			exit = true;
			return;
		
		}
		


		if(!alreadyRead){

			alreadyRead = true;
			
			readStoredLocation = memoryCounter;
		
			mainVisitor.visitTypeInsn(NEW, "java/util/Scanner");
            		mainVisitor.visitInsn(DUP);
            		mainVisitor.visitFieldInsn(GETSTATIC,"java/lang/System", "in", "Ljava/io/InputStream;");
            		mainVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V" , false);
            		mainVisitor.visitVarInsn(ASTORE,readStoredLocation);
		
		
			memoryCounter++;
			
		}
		
		
		

	
	}// end enterRead
	@Override 
	public void exitRead(KnightCodeParser.ReadContext ctx){ 
		if(exit)
			return;
			

		mainVisitor.visitVarInsn(ALOAD,readStoredLocation);
		
		
          	
	
		genBool = isString(currvar);
			
		if(genBool){
	
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
			mainVisitor.visitVarInsn(ASTORE,currvar.memLoc);
			

			
		} else {
		

			
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
			mainVisitor.visitVarInsn(ISTORE,currvar.memLoc);
		
			
			mainVisitor.visitVarInsn(ALOAD,readStoredLocation);
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
			mainVisitor.visitInsn(POP);
			
		}
		

		currvar.valueSet = true;
		SymbolTable.put(key, currvar);
		
		int tempBoolElse = peek(decElseStacker);

		
		if(tempBoolElse > 0){

		
			int newUsage = peek(decIfStacker);

		

			
				if(newUsage == 1){

					
					Label temper;
					Label tempEnd;
			 		int currentUsage = Character.getNumericValue(decNestStack.charAt(0));
					switch(currentUsage){
						case 1: {
							tempEnd = endDecLab0;
							temper = startOfElse0;						
							break;
						}
						case 2: {
							tempEnd = endDecLab1;
							temper = startOfElse1;
							break;
						}
						case 3: {
							tempEnd = endDecLab2;
							temper = startOfElse2;						
							break;
						}
						case 4: {
							tempEnd = endDecLab3;
							temper = startOfElse3;
							break;
						}
						case 5: {
							tempEnd = endDecLab4;
							temper = startOfElse4;
							break;
						}
						case 6: {						
							tempEnd = endDecLab5;
							temper = startOfElse5;
							break;
						}
						case 7: { 						
							tempEnd = endDecLab6;
							temper = startOfElse6;
							break;
					
						}
						case 8: {
							tempEnd = endDecLab7;
							temper = startOfElse7;
							break;
					
						}
						case 9: { 
							tempEnd = endDecLab8;
							temper = startOfElse8;
							break;
						
						}
						case 10: { 
							tempEnd = endDecLab9;
							temper = startOfElse9;
							break;
					
						}
						default: {
						
							System.out.println("\n\n------------------------------------------");
							System.out.println("COMPILER ERROR");
							System.out.println("------------------------------------------");
						
							System.out.println("jump label failure for if-else statement in print!");
							
							exit = true;
							return;
						}	
							
					}
			
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
					
				} else if(newUsage > 1){

					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
				}
			
			
		}
		System.out.println("Exit read\n");
		

	}//end exitRead

	
	public int loopLabCount = 0;
	public int loopCount = 0;
	
	public String loopOp1;
	public int loopOperator1;
	public String loopOp2;
	public int loopOperator2;
	
	public String loopCompSymbol;
	
	public String loopNestStack = "000";	
	
	
	Label endOfloop0 = new Label();
	Label endOfloop1 = new Label();
	Label endOfloop2 = new Label();
	Label endOfloop3 = new Label();
	Label endOfloop4 = new Label();
	Label endOfloop5 = new Label();
	Label endOfloop6 = new Label();
	Label endOfloop7 = new Label();
	Label endOfloop8 = new Label();
	Label endOfloop9 = new Label();
	
	Label startOfloop0 = new Label();
	Label startOfloop1 = new Label();
	Label startOfloop2 = new Label();
	Label startOfloop3 = new Label();
	Label startOfloop4 = new Label();
	Label startOfloop5 = new Label();
	Label startOfloop6 = new Label();
	Label startOfloop7 = new Label();
	Label startOfloop8 = new Label();
	Label startOfloop9 = new Label();
	
	
	
	
	/**
	 * Loop
	 * Similar, yet simpler than the decision method, it works in the same way. It counts and remembers the depth of the loop through stacks.
	 *
	 */
	@Override 
	public void enterLoop(KnightCodeParser.LoopContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter loop");
		
		if(loopLabCount > 9){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Too many While-loops, compiler can only handle 10 or less!");
			exit = true;
			return;

		} else {
			loopLabCount++;	
			loopCount++;		
		}
		loopNestStack = loopLabCount + loopNestStack;


		int syntaxTest = ctx.getChildCount();
		if(syntaxTest < 7){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for loop statement!");
			
			exit = true;
			return;
		
		
		}
		
		//IF
		
		if(!ctx.getChild(0).getText().equalsIgnoreCase("WHILE")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for while loop");
			
			exit = true;
			return;
		
		
		}
		if(!ctx.getChild(4).getText().equalsIgnoreCase("DO")){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for while-loop! after comparison must come \"DO\"");
			
			exit = true;
			return;
		
		
		}
		if(!ctx.getChild(syntaxTest-1).getText().equalsIgnoreCase("ENDWHILE")){
		
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				//System.out.println("ELSE");
				System.out.println("Syntax is wrong for while-loop, must end with \"ENDWHILE\"");
			
				exit = true;
				return;	
		}
		
		
		Label temp; 
		Label tempEnd;
		
			
			int currentUsage = Character.getNumericValue(loopNestStack.charAt(0));
			switch(currentUsage){
				case 1: {
					temp = startOfloop0;
					tempEnd = endOfloop0;
					break;
				}
				case 2: {
					temp = startOfloop1;
					tempEnd = endOfloop1;
					break;
				}
				case 3: {
					temp = startOfloop2;
					tempEnd = endOfloop2;
					break;
				}
				case 4: {
					temp = startOfloop3;
					tempEnd = endOfloop3;
					break;
				}
				case 5: {
					temp = startOfloop4;
					tempEnd = endOfloop4;
					break;
				}
				case 6: {
					temp = startOfloop5;
					tempEnd = endOfloop5;
					break;
				}
				case 7: { 
					temp = startOfloop6;
					tempEnd = endOfloop6;
					break;
				
				}
				case 8: {
					temp = startOfloop7;
					tempEnd = endOfloop7;
					break;
				
				}
				case 9: { 
					temp = startOfloop8;
					tempEnd = endOfloop8;
					break;
				
				}
				case 10: { 
					temp = startOfloop9;
					tempEnd = endOfloop9;
					break;
				
				}
				default: {
				
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for loop at enter!");
					
					exit = true;
					return;
				}		
			}
		
		//ASM stuff
		mainVisitor.visitLabel(temp);

		
		loopOp1 = ctx.getChild(1).getText();
		if(SymbolTable.containsKey(loopOp1)){
			var1 = SymbolTable.get(loopOp1);
			
			if(!var1.valueSet||var1.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;
        			
				
			}
			
			operator1 = var1.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator1);
		} else {
			try{
            			
            			loopOperator1 = Integer.valueOf(loopOp1);
            			mainVisitor.visitIntInsn(SIPUSH, loopOperator1);
            			
            			
       		     } catch(NumberFormatException e){

        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp1 + " does not exist, is not assigned a value or is not a valid integer.");
				
				exit = true;
				return;
        			
        		     }
		
		}
		
		loopOp2 = ctx.getChild(3).getText();
		if(SymbolTable.containsKey(loopOp2)){
			var2 = SymbolTable.get(loopOp2);

			if(!var2.valueSet || var2.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp1 + " value has not been set, or ID is a String!");
				
				exit = true;
				return;	
			}
			
			operator2 = var2.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator2);
		} else {
		
			  try{
            			
            			loopOperator2 = Integer.valueOf(loopOp2);
            			mainVisitor.visitIntInsn(SIPUSH, loopOperator2);	
       		     } catch(NumberFormatException e){
        			System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("Comparison failed!");
				System.out.println("ID: " + loopOp2 + " does not exist, is not assigned a value or is not a valid integer.");
				
				exit = true;
				return;
        			
        		     }	
		}		
			
		loopCompSymbol = ctx.getChild(2).getChild(0).getText();
	
		if(loopCompSymbol.equals(">")){
				

				mainVisitor.visitJumpInsn(IF_ICMPLE, tempEnd);

			} else if(loopCompSymbol.equals("<")){	
				mainVisitor.visitJumpInsn(IF_ICMPGE,tempEnd);
			
			} else if(loopCompSymbol.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, tempEnd);
			
			} else if(loopCompSymbol.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, tempEnd);
			
			}

			syntaxTest = ctx.getChildCount() - 6;		
		
	}// end enterLoop
	@Override 
	public void exitLoop(KnightCodeParser.LoopContext ctx){ 
		if(exit)
			return;
			
			
				
			Label temp;
			Label temper;
			int currentUsage = Character.getNumericValue(loopNestStack.charAt(0));
			
			switch(currentUsage) {
		
				case 1: {
					temp = endOfloop0;
					temper = startOfloop0;
					break;
				}
				case 2: {
					temp = endOfloop1;
					temper = startOfloop1;
					break;
				}
				case 3: {
					temp = endOfloop2;
					temper = startOfloop2;
					break;
				}
				case 4: {
					temp = endOfloop3;
					temper = startOfloop3;
					break;
				}
				case 5: {
					temp = endOfloop4;
					temper = startOfloop4;
					break;
				}
				case 6: {
					temp = endOfloop5;
					temper = startOfloop5;
					break;
				}
				case 7: { 
					temp = endOfloop6;
					temper = startOfloop6;
					break;
				
				}
				case 8: {
					temp = endOfloop7;
					temper = startOfloop7;
					break;
				
				}
				case 9: {
					temp = endOfloop8;
					temper = startOfloop8;
					break;
				
				}
				case 10: { 
					temp = endOfloop9;
					temper = startOfloop9;
					break;
				
				}
				default: {
	
			
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for if-else statement at exit");
					
					exit = true;
					return;
				}	
					
			}
			
		//ASM
		mainVisitor.visitJumpInsn(GOTO,temper);
		mainVisitor.visitLabel(temp);
		
		  
		if(loopNestStack.length() != 0)
			loopNestStack = loopNestStack.substring(1);
		

			
		int tempBoolElse = peek(decElseStacker);

		if(tempBoolElse > 0){
			int newUsage = peek(decIfStacker);
		

			
				if(newUsage == 1){
					Label tempEnd;
			 		currentUsage = Character.getNumericValue(decNestStack.charAt(0));
					switch(currentUsage){
						case 1: {
							tempEnd = endDecLab0;
							temper = startOfElse0;						
							break;
						}
						case 2: {
							tempEnd = endDecLab1;
							temper = startOfElse1;
							break;
						}
						case 3: {
							tempEnd = endDecLab2;
							temper = startOfElse2;						
							break;
						}
						case 4: {
							tempEnd = endDecLab3;
							temper = startOfElse3;
							break;
						}
						case 5: {
							tempEnd = endDecLab4;
							temper = startOfElse4;
							break;
						}
						case 6: {						
							tempEnd = endDecLab5;
							temper = startOfElse5;
							break;
						}
						case 7: { 						
							tempEnd = endDecLab6;
							temper = startOfElse6;
							break;
					
						}
						case 8: {
							tempEnd = endDecLab7;
							temper = startOfElse7;
							break;
					
						}
						case 9: { 
							tempEnd = endDecLab8;
							temper = startOfElse8;
							break;
						
						}
						case 10: { 
							tempEnd = endDecLab9;
							temper = startOfElse9;
							break;
					
						}
						default: {
						
							System.out.println("\n\n------------------------------------------");
							System.out.println("COMPILER ERROR");
							System.out.println("------------------------------------------");
						
							System.out.println("jump label failure for if-else statement in print!");
							
							exit = true;
							return;
						}	
							
					}

					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);

				} else if(newUsage > 1){

					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
				}
						
		}
	

		System.out.println("Exit loop");

	}// end exitLoop
	
}//end class

