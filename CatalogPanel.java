import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.beans.*;

/** 
* Main panel that contains all the catalog information, launched in CatalogDriver.
*
* @author Anhui Zhang [zhanganhui@vt.edu]
* @version 07.2023
*/
 
public class CatalogPanel extends JPanel
{ 
   /** loaded as Franklin Gothic Book Cond C, 14pt size. used in descriptions and titles */
   private Font f14 = null;
 
   /** dropdown menu of departments */
   private JComboBox deptBox; 
   /** search by course number */
   private JTextField numField; 
   /** search by keyword */ 
   private JTextField keyField; 
   
   /** search button */
   private JButton sbutton; 
 
   private JPanel titlePanel; 
   private JList titles; 
   private DefaultListModel titleModel;
   private JScrollPane scroll;
 
   private JButton loadDescButton, prereqButton; //button to load prereqs + course descs
 
   private JButton pinButton; //button to pin courses to a new window
   private int pinned;
   private Course[] pinArray;
   private boolean[] isPinned;
 
   private JFrame descFrame, prereqFrame; //frames for new windows
   private JPanel descPanel, prereqPanelWindow; //panel in descFrame to display course descs + prereqs
   private JLabel descTitle;
   private JTextArea title, description, prereqs; //text areas to display the description + prereqs
   private JTextArea creditArea;
   
   private EmptyBorder eb;
   private int popupWidth, rowWidth;
   
   private JButton duplicateDesc, duplicatePrereq;
   
   private Catalog catalog;
   private int deptIndex = 0;
   
 
    /** helper method to load undergrad Department from a plaintext file */
   private Department loadUG(String prefix)
   {
      Scanner s;
      
      try
      {
         s = new Scanner(new File("ug_cat_"+prefix.toLowerCase()+".txt"));      
      } 
      catch(Exception e)
      {
         System.out.print("oops");
         return null;
      }
   
      Department d = new Department(prefix, "Undergraduate"); 
      
      String line = "";
      while(s.hasNext())
      {
         line = s.nextLine();
         if(!line.equals(""))
            if(!Course.isPaired(Course.getNumber(line)))
               d.addCourse(Course.createCourse(line, prefix));
            else
            {
               d.addCourse(Course.createUnpairedI(line, prefix));
               d.addCourse(Course.createUnpairedII(line, prefix));
            }
      }
      
      return d;
   }
   
    /** loads grad department helper */
   private Department loadGrad(String prefix)
   {
      Scanner s;
      
      try
      {
         s = new Scanner(new File("grad_cat_"+prefix.toLowerCase()+".txt"));      
      } 
      catch(Exception e)
      {
         System.out.print("oops");
         return null;
      }
   
      Department d = new Department(prefix, "Graduate");
      
      String line = "";
      while(s.hasNext())
      {
         line = s.nextLine(); //name and title
         if(!line.equals(""))
         {
            int n = Integer.parseInt(line.substring(prefix.length()+1, prefix.length()+5));
            String t = line.substring(line.indexOf("-")+2);
            Course c = new Course(prefix, n, t.toUpperCase(), false); //no grad courses have honors
            line = s.nextLine();
            if(!line.contains("Credit Hour(s)"))
            {
               c.setDesc(line); //desc
               line = s.nextLine(); //credits
            }
            
            if(line.contains("TO"))
               c.setCredits(-1);
            else
               try
               {
                  c.setCredits(Integer.parseInt(""+line.charAt(line.length()-1)));
               }
               catch(Exception e)
               {
                  c.setCredits(-1);
               }
            
            s.nextLine(); //lecture hours
            s.nextLine(); //level
            s.nextLine(); //mode of instruction
            line = s.nextLine(); //prereqs
            if(line.indexOf(":")+2 < line.length())
               c.setPrereqString(line.substring(line.indexOf(":")+2));
            s.nextLine(); //coreqs
            
            d.addCourse(c);
         }
      }
   
      return d;
   }

