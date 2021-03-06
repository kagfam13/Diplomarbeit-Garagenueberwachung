/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodtester.easyModbus;

import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ReadCoilsRequest;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteCoilRequest;
import net.wimpi.modbus.msg.WriteMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteSingleRegisterRequest;
import net.wimpi.modbus.net.TCPMasterConnection;
import net.wimpi.modbus.procimg.Register;
import net.wimpi.modbus.procimg.SimpleRegister;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.modbus.Modbus;


/**
 *
 * @author Kager Fabian
 */
public class EasyModbusMaster
{

  private final int unitId;
  private final int wCoils, rCoils, wRegisters, rRegisters;
  private final TCPMasterConnection connection;


  public EasyModbusMaster (int port, int unitId, InetAddress address, int wCoils, int rCoils, int wRegisters, int rRegisters)
          throws UnknownHostException
  {
    this.unitId = unitId;
    this.wCoils = wCoils;
    this.rCoils = rCoils;
    this.wRegisters = wRegisters;
    this.rRegisters = rRegisters;

    connection = new TCPMasterConnection(address);
    connection.setPort(port);
    connection.setTimeout(3000);
  }

  private Boolean[] hexToBinConvert(String data,int length) {
        Boolean[] coils;
        String daten = new StringBuffer(data).reverse().toString(); 
        String coilsss = "";
        while(!daten.isEmpty())
        {
            System.out.println(daten);
            int intVal = Integer.parseInt(""+daten.charAt(0), 16);
            String bin = Integer.toBinaryString(intVal);
            while(bin.length()<4)
                bin = "0" + bin;
            daten = daten.substring(1);
            coilsss += new StringBuffer(bin).reverse().toString();
            System.out.println(coilsss);
        }
        coils = new Boolean[length];
        int i;
        for(i=0;i<coilsss.length();i++)
        {
            if(i<length)
                coils[i] = coilsss.charAt(i) == '1';
        }
        return coils;
    }
  
  public int getRegister (int index) throws Exception
  {
    // Range check
    if(index < 0 || index >= wRegisters+rRegisters)
      throw new Exception("Register out of range");
    
    int[] rv = getMultipleRegisters(index, 1);
    
    return rv[0];
  }

  public int[] getRegisters() throws Exception
  {
    return getMultipleRegisters(0, rRegisters+wRegisters);
  }

  public int[] getMultipleRegisters (int start, int cnt) throws Exception
  {
    connection.connect();
    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
    ModbusRequest request = new ReadMultipleRegistersRequest(start, cnt);
    request.setUnitID(unitId);
    transaction.setRequest(request);
    transaction.execute();
    connection.close();
    String hexMessage = transaction.getResponse().getHexMessage();
    hexMessage = hexMessage.replaceAll(" ", "");

    hexMessage = hexMessage.substring(18); // Extracted the Data
    int[] val = new int[cnt];
    for (int i = 0; i < cnt; i++)
    {
      int hB = Integer.parseInt(hexMessage.substring(i * 4, i * 4 + 2), 16);
      int lB = Integer.parseInt(hexMessage.substring(i * 4 + 2, i * 4 + 4), 16);
      val[i] = (hB << 8) | (lB);
    }
    return val;
  }


  public String getString (int start, int maxLengthInRegs) throws Exception
  {
    final int[] reg = getMultipleRegisters(start, maxLengthInRegs);
    final StringBuilder sb = new StringBuilder();
    for (int aReg : reg) {
      final int hi = (aReg >> 8) & 0xFF,
              lo = aReg & 0xFF;
      if (hi != 0) {
        sb.append((char) hi);
      } else {
        break;
      }
      if (lo != 0) {
        sb.append((char) lo);
      } else {
        break;
      }
    }
    return sb.toString();
  }

  public Boolean[] getCoils () throws Exception
  {
    return getMultipleCoils(0, rCoils+wCoils);
  }
  
