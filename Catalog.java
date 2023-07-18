import java.util.*;

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
