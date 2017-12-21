/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htlkaindorf.kagfam13.androidsimmaster.easyModbus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadInputRegistersRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.SimpleRegister;

/**
 *
 * @author Fabian
 */
public class EasyModbusMaster {
    private final static String ERRORMESSAGE = "***MODBUS ERROR: ";
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
        try {
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
        } catch (Exception e) {
            throw(new Exception(ERRORMESSAGE + e.getMessage()));
        }
    }

    public Boolean[] getCoils() throws Exception
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
            throw(new Exception(ERRORMESSAGE + e.getMessage()));
        }
    }
    
    public void writeCoil(int id, boolean state) throws Exception
    {
        try {
            if(id<0)
                throw new Exception(ERRORMESSAGE + "Index des zu beschreibenden Coils ist kleiner 0.");
            if(id>=wCoils)
                throw new Exception(ERRORMESSAGE + "Index des zu beschreibenden Coils ist zu groß.");
            while(connection.isConnected());
            connection.connect();

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

            ModbusRequest request = new WriteCoilRequest(id, state);
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            connection.close();
        } catch (Exception e) {
            throw(new Exception(ERRORMESSAGE + e.getMessage()));
        }
    }
    
    public void writeRegisrer(int index, int value) throws Exception
    {
        try {
            if(index<0)
                throw new Exception("Index des zu beschreibenden Registers ist kleiner 0.");
            if(index>=wRegisters)
                throw new Exception("Index des zu beschreibenden Registers ist zu groß.");
            while(connection.isConnected());
            connection.connect();

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

            ModbusRequest request = new WriteSingleRegisterRequest(index, new SimpleRegister(value));
            request.setUnitID(unitId);
            transaction.setRequest(request);
            transaction.execute();
            connection.close();
        } catch (Exception e) {
            throw(new Exception(ERRORMESSAGE + e.getMessage()));
        }
    }
    
    public static void main(String[] args) {
        try {
            EasyModbusMaster master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getByName("127.0.01"), 0, 1, 0, 0);
            System.out.println(Arrays.toString(master.getCoils()));
        } catch (UnknownHostException ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
