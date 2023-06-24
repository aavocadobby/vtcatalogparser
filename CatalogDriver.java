import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.beans.*;

public class CatalogDriver
{
   private static JFrame choose;

   public static void main(String[] args) 
   {
      choose = new JFrame("Startup");
      /*choose.setSize(400, 200);
      choose.setLocation(500,200);
      choose.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
      JPanel p = new JPanel();
      p.setLayout(new GridLayout(2,1));
      
      JPanel pl = new JPanel();
      JLabel l = new JLabel("Select level: ", SwingConstants.CENTER);
      l.setVerticalAlignment(SwingConstants.CENTER);
     
      p.add(l);
      
      JPanel b = new JPanel();
   
      JButton ug = new JButton("Undergrad");
      ug.addActionListener(new UGListener());
      b.add(ug);
   
      JButton grad = new JButton("Graduate");
      grad.addActionListener(new GradListener());
      b.add(grad);
      
      JButton both = new JButton("All");
      both.addActionListener(new AllListener());
      b.add(both);
       
      p.add(b);
            
      choose.setContentPane(p);
      
      choose.setVisible(true);*/
      
      
      launch();
   }
   
   private static void launch()
   {
      choose.setVisible(false);
      
      JFrame j = new JFrame("Catalog");
      j.setSize(700, 595);
      j.setLocation(100, 50);
      j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      j.setContentPane(new CatalogPanel());
      j.setVisible(true);
   }
   
   private static class UGListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         launch();
      }
   }
  
   private static class GradListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         System.out.println("not yet implemented");
      }
   }
  
   private static class AllListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         System.out.println("not yet implemented");
      }
   }
}