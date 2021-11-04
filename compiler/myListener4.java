package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

import lexparse.*; //classes for lexer parser
import java.util.*;

public class myListener4 extends KnightCodeBaseListener{

	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status
	
	
//Here are general stuff that I made


public class variable{

	public String variableType = "";
	public String value = "";
	public int memLoc = -1;


	public variable(String variableType, String value){
		this.variableType = variableType;
		this.value = value;
	
	}
	
	public variable(){
		variableType = "";
		value = "";
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
	public String genIntStr;
	public String genString;
	public String op1 = "";
	public String op2 = "";
	public String operation = " ";
	
	
	public int operator1;
	public int operator2;
	public int num;
	public int outputInt;
	public int count;
	public int skipCount = 0;
	
	
	public boolean printString;
	public boolean operationDone;
	public boolean genBool;
	public boolean genPrint;
	public boolean expre;
	public boolean enter;
	public boolean exit = false;
	//public boolean parenthesisWait;
	
	public int memoryCounter = 1;
	//public boolean changeVar;
	
	
	
	public void printHashMap(HashMap<String,variable> map){
	
		Object[]keys = map.keySet().toArray();
		String val;
		int mem;
		
		for(int i = 0; i < keys.length; i++){
			System.out.print(keys[i]);
			System.out.print(": " + map.get(keys[i]).variableType); 
			val = map.get(keys[i]).value;
			mem = map.get(keys[i]).memLoc;
			System.out.println(", " + val + ", " + mem);
			
		} 	
		
	}
	
	public boolean isString(variable var){
		
		if(var.variableType.equals(STR))
			return true;
		return false;
	}
	
//End general stuff	
	
	
	
	

	public myListener4(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor
	
	public myListener4(String programName){
	       
		this.programName = programName;
		debug = false;

	}//end constructor


	//Labels
		Label lable1 = new Label();
        	Label lable2 = new Label();
        	Label lable3 = new Label();
        	Label startOfLoop = new Label();
        	Label endOfLoop = new Label();
            	Label returnl = new Label();

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
        	
        	
        	

	}//end setupClass
	
		

	public void closeClass(){
	
		if(exit)
			return;
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		
		//mainVisitor.visitLabel(returnl);
		
		
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(20, 20);
		mainVisitor.visitEnd();

		cw.visitEnd();

        	byte[] b = cw.toByteArray();



        	Utilities.writeFile(b,this.programName+".class");
        
        	System.out.println("Done!");

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

		
		closeClass();
		System.out.println("Leaving program rule. . .");

	}


	@Override 
	public void enterDeclare(KnightCodeParser.DeclareContext ctx){
		if(exit)
			return;
			
		System.out.println("Enter declare");
		
		enter = false;
		count = ctx.getChildCount();
		
	}
	@Override 
	public void exitDeclare(KnightCodeParser.DeclareContext ctx){
		if(exit)
			return;
			
		printHashMap(SymbolTable);
		
		enter = true;
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
		
		System.out.println("Exit body!");
	}
	
	
	
	@Override public void enterStat(KnightCodeParser.StatContext ctx) { }
	@Override public void exitStat(KnightCodeParser.StatContext ctx) { }
	
	
	
	@Override 
	public void enterSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
			
		System.out.println("Enter setvar");
		genIntStr = "";
		
		if(ctx.getChild(1) != null)
			key = ctx.getChild(1).getText();
			
		System.out.println("\n"+key);	
		if(SymbolTable.containsKey(key)){
			currvar = SymbolTable.get(key);
		} else {
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Identifier: " + key + " was not declared");
			exit = true;
			return;
		
		}
		
		if(isString(currvar)){
			genIntStr = ctx.getChild(3).getText();
		}
	
	}
	
	@Override 
	public void exitSetvar(KnightCodeParser.SetvarContext ctx){ 
		if(exit)
			return;
	
		
		currvar.value = genIntStr;
		SymbolTable.put(key, currvar);
		
		//ASM bytecode stuff
		int store = currvar.memLoc;
		
		genBool = isString(currvar);
			
		if(genBool){
			mainVisitor.visitLdcInsn(currvar.value);
			mainVisitor.visitVarInsn(ASTORE,store);
		} else {
			
			mainVisitor.visitVarInsn(ISTORE,store);
		}
    	    
		System.out.println("Exit setvar");
	}
	
	
	@Override 
	public void enterNumber(KnightCodeParser.NumberContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter Number");
		genIntStr = ctx.getText();		
	}
	
	@Override 
	public void exitNumber(KnightCodeParser.NumberContext ctx){ 
		if(exit)
			return;
	
		num = Integer.valueOf(genIntStr);	
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
			//op1 = var1.value;
			op1 = keyID;
			operator1 = var1.memLoc;
			mainVisitor.visitIntInsn(ILOAD, operator1);
		} else {
			System.out.println("------------------------------------------");
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
		
		genIntStr += operation.charAt(0);
		if(operation.length() != 0)
			operation = operation.substring(1);
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
		operation = ")" + operation;
		//parenthesisWait = true;
	
	}
	@Override 
	public void exitParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		//skipCount = 1;
		
		genIntStr += operation.charAt(0);
		if(operation.length() != 0)
			operation = operation.substring(1);
		
		
		//parenthesisWait = false;
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
		
