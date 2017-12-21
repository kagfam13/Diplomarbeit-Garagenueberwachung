/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postgresql.easyDatabase;

import java.sql.*;

/**
 *
 * @author werner
 */
public class Database implements AutoCloseable
{
  private final String url,user,pass;
  private Connection connection = null;

  public Database(String url, String user, String pass)
  {
    this.url = url;
    this.user = user;
    this.pass = pass;
  }

  public void open() throws SQLException
  {
    if (connection==null || connection.isClosed())
      connection = DriverManager.getConnection(url, user, pass);
  }
  
  @Override
  public void close() throws SQLException
  {
    if (connection!=null && !connection.isClosed())
      connection.close();
    connection=null;
  }
  
  public int executeUpdate(String sql) throws SQLException
  {
    try (Statement statement = connection.createStatement())
    {
      return statement.executeUpdate(sql);
    }
  }
  
  public int executeUpdateReturnLastVal(String sql) throws SQLException
  {
    try (Statement statement = connection.createStatement())
    {
      statement.executeUpdate(sql);
      try (final ResultSet resultSet = statement.executeQuery("SELECT LASTVAL()"))
      {
        resultSet.next();
        return resultSet.getInt(1);
      }
    }
  }
  public Statement createStatement() throws SQLException
  {
    return connection.createStatement();
  }
  
  public static void main(String[] args)
  {
    try (Database db = new Database(
        "jdbc:postgresql://127.0.0.1/schule_0ahmea", "0ahmea", "geheim"
      ))
    {
      db.open();
      System.out.println("Verbindung erfolgreich geï¿½ffnet!!!");
      /*int result =
        db.executeUpdate(
          "INSERT" +
          "  INTO klasse" +
          "  (jahrgang, bezeichnung)" +
          "  VALUES ('ahme12', '8ahmea')"
       );
      System.out.format("%d Datenstze hinzugefgt!%n%n", result);*/
      
      Statement statement = db.createStatement();
      System.out.println("Statement erstellt");
      
      System.out.println("Jahrgnge:");
      try (ResultSet rs = statement.executeQuery("SELECT * FROM klasse"))
      {
        // Solange noch weitere Datenstze vorhanden sind
        while (rs.next())
        {
          System.out.println(
            rs.getString("jahrgang") + " / " +    // Spalte "jahrgang"
            rs.getString(3)                       // 3. Spalte
          );
        }
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }
}
