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
   


}