		operation = "+" + operation;
		
		//Test
		//System.out.println(ctx.getChild(0).getChildCount());
		/*
		if(ctx.getChild(0).getChildCount() == 1){
			var1 = SymbolTable.get(ctx.getChild(0).getText());
			
			if(var1 != null){
				op1 = var1.value;
				operator1 = var1.memLoc;
				mainVisitor.visitIntInsn(ILOAD, operator1);
			}
			genIntStr = op1;
		}
		*/
		
		//Test
		//System.out.println(ctx.getChild(0).getText());	


	}
	
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;
		
		/*
		var2 = SymbolTable.get(ctx.getChild(2).getText());
		if(var2 != null){
			op2 = var2.value;
			operator2 = var2.memLoc;
		}
	
		genIntStr += " + " + op2;
		operationDone = true;
		
		*/
		
		//ASM byte code stuff
	
		//mainVisitor.visitIntInsn(ILOAD, operator2);
		
		
		
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
		operation = "*" + operation;
		
		/*
		if(ctx.getChild(0).getChildCount() == 1){
			var1 = SymbolTable.get(ctx.getChild(0).getText());
			
			if(var1 != null){
				op1 = var1.value;
				operator1 = var1.memLoc;
				mainVisitor.visitIntInsn(ILOAD, operator1);
			}
			genIntStr = op1;
		}
		*/
		
	}
	@Override 
	public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx){ 
		if(exit)
			return;
	/*
		var2 = SymbolTable.get(ctx.getChild(2).getText());
		if(var2 != null){
			op2 = var2.value;
			operator2 = var2.memLoc;
		}
	
		//" * " + 
		genIntStr += " * " + op2 ;
		operationDone = true;
		
		//ASM byte code stuff
	
		//if(skipCount == 0)
		mainVisitor.visitIntInsn(ILOAD, operator2);
		
		
	*/	
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
		operation = "/"+operation;
		/*
		
		if(ctx.getChild(0).getChildCount() == 1){
			var1 = SymbolTable.get(ctx.getChild(0).getText());
			
			if(var1 != null){
				op1 = var1.value;
				operator1 = var1.memLoc;
				mainVisitor.visitIntInsn(ILOAD, operator1);
			}
			genIntStr = op1;
		}
		*/
		
	}
	@Override 
	public void exitDivision(KnightCodeParser.DivisionContext ctx){ 
		if(exit)
			return;
		/*
		var2 = SymbolTable.get(ctx.getChild(2).getText());
		if(var2 != null){
			op2 = var2.value;
			operator2 = var2.memLoc;
		}
	
		genIntStr += " / " + op2;
		operationDone = true;
		
		//ASM byte code stuff
		//if(skipCount == 0)
		mainVisitor.visitIntInsn(ILOAD, operator2);
		
		
		*/
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
		operation = "-"+operation;
		/*
		
		if(ctx.getChild(0).getChildCount() == 1){
			var1 = SymbolTable.get(ctx.getChild(0).getText());
			
			if(var1 != null){
				op1 = var1.value;
				operator1 = var1.memLoc;
				mainVisitor.visitIntInsn(ILOAD, operator1);
			}
			genIntStr = op1;
		}
		
		*/
	}
	@Override 
	public void exitSubtraction(KnightCodeParser.SubtractionContext ctx){ 
		if(exit)
			return;
			
		/*	
		var2 = SymbolTable.get(ctx.getChild(2).getText());
		if(var2 != null){
			op2 = var2.value;
			operator2 = var2.memLoc;
		}
	
		genIntStr += " - " + op2;
		operationDone = true;
		
		//ASM byte code stuff
	
		//if(skipCount == 0)
		mainVisitor.visitIntInsn(ILOAD, operator2);
		
		*/
		mainVisitor.visitInsn(ISUB);
            		
            	
		System.out.println("Exit subtraction");
	}
	
	
	

	/**
	 * Comparison
	 * comp: GT | LT | EQ | NEQ
	 *
	 */
	@Override 
	public void enterComparison(KnightCodeParser.ComparisonContext ctx){ 
	
	}
	@Override 
	public void exitComparison(KnightCodeParser.ComparisonContext ctx){ 
	
	}
	 
	@Override 
	public void enterComp(KnightCodeParser.CompContext ctx){ 
	
	}
	@Override 
	public void exitComp(KnightCodeParser.CompContext ctx){ 
	
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
		System.out.println("Exit print");
	}
	
	/**
	 * Read
	 *
	 *
	 */
	@Override 
	public void enterRead(KnightCodeParser.ReadContext ctx){ 
	
	}
	@Override 
	public void exitRead(KnightCodeParser.ReadContext ctx){ 
	
	}
	
	
	/**
	 * Decision
	 *
	 *
	 */
	@Override 
	public void enterDecision(KnightCodeParser.DecisionContext ctx){ 
	
	}
	@Override 
	public void exitDecision(KnightCodeParser.DecisionContext ctx){ 
	
	}

	/**
	 * Loop
	 *
	 *
	 */
	@Override 
	public void enterLoop(KnightCodeParser.LoopContext ctx){ 
	
	}
	@Override 
	public void exitLoop(KnightCodeParser.LoopContext ctx){ 
	
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
