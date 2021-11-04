package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import static org.objectweb.asm.Opcodes.*;

import lexparse.*; //classes for lexer parser
import java.util.*;

public class myListener3 extends KnightCodeBaseListener{





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
	
	
	public int count;
	public String outputString;
	public int outputInt;
	public String key;
	public String key2;
	public variable currvar;
	public variable extravar;
	
	public int num;
	public String id;
	
	public variable var1;
	public variable var2;
	
	public String genNum;
	public String genIntStr;
	public String genString;
	public String op1 = "";
	public String op2 = "";
	public int operator1;
	public int operator2;
	public boolean printString;
	public boolean operationDone;
	public boolean genBool;
	public boolean expre;
	
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
	
	
	
	

	public myListener3(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor
	
	public myListener3(String programName){
	       
		this.programName = programName;
		debug = false;

	}//end constructor


	public void setupClass(){
		
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
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(3, 3);
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

		System.out.println("Leaving program rule. . .");
		closeClass();

	}


	@Override 
	public void enterDeclare(KnightCodeParser.DeclareContext ctx){
		System.out.println("Enter declare");
		
		count = ctx.getChildCount();
		/*
		int i = 1;
		while(i < count){
			System.out.println(ctx.getChild(i));
			i++;
			
		}
		*/
	}
	@Override 
	public void exitDeclare(KnightCodeParser.DeclareContext ctx){
		printHashMap(SymbolTable);
		
		System.out.println("Exit declare");
	}

	@Override 
	public void enterVariable(KnightCodeParser.VariableContext ctx){
		
		System.out.println("Enter variable");
		
		variable var = new variable();
		
		String identifier = ctx.getChild(1).getText();
		var.variableType = ctx.getChild(0).getText();
		var.memLoc = memoryCounter;
		
		
		/*
		if(var.variableType.equals("INTEGER")){
			var.value = (int)5;
		} 
		
		*/
		
		
		SymbolTable.put(identifier, var);
		/*
		if(vartype.equals(INT){
			
			
		}else{
			
		}
		*/
		
		memoryCounter++;
		
	
	}
	@Override 
	public void exitVariable(KnightCodeParser.VariableContext ctx){ 
	
		System.out.println("Exit variable");
	}
	
	@Override 
	public void enterIdentifier(KnightCodeParser.IdentifierContext ctx){
	
	}
	
	@Override 
	public void exitIdentifier(KnightCodeParser.IdentifierContext ctx){ 
	
	}
	
	
	
	
	@Override 
	public void enterBody(KnightCodeParser.BodyContext ctx){ 
		System.out.println("Enter body!");
		
		count = ctx.getChildCount();
		//System.out.println("Count: " + count);
		
		
	}
	@Override 
	public void exitBody(KnightCodeParser.BodyContext ctx){ 
		
		
		printHashMap(SymbolTable);
		
		System.out.println("Exit body!");
	}
	
	@Override 
	public void enterSetvar(KnightCodeParser.SetvarContext ctx){ 
		System.out.println("Enter setvar");
		
		if(ctx.getChild(1) != null)
			key = ctx.getChild(1).getText();
			
		System.out.println("\n"+key);	
		currvar = SymbolTable.get(key);
			
			
			
			
	}
	
	@Override 
	public void exitSetvar(KnightCodeParser.SetvarContext ctx){ 
	
		boolean skip = false;
	
		if(operationDone){
			currvar.value = genIntStr;
			skip = true;
			
		}	
			
		//Already in variable;	
		//currvar.memLoc = memoryCounter;
		SymbolTable.put(key, currvar);
		
		//ASM bytecode stuff
		if(!skip){
		
			int store = currvar.memLoc;
		
			genBool = isString(currvar);
			
		
			if(genBool){
				mainVisitor.visitLdcInsn(currvar.value);
			} else {
				num = Integer.valueOf(currvar.value);
				mainVisitor.visitIntInsn(SIPUSH, num);
			}
            		mainVisitor.visitVarInsn(ISTORE,store);	
            	
            	}
            	
            	
		
		
		
		
		//Already in variable
		//memoryCounter++;
		System.out.println("Exit setvar");
	}
	
	
	@Override 
	public void enterNumber(KnightCodeParser.NumberContext ctx){ 
		System.out.println("Enter Number");
		genNum = ctx.getText();
		currvar.value = genNum;
		
		
		//Test 			WORKS!
		//System.out.println("num = " + num);
		
	}
	
	@Override 
	public void exitNumber(KnightCodeParser.NumberContext ctx){ 
	
	
		System.out.println("Exit Number");
	}
	
	
	@Override 
	public void enterId(KnightCodeParser.IdContext ctx){ 
		System.out.println("enter ID");
		
		/*
		key2 = ctx.getText();
		extravar = SymbolTable.get(key);
		*/
		
	}
	@Override 
	public void exitId(KnightCodeParser.IdContext ctx){ 
	
	
	
		/*
		genString = extravar.variableType;
		
		
		if(extravar.value != null){
		
			System.out.println("val is not null");
			if(genString.equals(INT)){
			
				isInteger = true;
			
				//		    \
				//Error right here  V
			
				outputInt = (int)extravar.value;
				
				//System.out.println("About to exit ID");
			
			} else{
				//System.out.println("About to exit ID");
				outputString = String.valueOf(extravar.value);
					
			}
		}
		*/
	
	
		System.out.println("Exit ID");
	}
	
	
	
	
	@Override 
	public void enterAddition(KnightCodeParser.AdditionContext ctx){ 
		System.out.println("Enter addition");
		
		var1 = SymbolTable.get(ctx.getChild(0).getText());
		op1 = var1.value;
		operator1 = var1.memLoc;
		//System.out.println("\n " + op1);
		
		var2 = SymbolTable.get(ctx.getChild(2).getText());
		op2 = var2.value;
		operator2 = var2.memLoc;
		//System.out.println("\n " + op2);
		
		//operator1 = Integer.valueOf(op1);
		//operator2 = Integer.valueOf(op2);	
	}
	
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx){ 
	
		genIntStr = op1 + " + " + op2;
		operationDone = true;
		
		
		//ASM byte code stuff
		
		int store = currvar.memLoc;
		
		
		mainVisitor.visitIntInsn(ILOAD, operator1);
		mainVisitor.visitIntInsn(ILOAD, operator2);
		mainVisitor.visitInsn(IADD);
            	mainVisitor.visitVarInsn(ISTORE,store);	
            	
		System.out.println("Exit addition");
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



	
	



	@Override
	public void enterPrint(KnightCodeParser.PrintContext ctx){
		System.out.println("Enter print");
		

		key2 = ctx.getChild(1).getText();

		//ASM bytecode
		mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
		
		if(SymbolTable.containsKey(key2)){
		
			extravar = SymbolTable.get(key2);
			
			if(isString(extravar)){
				
				outputString = extravar.value;
			
				printString = true;
				
			} else { 
				outputInt = extravar.memLoc;
				
				printString = false;
			}
			
		
		} else {
			outputString = key2; 
		}
		//printString = true;
		
		//String output = ctx.getText();
		
		
		

	}//end enterWrite_stmt

	@Override 
	public void exitPrint(KnightCodeParser.PrintContext ctx){ 
	
	
		if(printString){
			System.out.println("\nString will be printed to bytecode file\n");
			mainVisitor.visitLdcInsn(outputString);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
		} else {
			System.out.println("\nInt will be printed to bytecode file\n");
			mainVisitor.visitVarInsn(Opcodes.ILOAD, outputInt);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		
		}
	
		
	
	
	/*
		if(printString){
			System.out.println("\nString will be printed to bytecode file\n");
			mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mainVisitor.visitLdcInsn(outputString);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);
		} else {
			System.out.println("\nInt will be printed to bytecode file\n");
			mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
			//mainVisitor.visitVarInsn(outputInt);
			mainVisitor.visitVarInsn(Opcodes.ILOAD,2);
			mainVisitor.visitIntInsn(Opcodes.BIPUSH, outputInt);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);
		
		}
		
		//printString = false;
		
	*/	
	
	
	/*
			System.out.println("\nString will be printed to bytecode file\n");
			mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mainVisitor.visitLdcInsn(outputString);
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);	
	*/
		
		System.out.println("Exit print");
	}
	

	



}//end class
