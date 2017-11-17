/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.easyDatabase;

import java.sql.*;
import java.text.*;
import java.time.*;
import java.util.logging.*;
import postgresql.*;

/**
 *
 * @author User
 */
public class ConnectToDatabase
{
  private final String user,pw;
  private final int typId, objektId;
  Connection c;
  public ConnectToDatabase(String user, String pw, int typId, int objektId)
  {
    this.user = user;
    this.pw = pw;
    this.typId = typId;
    this.objektId = objektId;
  }
  
  
  public void connect()
  {
    int id=0;
    int oldTypId=0,oldObjektId=0;
    Timestamp zeit = null;
    String strId=null;
    Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/garagenueberwachung",
            user, pw);
        Statement stmt = c.createStatement();
        // holt die letzte id
          ResultSet rs = stmt.executeQuery( "SELECT * FROM EREIGNIS;" );
          // holt die letzte id
          while ( rs.next() )
          {
            id = rs.getInt("ereignisId");
            zeit = rs.getTimestamp("zeit");
            oldTypId = rs.getInt("typId");
            oldObjektId = rs.getInt("objektId");
          }
          
//        Timestamp time = new Timestamp(0);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        String srTime  = dateFormat.toString();
        
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        //long time = System.currentTimeMillis();
        strId = String.format("%d", id);
        id++;
        String sql = "INSERT INTO EREIGNIS (EREIGNISID,ZEIT,TYPID,OBJEKTID) "
              + "VALUES (\'"+id+"\',\'"+timeStamp+"\',\'"+typId+"\',\'"+objektId+"\');";
        stmt.executeUpdate(sql);
        stmt.close();
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
      System.out.println(strId);
      System.out.println("Opened database successfully");
  }

  public Connection getC()
  {
    return c;
  }
  
  
}

