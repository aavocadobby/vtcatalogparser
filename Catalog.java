import java.util.*;

/** contains all departments: centralized object for information (at the price of constant embedded accesses),
* will be important for course lookup when implementing prereqs specs in the future */
public class Catalog
{
   private ArrayList<Department> catalog;

   public Catalog()
   {
      catalog = new ArrayList<Department>();
   }
   public Catalog(ArrayList<Department> d)
   {
      catalog = d;
   }
   
   //accessor
   public ArrayList<Department> depts()
   {
      return catalog; //returns all departments
   }
   public int size()
   {
      return catalog.size();
   }
   
   //modifier
   public void add(Department d)
   {
      catalog.add(d);
   }
   
   public void findPrereqs()
   {
      for(int x = 0; x < size(); x++)
         for(int d = 0; d < catalog.get(x).getCourseArrayList().size(); d++)
         {
            Course c = catalog.get(x).getCourseArrayList().get(d);
          
            ArrayList<Course> prereqs = identifyCourseNumbers(c, "prereqs");
            ArrayList<Course> coreqs = identifyCourseNumbers(c, "coreqs");
            eraseDuplicates(c, prereqs, coreqs);
         }
   }
   
   private ArrayList<Course> identifyCourseNumbers(Course c, String s)
   {
      ArrayList<Course> i = new ArrayList<Course>();
      
      String[] p = null;
      switch(s)
      {
         case "prereqs":
            p = c.getPrereqString().split(" ");
            break;
         case "coreqs":
            p = c.getCoreqString().split(" ");
            break;
      }
      
     
      if(p.length <= 0)
         return null;
      
      for(int x = 0; x < p.length; x++)
         if(p[x].length() > 0)
            if("0123456789".contains(p[x].substring(0,1)) )
               if(p[x-1].length() > 0)
                  if(x >= 1 && "QWERTYUIOPASDFGHJKLZXCVBNM".contains(p[x-1].substring(0,1)) )
                     i.add(new Course(c.getDept(), Integer.parseInt(p[x].substring(0,4)), (p[x].length() == 5)) );
                  else
                     i.add(new Course(p[x-1], Integer.parseInt(p[x].substring(0,4)), (p[x].length() == 5)) );
         
      return i;
   }
   
   public void eraseDuplicates(Course cr, ArrayList<Course> prereqs, ArrayList<Course> coreqs)
   {
      String p = cr.getPrereqString();
      String c = cr.getCoreqString();
   
      for(int x = 0; x < prereqs.size(); x++)
      {
         String n = ""+prereqs.get(x).getNumber();
         if(p.indexOf(n) != p.lastIndexOf(n))
            p.replaceFirst(n, "");
      }
      for(int y = 0; y < coreqs.size(); y++)
      {
         String n = ""+coreqs.get(y).getNumber();
         if(c.indexOf(n) != c.lastIndexOf(n))
            c.replaceFirst(n, "");
      }
      
      cr.setPrereqString(p);
      cr.setCoreqString(c);
   }
   
   
}
