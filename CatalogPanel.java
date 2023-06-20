import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.beans.*;
 
public class CatalogPanel extends JPanel
{ 
   private JComboBox deptBox; //dropdown menu of departments
   private JTextField numField, keyField; //search by course number or keyword
   private JButton sbutton; //search button
 
   private JPanel titlePanel; 
   private JList titles; 
   private DefaultListModel titleModel;
   private JScrollPane scroll;
 
   private JButton loadDescButton, prereqButton; //button to load prereqs + course descs
 
   private JButton pinButton; //button to pin courses to a new window
   int pinned;
   Course[] pinArray;
   boolean[] isPinned;
 
   private JFrame descFrame, prereqFrame; //frames for new windows
   private JPanel descPanel, prereqPanelWindow; //panel in descFrame to display course descs + prereqs
   private JLabel descTitle;
   private JTextArea description, prereqs; //text areas to display the description + prereqs
   private JTextArea creditArea;
 
   private ArrayList<Department> depts;
   private int deptIndex = 0;
 
   private Department load(String prefix, String level)
   {
      Scanner s;
      try
      {
         s = new Scanner(new File(level+"_cat_"+prefix.toLowerCase()+".txt"));      
      } 
      catch(Exception e)
      {
         System.out.print("oops");
         return null;
      }
   
      Department d = new Department(prefix, level); 
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

   private void loadDepartments()
   {
      depts = new ArrayList<Department>();
      depts.add(load("CHEM", "ug"));
      // depts.add(load("CMDA", "ug"));
      depts.add(load("CS", "ug"));
      // depts.add(load("ENGR", "ug"));
      depts.add(load("MATH", "ug"));
      deptIndex = depts.size()-1; //catalog will always default to math, even if 
                        //other catalogs are not loaded
      depts.add(load("MUS", "ug"));
      depts.add(load("PHYS", "ug"));
      depts.add(load("STAT", "ug"));
      // depts.add(load("STS"), "ug");
   }

   public CatalogPanel()
   { 
      loadDepartments();
   
      //setting layout of host CatalogPanel
      setLayout(new FlowLayout());
      setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
   
   
      //font instantiation
      Font f = null;
      Font f14 = null;
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
      String[] prefixes = new String[depts.size()];
      for(int a = 0; a < prefixes.length; a++)
         prefixes[a] = depts.get(a).getDept();
      Arrays.sort(prefixes);
      deptBox = new JComboBox(prefixes);
      deptBox.setSelectedIndex(deptIndex);
      deptBox.addKeyListener(enter);
      deptBox.setFocusable(true);
      deptBox.setMaximumRowCount(depts.size());
   
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
      
      Course[] deptCourses = depts.get(deptIndex).getCourseArray();
      for(int x = 0; x < deptCourses.length; x++)
         titleModel.addElement(deptCourses[x]);
   
      titles = new JList();
      titles.setModel(titleModel);
      titles.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      titles.setLayoutOrientation(JList.VERTICAL);
      titles.setVisibleRowCount(-1); //-1 means shows the maximum possible number of items 
      titles.setFont(f14);
      titles.setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
   
      titles.addKeyListener(new DescKeyListener());
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
   
   
      int popupWidth = 450;
      int rowWidth = 42;
      
      EmptyBorder eb = (EmptyBorder)BorderFactory.createEmptyBorder(5,10,5,10); //top,left,bottom,right
   
      //new popup window for description
      descFrame = new JFrame("Course Description");
      descFrame.setSize(popupWidth, 300);
      descFrame.setLocation(800, 160);
      descFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      descPanel = new JPanel();
      descFrame.setContentPane(descPanel);
   
   
      //text area
      description = new JTextArea(7, rowWidth);
      description.setBorder(eb);
   
      //JTextArea formatting
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
   
   
      descPanel.add(description);
      descPanel.add(creditArea);
   
   
   
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
      
   
      prereqPanelWindow.add(prereqs);
      
      
      
      
     
   }
 
   private int findIndex(String dept)
   {
      for(int x = 0; x < depts.size(); x++)
         if(depts.get(x).getDept().equals(dept))
            return x; 
      return -1;
   }
 
   private void search()
   {
      String selectedDept = (String)deptBox.getSelectedItem(); 
   
      deptIndex = findIndex(selectedDept);
      boolean hasNumber = !numField.getText().equals("");
      boolean hasKeyword = !keyField.getText().equals("");
   
      //if number and keyword searches are empty
      if(!hasNumber && !hasKeyword)
      {
         Course[] deptCourses = depts.get(deptIndex).getCourseArray();
         titles.setListData(deptCourses);
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
   
   private boolean searchByNumber()
   {
      boolean found = false;
      int n = 0;
      
      titleModel.clear();
      try
      {
         n = Integer.parseInt(numField.getText());
         found = depts.get(deptIndex).containsNumber(n);
         if(found)
         {
            titleModel.addElement(depts.get(deptIndex).getCourse(n));
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
    
      
      return found;
   }
   
   private boolean searchByKeyword()
   {
      String keyword = keyField.getText();
      titleModel.clear();
      
      Course[] c = depts.get(deptIndex).getCourseArray();
         
      boolean found = false;
         
      for(int x = 0; x < c.length; x++)
         if(c[x].getName().toLowerCase().contains(keyword.toLowerCase()))
         {
            titleModel.addElement(c[x]);
            found = true;
         }
      
      if(!found)
         JOptionPane.showMessageDialog(null, 
            "Could not find keyword in course listing titles."); 
            
      titles.setModel(titleModel);
            
      return found;
   }
   


   private void reloadPins()
   { 
   
   
   }

   private class PinCourseListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         if(pinned >= 5)
            pinButton.setEnabled(false);
            
         if(pinButton.getText().equals("Pin"))
         {
            if(!pinArray[pinned].equals(titles.getSelectedValue()))
            {
               pinArray[pinned] = (Course)titles.getSelectedValue();
               isPinned[pinned] = true;
               pinned++;
               reloadPins();
            }
         }
            
         if(pinButton.getText().equals("Unpin"))
         {}
         
      }
   }
 
   private class PinListener implements ListSelectionListener
   {
      public void valueChanged(ListSelectionEvent e)
      {
         if(titles.getSelectedIndex() == -1)
         {
            System.out.println("out of bounds -1");
            return;
         }
         
         
         if( (e.getFirstIndex() >= 0) && (e.getLastIndex() >= 0) )
         {
            int i = titles.getSelectedIndex();
            System.out.println(titles.getSelectedIndex());
            
            if(i < isPinned.length && isPinned[i])
               pinButton.setText("Unpin");
            else
               pinButton.setText("Pin Course");
             
            pinButton.setEnabled(true);
         }
         else
            System.out.println("first: "+e.getFirstIndex()+", last: "+e.getLastIndex()+", model size: "+titles.getModel().getSize());
        
      }
   }
 
   private class SearchListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e) 
      {
         search();
      }
   }
 
   private class SearchKeyListener extends KeyAdapter 
   {                 //KeyAdapter implements ActionListener, KeyListener
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_ENTER)
            search();
      }
   }
 
   // new window will pop up beside the main frame, containing the course description
   private void loadDesc()
   {
      Course current = (Course)titles.getSelectedValue();
      
      description.setText(current.getDesc());
      if(current.getCredits() == -1)
         creditArea.setText(Restriction.VAR_CREDIT);
      else
         creditArea.setText("Credits: "+current.getCredits());
       
      descFrame.setVisible(true);
   }
 
 
   private class DescriptionListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      { 
         loadDesc();
      }
   }
 
 
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
 
   private class PrereqListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      { 
         loadPrereq();
      }
   }
 

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
}