  public Boolean getCoil(int id) throws Exception 
  {
    if(id<0 || id>=wCoils+rCoils)
      throw new Exception("Coil out of range");
    Boolean[] rv = getMultipleCoils(id, 1);
    return rv[0];
  }
  
  public Boolean[] getMultipleCoils (int start, int cnt) throws Exception
  {
    // Range check
    if(start < 0) 
      throw new Exception("Index des zu lesenden Coils ist kleiner 0.");
    if(start+cnt >= rCoils + wCoils)
      throw new Exception("Index des zu beschreibenden Registers ist zu groß.");
    
    connection.connect();

    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

    ModbusRequest request = new ReadCoilsRequest(start, cnt);
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
    while (!hexMessage.isEmpty())
    {
      orderedHex = String.format("%s%s%s", hexMessage.charAt(0), hexMessage.charAt(1), orderedHex);

      hexMessage = hexMessage.substring(2);
    }
    return hexToBinConvert(orderedHex, rCoils + wCoils);
  }


  public void writeCoil (int id, boolean state) throws Exception
  {
    // Range check
    if (id < 0)
    {
      throw new Exception("Index des zu beschreibenden Coils ist kleiner 0.");
    }
    if (id >= wCoils)
    {
      throw new Exception("Index des zu beschreibenden Coils ist zu groß.");
    }
    connection.connect();

    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

    ModbusRequest request = new WriteCoilRequest(id, state);
    request.setUnitID(unitId);
    transaction.setRequest(request);
    transaction.execute();
    connection.close();
  }


  public void writeRegister (int index, int value) throws Exception
  {
    // Range check
    if (index < 0)
    {
      throw new Exception("Index des zu beschreibenden Registers ist kleiner 0.");
    }
    if (index >= wRegisters)
    {
      throw new Exception("Index des zu beschreibenden Registers ist zu groß.");
    }
    connection.connect();

    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

    ModbusRequest request = new WriteSingleRegisterRequest(index, new SimpleRegister(value));
    request.setUnitID(unitId);
    transaction.setRequest(request);
    transaction.execute();
    connection.close();
  }


  public void writeMultipleRegisters (int start, int... value) throws Exception
  {
    if (start < 0)
    {
      throw new Exception("Index des zu beschreibenden Registers ist kleiner 0.");
    }
    if (start + value.length >= wRegisters)
    {
      throw new Exception("Index des zu beschreibenden Registers ist zu groß.");
    }
    while (connection.isConnected());
    connection.connect();

    ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);

    Register[] regs = new Register[value.length];
    for (int i = 0; i < value.length; i++)
    {
      regs[i] = new SimpleRegister(value[i]);
    }
    ModbusRequest request = new WriteMultipleRegistersRequest(start, regs);
    request.setUnitID(unitId);
    transaction.setRequest(request);
    transaction.execute();
    connection.close();
  }


  public void writeString (int start, int maxLengthInRegs, String text) throws Exception
  {
    final byte[] b = text.getBytes("utf8");
    final int[] register = new int[maxLengthInRegs];
    for (int i = 0; i < maxLengthInRegs; i++)
    {
      final int indexH = i * 2,
              indexL = i * 2 + 1;
      final byte bH = indexH < b.length ? b[indexH] : (byte) 0,
              bL = indexL < b.length ? b[indexL] : (byte) 0;
      register[i] = (bH << 8) | bL;
    }
    writeMultipleRegisters(start, register);
  }
  
  public static void main (String[] args)
  {
    try
    {
      EasyModbusMaster master = new EasyModbusMaster(4567, Modbus.DEFAULT_UNIT_ID, InetAddress.getByName("10.0.0.15"), 4, 0, 0, 0);
      
      master.writeCoil(0, true);
      System.out.println(Arrays.toString(master.getMultipleCoils(0, 5)));
      
    }
    catch (Exception ex)
    {
      Logger.getLogger(EasyModbusMaster.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
