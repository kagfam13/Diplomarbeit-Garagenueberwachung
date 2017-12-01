/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.data;

import java.time.*;

/**
 *
 * @author User
 */
public class Ereignis
{
  private final int id;
  private final Ereignistyp ereignsityp;
  private final Objekt objekt;
  private final LocalDateTime zeit; 

  public Ereignis(int id, Ereignistyp ereignsityp, Objekt objekt, LocalDateTime zeit)
  {
    this.id = id;
    this.ereignsityp = ereignsityp;
    this.objekt = objekt;
    this.zeit = zeit;
  }

  public int getId()
  {
    return id;
  }

  public Ereignistyp getEreignsityp()
  {
    return ereignsityp;
  }

  public Objekt getObjekt()
  {
    return objekt;
  }

  public LocalDateTime getZeit()
  {
    return zeit;
  }

  @Override
  public String toString()
  {
    return "Ereignis{" + "id=" + id + ", ereignsityp=" + ereignsityp + ", objekt=" + objekt + ", zeit=" + zeit + '}';
  }
  
  
  
}