   /** calls load for all desired Departments */
   private void loadDepartments(String level)
   {
      catalog = new Catalog();
      if(level.equals("ug"))
      {
         catalog.add(loadUG("CHEM"));
         //catalog.add(loadUG("CMDA", "ug"));
         catalog.add(loadUG("CS"));
         catalog.add(loadUG("ENGR"));
         catalog.add(loadUG("MATH"));
         deptIndex = catalog.size()-1; //catalog will always default to math, even if 
                        //other catalogs are not loaded
         catalog.add(loadUG("MUS"));
         catalog.add(loadUG("PHYS"));
         catalog.add(loadUG("STAT"));
         catalog.add(loadUG("STS"));
      }
      else if(level.equals("grad"))
      {
         catalog.add(loadGrad("CHEM"));
         catalog.add(loadGrad("CS"));
         catalog.add(loadGrad("MATH"));
         deptIndex = catalog.size()-1; 
         catalog.add(loadGrad("MUS"));
         catalog.add(loadGrad("PHYS"));
         catalog.add(loadGrad("STAT"));
         catalog.add(loadGrad("STS"));
      }
      
      else
      {   
         //first, load undergraduate classes
         catalog.add(loadUG("CHEM"));
         //catalog.add(loadUG("CMDA", "ug"));
         catalog.add(loadUG("CS"));
         catalog.add(loadUG("ENGR"));
         catalog.add(loadUG("MATH"));
         deptIndex = catalog.size()-1; 
         catalog.add(loadUG("MUS"));
         catalog.add(loadUG("PHYS"));
         catalog.add(loadUG("STAT"));
         catalog.add(loadUG("STS"));
         
         
         ArrayList<Department> clone = new ArrayList<Department>();
         clone.add(loadGrad("CHEM"));
         clone.add(loadGrad("CS"));
         clone.add(loadGrad("MATH"));
         clone.add(loadGrad("MUS"));
         clone.add(loadGrad("PHYS"));
         clone.add(loadGrad("STAT"));
         clone.add(loadGrad("STS"));
         
         for(int x = 0; x < clone.size(); x++)
            for(int y = 0; y < clone.get(x).getCourseArrayList().size(); y++)
               catalog.depts().get(findIndex(clone.get(x).getDept())).addCourse(clone.get(x).getCourseArrayList().get(y));
                       //index of prefix     get prefix               dep          courselist        course
      }
      
   }


