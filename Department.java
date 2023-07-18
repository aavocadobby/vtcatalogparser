import java.util.*;

/** 
* Department Object contains a list for all the Courses in a particular Department.
*
* @author Anhui Zhang [zhanganhui@vt.edu]
* @version 07.2023
*/

public class Department implements Comparable<Department>
{
   /** department prefix (2, 3, or 4 captial letters) */
   private String prefix;
   /** undergrad or graduate */
   private String level; 
   /** list of courses in this department */
   private ArrayList<Course> list;
    
   public Department(String p, String l)
   {
      prefix = p;
      level = l;
      list = new ArrayList<Course>();
   }
   /** adds course (instance) 
   * @param c  course to be added */
   public void addCourse(Course c)
   {
      list.add(c);
   }
   
   /** prefix accesor
   * @return   the prefix of this department */
   public String getDept()
   {
      return prefix;
   }
   
   /** course list accesor
   * @return   returns the entire list of courses in this department */
   public ArrayList<Course> getCourseArrayList()
   {
      return list;
   }
   
   /** checks whether a course number exists in this department 
   * @param n  the four-digit number of the course
   * @return   true if the course is found, false if not */
   public boolean containsNumber(int n)
   {
      for(int x = 0; x < list.size(); x++)
         if(list.get(x).getNumber() == n)
            return true;
   
      return false;
   }
   
   /** gets the course with the desired course number
   * @param n  the four-digit number of the course
   * @return   the course if found, else null if this course number doesn't exist in this department*/
   public Course getCourse(int n)
   {
      for(int x = 0; x < list.size(); x++)
         if(list.get(x).getNumber() == n)
            return list.get(x);
   
      return null;
   }
   
   /** number of courses in this department
   * @return   returns the number of courses in this department */
   public int numberOfCourses()
   {
      return list.size();
   }
   
   /** overrides Object's toString method
   * @return   all the titles of all the courses in this department, each course separated by a newline */
   @Override public String toString()
   {
      String r = "";
   
      for(int x = 0; x < list.size(); x++)
         r += prefix + " " + list.get(x).toString() + "\n";
      
      return r;   
   }
   
   /** implements Comparable
   * @param d  the department to be compared to
   * @return   compares the prefix string to the other department's prefix string */ 
   @Override public int compareTo(Department d)
   {
      return getDept().compareTo(d.getDept());   
   }
}