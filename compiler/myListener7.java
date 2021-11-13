package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

import lexparse.*; //classes for lexer parser
import java.util.*;

public class myListener7 extends KnightCodeBaseListener{

	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status
	
	
//Here are general stuff that I made


public class variable{

	public String variableType = "";
	public String value = "";
	public int memLoc = -1;
	public boolean valueSet = false;


	public variable(String variableType, String value){
		this.variableType = variableType;
		this.value = value;
	
	}
	
	public variable(){
		variableType = "";
		value = "";
	}


}

public class stacker{

	public int[]stack = new int[30];
	public int head = 0;


	public stacker(){
		
	}
	
	

}


	public HashMap<String, variable> SymbolTable = new HashMap<String, variable>();
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
	public boolean expression1;
	public boolean printTwice;
	//public boolean enter;
	//public boolean parenthesisWait;
	
	public int memoryCounter = 1;
	//public boolean changeVar;
	
	
	
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
		
	}
	
	public boolean isString(variable var){
		
		if(var.variableType.equals(STR))
			return true;
		return false;
	}
	
	public void printStack(stacker s){
	
		for(int i = s.head;i>= 0;i--){
			System.out.print(s.stack[i]+",");
		}	
	
	}
	
	public int push(stacker s, int value){
	
		if(s.head == 29){
			return -1;
		} else {	
			s.head++;
			s.stack[s.head] = value;
			return value;
		}
	
	}
	
	public int pop(stacker s){
	
		int value = -1;
		if(s.head == 0){
			return value;
		} else {	
			
			value = s.stack[s.head];
			s.stack[s.head] = 0;
			s.head--;
			return value;
		}
	
	}
	
	
	public int peek(stacker s){
	
		return s.stack[s.head];
		
	
	}

	
