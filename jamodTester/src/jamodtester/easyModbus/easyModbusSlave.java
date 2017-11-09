/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.easyModbus;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.SimpleDigitalIn;
import net.wimpi.modbus.procimg.SimpleDigitalOut;
import net.wimpi.modbus.procimg.SimpleProcessImage;

/**
 *
 * @author Fabian
 */
public class easyModbusSlave {
    private final int port, unitId, wCoils, rCoils;
    ModbusTCPListener listener;
    SimpleProcessImage spi;

    public easyModbusSlave(int port, int unitId, int wCoils, int rCoils) {
        this.port = port;
        this.unitId = unitId;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
    }
    
    public void start()
    {
        spi = new SimpleProcessImage();
        int i;
        for(i = 0; i < wCoils; i++)
        {
            spi.addDigitalIn(new SimpleDigitalIn());
        }
        for(i = 0; i < wCoils + rCoils; i++)
        {
            spi.addDigitalOut(new SimpleDigitalOut());
        }
        
        ModbusCoupler.getReference().setUnitID(unitId);
        ModbusCoupler.getReference().setMaster(false);
        ModbusCoupler.getReference().setProcessImage(spi);
        
        listener = new ModbusTCPListener(3);
        listener.setPort(port);
        listener.start();
    }
    
    public boolean getCoil(int id) throws Exception
    {
        if(id >= wCoils + rCoils || id < 0)
            throw new Exception("Geht ned");
        return ModbusCoupler.getReference().getProcessImage().getDigitalOut(id).isSet();
    }
    
    public void setCoil(int id,boolean state) throws Exception
    {
        if(id >= wCoils + rCoils || id < 0)
            throw new Exception("Geht ned");
        spi.setDigitalOut(id, new SimpleDigitalOut(state));
    }
    
    public void stop()
    {
        listener.stop();
    }
    
    public static void main(String[] args) {
        easyModbusSlave slave = new easyModbusSlave(Modbus.DEFAULT_PORT, 15, 10, 15);
        slave.start();
        
        try {
            slave.setCoil(24, true);
        } catch (Exception ex) {
            Logger.getLogger(easyModbusSlave.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(easyModbusSlave.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        slave.stop();
        System.exit(0);
        return;
    }
    
}
