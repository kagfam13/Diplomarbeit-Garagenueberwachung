/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.easyDatabase;

import java.sql.*;
import java.util.logging.*;
import postgresql.*;

/**
 *
 * @author User
 */
public class ConnectToDatabase
{
  private final String user,pw;
  Connection c;
  public ConnectToDatabase(String user, String pw)
  {
    this.user = user;
    this.pw = pw;
    
  }
  
  
  public void connect()
  {
    int id=0;
    Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/garagenueberwachung",
            user, pw);
      System.out.println("x");
        Statement stmt = c.createStatement();
        System.out.println("x");
        // holt die letzte id
        try (ResultSet rs = stmt.executeQuery( "SELECT * FROM EREIGNIS;" ))
        {
          // holt die letzte id
          while ( rs.next() )
          {
            id = rs.getInt("ereignisId");
          }
        }
        stmt.close();
        /*final WriteToDatabase writeToDatabase = new WriteToDatabase(conn, "0", "1");
        writeToDatabase.write();
        boolean status = writeToDatabase.isStatus();
        System.out.println(""+status);
        */
        c.close();
      }
    catch (SQLException ex)
    {
      Logger.getLogger(DatabaseTester.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (ClassNotFoundException ex)
    {
      Logger.getLogger(ConnectToDatabase.class.getName()).log(Level.SEVERE, null, ex);
    }
      System.out.println(""+id);
      System.out.println("Opened database successfully");
  }

  public Connection getC()
  {
    return c;
  }
  
  
}