//End general stuff	
	
	
	
	

	public myListener7(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor
	
	public myListener7(String programName){
	       
		this.programName = programName;
		debug = false;

	}//end constructor


	//Labels
		//public static Label label1 = new Label();
        	//public static Label label2 = new Label();
        	public static Label label3 = new Label();
        	public static Label label4 = new Label();
        	Label startOfLoop = new Label();
        	Label endOfLoop = new Label();
            	Label returnl = new Label();
            	public Label printSkipIf = new Label();
            	public Label printSkipElse = new Label();
            	public Label secondPart = new Label();

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
	
		

	public void closeClass(){
	
		if(exit)
			return;
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		
		//mainVisitor.visitLabel(returnl);
		
		mainVisitor.visitLabel(returnl);
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


	@Override
	public void enterFile(KnightCodeParser.FileContext ctx){
	
		System.out.println("Enter program rule for first time");
		setupClass();
	}
	
	@Override
	public void exitFile(KnightCodeParser.FileContext ctx){
		if(exit)
			return;
			
			
		System.out.println("Attempting to exit file");	

		
		closeClass();
		System.out.println("Leaving program rule. . .");

	}


	@Override 
	public void enterDeclare(KnightCodeParser.DeclareContext ctx){
		if(exit)
			return;
			
		System.out.println("Enter declare");
		
		//enter = false;
		count = ctx.getChildCount();
		
	}
	@Override 
	public void exitDeclare(KnightCodeParser.DeclareContext ctx){
		if(exit)
			return;
			
		printHashMap(SymbolTable);
		
		//enter = true;
		System.out.println("Exit declare");
	}

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
		
	
	}
	@Override 
	public void exitVariable(KnightCodeParser.VariableContext ctx){ 
		if(exit)
			return;
	
		System.out.println("Exit variable");
	}
	
	@Override 
	public void enterIdentifier(KnightCodeParser.IdentifierContext ctx){
		if(exit)
			return;
	
	}
	
	@Override 
	public void exitIdentifier(KnightCodeParser.IdentifierContext ctx){ 
		if(exit)
			return;
	
	}
	
	@Override public void enterVartype(KnightCodeParser.VartypeContext ctx) { }
	@Override public void exitVartype(KnightCodeParser.VartypeContext ctx) { }
	
	
	
	
	@Override 
	public void enterBody(KnightCodeParser.BodyContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter body!");
		
		count = ctx.getChildCount();

	}
	@Override 
	public void exitBody(KnightCodeParser.BodyContext ctx){ 
		if(exit)
			return;
	
		printHashMap(SymbolTable);
		
		mainVisitor.visitLabel(printSkipIf);
		
		System.out.println("Exit body!");
	}
	
	
	
	@Override public void enterStat(KnightCodeParser.StatContext ctx) { }
	@Override public void exitStat(KnightCodeParser.StatContext ctx) { }
	
	
	public int operationCount = 0;
	@Override 
	public void enterSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter setvar");
		operationCount = 0;
		genIntStr = "";
		
		if(ctx.getChild(1) != null)
			key = ctx.getChild(1).getText();
			
		System.out.println("\n"+key);	
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
			System.out.println(genIntStr);
		}
	
	}
	
	@Override 
	public void exitSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
	
		System.out.println("final value of id = " + genIntStr);
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
		//int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
		
		if(tempBoolElse > 0){
	      //if(decElseStack.length() > 3 && tempBoolElse > 0){
		
			int newUsage = peek(decIfStacker);
			//int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(newUsage == 1){
			      //if(decIfStack.length() > 3 && newUsage == 1){		
					System.out.println("\nTime to visit the if else\n");
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decIfStacker);
					System.out.println();
					
					
					/*
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					*/
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decElseStacker);
					System.out.println();
					/*
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					*/
					
				}
			
			
		}
    	    	
    	    	/*
    	    	int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
	
		
		if(decElseStack.length() > 3 && tempBoolElse > 0){
		
		
			int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(decIfStack.length() > 3 && newUsage == 1){
					
					System.out.println("\nTime to visit the if else\n");
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					
				}
			
			
    	    	
    	    	
    	    	}
    	    	*/
    	    	
		System.out.println("Exit setvar");
	}
	
	public String enterAndExitNumber;
	
	@Override 
	public void enterNumber(KnightCodeParser.NumberContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter Number");
		enterAndExitNumber = ctx.getText();
		genIntStr += enterAndExitNumber;			
	}
	
	@Override 
	public void exitNumber(KnightCodeParser.NumberContext ctx){ 
		if(exit)
			return;
	
		num = Integer.valueOf(enterAndExitNumber);	
		mainVisitor.visitIntInsn(SIPUSH, num);
		System.out.println("Exit Number");
	}
	
	
	
	
	@Override 
	public void enterId(KnightCodeParser.IdContext ctx){ 
		if(exit)
			return;
		System.out.println("enter ID");
		
	
		
		keyID = ctx.getText();
		
		if(SymbolTable.containsKey(keyID)){
			var1 = SymbolTable.get(keyID);
			op1 = keyID;
			
			//If we want the value in the symbol table to be contained as a numerical expression instead of variable expression
			//op1 = var1.value;
			
			
			
			operator1 = var1.memLoc;
			
			
			if(var1.variableType.equalsIgnoreCase(STR) && operationCount > 0){
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				
				System.out.println("Cannot perform arithmetic operations on a String");
				//System.out.println("ID: " + keyID + " does not exist!");
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
		
	}
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
	}
	
	
	
	
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
		
	
	}
	@Override 
	public void exitParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		//skipCount = 1;
		
		genIntStr += arithmeticOperation.charAt(0);
		if(arithmeticOperation.length() != 0)
			arithmeticOperation = arithmeticOperation.substring(1);
		
		System.out.println("Exit parenthesis");
	}
	
	
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
	}
	
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;
		System.out.println("Is it here?");	
			
		operationCount--;	

		//ASM stuff
		mainVisitor.visitInsn(IADD);
                      	
		System.out.println("Exit addition");
	}
	
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
	
	}
	@Override 
	public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	
		
		//ASM stuff
		mainVisitor.visitInsn(IMUL);
            		
		System.out.println("Exit multiplication");
	
	}
	
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
	
	}
	@Override 
	public void exitDivision(KnightCodeParser.DivisionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;	
		
		//ASM stuff
		mainVisitor.visitInsn(IDIV);
            		
		System.out.println("Exit division");
	
	}
	
	
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
	
	}
	@Override 
	public void exitSubtraction(KnightCodeParser.SubtractionContext ctx){ 
		if(exit)
			return;
			
		operationCount--;
			
		//ASM stuff
		mainVisitor.visitInsn(ISUB);
            	           	
		System.out.println("Exit subtraction");
	}
	
	
	

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
	
	}
	@Override 
	public void exitComparison(KnightCodeParser.ComparisonContext ctx){ 
		if(exit)
			return;		       
		Label label1 = new Label();
        	Label label2 = new Label();
        	
        	//Tests
        	/*
        	System.out.println("compString equals >: " +compString.equals(">"));
        	System.out.println("compString equals <: " +compString.equals("<"));
        	System.out.println("compString equals <>: " +compString.equals("<>"));
        	System.out.println("compString equals =: " +compString.equals("="));	
        	*/
        		if(compString.equals(">")){
				
				//System.out.println("compString equals >: " +compString.equals(">"));
				mainVisitor.visitJumpInsn(IF_ICMPLE, label1);
				//compString = "IF_ICMPLE"; 
				arithmeticOperation = ">"+arithmeticOperation;
				
			} else if(compString.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE, label1);
				//compString = "IF_ICMPGE"; 
				arithmeticOperation = "<"+arithmeticOperation;
			
			} else if(compString.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, label1);
				//compString = "IF_ICMPEQ";
				arithmeticOperation = "<>"+arithmeticOperation;
			
			} else if(compString.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, label1);
				//compString = "IF_ICMPNE";	
				arithmeticOperation = "="+arithmeticOperation;
			
			}
			
		//mainVisitor.visitJumpInsn(compString, label1);
		mainVisitor.visitInsn(ICONST_1);
		mainVisitor.visitJumpInsn(GOTO, label2);
		mainVisitor.visitLabel(label1);
		mainVisitor.visitInsn(ICONST_0);
		mainVisitor.visitLabel(label2);
		
			    	
		System.out.println("Exit Comparison");
	}
	 
	/**
	 * Comp: GT | LT | EQ | NEQQ"
	 *
	 */ 
	@Override 
	public void enterComp(KnightCodeParser.CompContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter Comp");	
			
	}
	@Override 
	public void exitComp(KnightCodeParser.CompContext ctx){ 
		if(exit)
			return;
			
		//mainVisitor.visitJumpInsn(compString, Label label);
		
		System.out.println("Exit Comp");
	}
	
	

	
	



	
	


	
	

	
	
	//Label[] decLabelAr = new Label[5];
	//Label[] decLabelEndAr = new Label[5];

	

	/*
	Label decLab0 = new Label();
	Label decLab1 = new Label();
	Label decLab2 = new Label();
	Label decLab3 = new Label();
	Label decLab4 = new Label();
	*/
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
	/*
	decLabelAr[0] = decLab0;
	decLabelAr[1] = decLab1;
	decLabelAr[2] = decLab2;
	decLabelAr[3] = decLab3;
	decLabelAr[4] = decLab4;
	
	decLabelEndAr[0] = decLab0;
	decLabelEndAr[1] = decLab1;
	decLabelEndAr[2] = decLab2;
	decLabelEndAr[3] = decLab3;
	decLabelEndAr[4] = decLab4;
	*/
	
	/*
	if(decNestStack.length() != 0)
			decNestStack = decNestStack.substring(1);
		System.out.println("Current stack = " + decNestStack);
		
		public String decNestStack += decLabCount;
		System.out.println("Current stack = " + decNestStack);
		public String decNestStack = "000";	
			
	
	*/
	
	public static int ifCount1 = 0;
	public static int elseCount1 = 0;
	
	public int[]ifCounterArr = new int[10];
	public int ifCount00 = 0;
	public int ifCount01 = 0;
	public int ifCount02 = 0;
	public int ifCount03 = 0;
	public int ifCount04 = 0;
	public int ifCount05 = 0;
	public int ifCount06 = 0;
	public int ifCount07 = 0;
	public int ifCount08 = 0;
	public int ifCount09 = 0;
	/*
	ifCounterArr[0] = ifCount00;
	ifCounterArr[1] = ifCount01;
	ifCounterArr[2] = ifCount02;
	ifCounterArr[3] = ifCount03;
	ifCounterArr[4] = ifCount04;
	ifCounterArr[5] = ifCount05;
	ifCounterArr[6] = ifCount06;
	ifCounterArr[7] = ifCount07;
	ifCounterArr[8] = ifCount08;
	ifCounterArr[9] = ifCount09;
	*/

	public static int decLabCount = 0;
	public int decCount2 = 0;

	public String decNestStack = "000";	
	public String decElseStack = "000";	
	public String decIfStack = "000";
	
	public stacker decIfStacker = new stacker();
	public stacker decElseStacker = new stacker();

	public boolean firstNestedDec = false;
	public int elseVisitor = 0;
	
	
	/**
	 * Decision
	 *
	 *
	 */
	@Override 
	public void enterDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;
		System.out.println("\n\n------------------------------------------------------------------------------------------------------------");	
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
		System.out.println("Current decision depth = " + decNestStack);
		
		
		
		
		
		
		
		
		
		
			
		
		decCount = ctx.getChildCount();
		/*
		System.out.println("decCount2 = " +decCount2);
		System.out.println("decLabCount = " + decLabCount);
		*/
		
		//Possible issue might occurr for nested if-else statements, because when you enter a decision these values reset.
		//Løsning til dette kan være å ha en array med en haug med sånne her så vi kan ha "nested"-enten-eller
		//Samme prinsippet for løkker
		//ifCount1 = 0;
		//elseCount1 = 0;
		
		
		
		
		
		
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
			
				//System.out.println("ELSE");
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
        			//System.out.println(e.getMessage());
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
		//compString = ctx.getChild(2).getChild(0).getText();
		
		String temporaryCounterString;
		
		int temporaryIfCounter = 0;
		int temporaryElseCounter = 0;
		int tempI = 5;
		int elseNodeNum = -1;
		boolean elseFound = false;
		
		while(tempI < decCount && !elseFound){
		
			/*
			if(temporaryIfCounter > 9){
				System.out.println("\n\n------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("If-statement overflow!");
				System.out.println("Compiler cannot handle more than 9 statements within an If-, or else-statement.");
				System.out.println("Label error would occur.");
				
				exit = true;
				return;
				
			
			}
			*/
		
		
			if(ctx.getChild(tempI).getText().equalsIgnoreCase("ELSE")){
					prev = "ELSE";
					System.out.println("Else node number is: " + (tempI)); 
					elseNodeNum = tempI;
					elseFound = true;
			} else {
			
				if(!ctx.getChild(tempI).getText().equalsIgnoreCase("ENDIF")){
					temporaryIfCounter++;
					
					System.out.println(ctx.getChild(tempI).getText());
					
					
					/*
					if(temporaryCounterString.length()>5)
						temporaryCounterString = temporaryCounterString.substring(0,5);
					*/
					if(ctx.getChild(tempI).getText().substring(0,5).equalsIgnoreCase("WHILE")){
				
							ifCount1 += (ctx.getChild(tempI).getChild(0).getChildCount() - 6);
					
						//System.out.println("--------------------------------------------------------");
						//System.out.println("ifCount before addition: " + ifCount1);
						//System.out.println("\nChild count: " + ctx.getChild(decCount-tempInt-2).getChild(0).getChildCount());
						//System.out.println("ifCount after addition: " + ifCount1);
						//System.out.println("--------------------------------------------------------");	
					}
			
			
				}
			}
				
			tempI++;
		}	
		
		//System.out.println("Else node number: "+ elseNodeNum);
		System.out.println("number of statements for if: " + temporaryIfCounter);
		
		/*
		decElseStack = temporaryIfCounter + decElseStack;
		System.out.println("Current stack for else= " + decElseStack);
		*/
		if(elseFound){
			
			elseVisitor++;
			while(tempI < decCount){
			
				/*
				if(temporaryElseCounter > 9){
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("Else-statement overflow!");
					System.out.println("Compiler cannot handle more than 9 statements within an If-, or Else-statement.");
					System.out.println("A label error would occur.");
					
					exit = true;
					return;
				
				
				}
				*/
			
				if(!ctx.getChild(tempI).getText().equalsIgnoreCase("ENDIF")){
					temporaryElseCounter++;
					
					System.out.println(ctx.getChild(tempI).getText());
				
				}
			
				tempI++;
			
			}
			System.out.println("number of statements for else: " + temporaryElseCounter);
		
		
			
		
		} else {
			System.out.println("No else statements for this one");
		}	
		
		/*
		System.out.println("\nPrior to stack manipulation: " +decIfStack);
		int brukOgKast = Character.getNumericValue(decIfStack.charAt(0));
		decIfStack = decIfStack.substring(1);
		System.out.println("mid stack manipulation: " +decIfStack);
		brukOgKast += temporaryElseCounter;
		decIfStack = brukOgKast + decIfStack;
		System.out.println("post stack manipulation: " +decIfStack+ "\n");
		*/
		System.out.print("\nPrior to stack manipulation: ");printStack(decIfStacker);
		int brukOgKast = pop(decIfStacker);
		System.out.print("\nmid stack manipulation: ");printStack(decIfStacker);
		brukOgKast += temporaryElseCounter;
		push(decIfStacker, brukOgKast);
		System.out.print("\npost stack manipulation: ");printStack(decIfStacker);
		System.out.println("");
		
	
		
	
	
		push(decIfStacker, temporaryIfCounter);
		System.out.print("decIfStack:  ");
		printStack(decIfStacker);
		System.out.println();
	
		/*
		decIfStack = temporaryIfCounter + decIfStack;
		System.out.println("Current stack for if: " + decIfStack);
		decElseStack = temporaryElseCounter + decElseStack;
		System.out.println("Current stack for else= " + decElseStack);
		*/
		
		push(decElseStacker, temporaryElseCounter);
		System.out.print("decElseStack:  ");
		printStack(decElseStacker);
		System.out.println();
		
		
		
		
		int tempElse = elseCount1;
		System.out.println("old else " + tempElse);
		elseCount1 += temporaryElseCounter;
		System.out.println("new else " + elseCount1 + "\n");
		ifCount1 +=temporaryIfCounter;	
				
