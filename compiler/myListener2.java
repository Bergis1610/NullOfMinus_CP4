package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import lexparse.*; //classes for lexer parser
import java.util.*;

public class myListener2 extends KnightCodeBaseListener{





	private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status
	
	
//Here are general stuff that I made


public class variable{

	public String variableType;
	public Object value;

	public variable(String variableType, Object value){
		this.variableType = variableType;
		this.value = value;
	
	}
	
	public variable(){
		variableType = "";
		value = null;
	}


}

	public void printHashMap(HashMap<String,variable> map){
	
		Object[]keys = map.keySet().toArray();
		Object val;
		
		for(int i = 0; i < keys.length; i++){
			System.out.print(keys[i]);
			System.out.print(": " + map.get(keys[i]).variableType); 
			val = map.get(keys[i]).value;
			if(val != null)
				System.out.print(", " + val);
			System.out.println("");
			
		} 	
		
	}


	
	public int count;
	public HashMap<String, variable> SymbolTable = new HashMap<String, variable>();
	
	public String integ = "INTEGER";
	public String str = "STRING";
	
	
	
	
	
	
//End general stuff	
	
	
	
	

	public myListener2(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor
	
	public myListener2(String programName){
	       
		this.programName = programName;
		debug = false;

	}//end constructor


	public void setupClass(){
		
		//Set up the classwriter
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        	cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,"output/"+this.programName, null, "java/lang/Object",null);
	
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



        	Utilities.writeFile(b,"output/"+this.programName+".class");
        
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
		//count = ctx.getChildCount();
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
	}

	

	@Override 
	public void enterVariable(KnightCodeParser.VariableContext ctx){
		
		variable var = new variable();
		
		String identifier = ctx.getChild(1).getText();
		var.variableType = ctx.getChild(0).getText();
		
		
		/*
		if(var.variableType.equals("INTEGER")){
			var.value = (int)5;
		} 
		
		*/
		
		
		SymbolTable.put(identifier, var);
		/*
		if(vartype.equals(integ){
			
			
		}else{
			
		}
		*/
		
		
	
	}
	@Override 
	public void exitVariable(KnightCodeParser.VariableContext ctx){ 
	
		
	}
	
	@Override 
	public void enterIdentifier(KnightCodeParser.IdentifierContext ctx){
	
	}
	
	@Override 
	public void exitIdentifier(KnightCodeParser.IdentifierContext ctx){ 
	
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
	public void enterBody(KnightCodeParser.BodyContext ctx){ 
	
		
	}
	@Override 
	public void exitBody(KnightCodeParser.BodyContext ctx){ 
	
		
	}
	


/*
	@Override
	public void enterPrint(KnightCodeParser.PrintContext ctx){

		String output = ctx.getChild(1).getText();
		
		//String output = ctx.getText();
		
		
		mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mainVisitor.visitLdcInsn(output);
		mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);

	}//end enterWrite_stmt

	@Override 
	public void exitPrint(KnightCodeParser.PrintContext ctx){ 
	
		
	}
	
*/
	



}//end class
