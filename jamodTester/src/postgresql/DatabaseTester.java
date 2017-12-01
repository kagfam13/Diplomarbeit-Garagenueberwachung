/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql;

import java.sql.*;
import java.util.logging.*;
import org.postgresql.util.*;
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
      
        //final ConnectToDatabase connectToDatabase= new ConnectToDatabase(0,2);
        
//        final postgresql.easyDatabase.UpdateDatabase connecter = new postgresql.easyDatabase.UpdateDatabase(0,1);
        final GetInformationFromDatabase getInfo = new GetInformationFromDatabase();
        id= getInfo.getId();
        System.out.format("%d",id);
        
            
  
      
  }
}

