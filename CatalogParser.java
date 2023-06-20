import java.util.*;
import java.io.*;

public class CatalogParser
{
   private Department chem;
   private Department cs;
   private Department math;
   private Department phys;

   private static Department load(String prefix)
   {
      Scanner s;
      try
      {
         s = new Scanner(new File("cat_"+prefix.toLowerCase()+".txt"));
      }
      catch(Exception e)
      {
         System.out.print("oops");
         return null;
      }
      String line = "";
   
   
      Department d = new Department(prefix);
      
      System.out.println("loading "+d.getDept());
      
      if(depts.size() > 0)
         System.out.println("second loading "+depts.get(0).getDept());
      
      
      while(s.hasNext())
      {
         line = s.nextLine();
         if(!line.equals(""))
         {
            if(!Course.isPaired(Course.getNumber(line)))
               d.addCourse(Course.createCourse(line));
            else
            {
               d.addCourse(Course.createUnpairedI(line));
               d.addCourse(Course.createUnpairedII(line));
            }
         }
      }
      
      return d;
   }


   public static Department load(String prefix)
   {
      Scanner s;
      try
      {
         s = new Scanner(new File("cat_"+prefix.toLowerCase()+".txt"));
      }
      catch(Exception e)
      {
         System.out.print("oops");
         return null;
      }
      String line = "";
   
   
      Department d = new Department(prefix);
      
      while(s.hasNext())
      {
         line = s.nextLine();
         if(!line.equals(""))
         {
            if(!Course.isPaired(Course.getNumber(line)))
               d.addCourse(Course.createCourse(line));
            else
            {
               d.addCourse(Course.createUnpairedI(line));
               d.addCourse(Course.createUnpairedII(line));
            }
         }
      }
      
      return d;
   }

   public static void main(String[] args) throws Exception
   {
   
      Scanner chemscan = new Scanner(new File("cat_chem.txt"));
      Scanner csscan = new Scanner(new File("cat_cs.txt"));
      Scanner mathscan = new Scanner(new File("cat_math.txt"));
      Scanner physscan = new Scanner(new File("cat_phys.txt"));
   
      String line = "";
      
      /*Department chem = new Department("CHEM");
      
      while(chemscan.hasNext())
      {
         line = chemscan.nextLine();
         if(!line.equals(""))
         {
            if(!Course.isPaired(Course.getNumber(line)))
               chem.addCourse(Course.createCourse(line));
            else
            {
               chem.addCourse(Course.createUnpairedI(line));
               chem.addCourse(Course.createUnpairedII(line));
            }
         }
      }
      
      System.out.print(chem.toString()); */
      
      /*Department cs = new Department("CS");
      
      while(csscan.hasNext())
      {
         line = csscan.nextLine();
         if(!line.equals(""))
         {
            if(!Course.isPaired(Course.getNumber(line)))
               cs.addCourse(Course.createCourse(line));
            else
            {
               cs.addCourse(Course.createUnpairedI(line));
               cs.addCourse(Course.createUnpairedII(line));
            }
         }
      }
      
      System.out.print(cs.toString()); */
      
     /*Department math = new Department("MATH");
      
      while(mathscan.hasNext())
      {
         line = mathscan.nextLine();
         if(!line.equals(""))
         {
            if(!Course.isPaired(Course.getNumber(line)))
               math.addCourse(Course.createCourse(line));
            else
            {
               math.addCourse(Course.createUnpairedI(line));
               math.addCourse(Course.createUnpairedII(line));
            }
         }
      }
      
      System.out.print(math.toString());*/
      
      
      Department phys = new Department("PHYS");   
      
      while(physscan.hasNext())
      {
         line = physscan.nextLine();
         if(!line.equals(""))
            if(!Course.isPaired(Course.getNumber(line)))
               phys.addCourse(Course.createCourse(line));
            else
            {
               phys.addCourse(Course.createUnpairedI(line));
               phys.addCourse(Course.createUnpairedII(line));
            }
      }     
      
      System.out.print(phys.toString()); 
   
   }
}