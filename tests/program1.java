package tests;

public class program1{

	public static void main(String[]args){
	
		boolean l = true;
		
		if(l)
			System.out.println("ITS TRUE!");
		else 
			System.out.println("false");
		
		
	
	
	}
	
	
/**	ASM byte code section
 
 	//x
 		mv.visitIntInsn(BIPUSH, 10);
            	mv.visitVarInsn(ISTORE,1);
            	
        //y    	
		mv.visitIntInsn(BIPUSH, 12);
		mv.visitVarInsn(ISTORE,2);
	
	//z = x + y	   	
           	mv.visitVarInsn(ILOAD,1);
            	mv.visitVarInsn(ILOAD,2);
		mv.visitInsn(IADD);
		mv.visitVarInsn(ISTORE,3);
		
	//print	
		mv.visitFieldInsn(GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ILOAD,3);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

 */	
	
	
	
	



}
