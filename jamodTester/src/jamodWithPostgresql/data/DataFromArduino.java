/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jamodWithPostgresql.data;

/**
 *
 * @author User
 */
public class DataFromArduino
{
  private boolean Auto, sensorOben,sensorUnten,oeffnen,schließen;

  public DataFromArduino(boolean Auto, boolean sensorOben, boolean sensorUnten, boolean oeffnen, boolean schließen)
  {
    this.Auto = Auto;
    this.sensorOben = sensorOben;
    this.sensorUnten = sensorUnten;
    this.oeffnen = oeffnen;
    this.schließen = schließen;
  }

  public boolean isOeffnen()
  {
    return oeffnen;
  }

  public boolean isSchließen()
  {
    return schließen;
  }

  public void setOeffnen(boolean oeffnen)
  {
    this.oeffnen = oeffnen;
  }

  public void setSchließen(boolean schließen)
  {
    this.schließen = schließen;
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
