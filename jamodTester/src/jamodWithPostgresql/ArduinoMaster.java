/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql;

import jamodtester.arduinoSim.*;
import jamodtester.easyModbus.*;
import java.net.*;
import java.time.*;
import java.util.logging.*;
import javax.swing.*;
import net.wimpi.modbus.*;
import static org.postgresql.hostchooser.HostRequirement.master;
import postgresql.data.*;
import postgresql.easyDatabase.*;

/**
 *
 * @author User
 */
public class ArduinoMaster
{
  private final InetAddress addy;
  private final int carnumber;
  private int gatenumber;
  //carnumber == Wert des Autos in der Datenbank
  
  EasyModbusMaster master;
  public ArduinoMaster(InetAddress addy,int carnumber)
  {
    this.addy = addy;
    this.carnumber = carnumber;
    handler();
    work();
  }
  private void handler()
  {
    switch(carnumber)
    {
      case 0:
        gatenumber=5;
        break;
      case 1:
        gatenumber=6;
        break;
      case 2:
        gatenumber=7;
        break;
      case 3:
        gatenumber=8;
        break;
      case 4:
        gatenumber=9;
        break;
      default:
        // Default Value
        break;
    }
  }
  private void work()
  {
    try {
            // master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getByName("10.200.112.70"), 2, 3);
            master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, addy , 2, 3);
            new manageLabels().execute();
             
         } catch (UnknownHostException ex) {
             Logger.getLogger(ArduinoSimMaster.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    private class manageLabels extends SwingWorker<Object, Object>
    {
        
        @Override
        protected Object doInBackground() throws Exception
        {
          
            while(true)
            {
                //System.out.println("*********************************");
                GetCoilsResp resp = new GetCoilsResp(master.getCoils());
                //System.out.println(resp.toString());
                final GaragenDb db = GaragenDb.getInstance();
                final int ereignisID = db.getLastId();
                final Objekt objekt = db.getObjekt(carnumber);
                final Objekt torobjekt = db.getObjekt(gatenumber);
                if(resp.getCoil(2))
                {
                  System.out.println("auto da");
                  final Ereignistyp typ = db.getEreignistyp(1);
                  Ereignis ereignis = new Ereignis(ereignisID+1, typ, objekt, LocalDateTime.now());
                }
                else
                {
                  
                  System.out.println("auto nix da");
                  final Ereignistyp typ = db.getEreignistyp(0);
                  Ereignis ereignis = new Ereignis(ereignisID+1, typ, objekt, LocalDateTime.now());
                }
                if(resp.getCoil(4))// Tor offen
                {
                  System.out.println("Tor offen");
                  final Ereignistyp typ = db.getEreignistyp(3);
                  Ereignis ereignis = new Ereignis(ereignisID+1, typ, torobjekt, LocalDateTime.now());
                }
                else if(resp.getCoil(3)) // Tor geschlossen
                {
                  System.out.println("Tor geschlossen");
                  final Ereignistyp typ = db.getEreignistyp(4);
                  Ereignis ereignis = new Ereignis(ereignisID+1, typ, torobjekt, LocalDateTime.now());
                }
                else // Tor halb offen oder geschlossen
                {
                  System.out.println("Tor is halb offen oder halb geschlossen");
                  final Ereignistyp typ = db.getEreignistyp(5);
                  Ereignis ereignis = new Ereignis(ereignisID+1, typ, torobjekt, LocalDateTime.now());
                }
                Thread.sleep(5000);
            }
        }
              
    }
    
    private class manageTor extends SwingWorker<Object, Object>
    {
        int coil;

        public manageTor(int coil)
        {
          this.coil = coil;
        }
    
      @Override
      protected Object doInBackground() throws Exception
      {
        master.writeCoil(coil, true);
        return 0;
      }
       
    }
    
}
  
