import java.util.*;

/** 
* Course Object stores information of a particular course.
*
* @author Anhui Zhang [zhanganhui@vt.edu]
* @version 07.2023
*/

public class Course implements Comparable<Course>
{
   /** department the course belongs to (its prefix) */
   private String dept;
   /** name of course */
   private String name;
   /** course number */
   private int number;
   /** course description */
   private String desc = "";
   
   
   /** stores the prereqs of this course as a string */
   private String prereqString = "";
   private String coreqString = "";
   /** stores the credit-bearing information of this course as a string */
   private String creditString = "";
   
   
   /** list of prereqs for this course */
   private ArrayList<Course> prereqs;
   /** list of coreqs for this course */
   private ArrayList<Course> coreqs;
   /** list if this course is cross-listed as another course */
   private ArrayList<String> crossListing;   
   
   
   /** credits for this course; if == -1, means variable course credit */
   private int credits = 0; 
   /** lab */
   private boolean isLab = false;
   /** honors */
   private boolean isHonors = false;
   /** char if this has a special designation, only for some engineering courses or special cases */
   private char specialDesignation;
   
   
   
   public Course(String dep)
   {
      dept = dep;
      name = "";
      number = -1;
   }
   
   public Course(String dep, int n, boolean h)
   {
      dept = dep;
      number = n;
      name = "";
      isHonors = h;
   }
   
   public Course(String dep, int num, String nam, boolean h)
   {
      dept = dep;
      name = nam;
      number = num;   
      isHonors = h;
   }
   
   
   //getters (accessor)
   
   /** accessor
   * @return   returns department of this course*/
   public String getDept()
   {
      return dept;
   }
    /** accessor 
    * @return   returns name of this course */
   public String getName()
   {
      return name;
   }
    /** accessor 
    * @return   return course number */
   public int getNumber()
   {
      return number;
   }
    /** accessor 
    * @return  returns course description */
   public String getDesc()
   {
      return desc;
   }
   
    /** accessor 
    * @return  returns string for this course's prereqs */
   public String getPrereqString()
   {
      return prereqString;
   }
   
   public String getCoreqString()
   {
      return coreqString;
   }
    /** accessor 
    * @return  returns string for this course's credits */
   public String getCreditString()
   {
      return creditString;
   }
   
    /** accessor 
    * @return  returns number of credit hours */
   public int getCredits()
   {
      return credits;
   }

   
   //public setters (instance)
   
   private void parseCredits()
   {
      if(credits != 0)
         return;
      if(!creditString.equals(""))
         if(creditString.indexOf("C") == -1)
            creditString = "";
         else
            credits = Integer.parseInt(creditString.substring(creditString.indexOf("C")-1, creditString.indexOf("C")));
   }
   public void setDesc(String main)
   {
      desc = main;
      if(desc.contains(Restriction.VAR_CREDIT))
      {
         credits = -1;
         desc = desc.substring(desc.indexOf(Restriction.VAR_CREDIT), desc.indexOf(Restriction.VAR_CREDIT)+Restriction.VAR_CREDIT.length());
      }
   }
   public void setPrereqString(String s)
   {
      prereqString = s;
   }
   public void setCoreqString(String s)
   {
      coreqString = s;
   }
   public void setCredits(int c)
   {
      credits = c;
   }
   
   
   //private instance
  
