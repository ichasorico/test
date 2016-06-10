package sgbd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.*;

public class bbdd {

	
	  InitialContext ctx = null;
	  DataSource ds = null;
	  Connection conn = null;
	  boolean conexion = false;
	  String user, pwd;


	public bbdd(){
	  }
	  
	public List<String> getListaTablasDatabase(String database){
		List<String> tablas = new ArrayList<String>();
		String sql = "SELECT * FROM INFORMATION_SCHEMA.tables WHERE TABLE_SCHEMA='" + database + "'";
		try {
			Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultado = sentencia.executeQuery(sql);
			while (resultado.next()) {
				tablas.add(resultado.getString("TABLE_NAME"));
			}
            return tablas;
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	  public boolean Login(String user, String pwd){
		  try {
			  

    	    Class.forName("com.mysql.jdbc.Driver");
    	    System.out.println("Intento Login usuario="+user + " pwd="+pwd);
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306",user, pwd);
			conexion = true;
			this.user = user;
			this.pwd = pwd;
		  }

    	catch (ClassNotFoundException e) {
    	    // TODO Auto-generated catch block
    	    e.printStackTrace();
    	} 
			
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return conexion;
	  }
	  
	  public List<String> getListaSqchemas(){
		  
		List<String> squemas = new ArrayList<String>();
		try {
			ResultSet rs = conn.getMetaData().getCatalogs();
			while (rs.next()) {
				System.out.println("Recupera schema " + rs.getString("TABLE_CAT"));
				squemas.add(rs.getString("TABLE_CAT"));
			}
			return squemas;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		  
		return null;
		 		  
	  }

  public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}
}
