/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.easyDatabase;

import java.sql.*;
import java.time.*;
import java.util.*;
import postgresql.data.*;

/**
 *
 * @author User
 */
public class GaragenDb extends Database
{
  private static GaragenDb theInstance = null;
  public static GaragenDb getInstance() // Singelton 
  {
    if(theInstance==null)
      theInstance = new GaragenDb();
    return theInstance;
  }
  private GaragenDb()
  {
    super("jdbc:postgresql://localhost:5432/garagenueberwachung", "remote","1234");
  }
  
  public Map<Integer,Ereignistyp> getEreignistypen() throws Exception
  {
    try (GaragenDb db = GaragenDb.getInstance())
    {
      final Map<Integer,Ereignistyp> map = new HashMap<>();
      db.open();
      try (Statement stmt = db.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT * FROM EREIGNISTYP;"))
      {
        while(rs.next())
        {
          final int id = rs.getInt("typId");
          final String text = rs.getString("text");
          map.put(id, new Ereignistyp(id, text));
        }
      }
      return map; 
    }
  }
  public Map<Integer,Objekt> getObjekte() throws Exception
  {
    try (GaragenDb db = GaragenDb.getInstance())
    {
      final Map<Integer,Objekt> map = new HashMap<>();
      db.open();
      try (Statement stmt = db.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT * FROM OBJEKT;"))
      {
        while(rs.next())
        {
          final int id = rs.getInt("objektId");
          final String name = rs.getString("name");
          map.put(id, new Objekt(id, name));
        }
      }
      return map; 
    }
  }
  public Map<Integer,Ereignis> getEreignisse() throws Exception
  {
    
    try (GaragenDb db = GaragenDb.getInstance())
    {
      final Map<Integer,Ereignistyp> ereignistypen = db.getEreignistypen();
      final Map<Integer,Objekt> objekte = db.getObjekte();
      final Map<Integer,Ereignis> ereignisse = new HashMap<>();
      db.open();
      try (Statement stmt = db.createStatement();
           ResultSet rs = stmt.executeQuery("SELECT * FROM EREIGNIS;"))
      {
        while(rs.next())
        {
          final int id = rs.getInt("ereignisId");
          final Timestamp zeit = rs.getTimestamp("zeit");
          final long ms = zeit.getTime();
          final LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochSecond(ms/1000), TimeZone.getDefault().toZoneId());
          final Ereignistyp ereignistyp = ereignistypen.get(rs.getInt("typId"));
          final Objekt objekt = objekte.get(rs.getInt("objektId"));
          final Ereignis ereignis = new Ereignis(id, ereignistyp, objekt, dt);
          ereignisse.put(id, ereignis);
        }
      }
      return ereignisse; 
    }
  }
  
  public void schreibeEreignis(final Ereignis ereignis) throws Exception
  {
    try (GaragenDb db = GaragenDb.getInstance())
    {
      // make LocalDateTime to long 
      final LocalDateTime ldt = ereignis.getZeit();
      final ZonedDateTime zdt = ldt.atZone(TimeZone.getDefault().toZoneId());
      final long ms = zdt.toInstant().toEpochMilli();
      
      final int typId = ereignis.getEreignsityp().getTypId();
      final int objektId = ereignis.getObjekt().getObjektId();
      
      final String sql = String.format(
        "INSERT INTO EREIGNIS (EREIGNISID,ZEIT,TYPID,OBJEKTID)" +
        "  VALUES (" +
        "  TO_TIMESTAMP(%ld)" +
        " %d" + 
        " &d" ,ms,typId,objektId);
      db.open();
      db.executeUpdate(sql);
    }
  }
  
  public static void main(String[] args)
  {
    try
    {
      final GaragenDb db = GaragenDb.getInstance();
      final Map<Integer,Ereignistyp> ereignistypen = db.getEreignistypen();
      final Map<Integer,Objekt> objekte = db.getObjekte();
      final Map<Integer,Ereignis> ereignisse = db.getEreignisse();
      for(Ereignistyp typ : ereignistypen.values())
      {
        System.out.println(typ);
        if(typ.getTypId()==2)
        {
          for(Objekt objekt : objekte.values())
          {
            System.out.println(objekt);
            if(objekt.getObjektId()==0)
            {
              Ereignis ereignis = new Ereignis(0, typ, objekt, LocalDateTime.MIN);
            }
          }
        }
      }
//      for(Objekt objekt : objekte.values())
//      {
//        System.out.println(objekt);
//      }
      for(Ereignis ereignis : ereignisse.values())
      {
        System.out.println(ereignis);
      }
      
      
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
}
