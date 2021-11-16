


package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import lexparse.*; //classes for lexer parser
import java.util.*;


public class myVisitor extends KnightCodeBaseVisitor {
    
    public int memoryCount = 0;
    public HashMap<String, variable> SymbolTable = new HashMap<String, variable>();

    public String fileName = "";

    private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor

    public class variable{

        public String variableType;
        public Object value;
        public int memory;
        
    
        public variable(String variableType, Object value, int memory){
            this.variableType = variableType;
            this.value = value;
            this.memory = memory;
        
        }
        
        public variable(){
            variableType = "";
            value = null;
        }
    
    
    }

    public myVisitor(String filestuff){

        this.fileName = filestuff;
        setupASM();

    }// end constructor

    public void setupASM(){

        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        	cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,"output/"+this.fileName, null, "java/lang/Object",null);
	
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

    }// end setupASM

    public void endASM(){

        //Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(3, 3);
		mainVisitor.visitEnd();

		cw.visitEnd();

        	byte[] b = cw.toByteArray();



        	Utilities.writeFile(b,"output/"+this.fileName+".class");
        
        	System.out.println("Done!");

    }// end endASM

    
    @Override 
    public Object visitFile(KnightCodeParser.FileContext ctx) { 
        
        System.out.print(ctx.getText());

        return super.visitChildren(ctx);
    
    }

    @Override 
    public Object visitVariable(KnightCodeParser.VariableContext ctx) { 
        

        variable var = new variable();
		
		String identifier = ctx.getChild(1).getText();
		var.variableType = ctx.getChild(0).getText();
        var.memory = memoryCount;
        System.out.print("    " +identifier + " " + var.variableType +" "+ var.memory+"   " );
        SymbolTable.put(identifier, var);

       

        memoryCount++;

        return super.visitChildren(ctx); 
    
    
    }

    
    @Override 
    public Object visitRead(KnightCodeParser.ReadContext ctx) { 

        variable var = SymbolTable.get(ctx.getChild(1).getText());
       

        mainVisitor.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mainVisitor.visitInsn(Opcodes.DUP);
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System", "in", "Ljava/io/InputStream;");
        mainVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/Scanner", "<init>", "(Ljava/io/InputStream;)V" , false);
        mainVisitor.visitVarInsn(Opcodes.ASTORE,memoryCount);

        if(var.variableType.charAt(0) == 'S'){

           
            mainVisitor.visitVarInsn(Opcodes.ALOAD, memoryCount);
            mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextLine", "()Ljava/lang/String;", false);
            mainVisitor.visitVarInsn(Opcodes.ASTORE, var.memory);


        }
        else{

           
            mainVisitor.visitVarInsn(Opcodes.ALOAD, memoryCount);
            mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/Scanner", "nextInt", "()I", false);
            mainVisitor.visitVarInsn(Opcodes.ISTORE, var.memory);

        }

        memoryCount++;
        return super.visitChildren(ctx);
    
    
    }
    

    @Override 
    public Object visitSetvar(KnightCodeParser.SetvarContext ctx) { 
    
        String setvar = "";
        String assignvar = ""; // will need to be messed with to get assignment to work right
        variable replacement = new variable();
        replacement.variableType = SymbolTable.get(ctx.getChild(1).getText()).variableType;

        setvar = ctx.getChild(1).getText(); assignvar = ctx.getChild(3).getText();
        replacement.value = assignvar;

        SymbolTable.replace(setvar, replacement);

        variable var = SymbolTable.get(ctx.getChild(1).getText());


        if(var.variableType == "STRING"){

            mainVisitor.visitLdcInsn(var.value);
			mainVisitor.visitVarInsn(Opcodes.ASTORE,var.memory);

        }
        else if(var.variableType == "INTEGER"){

            int x = (int)var.value;
            mainVisitor.visitIntInsn(Opcodes.SIPUSH, x);
            mainVisitor.visitVarInsn(Opcodes.ISTORE, var.memory);

        }
        

        return super.visitChildren(ctx); 
    
    }
/*
    @Override 
    public Object visitAddition(KnightCodeParser.AdditionContext ctx) { 
        

        // doesn't work w/ asm yet
        String output = "";
        int op1 =0;
        int op2 =0;
        op1 = (int)SymbolTable.get(ctx.getChild(1).getText()).value;
        op2 = (int)SymbolTable.get(ctx.getChild(2).getText()).value;
        
        System.out.print(op1+op2);

        return super.visitChildren(ctx); 

    }*/

    @Override 
    public Object visitPrint(KnightCodeParser.PrintContext ctx) { 
        
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");

        if(ctx.getChild(1).getText().charAt(0)=='"'){

            System.out.print(ctx.getChild(1).getText());

            mainVisitor.visitLdcInsn(ctx.getChild(1).getText());
			mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);

        }
        else{

            variable var = new variable();
            var = SymbolTable.get(ctx.getChild(1).getText());

            if(var.variableType == "STRING"){

                mainVisitor.visitVarInsn(Opcodes.ALOAD, var.memory);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream",  "println", "(Ljava/lang/String;)V", false);

            }
            else if(var.variableType == "INTEGER"){

                mainVisitor.visitVarInsn(Opcodes.ILOAD, var.memory);
				mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

            }

          String output = (String)var.value;

          System.out.print(output);

        }

        


        return super.visitChildren(ctx); 
    
    }

   

    

}// end class myVisitor
