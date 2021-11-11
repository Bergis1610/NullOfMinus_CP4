


package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import lexparse.*; //classes for lexer parser
import java.util.*;


public class myVisitor extends KnightCodeBaseVisitor {
    

    public HashMap<String, variable> SymbolTable = new HashMap<String, variable>();

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
        System.out.print(identifier + "   " + var.variableType  );
        SymbolTable.put(identifier, var);


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
        

        return visitChildren(ctx); 
    
    }

    @Override 
    public Object visitPrint(KnightCodeParser.PrintContext ctx) { 
        
        variable var = new variable();
        var = SymbolTable.get(ctx.getChild(1).getText());

        String output = (String)var.value;

        System.out.print(output);


        return visitChildren(ctx); 
    
    }

    /*@Override 
    public Object VisitRead(KnightCodeParser.ReadContext ctx) { 

        return super.visitChildren(ctx);
    
    
    }*/

}// end class myVisitor
