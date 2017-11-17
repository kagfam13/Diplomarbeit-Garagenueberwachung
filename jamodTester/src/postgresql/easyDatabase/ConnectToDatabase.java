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
  private final String user="remote",pw="1234";
  private final int typId, objektId;
  Connection c;
  private int id=0;
  private int oldTypId=0,oldObjektId=0;
  private Timestamp zeit = null;
  
  public ConnectToDatabase(int typId, int objektId)
  {
    this.typId = typId;
    this.objektId = objektId;
    update();
  }

  public ConnectToDatabase()
  {
    this(0,0);
    connect();
  }
  
  
  public void update()
  {
    String strId=null,strZeit=null,strOldTypId=null,strOldObjektId=null;
    Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/garagenueberwachung",
            user, pw);
        
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        Statement stmt = c.createStatement();
        // holt die letzte id
        ResultSet rs1 = stmt.executeQuery( "SELECT * FROM EREIGNIS;" );
        while ( rs1.next() )
          {
            id = rs1.getInt("ereignisId");
          }
        id++;
        String sql = "INSERT INTO EREIGNIS (EREIGNISID,ZEIT,TYPID,OBJEKTID) "
              + "VALUES (\'"+id+"\',\'"+timeStamp+"\',\'"+typId+"\',\'"+objektId+"\');";
        stmt.executeUpdate(sql);
        
        ResultSet rs = stmt.executeQuery( "SELECT * FROM EREIGNIS;" );
        while ( rs.next() )
          {
            id = rs.getInt("ereignisId");
            zeit = rs.getTimestamp("zeit");
            oldTypId = rs.getInt("typId");
            oldObjektId = rs.getInt("objektId");
          }
        strId = String.format("%d", id);
        strZeit = timeStamp.toString();
        strOldTypId = String.format("%d",oldTypId);
        strOldObjektId = String.format("%d",oldObjektId);
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
      System.out.println(strZeit);
      System.out.println(strOldTypId);
      System.out.println(strOldObjektId);
      System.out.println("Opened database successfully");
  }
  public void connect()
  {
    
    String strId=null,strZeit=null,strOldTypId=null,strOldObjektId=null;
    Connection c = null;
      try {
         Class.forName("org.postgresql.Driver");
         c = DriverManager
            .getConnection("jdbc:postgresql://localhost:5432/garagenueberwachung",
            user, pw);
          Statement stmt = c.createStatement();
          String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
          
//        // holt die letzte id
//        ResultSet rs1 = stmt.executeQuery( "SELECT * FROM EREIGNIS;" );
//        while ( rs1.next() )
//          {
//            id = rs1.getInt("ereignisId");
//          }
//        id++;
//        String sql = "INSERT INTO EREIGNIS (EREIGNISID,ZEIT,TYPID,OBJEKTID) "
//              + "VALUES (\'"+id+"\',\'"+timeStamp+"\',\'"+typId+"\',\'"+objektId+"\');";
//        stmt.executeUpdate(sql);
        
        ResultSet rs = stmt.executeQuery( "SELECT * FROM EREIGNIS;" );
        while ( rs.next() )
          {
            id = rs.getInt("ereignisId");
            zeit = rs.getTimestamp("zeit");
            oldTypId = rs.getInt("typId");
            oldObjektId = rs.getInt("objektId");
          }
        strId = String.format("%d", id);
        strZeit = timeStamp.toString();
        strOldTypId = String.format("%d",oldTypId);
        strOldObjektId = String.format("%d",oldObjektId);
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
      System.out.println(strZeit);
      System.out.println(strOldTypId);
      System.out.println(strOldObjektId);
      System.out.println("Opened database successfully");
  }

  public Connection getC()
  {
    return c;
  }

  public int getId()
  {
    return id;
  }

  public int getOldTypId()
  {
    return oldTypId;
  }

  public int getOldObjektId()
  {
    return oldObjektId;
  }

  public Timestamp getZeit()
  {
    return zeit;
  }
  
  
}

