/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.easyDatabase;

import java.sql.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
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
    super("jdbc:postgresql://localhost:5432/garagenueberwachung", "remote","1234");  // "pi", "12345" || "remote", "1234"
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
          final int id = rs.getInt("ereignisid");
          final Ereignistyp ereignistyp = ereignistypen.get(rs.getInt("typid"));
          final Objekt objekt = objekte.get(rs.getInt("objektid"));
          final Timestamp zeit = rs.getTimestamp("zeit");
//          final long ms = zeit.getTime();
//          final LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochSecond(ms/1000), TimeZone.getDefault().toZoneId());
          final LocalDateTime dt = zeit.toLocalDateTime();
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
//      final LocalDateTime ldt = ereignis.getZeit();
//      final ZonedDateTime zdt = ldt.atZone(TimeZone.getDefault().toZoneId());
//      final long ms = zdt.toEpochSecond();
      
      final int typId = ereignis.getEreignistyp().getTypId();
      final int objektId = ereignis.getObjekt().getObjektId();
      LocalDateTime ldt = ereignis.getZeit();
      ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
      long ms = zdt.toInstant().toEpochMilli();
      final Timestamp ts = new Timestamp(ms);
//      System.out.println(ts.toString());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      sdf.setTimeZone(TimeZone.getDefault());
      String tsString = sdf.format(ts.getTime());
      final String sql = String.format(
        "INSERT INTO ereignis (typid,objektid,zeit)" +
        "  VALUES (" +
        "'%d'," + 
        " '%d'," + 
        " '%s')",typId,objektId,tsString);
      
//      System.out.println(tsString);
//      System.out.println(sql);
      db.open();
      int lastID = db.executeUpdateReturnLastVal(sql);
//      System.out.format("%d%n",lastID);
      ereignis.setId(lastID);
//      System.out.format("%s",ereignis.toString());
    }
  }
  
  public int getLastId() throws Exception
  {
    int id=1;
    final Map<Integer,Ereignis> ereignisse = getEreignisse();
      for(Ereignis ereignis : ereignisse.values())
      {
        System.out.println(ereignis);
        id = ereignis.getId();
      }
    return id;
  }
  
  public Ereignistyp getEreignistyp(int typID) throws Exception
  {
    final Map<Integer,Ereignistyp> ereignistypen = getEreignistypen();
    for(Ereignistyp typ : ereignistypen.values())
      {
        if(typ.getTypId() == typID)
        {
          return typ;
        }
      }
    return null;
  }
  public Objekt getObjekt(int objektID) throws Exception
  {
    final Map<Integer,Objekt> objekte = getObjekte();
    for(Objekt objekt : objekte.values())
      {
        if(objekt.getObjektId() == objektID)
          return objekt;
      }
    return null;
  }
  
  public Ereignis getLastEreignis(int objektID ) throws Exception
  {
    try(GaragenDb db = GaragenDb.getInstance())
    {
      final Map<Integer,Ereignistyp> ereignistypen = db.getEreignistypen();
      final Map<Integer,Objekt> objekte = db.getObjekte();
      String sql =  String.format("SELECT * FROM ereignis WHERE objektid = %d ORDER BY zeit DESC LIMIT 1", objektID);
      db.open();
      try (Statement statement = db.createStatement();final ResultSet rs = statement.executeQuery(sql))
      {
        
        if (!rs.next())
          return null;
        final int id = rs.getInt("ereignisid");
        final Ereignistyp ereignistyp = ereignistypen.get(rs.getInt("typid"));
        final Objekt objekt = objekte.get(rs.getInt("objektid"));
        final Timestamp zeit = rs.getTimestamp("zeit");
        final LocalDateTime dt = zeit.toLocalDateTime();
        final Ereignis ereignis = new Ereignis(id, ereignistyp, objekt, dt);
        return ereignis;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  public boolean checkTypId(int typId,int objektId)
  {
    try(GaragenDb db = GaragenDb.getInstance())
    {
      final Ereignis lastEreignis = db.getLastEreignis(objektId);
      if(lastEreignis!=null && lastEreignis.getEreignistyp().getTypId() == typId)
        return false;
      else
        return true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
  public boolean HandleDatabase(int typId, int objektId)
  {
    try
    {
      final GaragenDb db = GaragenDb.getInstance();
//        final Map<Integer,Ereignis> ereignisse = db.getEreignisse();
        final Ereignistyp typ = db.getEreignistyp(typId); //TODO GET VALUE FROM COILS
        final Objekt objekt = db.getObjekt(objektId);
        boolean isOk = db.checkTypId(typId, objektId);
        Ereignis ereignis = new Ereignis(0,typ, objekt, LocalDateTime.now());
        System.out.println("neues Ereignis erzeugt");
        System.out.println(ereignis);
        if(isOk)
        {
//          System.out.format("Neues Ereignis mit der ID = %d wurde gerschrieben \n",);
          db.schreibeEreignis(ereignis);
          return true;
        }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  
  
  public static void main(String[] args)
  {
    try
    {
//      final Map<Integer,Ereignistyp> ereignistypen = db.getEreignistypen();
//      final Map<Integer,Objekt> objekte = db.getObjekte();
//      final GaragenDb db = GaragenDb.getInstance();
//      final Map<Integer,Ereignis> ereignisse = db.getEreignisse();
//      for(Ereignis ereignis : ereignisse.values())
//      {
//        System.out.println(ereignis);
//      }
//      for(Ereignistyp typ : ereignistypen.values())
//      {
//        System.out.println(typ);
//          
//      }
//      for(Objekt objekt : objekte.values())
//      {
//        System.out.println(objekt);
//        Ereignis ereignis = new Ereignis(id+1, typ, objekt, LocalDateTime.MIN);
//        db.schreibeEreignis(ereignis);
//      }

      
      
//    Ein Ereignis schreiben 
//        final GaragenDb db = GaragenDb.getInstance();
//        int ereignisID = db.getLastId();
//        final Map<Integer,Ereignis> ereignisse = db.getEreignisse();
//        final Ereignistyp typ = db.getEreignistyp(0); //TODO GET VALUE FROM COILS
//        int typID = typ.getTypId(); 
//        final Objekt objekt = db.getObjekt(1);
//        int objektID = objekt.getObjektId();
//        ereignisID ++;
//        boolean isOk = db.getEreignisfromTypId(typ, objekt);
//        Ereignis ereignis = new Ereignis(ereignisID, typ, objekt, LocalDateTime.now());
//        System.out.println("neues Ereignis erzeugt");
//        System.out.println(ereignis);
//        if(isOk)
//        {
//          System.out.println("Funzt");
//          db.schreibeEreignis(ereignis);
//        }
//        else
//        {
//          System.out.println("gaxi");
//        }
//      db.HandleDatabase(0, 2);
      //db.schreibeEreignis(ereignis);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
}
