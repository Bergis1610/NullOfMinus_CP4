


package compiler;

import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

import lexparse.*; //classes for lexer parser
import java.util.*;


public class myVisitor extends KnightCodeBaseVisitor {
    
    
    @Override 
    public Object visitFile(KnightCodeParser.FileContext ctx) { 
        
        System.out.print(ctx.getText());

        return visitChildren(ctx);
    
    }

}// end class myVisitor
