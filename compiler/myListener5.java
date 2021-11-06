package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import org.objectweb.asm.Label;

import static org.objectweb.asm.Opcodes.*;

import lexparse.*; //classes for lexer parser
import java.util.*;

public class myListener5 extends KnightCodeBaseListener{

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
	public String operation = "  ";
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
	
//End general stuff	
	
	
	
	

	public myListener5(String programName, boolean debug){
	       
		this.programName = programName;
		this.debug = debug;

	}//end constructor
	
	public myListener5(String programName){
	       
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
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Identifier: " + key + " was not declared");
			exit = true;
			return;
		
		}
		
		if(isString(currvar)){
		
			if(ctx.getChild(3).getChildCount() != 0){
			
				System.out.println("------------------------------------------");
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
    	    
    	    	operation = "  ";
    	    	genIntStr = "";
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
			op1 = keyID;
			operator1 = var1.memLoc;
			
			
			if(var1.variableType.equalsIgnoreCase(STR) && operationCount > 0){
				System.out.println("------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
				
				System.out.println("Cannot perform arithmetic operations on a String");
				//System.out.println("ID: " + keyID + " does not exist!");
				exit = true;
				return;
			
			}
			
			
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
		if(printTwice){
			genIntStr += operation.charAt(0);
			if(operation.length() != 0)
				operation = operation.substring(1);
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
		operation = ")" + operation;
		
	
	}
	@Override 
	public void exitParenthesis(KnightCodeParser.ParenthesisContext ctx){ 
		if(exit)
			return;
		//skipCount = 1;
		
		genIntStr += operation.charAt(0);
		if(operation.length() != 0)
			operation = operation.substring(1);
		
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
		
		operation = "+" + operation;
	}
	
	@Override 
	public void exitAddition(KnightCodeParser.AdditionContext ctx){ 
		if(exit)
			return;
			
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
		operation = "*" + operation;
	
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
		operation = "/"+operation;
	
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
		operation = "-"+operation;
	
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
				operation = ">"+operation;
				
			} else if(compString.equals("<")){
				
				mainVisitor.visitJumpInsn(IF_ICMPGE, label1);
				//compString = "IF_ICMPGE"; 
				operation = "<"+operation;
			
			} else if(compString.equals("<>")){
			
				mainVisitor.visitJumpInsn(IF_ICMPEQ, label1);
				//compString = "IF_ICMPEQ";
				operation = "<>"+operation;
			
			} else if(compString.equals("=")){
			
				mainVisitor.visitJumpInsn(IF_ICMPNE, label1);
				//compString = "IF_ICMPNE";	
				operation = "="+operation;
			
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
	
	

	
	



	
	


	
	
	public static int ifCount;
	public static int elseCount;
	
	public static int decLabCount = 0;
	public int decCount2 = 0;
	
	
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
	
	
	
	/**
	 * Decision
	 *
	 *
	 */
	@Override 
	public void enterDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;
		System.out.println("Enter Decision");
		if(decLabCount > 4){
		
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Too many If-Else statements, compiler can only handle 5 or less!");
			exit = true;
			return;

		} else {
		
			decLabCount++;	
			decCount2++;		
		}
		System.out.println("decCount2 = " +decCount2);
		System.out.println("decLabCount = " + decLabCount);
		
		
		//Possible issue might occurr for nested if-else statements, because when you enter a decision these values reset.
		//Løsning til dette kan være å ha en array med en haug med sånne her så vi kan ha "nested"-enten-eller
		//Samme prinsippet for løkker
		ifCount = 0;
		elseCount = 0;
		
		decCount = ctx.getChildCount();
		if(decCount < 7){
		
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		
		}
		
		//IF
		
		if(!ctx.getChild(0).getText().equalsIgnoreCase("IF")){
		
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		
		}
		if(!ctx.getChild(4).getText().equalsIgnoreCase("THEN")){
		
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		
		}
		if(decCount != 7){ 
			if(ctx.getChild(decCount-2).getText().equalsIgnoreCase("ELSE")||ctx.getChild(5).getText().equalsIgnoreCase("ELSE")){
		
				System.out.println("------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("ELSE");
				System.out.println("Syntax is wrong for If-Else statement!");
			
				exit = true;
				return;
		
		
			}
		}	
		if(!ctx.getChild(decCount-1).getText().equalsIgnoreCase("ENDIF")){
		
			System.out.println("------------------------------------------");
			System.out.println("COMPILER ERROR");
			System.out.println("------------------------------------------");
			
			//System.out.println("ENDIF");
			System.out.println("Syntax is wrong for If-Else statement!");
			
			exit = true;
			return;
		
		
		}
		
		
	
		
		decOp1 = ctx.getChild(1).getText();
		if(SymbolTable.containsKey(decOp1)){
			var1 = SymbolTable.get(decOp1);
			
			if(!var1.valueSet||var1.variableType.equalsIgnoreCase(STR)){
				
				System.out.println("------------------------------------------");
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
        			System.out.println("------------------------------------------");
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
				
				System.out.println("------------------------------------------");
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
        			//System.out.println(e.getMessage());
        			System.out.println("------------------------------------------");
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
		
		Label temp; 
		switch(decLabCount){
			case 1: {
				temp = endDecLab0;
				break;
			}
			case 2: {
				temp = endDecLab1;
				break;
			}
			case 3: {
				temp = endDecLab2;
				break;
			}
			case 4: {
				temp = endDecLab3;
				break;
			}
			case 5: {
				temp = endDecLab4;
				break;
			}
			default: {
			
				System.out.println("------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("jump label failure for if-else statement at enter!");
				
				exit = true;
				return;
			}	
				
		}
			
		
		
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
	
		prev = "THEN";		
		tempInt = decCount-7;
		//System.out.println("Temp int = " +tempInt);
		
		//THEN
		while(tempInt > 0 && prev.equalsIgnoreCase("THEN")){
		
			if(ctx.getChild(decCount-tempInt-1).getText().equalsIgnoreCase("ELSE"))
				prev = "ELSE";
				
			//System.out.println("THEN statement!");	
			ifCount++;
			
			tempInt--;	
			
		}	
		//prev = "ELSE";
		//ELSE
		while(tempInt > 0){
		
			//System.out.println("ELSE statement!");
			elseCount++;
		
			tempInt--;	
		}
		
		//ENDIF
		
		System.out.println("ifCount: " + ifCount);
		System.out.println("elseCount: " + elseCount);
			
	
		

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
		
		
		System.out.println("Exit print");
	}
	
	
	@Override 
	public void exitDecision(KnightCodeParser.DecisionContext ctx){ 
		if(exit)
			return;
			
		//decCount2--;
		//System.out.println("decCount2 = " + decCount2);
		
		Label temp; 
		switch(decLabCount) {
		
			case 1: {
				temp = endDecLab0;
				break;
			}
			case 2: {
				temp = endDecLab1;
				break;
			}
			case 3: {
				temp = endDecLab2;
				break;
			}
			case 4: {
				temp = endDecLab3;
				break;
			}
			case 5: {
				temp = endDecLab4;
				break;
			}
			default: {
			
				System.out.println("------------------------------------------");
				System.out.println("COMPILER ERROR");
				System.out.println("------------------------------------------");
			
				System.out.println("jump label failure for if-else statement at exit");
				
				exit = true;
				return;
			}	
				
		}
		
		
		mainVisitor.visitLabel(temp);
		System.out.println("Exit Decision");
	}
	
	
		//while
			
		/*
		0 = IF

		1 = x

		2 = comp
	
		3 = y
		
		4 = THEN

		5 = 1_stat (1 or more)
	
		(6 or more) = ELSE
	
		(7 or more) = 2_stat (1 or more)
	
		(8 or more)	
		decCount = ENDIF 
		
		/*	
		if(ifCount == 1){
			System.out.println("Here should be label and GOTO");
			mainVisitor.visitJumpInsn(GOTO, secondPart);
			mainVisitor.visitLabel(printSkipIf);
		}
		if(ifCount != 0){	
			System.out.println("ifCount--");
			ifCount--;
		} else {
			System.out.println("elseCount--");
			elseCount--;
		}
		*/
		/*
		if(elseCount == 0){
				System.out.println("Here should be label only");
				mainVisitor.visitLabel(secondPart);
		}
		
				
		*/
		//mainVisitor.visitLabel(printskip2);


		
	
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
