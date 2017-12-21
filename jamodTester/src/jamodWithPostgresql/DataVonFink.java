/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql;

/**
 *
 * @author User
 */
public class DataVonFink
{
  private boolean Auto, sensorOben,sensorUnten;

  public DataVonFink(boolean Auto, boolean sensorOben, boolean sensorUnten)
  {
    this.Auto = Auto;
    this.sensorOben = sensorOben;
    this.sensorUnten = sensorUnten;
  }

  
  
  public boolean isAuto()
  {
    return Auto;
  }

  public void setAuto(boolean Auto)
  {
    this.Auto = Auto;
  }

  public boolean isSensorOben()
  {
    return sensorOben;
  }

  public void setSensorOben(boolean sensorOben)
  {
    this.sensorOben = sensorOben;
  }

  public boolean isSensorUnten()
  {
    return sensorUnten;
  }

  public void setSensorUnten(boolean sensorUnten)
  {
    this.sensorUnten = sensorUnten;
  }
  
  
}