//---------------------------------------------------------------------------------------------------			
			
			
		/*
			prev = "THEN";	
				
			tempInt = decCount-7;
			//System.out.println("Temp int = " +tempInt);
		if(tempInt > 0){
			//THEN
			while(tempInt > 0 && prev.equalsIgnoreCase("THEN")){
		
				if(ctx.getChild(decCount-tempInt-1).getText().equalsIgnoreCase("ELSE")){
					prev = "ELSE";
					System.out.println("Else node is here: nodenum: " + (decCount-tempInt-1)); 
				}
				
				//NESTED LOOP STUFF inside of if-else
				temporaryCounterString = ctx.getChild(decCount-tempInt-2).getText();
				System.out.println(temporaryCounterString);
				if(temporaryCounterString.length()>5)
					temporaryCounterString = temporaryCounterString.substring(0,5);
				//System.out.println(temporaryCounterString);
				if(temporaryCounterString.equalsIgnoreCase("WHILE")){
				
					System.out.println("--------------------------------------------------------");
					System.out.println("ifCount before addition: " + ifCount1);
					
					System.out.println("\nChild count: " + ctx.getChild(decCount-tempInt-2).getChild(0).getChildCount());
					ifCount1 += (ctx.getChild(decCount-tempInt-2).getChild(0).getChildCount() - 6);
					System.out.println("ifCount after addition: " + ifCount1);
					System.out.println("--------------------------------------------------------");
					
				
				}

				
				
				//System.out.println("THEN statement!");	
				ifCount1++;
			
				tempInt--;	
			
			}	
			//prev = "ELSE";
			//ELSE
			while(tempInt > 0){
		
				//System.out.println("ELSE statement!");
				elseCount1++;
		
				tempInt--;	
			}
		
			//ENDIF
		} else {
			ifCount1++;
		}
		*/
			System.out.println("ifCount: " + ifCount1);
			System.out.println("elseCount: " + elseCount1);
			
		
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
			
			//System.out.println("decLabCount = " + decLabCount);
		String tempStringDecBla = "ifComp... ";
		if(elseCount1 > tempElse){
			tempStringDecBla += "startOfElse Label: ";
			
		} else {
			tempStringDecBla += "end Label: ";
			temp = tempEnd;
				
		}	
		
		System.out.println("-------------------------------------------------------");
		System.out.println(tempStringDecBla + temp);	
		System.out.println("current depth " + currentUsage);
		System.out.println("-------------------------------------------------------");
			if(decCompSymbol.equals(">")){
				
				//System.out.println("compString equals >: " +compString.equals(">"));
				mainVisitor.visitJumpInsn(IF_ICMPLE, temp);
				//compString = "IF_ICMPLE"; 
				//operation = ">"+operation;
				
			} else if(decCompSymbol.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE,temp);
				//compString = "IF_ICMPGE"; 
				//operation = "<"+operation;
			
			} else if(decCompSymbol.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, temp);
				//compString = "IF_ICMPEQ";
				//operation = "<>"+operation;
			
			} else if(decCompSymbol.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, temp);
				//compString = "IF_ICMPNE";	
				//operation = "="+operation;
			
			}
	

	}
	
	
	
	@Override 
	public void exitDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;
		
		System.out.println("decNestStack = " + decNestStack);	
		//decCount2--;
		//System.out.println("decCount2 = " + decCount2);
		
		//if(elseCount == 0){
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
				//case 6: { }
				//case 7: { }
				//case 8: { }
				//case 9: { }
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
		//}
		//System.out.println("decLabCount = " + decLabCount);
		System.out.println("-------------------------------------------------------");
		System.out.println("Visit end label dec= " + temp);
		System.out.println("current depth " + currentUsage);
		//System.out.println("else label = " + temper);
		System.out.println("-------------------------------------------------------");
		
		//ASM
		mainVisitor.visitLabel(temp);
		
		if(decNestStack.length() != 0)
			decNestStack = decNestStack.substring(1);
		System.out.println("Current decision depth stack = " + decNestStack);
		
		
		
		int tempBoolElse = peek(decElseStacker);
		//int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
		
		if(tempBoolElse > 0){
	      //if(decElseStack.length() > 3 && tempBoolElse > 0){
		
			int newUsage = peek(decIfStacker);
			//int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(newUsage == 1){
			      //if(decIfStack.length() > 3 && newUsage == 1){		
					System.out.println("\nTime to visit the if else\n");
					
			
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decElseStacker);
					System.out.println();
					
					
					/*
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					*/
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decIfStacker);
					System.out.println();
					/*
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					*/
					
				}
			
			
		}
		
		//System.out.println("newStack = " + decElseStack);
		
			
		
		System.out.println("Exit Decision");
		System.out.println("------------------------------------------------------------------------------------------------------------\n\n");	
	}
	
	
	/**
	 * Print
	 *
	 *
	 */
	@Override
	public void enterPrint(KnightCodeParser.PrintContext ctx){
		if(exit)
			return;
		//System.out.println("-----------------------------------------------------------------");
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
		
	
	}//end enterWrite_stmt

	@Override 
	public void exitPrint(KnightCodeParser.PrintContext ctx){ 
		if(exit)
			return;
		//Label printskip = new Label();
	
		
		if(genPrint){
			System.out.println("\nString will be printed to bytecode file\n");
			
			
			mainVisitor.visitLdcInsn(outputString);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
		} else {
			if(printString){
				System.out.println("\nString will be printed to bytecode file\n");
				
				
				mainVisitor.visitVarInsn(ALOAD, outputInt);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
			} else {
				System.out.println("\nInt will be printed to bytecode file\n");
				
				
				mainVisitor.visitVarInsn(Opcodes.ILOAD, outputInt);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
			}
		}
		
		
		int tempBoolElse = peek(decElseStacker);
		//int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
		
		if(tempBoolElse > 0){
	      //if(decElseStack.length() > 3 && tempBoolElse > 0){
		
			int newUsage = peek(decIfStacker);
			//int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(newUsage == 1){
			      //if(decIfStack.length() > 3 && newUsage == 1){		
					System.out.println("\nTime to visit the if else\n");
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decIfStacker);
					System.out.println();
					
					
					/*
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					*/
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decElseStacker);
					System.out.println();
					/*
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					*/
					
				}
			
			
		}
		
		/*
		int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
	
		
		if(decElseStack.length() > 3 && tempBoolElse > 0){
		
		
			int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(decIfStack.length() > 3 && newUsage == 1){
					
					System.out.println("\nTime to visit the if else\n");
					
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					
					
				}
			
				
			if(ifCount1 > 0)
				ifCount1--;
			System.out.println("ifCount after decrement = " + ifCount1);
			System.out.println("decNestStack = " + decNestStack);	
		
		}
		*/
		
	
		System.out.println("Exit print");
	}
	
	
	
	
		
		
	public boolean alreadyRead = false;
	public int readStoredLocation;
	/**
	 * Read
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
			
		System.out.println("\n"+key);	
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
		
		
		/*
		
	    methodVisitor.visitTypeInsn(NEW, "java/util/Scanner");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitFieldInsn(GETSTATIC,"java/lang/System", "in", "Ljava/io/InputStream;");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V" , false);
            methodVisitor.visitVarInsn(ASTORE,store);
		
		*/


		if(alreadyRead){
			
		
		
		} else {
			alreadyRead = true;
			
			readStoredLocation = memoryCounter;
		
			mainVisitor.visitTypeInsn(NEW, "java/util/Scanner");
            		mainVisitor.visitInsn(DUP);
            		mainVisitor.visitFieldInsn(GETSTATIC,"java/lang/System", "in", "Ljava/io/InputStream;");
            		mainVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V" , false);
            		mainVisitor.visitVarInsn(ASTORE,readStoredLocation);
		
		
			memoryCounter++;
			
		}
		
		
		

	
	}
	
	
	@Override 
	public void exitRead(KnightCodeParser.ReadContext ctx){ 
		if(exit)
			return;
			
			
	
	
		
		System.out.println("ALOAD, readStoredLocation");
		mainVisitor.visitVarInsn(ALOAD,readStoredLocation);
		
		
          	
	
		genBool = isString(currvar);
			
		if(genBool){
	
			System.out.println("INVOKEVIRTUAL, nextLine");
			System.out.println("ASTORE, " + currvar.memLoc);
			
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
			mainVisitor.visitVarInsn(ASTORE,currvar.memLoc);
			
			//mainVisitor.visitLdcInsn(currvar.value);
			
		} else {
		
			System.out.println("INVOKEVIRTUAL, nextInt");
			System.out.println("ISTORE, " + currvar.memLoc);
			
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
			mainVisitor.visitVarInsn(ISTORE,currvar.memLoc);
			
			mainVisitor.visitVarInsn(ALOAD,readStoredLocation);
			mainVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
			mainVisitor.visitInsn(POP);
		}
		
		//currvar.value = "Value read from input";
		currvar.valueSet = true;
		SymbolTable.put(key, currvar);
		
		int tempBoolElse = peek(decElseStacker);
		//int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
		
		if(tempBoolElse > 0){
	      //if(decElseStack.length() > 3 && tempBoolElse > 0){
		
			int newUsage = peek(decIfStacker);
			//int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(newUsage == 1){
			      //if(decIfStack.length() > 3 && newUsage == 1){		
					System.out.println("\nTime to visit the if else\n");
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decIfStacker);
					System.out.println();
					
					
					/*
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					*/
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decElseStacker);
					System.out.println();
					/*
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					*/
					
				}
			
			
		}
		
		
		/*
		int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
	
		
		if(decElseStack.length() > 3 && tempBoolElse > 0){
		
		
			int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(decIfStack.length() > 3 && newUsage == 1){
					
					System.out.println("\nTime to visit the if else\n");
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					
				}
			
			/*	
			if(ifCount1 > 0)
				ifCount1--;
			System.out.println("ifCount after decrement = " + ifCount1);
			System.out.println("decNestStack = " + decNestStack);	
			
			
		}
		*/
		
		
		
		System.out.println("Exit read\n");
		

	}

	
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
	 *
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
		System.out.println("Current stack = " + loopNestStack);	
	
		/*
		decNestStack = decLabCount + decNestStack;
		System.out.println("Current stack = " + decNestStack);
		*/
		int syntaxTest = ctx.getChildCount();
		if(syntaxTest < 7){
		
			System.out.println("\n\n------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
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
		
		//String loopOp1;
		//int loopOperator1;
		//String loopOp2;
		//int loopOperator2;
		//String loopCompSymbol;
		
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
        			//System.out.println(e.getMessage());
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
				
				//System.out.println("compString equals >: " +compString.equals(">"));
				mainVisitor.visitJumpInsn(IF_ICMPLE, tempEnd);
				//compString = "IF_ICMPLE"; 
				//operation = ">"+operation;
				
			} else if(decCompSymbol.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE,tempEnd);
				//compString = "IF_ICMPGE"; 
				//operation = "<"+operation;
			
			} else if(decCompSymbol.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, tempEnd);
				//compString = "IF_ICMPEQ";
				//operation = "<>"+operation;
			
			} else if(decCompSymbol.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, tempEnd);
				//compString = "IF_ICMPNE";	
				//operation = "="+operation;
			
			}
			
			System.out.println("-------------------------------------------------------");
			System.out.println("Visit Start of loop label = " + temp);
			System.out.println("IFICMP, " + tempEnd);
			System.out.println("-------------------------------------------------------");
			
		
	}
	
	
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
				//case 6: { }
				//case 7: { }
				//case 8: { }
				//case 9: { }
				default: {
	
			
					System.out.println("\n\n------------------------------------------");
					System.out.println("COMPILER ERROR");
					System.out.println("------------------------------------------");
				
					System.out.println("jump label failure for if-else statement at exit");
					
					exit = true;
					return;
				}	
					
			}
			
		
		System.out.println("-------------------------------------------------------");
		System.out.println("GOTO = " + temper);
		System.out.println("Visit end label loop = " + temp);
		System.out.println("-------------------------------------------------------");
		
		//ASM
		mainVisitor.visitJumpInsn(GOTO,temper);
		mainVisitor.visitLabel(temp);
		
		  
		if(loopNestStack.length() != 0)
			loopNestStack = loopNestStack.substring(1);
		
		System.out.println("Current stack = " + loopNestStack);	
			
		int tempBoolElse = peek(decElseStacker);
		//int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
		
		if(tempBoolElse > 0){
	      //if(decElseStack.length() > 3 && tempBoolElse > 0){
		
			int newUsage = peek(decIfStacker);
			//int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(newUsage == 1){
			      //if(decIfStack.length() > 3 && newUsage == 1){		
					System.out.println("\nTime to visit the if else\n");
					
					
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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					pop(decElseStacker);
					pop(decIfStacker);
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decIfStacker);
					System.out.println();
					
					
					/*
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					*/
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					newUsage = pop(decIfStacker);
					newUsage--;
					push(decIfStacker,newUsage);
					
					System.out.print("updated if stacker: ");printStack(decIfStacker);
					System.out.print("\nupdated else stacker: ");printStack(decElseStacker);
					System.out.println();
					/*
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					*/
					
				}
			
			
		}
		
		/*
		int tempBoolElse = Character.getNumericValue(decElseStack.charAt(0));
	
		
		if(decElseStack.length() > 3 && tempBoolElse > 0){
		
		
			int newUsage = Character.getNumericValue(decIfStack.charAt(0));
		
			System.out.println("current newUsage = " + newUsage );
			
				if(decIfStack.length() > 3 && newUsage == 1){
					
					System.out.println("\nTime to visit the if else\n");
					

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
					System.out.println("-------------------------------------------------------");	
					System.out.println("GOTO, end label= " + tempEnd);
					System.out.println("Visit startOfElse Label= " + temper);
					System.out.println("current depth " + currentUsage);
					System.out.println("-------------------------------------------------------");
		
					mainVisitor.visitJumpInsn(GOTO, tempEnd);
					mainVisitor.visitLabel(temper);
					
					
					decElseStack = decElseStack.substring(1);
					decIfStack = decIfStack.substring(1);
					
					System.out.println("updated if stack: " + decIfStack);
					System.out.println("updated else stack: " + decElseStack);
					
					
					
					
					
					//if(decNestStack.length() != 0)
					//	decNestStack = decNestStack.substring(1);
					//System.out.println("Current stack = " + decNestStack);
				
				
				
					elseVisitor--;
				} else if(newUsage > 1){
					System.out.println("\nNot time for start of else yet!\n");
					
					
					decIfStack = decIfStack.substring(1);
					//decElseStack = decElseStack.substring(1);
					
					newUsage--;
					//tempBoolElse--;
					
					decIfStack = newUsage + decIfStack;
					//decElseStack = tempBoolElse + decElseStack;
					
					System.out.println("updated decIfStack: " + decIfStack);
					System.out.println("updated decElseStack: " + decElseStack);
					
				}
			
				
			if(ifCount1 > 0)
				ifCount1--;
			System.out.println("ifCount after decrement = " + ifCount1);
			System.out.println("decNestStack = " + decNestStack);	
			
			
		}
		*/
		
			
		System.out.println("Exit loop");
	}
	
	
	/**
	 * Prints context string. Used for debugging purposes
	 * @param ctx
	 */
	private void printContext(String ctx){
		System.out.println(ctx);
	}
	@Override 
	public void enterEveryRule(ParserRuleContext ctx){
	 	if(debug) 
	 		printContext(ctx.getText()); 
	 }

}//end class

