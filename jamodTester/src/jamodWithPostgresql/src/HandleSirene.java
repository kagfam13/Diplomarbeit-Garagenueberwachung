/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql.src;

import java.sql.*;
import java.time.*;
import postgresql.data.*;
import postgresql.easyDatabase.*;

/**
 *
 * @author User
 */
public class HandleSirene
{
  private final int sirnenobjektID;
  final GaragenDb db = GaragenDb.getInstance();
  public boolean newEreignis = MainProgramm.newCarEreignis;
  private long totalSec;

  public HandleSirene(int carnumber)
  {
    this.sirnenobjektID = carnumber;
    handle();
  }
  
  public long getSecondsOfTimestamp(int ereignisId) throws Exception
  {
    long sec;
    Ereignis ereignis = db.getLastEreignis(ereignisId);
    LocalDateTime ldt = ereignis.getZeit();
    ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
    sec = zdt.toEpochSecond();
    return sec;
  }
  
  private void handle()
  {
    try
    {
      // Write Sirenen Ereignis into Database
      db.HandleDatabase(2, sirnenobjektID);
      int lastSirenenID = db.getLastId();
      totalSec = getSecondsOfTimestamp(lastSirenenID);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public long getTotalSec()
  {
    return totalSec;
  }
  
}
