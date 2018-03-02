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
  private int id;

  public void setId(int id)
  {
    this.id = id;
  }
  private final Ereignistyp ereignistyp;
  private final Objekt objekt;
  private final LocalDateTime zeit; 

  public Ereignis(int id, Ereignistyp ereignistyp, Objekt objekt, LocalDateTime zeit)
  {
    this.id = id;
    this.ereignistyp = ereignistyp;
    this.objekt = objekt;
    this.zeit = zeit;
  }

  public int getId()
  {
    return id;
  }

  public Ereignistyp getEreignistyp()
  {
    return ereignistyp;
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
    return "Ereignis{" + "id=" + id + ", ereignsityp=" + ereignistyp + ", objekt=" + objekt + ", zeit=" + zeit + '}';
  }
  
  
  
}
