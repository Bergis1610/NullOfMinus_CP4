package tests;

import java.util.*;

public class tester3{


	public static void main(String[]args){
	
	
		int x;
		
		
		System.out.println("Enter a number");
		Scanner scan = new Scanner(System.in);
		x = scan.nextInt();
		
		while(x > 0){
			System.out.println(x);
			x = x - 1;
		}
		
		
	
	
	
	
	
	
	
	
	/*
		int x = 10;
		int y = 11;
		int f = 12;
		int e = 13;
		int z = x+y+f+e;
	*/
	/*
		String x;
		//int y;
		//int y = 0;
		//int z;
		
		System.out.println("ENTER A STRING: ");
		
		Scanner scan = new Scanner(System.in);
		x = scan.nextLine();//scan.nextLine();
	
		System.out.println(x);
	*/	
	
		//y = scan.nextInt();//scan.nextLine();	
		//System.out.println(y);
	/*
		if(x>y){
		
			System.out.println("CORRECT");	
			
		} else {
		
			System.out.println("WRONG");
		
		}
*/
	
	
		
		//boolean z = (x>y);	
		// 20>5
		
	//	System.out.println(z);
		
		/*
		if(x>y)
			System.out.println("CORRECT");		
		else 
			System.out.println("WRONG");
		*/
		
		//boolean z = (x==y);	
		// 20>5
		
		
		//System.out.println(z);
		
	
	/*
		String str = "Hello there";
		
		System.out.println(str);	
	*/
	
	}

/*
	//ASM byte code section
 
 	//x
 		mv.visitIntInsn(BIPUSH, 10);
            	mv.visitVarInsn(ISTORE,1);
            	
        //y    	
		mv.visitIntInsn(BIPUSH, 11);
		mv.visitVarInsn(ISTORE,2);
	
	 //f 	
		mv.visitIntInsn(BIPUSH, 12);
		mv.visitVarInsn(ISTORE,3);
		
	//e 	
		mv.visitIntInsn(BIPUSH, 13);
		mv.visitVarInsn(ISTORE,4);
	
	//z = x + y + f + e	
           	mv.visitVarInsn(ILOAD,1);
           	
            	mv.visitVarInsn(ILOAD,2);
		mv.visitInsn(IADD);
		
		mv.visitVarInsn(ILOAD,3);
		mv.visitInsn(IADD);
		
		mv.visitVarInsn(ILOAD,4);
		mv.visitInsn(IADD);
		
		mv.visitVarInsn(ISTORE,5);
		
		
		
		
	//print	
		mv.visitFieldInsn(GETSTATIC,"java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitVarInsn(ILOAD,5);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V", false);

*/

/*


IF 10 >1 
THEN 
PRINT "CORRECT 1" 			
ENDIF

IF 10 > 1 
THEN 
PRINT "CORRECT 2" 		
ENDIF

IF 10 > 1 
THEN 
PRINT "CORRECT 3"   			
ENDIF
IF 10 > 1 
THEN 
PRINT "CORRECT 4" 			
ENDIF
IF 10 > 1 
THEN 
PRINT "CORRECT 5" 			
ENDIF
*/






	
}	