   public CatalogPanel(String level)
   { 
      loadDepartments(level);
   
      //setting layout of host CatalogPanel
      setLayout(new FlowLayout());
      setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   
   
      //font instantiation
      Font f = null;
      try
      {
         f = Font.createFont( Font.TRUETYPE_FONT, 
            new File("FranklinGothicBookCondC.otf") );
         f14 = f.deriveFont((float)14);
      }
      catch(Exception e)
      {
         System.out.print("oops font");
      }
   
   
      //returns search if user presses ENTER key, any searchable 
      //field gets this KeyListener
      SearchKeyListener enter = new SearchKeyListener(); 
   
   
      //load department options, make dropdown selection box
      String[] prefixes = new String[catalog.size()];
      for(int a = 0; a < prefixes.length; a++)
         prefixes[a] = catalog.depts().get(a).getDept();
      Arrays.sort(prefixes);
      deptBox = new JComboBox(prefixes);
      deptBox.setSelectedIndex(deptIndex);
      deptBox.addKeyListener(enter);
      deptBox.setFocusable(true);
      deptBox.setMaximumRowCount(catalog.size());
   
      //panel for selecting department
      JPanel deptPanel = new JPanel(new BorderLayout());
      deptPanel.add(deptBox); 
   
      TitledBorder prefixBorder = (TitledBorder)BorderFactory.createTitledBorder("Prefix");
      prefixBorder.setTitleFont(f14);
      deptPanel.setBorder(BorderFactory.createCompoundBorder(
         prefixBorder, BorderFactory.createEmptyBorder(5,5,5,5)));
   
      add(deptPanel);
   
   
      //field + panel for searching by number
      numField = new JTextField(7);
      numField.addKeyListener(enter);
      numField.setFocusable(true);
   
      JPanel numPanel = new JPanel(new BorderLayout());
      numPanel.add(numField);
      TitledBorder numberBorder = (TitledBorder)BorderFactory.createTitledBorder("Number");
      numberBorder.setTitleFont(f14);
      numPanel.setBorder(BorderFactory.createCompoundBorder(
         numberBorder, BorderFactory.createEmptyBorder(5,10,10,10)));
   
      add(numPanel);
   
   
      //field + panel for searching by keyword
      keyField = new JTextField(20);
      keyField.addKeyListener(enter);
      keyField.setFocusable(true);
   
      JPanel keyPanel = new JPanel(new BorderLayout());
      keyPanel.add(keyField);
      TitledBorder keyBorder = (TitledBorder)BorderFactory.createTitledBorder("Keyword or Phrase");
      keyBorder.setTitleFont(f14);
      keyPanel.setBorder(BorderFactory.createCompoundBorder(keyBorder,
         BorderFactory.createEmptyBorder(5,10,10,10)));
   
      add(keyPanel);
   
   
      //field + panel for search button
      sbutton = new JButton("Search");
      sbutton.addActionListener(new SearchListener());
   
      JPanel searchPanel = new JPanel();
      searchPanel.add(sbutton);
      searchPanel.setBorder(BorderFactory.createEmptyBorder(15,50,10,20));
   
      add(searchPanel);
   
   
      //list, scrollpane, and panel to display course titles
      
      titleModel = new DefaultListModel();
      
      ArrayList<Course> deptCourses = catalog.depts().get(deptIndex).getCourseArrayList();
      for(int x = 0; x < deptCourses.size(); x++)
         titleModel.addElement(deptCourses.get(x));
   
      titles = new JList();
      titles.setModel(titleModel);
      titles.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      titles.setCellRenderer(new PinColor());
      titles.setLayoutOrientation(JList.VERTICAL);
      titles.setVisibleRowCount(-1); //-1 means shows the maximum possible number of items 
      titles.setFont(f14);
      titles.setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
   
      //listeners
      titles.addKeyListener(new DescKeyListener());
      titles.addKeyListener(new PinKeyListener());
      titles.addListSelectionListener(new PinListener());
     
   
      scroll = new JScrollPane(titles); 
      scroll.setPreferredSize(new Dimension(620, 400));
   
      titlePanel = new JPanel();
      titlePanel.add(scroll);
      add(titlePanel);
   
   
   
      //button to pin courses
      pinButton = new JButton("Pin Course");
      pinned = 0; //start with no courses pinned. maximum 5 pinned courses allowed by default
      pinArray = new Course[5];
      isPinned = new boolean[5];
      
      pinButton.addActionListener(new PinCourseListener());
      pinButton.setEnabled(false);
   
      JPanel pinPanel = new JPanel();
      pinPanel.add(pinButton);
      pinPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,10));
   
      add(pinPanel); 
   
   
   
   
      //button to load course desc, in new window with descriptiom
   
      //components in host CatalogPanel (loadDescButton, loadDescPanel w/ button)
      loadDescButton = new JButton("See Course Description");
      loadDescButton.addActionListener(new DescriptionListener());
   
      JPanel loadDescPanel = new JPanel();
      loadDescPanel.add(loadDescButton);
      loadDescPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,15));
   
      add(loadDescPanel);
   
   
      popupWidth = 450;
      rowWidth = 42;
      
      eb = (EmptyBorder)BorderFactory.createEmptyBorder(5,10,5,10); //top,left,bottom,right
   
      //new popup window for description
      descFrame = new JFrame("Course Description");
      descFrame.setSize(popupWidth, 300);
      descFrame.setLocation(800, 160);
      descFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      descPanel = new JPanel();
      descFrame.setContentPane(descPanel);
   
   
      //course title
      title = new JTextArea(1, (int)(rowWidth*(14.0/18.0)));
      title.setBorder(eb);
      title.setBackground(descPanel.getBackground());
      title.setFont(f14.deriveFont((float)18));
      title.setLineWrap(true);
      title.setWrapStyleWord(true);
      title.setEditable(false);
   
      //text area
      description = new JTextArea(7, rowWidth);
      description.setBorder(eb);
      description.setFont(f14);
      description.setBackground(descPanel.getBackground());
      description.setLineWrap(true);
      description.setWrapStyleWord(true);
      description.setEditable(false);
   
   
      //credit details area
      creditArea = new JTextArea(1, rowWidth);
      creditArea.setBorder(eb);
      creditArea.setFont(f14);
      creditArea.setBackground(descPanel.getBackground());
      creditArea.setLineWrap(true);
      creditArea.setWrapStyleWord(true);
      creditArea.setEditable(false);
      
      //duplicate window button
      duplicateDesc = new JButton("Duplicate Window");
      duplicateDesc.addActionListener(new DuplicateDescListener());
      
               
      descPanel.add(title);
      descPanel.add(description);
      descPanel.add(creditArea);
      descPanel.add(duplicateDesc);
   
   
   
      //button to load prereqs, new window w/ prereqs 
   
      //host components (button, panel for button)
      prereqButton = new JButton("Load Prereqs");
      prereqButton.addActionListener(new PrereqListener());
   
      JPanel prereqPanel = new JPanel();
      prereqPanel.add(prereqButton);
      add(prereqPanel);
      
   
      //new frame
      prereqFrame = new JFrame("Prerequisites");
      prereqFrame.setSize(popupWidth, 175);
      prereqFrame.setLocation(800, 470);
      prereqFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      prereqPanelWindow = new JPanel();
      prereqFrame.setContentPane(prereqPanelWindow);
   
   
      //frame text area
      prereqs = new JTextArea(5, rowWidth);
      prereqs.setBorder(eb);
      
      prereqs.setFont(f14);
      prereqs.setBackground(prereqPanelWindow.getBackground());
      prereqs.setLineWrap(true);
      prereqs.setWrapStyleWord(true);
      prereqs.setEditable(false);
      
      //button to duplicate window
      duplicatePrereq = new JButton("Duplicate Window");
      duplicatePrereq.addActionListener(new DuplicatePrereqListener());
      
      
      prereqPanelWindow.add(prereqs);
      prereqPanelWindow.add(duplicatePrereq);
   }
 
    
    /** finds the index of the desired department 
    * @param dept    the department to find the index of */
   private int findIndex(String dept)
   {
      for(int x = 0; x < catalog.size(); x++)
         if(catalog.depts().get(x).getDept().equals(dept))
            return x; 
      return -1;
   }
 
   /** search helper if numField is not empty */ 
   private boolean searchByNumber()
   {
      boolean found = false;
      int n = 0;
      
      titleModel.clear();
      for(int x = 0; x < pinned; x++)
         titleModel.add(x, pinArray[x]);
      try
      {
         n = Integer.parseInt(numField.getText());
         found = catalog.depts().get(deptIndex).containsNumber(n);
         if(found)
         {
            titleModel.addElement(catalog.depts().get(deptIndex).getCourse(n));
            titles.setModel(titleModel);
         }
         else
         {
            titles.setModel(titleModel);
            JOptionPane.showMessageDialog(null, "Could not find course number.");
         }  
      }
      catch(Exception e1)
      {
         titles.setModel(titleModel);
         JOptionPane.showMessageDialog(null, "Entered dialog was not a number.\n"+
               "Please enter a valid number."); 
      }
      
      for(int y = 0; y < pinned; y++)
         if(titleModel.lastIndexOf(pinArray[y]) != y)
            titleModel.remove(titleModel.lastIndexOf(pinArray[y]));
      
      return found;
   }
    /** search helper if keyField is not empty */
   private boolean searchByKeyword()
   {
      String keyword = keyField.getText();
      titleModel.clear();
      
      ArrayList<Course> c = catalog.depts().get(deptIndex).getCourseArrayList();
      for(int x = 0; x < pinned; x++)
         titleModel.add(x, pinArray[x]);
         
      boolean found = false;
         
      for(int x = 0; x < c.size(); x++)
         if(c.get(x).getName().toLowerCase().contains(keyword.toLowerCase()))
         {
            titleModel.addElement(c.get(x));
            found = true;
         }
      
      if(!found)
         JOptionPane.showMessageDialog(null, 
            "Could not find keyword in course listing titles."); 
            
      titles.setModel(titleModel);
            
      return found;
   }
 
    /** search helper, calls the appropriate searchByNumber, searchByKeyword, or load
    * the entire department if neither search field is filled */
   private void search()
   {
      String selectedDept = (String)deptBox.getSelectedItem(); 
   
      deptIndex = findIndex(selectedDept);
      boolean hasNumber = !numField.getText().equals("");
      boolean hasKeyword = !keyField.getText().equals("");
   
      //if number and keyword searches are empty
      if(!hasNumber && !hasKeyword)
      {
         ArrayList<Course> deptCourses = catalog.depts().get(deptIndex).getCourseArrayList();
         titleModel.clear();
         for(int v = 0; v < deptCourses.size(); v++)
            titleModel.addElement(deptCourses.get(v));
         
         for(int x = 0; x < pinned; x++)
         { 
            if(titleModel.contains(pinArray[x]))
               titleModel.removeElement(pinArray[x]);
            titleModel.add(x, pinArray[x]);
         }
         titles.setModel(titleModel);
      }
   
      if(hasNumber && !hasKeyword)
         searchByNumber();
      else if(!hasNumber && hasKeyword)
         searchByKeyword();
      else if(hasNumber && hasKeyword)
         if(searchByKeyword()) //keyword will always return # of results >= number
                                    //(number returns 1 course maximum)
            searchByNumber();  
       
      titles.setBorder(BorderFactory.createEmptyBorder(5,2,5,0));
   }
   
  
    /** helper to add a course as pinned */
   private void addPin()
   { 
      pinArray[pinned] = (Course)titles.getSelectedValue();
      isPinned[pinned] = true;
      titleModel.removeElement(pinArray[pinned]);
      titleModel.add(pinned, pinArray[pinned]);
      pinned++;
      
      titles.setModel(titleModel);
   }
    /** helper to remove a pinned course */
   private void removePin()
   {
      pinned--;
      isPinned[pinned] = false;
     
      Course c = (Course)titles.getSelectedValue();
      int index = titles.getSelectedIndex();
      
      int w;
      for(w = index; w < Math.min(4, pinned+1); w++)
      {
         pinArray[w] = pinArray[w+1];
         titleModel.remove(index);
      } 
      if(Math.min(4,pinned+1) == 4)
         pinArray[w] = null;
               
      for(int x = pinned; x < titleModel.getSize(); x++)
      {
         if(!((Course)titleModel.getElementAt(x)).getDept().equals(c.getDept()))
            break;
         if(((Course)(titleModel.getElementAt(x))).compareTo(c) > 0)
         {
            titleModel.add(x, c);
            break;
         }
      }
      
      for(int y = index; y < pinned; y++)
         titleModel.add(y, pinArray[y]);
     
      titles.setModel(titleModel);
   }
   
    /** helper to deal with pinning courses, appropriately calls the addPin or removePin helpers */
   private void pin()
   {
      if(pinButton.getText().equals("Unpin"))
         removePin();   
         
      if(pinned >= 5 && pinButton.getText().equals("Pin Course"))
      {
         pinButton.setEnabled(false);
         return;
      }
            
      if(pinButton.getText().equals("Pin Course"))
         addPin();
   }

    
    
    /** PinCourseListener pins the current course */
   private class PinCourseListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         pin();
      }
   }

    /** PinKeyListener pins the currently selected course when the P key is pressed,
    * unpins the selected course when BACKSPACE key is pressed, if possible */
   private class PinKeyListener extends KeyAdapter
   {
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_P)
            if(pinned < 5 && titles.getSelectedIndex() > pinned)
               addPin();
         if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            if(titles.getSelectedIndex() <= pinned && titles.getSelectedIndex() >= 0)
               if(isPinned[titles.getSelectedIndex()])
                  removePin();
      }
   }
   
    /** PinListener listens to whether should enable or disable the pin button 
    * based on the currently selected index */
   private class PinListener implements ListSelectionListener 
   {
      public void valueChanged(ListSelectionEvent e)
      {
         if(titles.getSelectedIndex() == -1)
         {
            pinButton.setEnabled(false);
            return;
         }
         
         if( (e.getFirstIndex() >= 0) && (e.getLastIndex() >= 0) )
         {
            int i = titles.getSelectedIndex();
            if(i < isPinned.length && isPinned[i])
               pinButton.setText("Unpin");
            else
               pinButton.setText("Pin Course");
            pinButton.setEnabled(true);
         }
         else
            System.out.println("first: "+e.getFirstIndex()+", last: "+e.getLastIndex()+", model size: "+titles.getModel().getSize());
                        
         if(pinned >= 5 && pinButton.getText().equals("Pin Course"))
         {
            pinButton.setEnabled(false);
            return;
         }
      }
   }
 
    /** PinColor changes highlight color of pinned courses */
   private class PinColor extends DefaultListCellRenderer
   {
      public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
      {
         Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         if(index < pinned)
            c.setBackground(Color.PINK);
      
         return c;
      }
   }
   

    /** SearchListener searches when the search button is pressed*/
   private class SearchListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e) 
      {
         search();
      }
   }
 
    /** SearchKeyListener performs a search when the ENTER key is pressed and 
    * the appropriate field is in focus (ie, when no course is selected) */
   private class SearchKeyListener extends KeyAdapter 
   {                 //KeyAdapter implements ActionListener, KeyListener
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_ENTER)
            search();
      }
   }
 
    /** helper to load popup window for course description */
   private void loadDesc()
   {
      Course current = (Course)titles.getSelectedValue();
      
      title.setText(current.toString());   
      description.setText(current.getDesc());
      
      if(current.getCredits() == -1)
         creditArea.setText(Restriction.VAR_CREDIT);
      else
         creditArea.setText("Credits: "+current.getCredits());
       
      descFrame.setVisible(true);
   }
    /** helpter to load popup window for course prereqs */
   private void loadPrereq()
   {
      Course current = (Course)titles.getSelectedValue();
      String p = current.getPrereqString();
      if(p.equals(""))
         prereqs.setText("No prerequisites.");
      else 
         prereqs.setText(p);
   
      prereqFrame.setVisible(true);
   }
 
 
    /** DescriptionListener loads a new window with course description */
   private class DescriptionListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      { 
         loadDesc();
      }
   }
  
    /** PrereqListener loads a new window that displays course prereqs */
   private class PrereqListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      { 
         loadPrereq();
      }
   }
   
    /** DescKeyListener loads two new windows (both the description and prereqs windows)
    * when a course is selected and user presses the ENTER key */ 
   private class DescKeyListener extends KeyAdapter
   {
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_ENTER)
         {
            loadDesc();
            loadPrereq();
         }
      }
   }
   
        

    /** DuplicateDescListener duplicates the window for a currently loaded description window */
   private class DuplicateDescListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         JFrame d = new JFrame("Duplicate Window");
         d.setLocation(850, 210);
         d.setSize(popupWidth, 300);
      
         //course title
         JTextArea ta = new JTextArea(1, (int)(rowWidth*(14.0/18.0)));
         ta.setBorder(eb);
         ta.setBackground(descPanel.getBackground());
         ta.setFont(f14.deriveFont((float)18));
         ta.setLineWrap(true);
         ta.setWrapStyleWord(true);
         ta.setEditable(false);
         ta.setText(title.getText());
      
         //text area
         JTextArea d2 = new JTextArea(7, rowWidth);
         d2.setBorder(eb);
         d2.setFont(f14);
         d2.setBackground(descPanel.getBackground());
         d2.setLineWrap(true);
         d2.setWrapStyleWord(true);
         d2.setEditable(false);
         d2.setText(description.getText());
      
         //credit details area
         JTextArea ca = new JTextArea(1, rowWidth);
         ca.setBorder(eb);
         ca.setFont(f14);
         ca.setBackground(descPanel.getBackground());
         ca.setLineWrap(true);
         ca.setWrapStyleWord(true);
         ca.setEditable(false);
         ca.setText(creditArea.getText());
                 
         JPanel p = new JPanel();
         p.add(ta);
         p.add(d2);
         p.add(ca);
         
         
         d.setContentPane(p);
         d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         d.setVisible(true);
      }
   }
   
    /** DuplicatePrereqListener duplicates the window for a currently loaded prereq window */
   private class DuplicatePrereqListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         JFrame d = new JFrame("Duplicate Window");
         d.setLocation(850, 520); // 50px increase to both x and y position
         d.setSize(popupWidth, 175); //same size
         
         JTextArea ta = new JTextArea(5, rowWidth);
         ta.setBorder(eb);
         ta.setFont(f14);
         ta.setBackground(d.getBackground());
         ta.setLineWrap(true);
         ta.setWrapStyleWord(true);
         ta.setEditable(false);
         ta.setText(prereqs.getText());
         
         JPanel p = new JPanel();
         p.add(ta);
         
         d.setContentPane(p);
         d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         d.setVisible(true);
      }
   }
}