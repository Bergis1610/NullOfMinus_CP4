package tests;

public class program1{

	public static void main(String[]args){
	
		int x = 10;
		int y = 12;
		int z = x+y;
		
		System.out.println(z);
	
	
	}
	
	
/**	ASM byte code section
 
 		mv.visitIntInsn(BIPUSH, 10);
            	mv.visitVarInsn(ISTORE,1);
		mv.visitIntInsn(BIPUSH, 12);
		mv.visitVarInsn(ISTORE,2);
           	mv.visitVarInsn(ILOAD,1);
            	mv.visitVarInsn(ILOAD,2);
		mv.visitInsn(IADD);
		mv.visitVarInsn(ISTORE,3);
		
		mv.visitFieldInsn(GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ILOAD,3);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

 */	
	
	
	
	



}
