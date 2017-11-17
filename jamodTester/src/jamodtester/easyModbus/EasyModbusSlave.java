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
import net.wimpi.modbus.procimg.SimpleInputRegister;
import net.wimpi.modbus.procimg.SimpleProcessImage;
import net.wimpi.modbus.procimg.SimpleRegister;

/**
 *
 * @author Fabian
 */
public class EasyModbusSlave {
    private final int port, unitId, wCoils, rCoils, wRegisters, rRegisters;
    ModbusTCPListener listener;
    SimpleProcessImage spi;

    public EasyModbusSlave(int port, int unitId, int wCoils, int rCoils, int wRegisters, int rRegisters) {
        this.port = port;
        this.unitId = unitId;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        this.wRegisters = wRegisters;
        this.rRegisters = rRegisters;
    }
    
    public EasyModbusSlave(int port, int unitId, int wCoils, int rCoils) {
        this.port = port;
        this.unitId = unitId;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        this.wRegisters = 0;
        this.rRegisters = 0;
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
        
        for(i = 0; i < wRegisters; i++)
        {
            spi.addInputRegister(new SimpleInputRegister());
        }
        for(i = 0; i < wRegisters + rRegisters; i++)
        {
            spi.addRegister(new SimpleRegister());
        }
        
        ModbusCoupler.getReference().setUnitID(unitId);
        ModbusCoupler.getReference().setMaster(false);
        ModbusCoupler.getReference().setProcessImage(spi);
        
        listener = new ModbusTCPListener(3);
        listener.setPort(port);
        listener.start();
    }
    
    public int getRegisterCount()
    {
        return ModbusCoupler.getReference().getProcessImage().getRegisterCount();
    }
    public void setRegister(int index,int value)
    {
        spi.setRegister(index, new SimpleRegister(value));
    }
    
    public int getRegister(int index)
    {
        return ModbusCoupler.getReference().getProcessImage().getRegister(index).getValue();
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
        EasyModbusSlave slave = new EasyModbusSlave(Modbus.DEFAULT_PORT, 15, 0, 0, 1, 1);
        slave.start();
        slave.setRegister(1, 500);
        System.out.println(slave.getRegisterCount());
        System.out.println(slave.getRegister(1));
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(EasyModbusSlave.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        slave.stop();
        System.exit(0);
        return;
    }
    
}
