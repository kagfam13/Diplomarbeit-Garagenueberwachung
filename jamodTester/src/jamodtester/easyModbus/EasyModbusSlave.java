/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.easyModbus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusCoupler;
import net.wimpi.modbus.net.ModbusTCPListener;
import net.wimpi.modbus.procimg.*;

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
        
        start();
    }
    
    public EasyModbusSlave(int port, int unitId, int wCoils, int rCoils) {
        this.port = port;
        this.unitId = unitId;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        this.wRegisters = 0;
        this.rRegisters = 0;
        
        start();
    }
    
    private void start()
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
        
        listener = new ModbusTCPListener(300);
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
    
    public void setMultipleRegisters(int start, int... value)
    {
        for (int i=start;i<start+value.length;i++)
            setRegister(i,value[i]);
    }
    
    public void setString(int start, int maxLengthInRegs, String text)
      throws UnsupportedEncodingException
    {
        final byte[] b = text.getBytes("utf8");
        final int[] register = new int[maxLengthInRegs];
        for (int i=0;i<maxLengthInRegs;i++)
        {
            final int
              indexH = i*2,
              indexL = i*2+1;
            final byte
              bH = indexH<b.length ? b[indexH] : (byte)0,
              bL = indexL<b.length ? b[indexL] : (byte)0;
            register[i] = (bH<<8) | bL;
        }
        setMultipleRegisters(start,register);
    }
    
    public int[] getMultipleRegisters(int start, int cnt)
    {
      final Register[] reg = spi.getRegisterRange(start, cnt);
      final int[] value = new int[cnt];
      for (int i=0;i<cnt;i++)
          value[i] = reg[i].getValue();
      return value;
    }
    
    public String getString(int start, int maxLengthInRegs)
    {
        final int[] reg = getMultipleRegisters(start, maxLengthInRegs);
        final StringBuilder sb = new StringBuilder();
        for (int i=0;i<reg.length;i++) 
        {
            final int
              hi = (reg[i]>>8) & 0xFF,
              lo = reg[i] & 0xFF;
            if (hi!=0)
                sb.append((char)hi);
            else
              break;
            if (lo!=0)
                sb.append((char)lo);
            else
                break;
        }
        return sb.toString();
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
        EasyModbusSlave slave = new EasyModbusSlave(Modbus.DEFAULT_PORT, Modbus.DEFAULT_UNIT_ID, 0, 0, 30, 0);
        slave.start();
        System.out.println("Slave gestartet");
        try {
            slave.setString(0, 10, "Hallo");
            System.out.println(slave.getString(0, 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            System.in.read();
        } catch (IOException ex) {
            Logger.getLogger(EasyModbusSlave.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println(slave.getString(0, 10));
        slave.stop();
        System.exit(0);
    }
    
}
