package compiler;

import java.io.*;

/**
 * Utilities.java
 * This is the utilities file that contains the write to file method.
 * @author Emil Bjørlykke Berglund
 * @author Adam Fischer
 * @author Denys Ladden
 * @version 1.0
 * Programming project 4
 * CS322 - Compiler Construction
 * Fall 2021
 **/

public class Utilities{

    public static void writeFile(byte[] bytearray, String fileName){

        try{
            FileOutputStream out = new FileOutputStream(fileName);
            out.write(bytearray);
            out.close();
        }
        catch(IOException e){
        System.out.println(e.getMessage());
        }
        
    }//end writeFile

}//end class    
