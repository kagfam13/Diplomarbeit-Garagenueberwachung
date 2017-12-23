/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql.src;

import jamodWithPostgresql.data.DataFromArduino;
import static jamodWithPostgresql.src.Constants.*;
import jamodtester.easyModbus.*;
import java.net.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.swing.*;
import net.wimpi.modbus.*;
import postgresql.data.*;
import postgresql.easyDatabase.*;

/**
 *
 * @author User
 */
public class MainProgramm
{
  private static int typIdGate, typIdCar;
  private static EasyModbusMaster master0;//,master1,master2,master3,master4;
  private static EasyModbusSlave slave;
  public static boolean newCarEreignis;
  
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture future;
  
  
  public void handleDB(EasyModbusMaster master,int arduinoNumber) {
      DataFromArduino data = getDataVonArduino(master);
      
      int carId=0, torId=0, gateSensorId=0;
      
      switch (arduinoNumber)
      {
        case 0:
          carId=0;
          torId=5;
          gateSensorId=15;
          break;
        case 1:
          carId=1;
          torId=6;
          gateSensorId=17;
          break;
        case 2:
          carId=2;
          torId=7;
          gateSensorId=19;
          break;
        case 3:
          carId=3;
          torId=8;
          gateSensorId=21;
          break;
        case 4:
          carId=4;
          torId=9;
          gateSensorId=23;
          break;
        default :
          break;
      }
      
      if(data.isAuto())
        typIdCar=1;
      else 
        typIdCar=0;
      if(data.isSensorOben())
        typIdGate=3;
      else
      {
        if(data.isSensorUnten())
          typIdGate=4;
        else
          typIdGate=5;
      }
        
      // create Database
      try
      {
        newCarEreignis = false;
        final GaragenDb db = GaragenDb.getInstance();
        boolean isOkCar = db.HandleDatabase(typIdCar,carId);
        boolean isOkGate = db.HandleDatabase(typIdGate,torId);
        // Daten im Slave ändern
        if(isOkCar)
        {
          slave.setCoil(carId+10,data.isAuto());
          // wenn ein Neues CarEreignis gesetzt wurde dann wird dies ausgeführt: 
          newCarEreignis = true;
        }
        if(isOkGate)
        {
          
          System.out.println(slave);
          slave.setCoil(gateSensorId+1,data.isSensorOben());
          slave.setCoil(gateSensorId, data.isSensorUnten());
        }
        
        // Sirne 
        
        
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      
    }
    
    
    private DataFromArduino getDataVonArduino(EasyModbusMaster master)
    {
      Boolean[] coils = master.getCoils();
      return new DataFromArduino(coils[2],coils[3],coils[4]);
    }
    
  public void handleDataFromAndroid(EasyModbusSlave slave) throws Exception
  {
    // TODO: Daten vom Android lesen und an Arduino weiterleiten 
    if(slave.getCoil(TOR1AUF)) {
      master0.writeCoil(ARDUINOTORAUF, true);
      slave.setCoil(TOR1AUF, false);
    }
    if(slave.getCoil(TOR1ZU)) {
      master0.writeCoil(ARDUINOTORZU, true);
      slave.setCoil(TOR1ZU, false);
    }
    /*if(slave.getCoil(TOR2AUF)) {
      master1.writeCoil(ARDUINOTORAUF, true);
    }
    if(slave.getCoil(TOR2ZU)) {
      master1.writeCoil(ARDUINOTORZU, true);
    }
    if(slave.getCoil(TOR3AUF)) {
      master2.writeCoil(ARDUINOTORAUF, true);
    }
    if(slave.getCoil(TOR3ZU)) {
      master2.writeCoil(ARDUINOTORZU, true);
    }
    if(slave.getCoil(TOR4AUF)) {
      master3.writeCoil(ARDUINOTORAUF, true);
    }
    if(slave.getCoil(TOR4ZU)) {
      master3.writeCoil(ARDUINOTORZU, true);
    }
    if(slave.getCoil(TOR5AUF)) {
      master4.writeCoil(ARDUINOTORAUF, true);
    }
    if(slave.getCoil(TOR5ZU)) {
      master4.writeCoil(ARDUINOTORZU, true);
    }*/
  }
  public long getSecondsOfTimestamp(int ereignisId) throws Exception
  {
    long sec;
    GaragenDb db = GaragenDb.getInstance();
    Ereignis ereignis = db.getLastEreignis(ereignisId);
    LocalDateTime ldt = ereignis.getZeit();
    ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
    sec = zdt.toEpochSecond();
    return sec;
  }
  private void handleSecondsOfLastSirenenalarm() throws Exception
  {
    GaragenDb db = GaragenDb.getInstance();
    long secondsofLastEreignis = 0;
    Ereignis ereignis = db.getLastEreignis(SIRENENOBJEKTID);
    int ereignisId = ereignis.getId();
    if(ereignis != null)
    {
      secondsofLastEreignis = getSecondsOfTimestamp(ereignisId+1);
    }
    HandleSirene sirene = new HandleSirene(SIRENENOBJEKTID);
    long secondsOfLastSirene = sirene.getTotalSec();
    long totalSec = secondsofLastEreignis - secondsOfLastSirene;
    slave.setRegister(0, (int) totalSec);
    
  }
  public static void main(String[] args)
  {
    try
    {
      master0 = new EasyModbusMaster(4444, Modbus.DEFAULT_UNIT_ID, InetAddress.getLocalHost(), 2, 3);
      /*
      master1 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP2), 2, 3);
      master2 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP3), 2, 3);
      master3 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP4), 2, 3);
      master4 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP5), 2, 3);
      */
//      System.out.println("test0");
      // new BackgroundWorker().execute();
      slave = new EasyModbusSlave(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID,10,15,0,1);
      slave.start();
      slave.setRegister(0, 0);
      while(true) {
        new MainProgramm().handleDB(master0, 0);
        /*
        new MainProgramm().handleDB(master1, 1);
        new MainProgramm().handleDB(master2, 2);
        new MainProgramm().handleDB(master3, 3);
        new MainProgramm().handleDB(master4, 4);
*/      
        new MainProgramm().handleSecondsOfLastSirenenalarm();
        new MainProgramm().handleDataFromAndroid(slave);
        
        Thread.sleep(2000);
      }

    }
    catch (UnknownHostException ex)
    {
      Logger.getLogger(MainProgramm.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (InterruptedException ex)
    {
      Logger.getLogger(MainProgramm.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (Exception ex)
    {
      Logger.getLogger(MainProgramm.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
