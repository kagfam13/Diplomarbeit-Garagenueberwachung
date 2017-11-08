/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.anotherTest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 *
 * @author Fabian
 */
public class easyModbusMaster {
    private final int port,unitId;
    private final InetAddress address;
    private final int wCoils, rCoils;
    private TCPMasterConnection connection;

    public easyModbusMaster(int port, int unitId, InetAddress address, int wCoils, int rCoils) throws UnknownHostException {
        this.port = port;
        this.unitId = unitId;
        this.address = address;
        this.wCoils = wCoils;
        this.rCoils = rCoils;
        
        connection = new TCPMasterConnection(address);
        connection.setPort(port);
        connection.setTimeout(3000);
    }
    
    public boolean readCoil(int id)
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
            for(i = 0; i<by; i++)
            {
                int intVal = Integer.parseInt(h[9+i]);
                String bin = Integer.toBinaryString(intVal);
                
                while(bin.length()< 8)
                    bin = "0" + bin;
                System.out.println(bin);
                doub = doub.concat(bin);
            }
                
            System.out.println(doub);

        } catch (Exception ex) {
            Logger.getLogger(easyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public static void main(String[] args) {
        try {
            easyModbusMaster master = new easyModbusMaster(Modbus.DEFAULT_PORT, 15, InetAddress.getLocalHost(), 10, 15);
            master.readCoil(0);
        } catch (UnknownHostException ex) {
            Logger.getLogger(easyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