   private void setAsLab()
   {
      isLab = true;
   }
   private void setCreditString(String s)
   {
      int openParen = s.lastIndexOf("(");
      int closeParen = s.lastIndexOf(")");
      if(closeParen == -1 || closeParen != s.length()-1 ) //there are parens in the course
         return;                       //desc that is not actually the credit
      creditString = s.substring(openParen+1, closeParen);
   }
  
   
   //other instance field modifiers
   public void addPreReqs(Course p)
   {
      prereqs.add(p);
   }
   public void addCoReqs(Course c)
   {
      coreqs.add(c);
   }
   
      
   /* COURSE NUMBER OPTIONS
   
   1. unpaired, no honors
         2114: BLACK HOLES
         
   2. paired, no honors
         2305-2306: FOUNDATIONS OF PHYSICS
         1155,1156: ASTRONOMY LABORATORY
         
   3. unpaired, honors
         2974H: INDEPENDENT STUDY 
         
   4. paired, honors
         3615H-3616H: HONORS PHYSICAL CHEMISTRY 
   */
   public static boolean isPaired(String num)
   {
      return num.contains("-") || num.contains(",");
   }
   
   public static boolean isHonors(String num)
   {
      return num.contains("H") && "1234567890".contains(""+num.charAt(num.indexOf("H")-1));
   }
  
   public static String getNumber(String line)
   {
      return line.substring(0, line.indexOf(":"));
   }
   
   private String getPrereq(String line)
   {
      if(line.lastIndexOf("Pre:") != -1)
      {
         System.out.println(this.toString()+", "+line.substring(line.indexOf("Pre:")) );
         String r = line.substring(line.indexOf("Pre:"));
         if(desc.indexOf("Pre") >=0 )
            desc = desc.substring(0, desc.lastIndexOf("Pre:"));
         r = r.substring(0, r.lastIndexOf("."));
         return r;
      }
                   
      return "";
   }
   private String getCoreq(String line)
   {
      if(line.lastIndexOf("Co:") != -1)
      {
         System.out.println(this.toString()+", "+line.substring(line.indexOf("Co:")) );
         String r = line.substring(line.indexOf("Co:"));
         if(desc.indexOf("Co:") >=0 )
            desc = desc.substring(0, desc.lastIndexOf("Co:"));
         r = r.substring(0, r.lastIndexOf("."));
         return r;
      }
            
      return "";   
   }
   
   private static boolean isTitleHelper(String sx) //helper to check if word is part of title
   {
      boolean upperCase = sx.equals(sx.toUpperCase()); //title is in uppercase
      boolean containsNumber = "0123456789".contains(sx.substring(0,1)); //ignore split-course descs
      if(containsNumber && sx.equals("20TH")) //hard-code case for the course MUS 3124: 20TH CENTURY MUSIC LITERATURE"
         containsNumber = false;
      return upperCase && !containsNumber;
   }
   public static String getName(String line)
   {
      String r = "";
      String c = ""; 
      
      if(line.lastIndexOf(":") != line.indexOf(":")) // if there is more than one colon
         c = line.substring(line.indexOf(":"), line.lastIndexOf(":"));
      else
         c = line.substring(line.indexOf(":"));
         
      String[] s = c.split(" ");
      
      int x = 1;
      if(s.length > 0) 
      {         
         while(isTitleHelper(s[x]))
         {   // the first word of the course desc is NOT "A ..." but "A" is part of the title
            if(s[x].equals("A") && !s[x+1].equals(s[x+1].toUpperCase()))
               break;
          
            r += s[x] + " ";
            x++;
         }
      }
          
      return r;
   }
   
   public static Course createCourse(String line, String prefix) //unpaired courses only
   {
      String n = getName(line).trim(); //name
     
      String numPrelim = getNumber(line); //number
      int number = Integer.parseInt(numPrelim.substring(0, 4));
      
      String d = line.substring(line.indexOf(n)+n.length()); //desc
      
      boolean honors = isHonors(numPrelim); //honors
      if(honors)
         n += " HONORS";
         
      Course c = new Course(prefix, number, n, honors);  
      c.setDesc(d.trim());  
      c.setCreditString(line);
      c.parseCredits();
      
      c.setPrereqString(c.getPrereq(line));   
      c.setCoreqString(c.getCoreq(line));
     
      return c;
   }
   
