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
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 *
 * @author Fabian
 */
public class EasyModbusMaster {
    private final int port,unitId;
    private final InetAddress address;
    private final int wCoils, rCoils, wRegisters, rRegisters;
    private final TCPMasterConnection connection;

    public EasyModbusMaster(int port, int unitId, InetAddress address, int wCoils, int rCoils, int wRegisters, int rRegisters) throws UnknownHostException {
        this.port = port;
        this.unitId = unitId;
        this.address = address;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        this.wRegisters = wRegisters;
        this.rRegisters = rRegisters;
        
        connection = new TCPMasterConnection(address);
        connection.setPort(port);
        connection.setTimeout(3000);
    }
    
    public EasyModbusMaster(int port, int unitId, InetAddress address, int wCoils, int rCoils) throws UnknownHostException {
        this.port = port;
        this.unitId = unitId;
        this.address = address;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        this.wRegisters = 0;
        this.rRegisters = 0;
        
        connection = new TCPMasterConnection(address);
        connection.setPort(port);
        connection.setTimeout(3000);
    }
    
    public int getRegister(int index) throws Exception
    {
        while(connection.isConnected());
        connection.connect();
        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        ModbusRequest request = new ReadMultipleRegistersRequest(index, 1);
        request.setUnitID(unitId);
        transaction.setRequest(request);
        transaction.execute();
        connection.close();
        transaction.getResponse();
        String hexMessage = transaction.getResponse().getHexMessage();
        System.out.println(hexMessage);
        hexMessage = hexMessage.replaceAll(" ", "");

        hexMessage = hexMessage.substring(18); // Extracted the Data

        System.out.println(hexMessage);
        int regValue = Integer.parseInt(hexMessage, 16);
        System.out.println(regValue);
        return regValue;
    }

    public Boolean[] getCoils()
    {
        try {
            //System.out.println("***** A");
            while(connection.isConnected());
            //System.out.println("***** B");
            connection.connect();
            connection.setTimeout(3000);
            //System.out.println("***** C");
            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            //System.out.println("***** D");
            ModbusRequest request = new ReadCoilsRequest(0,rCoils+wCoils);
            //System.out.println("***** E");
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            connection.close();
            String hexMessage = transaction.getResponse().getHexMessage();
            System.out.println(hexMessage);
            hexMessage = hexMessage.replaceAll(" ", "");
            
            hexMessage = hexMessage.substring(18); // Extracted the Data
            System.out.println(hexMessage);
            String orderedHex = "";
            while(!hexMessage.isEmpty())
            {
                orderedHex = String.format("%s%s%s",hexMessage.charAt(0),hexMessage.charAt(1),orderedHex);

                hexMessage = hexMessage.substring(2);
            }
            System.out.println(orderedHex);
            return new HexToBin(orderedHex, rCoils+wCoils).getCoils();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
            
            EasyModbusMaster master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getLocalHost(), 0, 1);
            
            System.out.println(master.getRegister(1));
            
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
