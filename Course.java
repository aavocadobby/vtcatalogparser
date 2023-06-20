import java.util.*;

public class Course
{
   private String dept;
   private String name;
   private int number;
   private String desc = "";
   
   private String prereqString = "";
   private String creditString = "";
   
   private ArrayList<Course> prereqs;
   private ArrayList<Course> coreqs;
   private ArrayList<String> crossListing;   
   
   private int credits = 0; //if == -1, means variable course credit
   private boolean isLab = false;
   private boolean isHonors = false;
   private char specialDesignation;
   
   public Course(String dep)
   {
      dept = dep;
      name = "";
      number = -1;
   }
   
   public Course(String dep, String nam, int num, boolean h)
   {
      dept = dep;
      name = nam;
      number = num;   
      isHonors = h;
   }
   
   
   //getters (accessor)
   public String getName()
   {
      return name;
   }
   public int getNumber()
   {
      return number;
   }
   public String getDesc()
   {
      return desc;
   }
   public String getPrereqString()
   {
      return prereqString;
   }
   public String getCreditString()
   {
      return creditString;
   }
   public int getCredits()
   {
      return credits;
   }

   
   //setters (instance)
   private void setPrereqString(String s)
   {
      prereqString = s;
   }
   private void setDesc(String main)
   {
      if(main.contains("Pre:"))
         main = main.substring(0, main.lastIndexOf("Pre:"));
      desc = main;
      if(desc.contains(Restriction.VAR_CREDIT))
      {
         credits = - 1;
         desc = desc.substring(desc.indexOf(Restriction.VAR_CREDIT), desc.indexOf(Restriction.VAR_CREDIT)+Restriction.VAR_CREDIT.length());
      }
   }
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
   private void setCredits()
   {
      if(credits != 0)
         return;
      if(!creditString.equals(""))
         if(creditString.indexOf("C") == -1)
         {
            System.out.println(this.toString()+""+creditString);
            creditString = "";
         }
         else
            credits = Integer.parseInt(creditString.substring(creditString.indexOf("C")-1, creditString.indexOf("C")));
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
   
   private static String getPrereq(String line)
   {
      if(line.lastIndexOf("Pre:") != -1)
         if(line.lastIndexOf("(") >= line.lastIndexOf("Pre:"))
            return line.substring(line.lastIndexOf("Pre:"), line.lastIndexOf("("));
         else
            return line.substring(line.lastIndexOf("Pre:"));
            
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
         
      Course c = new Course(prefix, n, number, honors);  
      c.setDesc(d.trim());  
      c.setCreditString(line);
      c.setCredits();
      c.setPrereqString(getPrereq(line));   
     
      return c;
   }
   
   
   
   private void splitDescI()
   {
      if(desc.contains(":") && desc.indexOf(":") < desc.lastIndexOf(":"))
      {
         String unison = "";
         unison = desc.substring(0, desc.indexOf(":")-4);
         desc = desc.substring(unison.length(), desc.lastIndexOf(":")-4);
         desc = unison + desc;
      }
   }
   
   private void splitDescII()
   {
      if(desc.contains(":") && desc.indexOf(":") < desc.lastIndexOf(":"))
      {
         String unison = "";
         unison = desc.substring(0, desc.indexOf(":")-4);
         desc = desc.substring(desc.lastIndexOf(":"));
         desc = unison + desc;
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
       
      Course c = new Course(prefix, n+" I", number, honors);
      c.setDesc(d.trim());    
      c.splitDescI();
      c.setCreditString(line); 
      c.setCredits();
      c.setPrereqString(getPrereq(line));
     
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
      
      Course c = new Course(prefix, n+" II", number, honors);
      c.setDesc(d.trim()); 
      c.splitDescII();
      c.setCreditString(line);    
      c.setCredits();
      c.setPrereqString(getPrereq(line));
    
      return c;
   }   
   
   public String toString()
   {
      return dept + " " + number + " - " + name;
   }
}