   private void splitDescI()
   {
      if(desc.indexOf(""+number) == 0 && desc.indexOf(""+(1+number)) > 0 ) //if the course description starts like 3055-3056: TITLE /n 3055: ... 
                                    //also must contain the other course number (so desc is acc split)
         desc = desc.substring(6, desc.indexOf((1+number)+"")); //go until the next number (split numbers
                                                                        //are always consecutive)
                                                                        
      else if(desc.indexOf(number+"") > 0 && desc.indexOf(""+(1+number)) > 0 ) //there will be a unison desc part
      {
         String unison = desc.substring(0, desc.indexOf(number+"")-1);
         desc = desc.substring(desc.indexOf(number+"")+6, desc.indexOf((1+number)+""));
         desc = desc.substring(0,1).toUpperCase() + desc.substring(1);
         
         desc = unison +"\n"+desc;
      }
   }   
   private void splitDescII()
   {
      if(desc.indexOf((number-1)+"") == 0 && desc.indexOf(""+number) > 0)
         desc = desc.substring(desc.indexOf(number+"")+6);
      
      else if(desc.indexOf((number-1)+"") >= 0 && desc.indexOf(number+"") > 0) 
      {
         String unison = desc.substring(0, desc.indexOf((number-1)+"")-1);
         desc = desc.substring(desc.indexOf(number+"")+6);
         desc = desc.substring(0,1).toUpperCase() + desc.substring(1);
         
         desc = unison +"\n"+desc;
      }
   }
   
   private void splitPrereqCoreqI()
   {
      if(prereqString.contains(";"))
         prereqString = prereqString.substring(0, prereqString.indexOf(";"));
      if(coreqString.contains(";"))
         coreqString = coreqString.substring(0, coreqString.indexOf(";"));
   }
   private void splitPrereqCoreqII()
   {
      if(prereqString.contains(";"))
         prereqString = "Pre: "+prereqString.substring(prereqString.indexOf(";")+1);
      if(coreqString.contains(";"))
      {
         coreqString = "Co: "+coreqString.substring(coreqString.indexOf(";")+2);
      }
   }
   
   public static Course createUnpairedI(String line, String prefix)
   {
      String n = getName(line).trim(); //name
    
      String numPrelim = getNumber(line); //number
      int number = Integer.parseInt(numPrelim.substring(0, 4));
      
      String d = line.substring(line.indexOf(n)+n.length()); //desc
     
      boolean honors = isHonors(numPrelim); //honors
      if(honors)
         n += " HONORS";
       
      Course c = new Course(prefix, number, n+" I", honors);
      c.setDesc(d.trim());    
      c.splitDescI();
      c.setCreditString(line); 
      c.parseCredits();
      
      c.setPrereqString(c.getPrereq(line));
      c.setCoreqString(c.getCoreq(line));
      c.splitPrereqCoreqI();
      
      return c;
   }
   public static Course createUnpairedII(String line, String prefix)
   {
      String n = getName(line).trim(); //name
   
      String numPrelim = getNumber(line); //numPrelim
      int number; //number (depends on if it is paired honors)
      
      boolean honors = isHonors(numPrelim); //honors
      if(!honors)
         number = Integer.parseInt(numPrelim.substring(5, 9));
      else
         number = Integer.parseInt(numPrelim.substring(6, 10));
      
      String d = line.substring(line.indexOf(n)+n.length()); //desc
     
      if(honors) //honors
         n += " HONORS";
      
      Course c = new Course(prefix, number, n+" II", honors);
      c.setDesc(d.trim()); 
      c.splitDescII();
      c.setCreditString(line);    
      c.parseCredits();
      
      c.setPrereqString(c.getPrereq(line));
      c.setCoreqString(c.getCoreq(line));
      c.splitPrereqCoreqII();
    
      return c;
   }   
    
   
   @Override public int compareTo(Course c)
   {
      return number-c.getNumber();
   }
   
   public String toString()
   {
      return dept + " " + number + " - " + name;
   }
}