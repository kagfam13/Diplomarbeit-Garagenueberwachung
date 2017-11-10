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
            connection.connect();
            
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            
            ModbusRequest request = new ReadCoilsRequest(0,rCoils+wCoils);
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            int len = transaction.getResponse().getDataLength();
            int by = (len/2)+(len%2);
            String hex = transaction.getResponse().getHexMessage();
            String h[] = hex.split(" ");
            int i;
            String doub = "" ;
            System.out.println(len);
            System.out.println(by);
            System.out.println(hex);
            for(i = by-1; i>=0; i--)
            {
                int intVal = Integer.parseInt(h[9+i]);
                String bin = Integer.toBinaryString(intVal);
                
                while(bin.length()< 8)
                    bin = "0" + bin;
                doub = doub.concat(bin);
            }
            return new StringCoilsResp(new StringBuffer(doub).reverse().toString().substring(0, wCoils+rCoils-1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean readCoil(int id)
    {
        try {
            if(id<0)
                throw new Exception("Depp");
            if(id>=wCoils+rCoils)
                throw new Exception("Dumm");
            
            connection.connect();
            
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            
            ModbusRequest request = new ReadCoilsRequest(0,rCoils+wCoils);
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            int len = transaction.getResponse().getDataLength();
            int by = (len/2)+(len%2);
            String hex = transaction.getResponse().getHexMessage();
            String h[] = hex.split(" ");
            int i;
            String doub = "" ;
            for(i = by-1; i>=0; i--)
            {
                int intVal = Integer.parseInt(h[9+i]);
                String bin = Integer.toBinaryString(intVal);
                
                while(bin.length()< 8)
                    bin = "0" + bin;
                doub = doub.concat(bin);
            }
            doub = new StringBuffer(doub).reverse().toString();
            if (doub.charAt(id) == '1')
                return true;
            return false;

        } catch (Exception ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
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
            EasyModbusMaster master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getByName("10.0.0.11"), 10, 15);
            
            master.writeCoil(2, true);
            
            
            
            System.out.println(master.readCoil(0));
            
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
