/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql.src;

//import com.pi4j.io.gpio.*;
//import com.pi4j.io.gpio.event.*;
import com.pi4j.io.gpio.*;
import jamodWithPostgresql.data.DataFromArduino;
import static jamodWithPostgresql.src.Constants.*;
import jamodtester.easyModbus.*;
import java.net.*;
import java.time.*;
import java.util.concurrent.*;
import java.util.logging.*;
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
  private static EasyModbusMaster master0,master1,master2,master3,master4;
  private static EasyModbusSlave slave;
  public static boolean newCarEreignis;
  
  private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  private ScheduledFuture future;
  private boolean Tor0IsZu,Tor0IsAuf,Tor1IsZu,Tor1IsAuf,Tor2IsZu,Tor2IsAuf,Tor3IsZu,Tor3IsAuf,Tor4IsZu,Tor4IsAuf;
  private boolean isOeffnen0,isOeffnen1,isOeffnen2,isOeffnen3,isOeffnen4;
  private boolean isSchliessen0,isSchliessen1,isSchliessen2,isSchliessen3,isSchliessen4;
  
    
        /* Clamp 1 */
//  private static GpioController gpio;
//  private static GpioPinDigitalInput clamp1;
//  private boolean active1 = false;
  
  public void handleDB(EasyModbusMaster master,int arduinoNumber) throws Exception {
      DataFromArduino data = getDataFromArduino(master);
      
      int carId=0, torId=0, gateSensorId=0;
      
      switch (arduinoNumber)
      {
        case 0:
          carId=0;
          torId=5;
          gateSensorId=15;
          if(data.isSensorUnten())
          {
            Tor0IsZu = true;
            Tor0IsAuf = false;
          }
          if(data.isSchließen())
            isSchliessen0 = true;
          else
            isSchliessen0 = false;
          if(data.isOeffnen()==true)
            isOeffnen0=true;
          else
            isOeffnen0 = false;
          if(data.isSensorOben())
          {
            Tor0IsZu = false;
            Tor0IsAuf = true;
          }
          break;
        case 1:
          carId=1;
          torId=6;
          gateSensorId=17;
          if(data.isSensorUnten())
          {
            Tor1IsZu = true;
            Tor1IsAuf = false;
          }
          if(data.isSensorOben())
          {
            Tor1IsZu = false;
            Tor1IsAuf = true;
          }
          break;
        case 2:
          carId=2;
          torId=7;
          gateSensorId=19;
          if(data.isSensorUnten())
          {
            Tor2IsZu = true;
            Tor2IsAuf = false;
          }
          if(data.isSensorOben())
          {
            Tor2IsZu = false;
            Tor2IsAuf = true;
          }
          break;
        case 3:
          carId=3;
          torId=8;
          gateSensorId=21;
          if(data.isSensorUnten())
          {
            Tor3IsZu = true;
            Tor3IsAuf = false;
          }
          if(data.isSensorOben())
          {
            Tor3IsZu = false;
            Tor3IsAuf = true;
          }
          break;
        case 4:
          carId=4;
          torId=9;
          gateSensorId=23;
          if(data.isSensorUnten())
          {
            Tor4IsZu = true;
            Tor4IsAuf = false;
          }
          if(data.isSensorOben())
          {
            Tor4IsZu = false;
            Tor4IsAuf = true;
          }
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
          newCarEreignis = true;
        slave.setCoil(carId+10,data.isAuto());
//        if(isOkGate)
//        {
          slave.setCoil(gateSensorId+1,data.isSensorUnten());
          slave.setCoil(gateSensorId, data.isSensorOben());
//        }
        
          
        
        System.out.println("Coils aktualisiert");
        
        
      }
    catch (Exception ex)
    {
      Logger.getLogger(MainProgramm.class.getName()).log(Level.SEVERE, null, ex);
      ex.printStackTrace();
      System.out.println("Fehler bei der Datenauslese" + ex.getMessage());
    }
      
    }
    
    
    private DataFromArduino getDataFromArduino(EasyModbusMaster master)
    {
      Boolean[] coils = master.getCoils();
      return new DataFromArduino(coils[2],coils[3],coils[4],coils[0],coils[1]);
    }
    
  public void handleDataFromAndroid(EasyModbusSlave slave) throws Exception
  {
    // TODO: Daten vom Android lesen und an Arduino weiterleiten 
    if(slave.getCoil(TOR1AUF) && !Tor0IsAuf) {
      master0.writeCoil(ARDUINOTORAUF, true);
      System.out.println("Tor1 auffahren");
    }
    if(!Tor0IsZu && slave.getCoil(TOR1AUF) && !isSchliessen0)
    {
      slave.setCoil(TOR1AUF,false);
      System.out.println("Coil zurückgesetzt");
    }
    if(slave.getCoil(TOR1ZU) && !Tor0IsZu) {
      master0.writeCoil(ARDUINOTORZU, true);
      System.out.println("Tor1 zufahren");
    }
      if(!Tor0IsAuf && isSchliessen0)
      {
        slave.setCoil(TOR1ZU,false);
        System.out.println("Coil zurückgesetzt");
      }
    if(slave.getCoil(TOR2AUF)) {
      master1.writeCoil(ARDUINOTORAUF, true);
      System.out.println("Tor2 auffahren");
    }
    if(slave.getCoil(TOR2ZU)) {
      master1.writeCoil(ARDUINOTORZU, true);
      System.out.println("Tor2 zufahren");
    }
    
    if(slave.getCoil(TOR3AUF)) {
      master2.writeCoil(ARDUINOTORAUF, true);
      System.out.println("Tor3 auffahren");
    }
    if(slave.getCoil(TOR3ZU)) {
      master2.writeCoil(ARDUINOTORZU, true);
      System.out.println("Tor3 zufahren");
    }
    if(slave.getCoil(TOR4AUF)) {
      master3.writeCoil(ARDUINOTORAUF, true);
      System.out.println("Tor4 auffahren");
    }
    if(slave.getCoil(TOR4ZU)) {
      master3.writeCoil(ARDUINOTORZU, true);
      System.out.println("Tor4 zufahren");
    }
    if(slave.getCoil(TOR5AUF)) {
      master4.writeCoil(ARDUINOTORAUF, true);
      System.out.println("Tor5 auffahren");
    }
    if(slave.getCoil(TOR5ZU)) {
      master4.writeCoil(ARDUINOTORZU, true);
      System.out.println("Tor5 zufahren");
    }
//    if((!Tor0IsAuf && Tor0IsZu) && !isSchliessen0)
//    {
//      slave.setCoil(TOR1ZU, false);
//      System.out.println("Coil zurückgesetzt");
//    }
//      if((!Tor0IsZu && Tor0IsAuf) && !isOeffnen0) 
//      {
//        slave.setCoil(TOR1AUF, false);
//        System.out.println("Coil zurückgesetzt");
//      }
//    if(!Tor1IsAuf)
//      slave.setCoil(TOR2ZU, false);
//    if(!Tor1IsZu)
//        slave.setCoil(TOR2AUF, false);
//    if(!Tor2IsAuf)
//      slave.setCoil(TOR2ZU, false);
//    if(!Tor2IsZu)
//        slave.setCoil(TOR2AUF, false);
//    if(!Tor3IsAuf)
//      slave.setCoil(TOR3ZU, false);
//    if(!Tor3IsZu)
//        slave.setCoil(TOR3AUF, false);
//    if(!Tor4IsAuf)
//      slave.setCoil(TOR4ZU, false);
//    if(!Tor4IsZu)
//        slave.setCoil(TOR4AUF, false);
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
//    slave.setRegister(0, (int) totalSec);
    slave.setRegister(0, 98);
  }
  
  private void handle()
  {
//    /* Clamp 1 */ 
//        GpioPinListenerDigital clamp1Listener = new GpioPinListenerDigital() {
//            @Override
//            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//              boolean active1= false;  
//              try {
//                  
//                    if(event.getState() == PinState.HIGH && !active1) {
//                        active1=true; 
//                        handleSecondsOfLastSirenenalarm();
//                        active1=false;                    
//                    }
//                }
//                catch (Exception e) { 
////                    log.error("failed to execute Sirenen Event");
//                      System.out.println("failed to execute Sirenen Event");
////                    log.fatal(e.toString());
//                    active1=false;    
//                }
//            }
//        };
//        clamp1.addListener(clamp1Listener);
}
  
  private static class HandleDbWorker implements Runnable
  {
    private final EasyModbusMaster master;
    private final int carNumber;

    public HandleDbWorker(EasyModbusMaster master, int carNumber )
    {
      this.master = master;
      this.carNumber = carNumber;
    }
    
    @Override
    public void run()
    {
      try
      {
        new MainProgramm().handleDB(master, carNumber);
      }
      catch (Exception ex)
      {
        System.out.println("Error in handleDB" + ex.getMessage());
      }
    }
  }
          
  private static class HandleAndroidDataWorker implements Runnable
  {
    private final EasyModbusSlave slave;

    public HandleAndroidDataWorker(EasyModbusSlave slave)
    {
      this.slave = slave;
    }
    
    @Override
    public void run()
    {
      try
      {
        new MainProgramm().handleDataFromAndroid(slave);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
  private static class HandleSireneWorker implements Runnable
  {
    @Override
    public void run()
    {
      try
      {
        new MainProgramm().handle();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
  public static void main(String[] args)
  {
    try
    {
//      GpioController gpio = GpioFactory.getInstance();
//      GpioPinDigitalInput clamp1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN);
      master0 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP1), 2, 3);
//      master1 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP2), 2, 3);
//      
//      master2 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP3), 2, 3);
//      
//      master3 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP4), 2, 3);
//      master4 = new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName(IP5), 2, 3);
      
//      System.out.println("test0");
      // new BackgroundWorker().execute();
      
      
//      slave.setRegister(0, 0);
      
      final ScheduledExecutorService exe = Executors.newScheduledThreadPool(CARS+2); // CARS+1 ist die Azahl an maximal gleichzeitig geöffneten Threads
      exe.scheduleWithFixedDelay(new HandleDbWorker(master0, 0), 1000, 2000, TimeUnit.MILLISECONDS);
//      exe.scheduleWithFixedDelay(new HandleDbWorker(master1, 1), 2000, 5000, TimeUnit.MILLISECONDS);
//      exe.scheduleWithFixedDelay(new HandleDbWorker(master2, 2), 3000, 5000, TimeUnit.MILLISECONDS);
//      exe.scheduleWithFixedDelay(new HandleDbWorker(master3, 3), 4000, 5000, TimeUnit.MILLISECONDS);
//      exe.scheduleWithFixedDelay(new HandleDbWorker(master4, 4), 5000, 5000, TimeUnit.MILLISECONDS);
      /*for(int i=0;i<CARS;i++) 
        exe.scheduleWithFixedDelay(new HandleDbWorker( // erstellt Threads die mit einem bestimmten Delay arbeiten Threads für Master
          new EasyModbusMaster(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID,
            InetAddress.getByName(IP[i]), 2, 3), i),
          1000, 5000, TimeUnit.MILLISECONDS);*/
//      exe.scheduleWithFixedDelay(new HandleDbWorker( // erstellt Threads die mit einem bestimmten Delay arbeiten Threads für Master
//              master0,1),
//          1000, 5000, TimeUnit.MILLISECONDS);

      exe.scheduleWithFixedDelay(new HandleSireneWorker(), 1000, 1000, TimeUnit.MILLISECONDS); //Thread für Sirene
      slave= new EasyModbusSlave(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID,10,15,0,1);
      exe.scheduleWithFixedDelay(new HandleAndroidDataWorker(slave), 4000, 3000, TimeUnit.MILLISECONDS); // Thread für Slave
      slave.start();
      System.out.println("Slave gestartet: "+slave.toString());
/*      while(true)
      {
        new MainProgramm().handleDB(master0, 0);
        new MainProgramm().handleDB(master1, 1);
        
        //new MainProgramm().handleDB(master2, 2);
        //new MainProgramm().handleDB(master3, 3);
        //new MainProgramm().handleDB(master4, 4);
             
//        new MainProgramm().handle();
        new MainProgramm().handleDataFromAndroid(slave);
        
        Thread.sleep(10000);
      } */

    }
    catch (Exception ex)
    {
      Logger.getLogger(MainProgramm.class.getName()).log(Level.SEVERE, null, ex);
      System.out.println("MainProgramm Error:" + ex.getMessage());
    }
  }
}
