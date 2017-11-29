/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql;

import java.net.*;
import java.util.logging.*;

/**
 *
 * @author User
 */
public class main
{
  public static void main(String[] args) 
  {
    
    try
    {
      final ArduinoMaster master = new ArduinoMaster(InetAddress.getLocalHost(), 0);
    }
    catch (UnknownHostException ex)
    {
      Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
