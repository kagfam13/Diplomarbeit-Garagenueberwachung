/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql.old;

import postgresql.data.*;
import postgresql.easyDatabase.*;

/**
 *
 * @author User
 */
public class TestAndroid
{
  public void handleDataFromDatabase(int objektId)
  {
    try(GaragenDb db = GaragenDb.getInstance())
    {
      Ereignis ereignis = db.getLastEreignis(objektId);
      int typId = ereignis.getEreignistyp().getTypId();
      switch(typId)
      {
        case 1: 
          break;
        case 2:
          break;
        case 3:
          break;
        case 4: 
          break;
        case 5:
          break;
        default:
          break;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
