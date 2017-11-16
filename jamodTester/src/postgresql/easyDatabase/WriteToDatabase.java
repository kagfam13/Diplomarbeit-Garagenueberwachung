/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.easyDatabase;

import java.sql.*;
import java.util.logging.*;

/**
 *
 * @author User
 */
public class WriteToDatabase
{
  private final Connection conn;
  private final String typId,objektId;

  
  Statement stmt,stmt1;
  public WriteToDatabase(Connection conn, String typId, String objektId)
  {
    this.conn = conn;
    this.typId = typId;
    this.objektId = objektId;
  }
  
  
  
  
  public void write()
  {
    try
    {
      System.out.println("x");
      Timestamp time = new Timestamp(0);
      System.out.println("x");
      String curTime= time.toString();
      System.out.println("x");
      int id = getId(); // letze id Abfragen <- Problem !!!!
      System.out.println("x");
      String strId = String.format("%d", id++);
      stmt= conn.createStatement();
      System.out.println("x");
      String sql = "INSERT INTO GARAGENUEBERWACHUNG (ID,ZEIT,TYPID,OBJEKTID) "
            + "VALUES (strId,curTime,typId,objektId);";
      stmt.executeUpdate(sql);
    }
    catch (SQLException ex)
    {
      Logger.getLogger(WriteToDatabase.class.getName()).log(Level.SEVERE, null, ex);
      System.out.println("Fehler");
    }
    
  }
  
  private int getId()
  {
    int id=0;
      Connection c = null;
      Statement stmt = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/garagenueberwachung",
            "remote", "1234");
         c.setAutoCommit(false);
      System.out.println("x");
      stmt1 = c.createStatement();
      System.out.println("x");
      // holt die letzte id
      try (ResultSet rs = stmt1.executeQuery( "SELECT * FROM EREIGNIS;" )) 
      {
        // holt die letzte id
        while ( rs.next() )
        {
          id = rs.getInt("ereignisId");
        }
      }
      stmt1.close();
    } 
    catch ( Exception e ) 
    {
         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
         System.exit(0);
    }
    System.out.println("Operation done successfully");
    return id;
  }
  
}
