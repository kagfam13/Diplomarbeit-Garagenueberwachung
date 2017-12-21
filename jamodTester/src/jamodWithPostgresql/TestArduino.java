/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql;

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
public class TestArduino
{
  private static int typID;
  private static EasyModbusMaster master0;
  private final static String IP1 = "10.200.112.71";
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture future;
  
  /*
  private static class BackgroundWorker extends SwingWorker<Object, Object> {
    
    private void handleDB(EasyModbusMaster master,int arduinoNumber) {
      DataVonFink data = getDataVonArduino(master);
      
      
      int carId=0, torId=0;
      
      switch (arduinoNumber)
      {
        case 0:
          carId=0;
          torId=5;
          break;
        case 1:
          carId=1;
          torId=6;
          break;
        case 2:
          carId=2;
          torId=7;
          break;
        case 3:
          carId=3;
          torId=8;
          break;
        case 4:
          carId=4;
          torId=9;
          break;
        default :
          break;
      }
      
      if(data.isAuto())
        typID=1;
      else 
        typID=0;
      if(data.isSensorOben())
        typID=3;
      else
      {
        if(data.isSensorUnten())
          typID=4;
        else
          typID=5;
      }
        
      // create Database
      try
      {
        final GaragenDb db = GaragenDb.getInstance();
        db.HandleDatabase(typID,carId);
        db.HandleDatabase(typID,torId);
      }
      catch (Exception e)
      {
      }
      
    }
    
    
    private DataVonFink getDataVonArduino(EasyModbusMaster master)
    {
      Boolean[] coils = master.getCoils();
      return new DataVonFink(coils[0],coils[1],coils[2]);
    }
    
    @Override
    protected Object doInBackground() throws Exception
    {
      System.out.println("test1");
      while (true) {
        System.out.println("test2");
        handleDB(master0, 0);
        
        Thread.sleep(5000);
      }
    }
    
  }
  */
  
  public void handleDB(EasyModbusMaster master,int arduinoNumber) {
      DataVonFink data = getDataVonArduino(master);
      
      int carId=0, torId=0;
      
      switch (arduinoNumber)
      {
        case 0:
          carId=0;
          torId=5;
          break;
        case 1:
          carId=1;
          torId=6;
          break;
        case 2:
          carId=2;
          torId=7;
          break;
        case 3:
          carId=3;
          torId=8;
          break;
        case 4:
          carId=4;
          torId=9;
          break;
        default :
          break;
      }
      
      if(data.isAuto())
        typID=1;
      else 
        typID=0;
      if(data.isSensorOben())
        typID=3;
      else
      {
        if(data.isSensorUnten())
          typID=4;
        else
          typID=5;
      }
        
      // create Database
      try
      {
        final GaragenDb db = GaragenDb.getInstance();
        db.HandleDatabase(typID,carId);
        db.HandleDatabase(typID,torId);
      }
      catch (Exception e)
      {
      }
      
    }
    
    
    private DataVonFink getDataVonArduino(EasyModbusMaster master)
    {
      Boolean[] coils = master.getCoils();
      return new DataVonFink(coils[0],coils[1],coils[2]);
    }
    
  
  public static void main(String[] args)
  {
    try
    {
      master0 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getLocalHost(), 2, 3);
      System.out.println("test0");
      // new BackgroundWorker().execute();
      
      while(true) {
        new TestArduino().handleDB(master0, 0);
        Thread.sleep(5000);
      }

    }
    catch (UnknownHostException ex)
    {
      Logger.getLogger(TestArduino.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (InterruptedException ex)
    {
      Logger.getLogger(TestArduino.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
