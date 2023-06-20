import java.util.*;
import java.io.*;

public class MinorParser
{
   public static void main(String[] args) throws Exception
   {
      Scanner s = new Scanner(new File("physelts.txt"));
      
      
      ArrayList<String[]> courses = new ArrayList<String[]>();
      
      while(s.hasNext())
      {
         s.next();
         courses.add( new String[] {s.next(), s.nextLine() } );
      }
               
               
      System.setOut(new PrintStream(new FileOutputStream("parsed.txt")));
             
      for(int x = 0; x < courses.size(); x++)
         System.out.println(courses.get(x)[1].substring(1, courses.get(x)[1].length()-2).toLowerCase());
         
      System.out.println();
         
      for(int y = 0; y < courses.size(); y++)
         System.out.println(courses.get(y)[0]);
         
   
               
   }
}