/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql;

import jamodtester.arduinoSim.*;
import jamodtester.easyModbus.*;
import java.net.*;
import java.util.logging.*;
import javax.swing.*;
import net.wimpi.modbus.*;
import static org.postgresql.hostchooser.HostRequirement.master;
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
             
                if(resp.getCoil(2))
                {
                  //lCar.setText("Auto da");
                  System.out.println("auto da");
                  final GetInformationFromDatabase getInfo = new GetInformationFromDatabase();
                  if(getInfo.getOldTypId()==0) // überprüft ob auto nicht da ist 
                  {
                    if(getInfo.getOldObjektId()==carnumber)
                    {
                      final postgresql.easyDatabase.UpdateDatabase connecter = new postgresql.easyDatabase.UpdateDatabase(1,carnumber);
                    }
                  }
                }
                else
                {
                  //lCar.setText("Auto nix da");
                  System.out.println("auto nix da");
                  final GetInformationFromDatabase getInfo = new GetInformationFromDatabase();
                  if(getInfo.getOldTypId()==1) //überprüft ob auto da ist
                  {
                    if(getInfo.getOldObjektId()==carnumber)
                    {
                      final postgresql.easyDatabase.UpdateDatabase connecter = new postgresql.easyDatabase.UpdateDatabase(0,carnumber);
                    }
                  }
                }
                if(resp.getCoil(4))// Tor offen
                {
                  //lTor.setText("Tor offen");
                  final GetInformationFromDatabase getInfo = new GetInformationFromDatabase();
                  if(getInfo.getOldTypId()==4 || getInfo.getOldTypId()==5)
                  {
                    if(getInfo.getOldObjektId()==gatenumber)
                    {
                      final postgresql.easyDatabase.UpdateDatabase connecter = new postgresql.easyDatabase.UpdateDatabase(3,gatenumber);
                    }
                  }
                  
                }
                else if(resp.getCoil(3)) // Tor geschlossen
                {
                  //lTor.setText("Tor geschlossen");
                  final GetInformationFromDatabase getInfo = new GetInformationFromDatabase();
                  if(getInfo.getOldTypId()==3 || getInfo.getOldTypId()==5)
                  {
                    if(getInfo.getOldObjektId()==gatenumber)
                    {
                      final postgresql.easyDatabase.UpdateDatabase connecter = new postgresql.easyDatabase.UpdateDatabase(4,gatenumber);
                    }
                  }
                  
                }
                else
                {
                  //lTor.setText("Ist das Tor halb offen oder halb geschlossen");
                  final GetInformationFromDatabase getInfo = new GetInformationFromDatabase();
                  if(getInfo.getOldTypId()==3 || getInfo.getOldTypId()==4)
                  {
                    if(getInfo.getOldObjektId()==gatenumber)
                    {
                      final postgresql.easyDatabase.UpdateDatabase connecter = new postgresql.easyDatabase.UpdateDatabase(5,gatenumber);
                    }
                  }
                  
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
  
