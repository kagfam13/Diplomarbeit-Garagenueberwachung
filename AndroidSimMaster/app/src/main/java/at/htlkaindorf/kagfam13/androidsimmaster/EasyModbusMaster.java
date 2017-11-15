/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlkaindorf.kagfam13.androidsimmaster;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 *
 * @author Fabian
 */
public class EasyModbusMaster {
    private final int port,unitId;
    private final InetAddress address;
    private final int wCoils, rCoils;
    private TCPMasterConnection connection;

    public EasyModbusMaster(int port, int unitId, InetAddress address, int wCoils, int rCoils) throws UnknownHostException {
        this.port = port;
        this.unitId = unitId;
        this.address = address;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        
        connection = new TCPMasterConnection(address);
        connection.setPort(port);
        connection.setTimeout(3000);
    }
    
    public StringCoilsResp getCoils()
    {
        try {
            while(connection.isConnected());
            connection.connect();
            
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            
            ModbusRequest request = new ReadCoilsRequest(0,rCoils+wCoils);
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            connection.close();

            String hexMessage = transaction.getResponse().getHexMessage();
            
            hexMessage = hexMessage.replaceAll(" ", "");
            
            hexMessage = hexMessage.substring(18);
            
            String bin = "";
            int i=0;
            while(i<hexMessage.length())
            {
                int intwert = Integer.parseInt(hexMessage.charAt(i+1) + "",16);
                String binwert = Integer.toBinaryString(intwert);
                while(binwert.length()<4)
                    binwert = String.format("0" + binwert);
                bin = binwert+bin;
                
                intwert = Integer.parseInt(hexMessage.charAt(i) + "");
                binwert = Integer.toBinaryString(intwert);
                while(binwert.length()<4)
                    binwert = String.format("0" + binwert);
                bin = binwert+bin;
                
                
                i+=2;
            }
            
            return new StringCoilsResp(new StringBuffer(bin).reverse().toString().substring(0, wCoils+rCoils));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean readCoil(int id)
    {
        try {
            while(connection.isConnected());
            connection.connect();
            
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            
            ModbusRequest request = new ReadCoilsRequest(0,rCoils+wCoils);
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            connection.close();
            String hexMessage = transaction.getResponse().getHexMessage();
            
            hexMessage = hexMessage.replaceAll(" ", "");
            
            hexMessage = hexMessage.substring(18);
            
            String bin = "";
            int i=0;
            while(i<hexMessage.length())
            {
                int intwert = Integer.parseInt(hexMessage.charAt(i+1) + "",16);
                String binwert = Integer.toBinaryString(intwert);
                while(binwert.length()<4)
                    binwert = String.format("0" + binwert);
                bin = binwert+bin;
                
                intwert = Integer.parseInt(hexMessage.charAt(i) + "");
                binwert = Integer.toBinaryString(intwert);
                while(binwert.length()<4)
                    binwert = String.format("0" + binwert);
                bin = binwert+bin;
                
                
                i+=2;
            }
            String coils = new StringBuffer(bin).reverse().toString().substring(0, wCoils+rCoils);
            if(coils.charAt(id) == '1')
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void writeCoil(int id, boolean state)
    {
        try {
            if(id<0)
                throw new Exception("Depp");
            if(id>=wCoils)
                throw new Exception("Dumm");
            while(connection.isConnected());
            connection.connect();
            
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            
            ModbusRequest request = new WriteCoilRequest(id, state);
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            connection.close();
        } catch (Exception e) {
        }
    }
    public static void main(String[] args) {
        try {
            EasyModbusMaster master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getLocalHost(), 10, 15);
            
            System.out.println(master.readCoil(0));
            System.out.println(master.readCoil(24));
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
