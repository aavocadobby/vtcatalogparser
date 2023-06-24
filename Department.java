import java.util.*;

public class Department implements Comparable<Department>
{
   private String prefix;
   private String level; //undergrad or graduate
   private ArrayList<Course> list;
   
   public Department(String p, String l)
   {
      prefix = p;
      level = l;
      list = new ArrayList<Course>();
   }
   public void addCourse(Course c)
   {
      list.add(c);
   }
   public String getDept()
   {
      return prefix;
   }
   public ArrayList<Course> getCourseArrayList()
   {
      return list;
   }
   public boolean containsNumber(int n)
   {
      for(int x = 0; x < list.size(); x++)
         if(list.get(x).getNumber() == n)
            return true;
   
      return false;
   }
   public Course getCourse(int n)
   {
      for(int x = 0; x < list.size(); x++)
         if(list.get(x).getNumber() == n)
            return list.get(x);
   
      return null;
   }
   public int numberOfCourses()
   {
      return list.size();
   }
   
   public String toString()
   {
      String r = "";
   
      for(int x = 0; x < list.size(); x++)
         r += prefix + " " + list.get(x).toString() + "\n";
      
      return r;   
   }
   
   @Override public int compareTo(Department d)
   {
      return getDept().compareTo(d.getDept());   
   }
}