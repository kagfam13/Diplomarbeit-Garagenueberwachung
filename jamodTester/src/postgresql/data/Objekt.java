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
public class Objekt
{
  private final int objektId;
  private final String name;

  public Objekt(int objektId, String name)
  {
    this.objektId = objektId;
    this.name = name;
  }

  public int getObjektId()
  {
    return objektId;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return "Objekt{" + "objektId=" + objektId + ", name=" + name + '}';
  }
  
  
}
