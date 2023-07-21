import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.beans.*;

/** 
* Driver class for the CatalogParser package. Prompts user to choose the level to load from
* (undergrad, graduate, or both), and launches the appropriate catalog.
*
* @author Anhui Zhang [zhanganhui@vt.edu]
* @version 07.2023
*/

public class CatalogDriver
{
   /** JFrame that holds buttons to choose level to launch. */
   private static JFrame choose;

    /** main method */
   public static void main(String[] args) 
   {
      choose = new JFrame("Startup");
      choose.setSize(400, 200);
      choose.setLocation(500,200);
      choose.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
   
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(2,1));
      
      JPanel pl = new JPanel();
      JLabel l = new JLabel("Select level: ", SwingConstants.CENTER);
      l.setVerticalAlignment(SwingConstants.CENTER);
     
      p.add(l);
      
      JPanel b = new JPanel();
      b.setFocusable(true);
   
      JButton ug = new JButton("Undergrad");
      ug.addActionListener(new UGListener());
      ug.setMnemonic(KeyEvent.VK_U);
      b.addKeyListener(new U()); //clicking U key also does same action as clicking button
      b.add(ug);
   
      JButton grad = new JButton("Graduate");
      grad.addActionListener(new GradListener());
      grad.setMnemonic(KeyEvent.VK_G);
      b.addKeyListener(new G());
      b.add(grad);
      
      JButton both = new JButton("All");
      both.addActionListener(new AllListener());
      both.setMnemonic(KeyEvent.VK_A);
      b.addKeyListener(new A());
      b.add(both);
      
                     
      p.add(b);
      choose.setContentPane(p);
      choose.setVisible(true);
   }
   
   /** private helper method to launch the catalog */   
   private static void launch(String level)
   {
      choose.setVisible(false);
      
      JFrame j;
      if(level.equals("ug"))
         j = new JFrame("Undergraduate Catalog");
      else if(level.equals("grad"))
         j = new JFrame("Graduate Catalog");
      else
         j = new JFrame("Full Catalog");
   
      j.setSize(700, 595);
      j.setLocation(100, 50);
      j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      j.setContentPane(new CatalogPanel(level));
      j.setVisible(true);
   }
   
   /**
   * UGListener is a listener for the button that selects the 
   * Undergraduate catalog to be launched.
   */
   private static class UGListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         launch("ug");
      }
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_U)
            launch("ug");
      }
   }
  
   private static class U extends KeyAdapter
   {
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_U)
            launch("ug");
      }
   }
   
   /**
   * GradListener is a listener for the button that selects the 
   * Gradeuate catalog to be launched.
   */
   private static class GradListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         launch("grad");
      }
   }
  
   private static class G extends KeyAdapter
   {
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_G)
            launch("grad");
      }
   }
   
   /**
   * AllListener is the listener for the button that launches 
   * a catalog with both Undergrad and Graduate courses.
   */
   private static class AllListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         launch("both");
      }
   }
   
   private static class A extends KeyAdapter
   {  
      public void keyPressed(KeyEvent e)
      {
         if(e.getKeyCode() == KeyEvent.VK_A)
            launch("both");
      }
   }
}