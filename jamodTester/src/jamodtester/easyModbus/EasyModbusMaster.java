/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.easyModbus;

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
    

    public GetCoilsResp getCoils()
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
            String orderedHex = "";
            while(!hexMessage.isEmpty())
            {
                orderedHex = hexMessage.charAt(1) + hexMessage.charAt(0) + orderedHex;
                
                orderedHex = orderedHex.substring(2);
            }
            return new GetCoilsResp(new HexToBin(orderedHex, rCoils+wCoils).getCoils());
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
            EasyModbusMaster master = new EasyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getLocalHost(), 10, 15);
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
