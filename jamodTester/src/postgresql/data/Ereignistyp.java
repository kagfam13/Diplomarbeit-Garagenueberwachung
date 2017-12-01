/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.data;

/**
 *
 * @author User
 */
public class Ereignistyp
{
  private final int typId;
  private final String text;

  public Ereignistyp(int typId, String text)
  {
    this.typId = typId;
    this.text = text;
  }
  
  public int getTypId()
  {
    return typId;
  }

  public String getText()
  {
    return text;
  }

  @Override
  public String toString()
  {
    return "Ereignistyp{" + "typId=" + typId + ", text=" + text + '}';
  }
  
}
