/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql;

import java.sql.*;
import postgresql.easyDatabase.*;

/**
 *
 * @author User
 */
public class DatabaseTester
{
  
  public static void main(String[] args)
  { 
      Connection conn;
      int id=0;
      
        final ConnectToDatabase connectToDatabase= new ConnectToDatabase("remote", "1234",1,2);
        connectToDatabase.connect();
        
            
  
      
  }
}

