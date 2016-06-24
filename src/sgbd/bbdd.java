package sgbd;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import sgbd.dbcrypt.BCrypt;

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
			 

			  
			 ctx = new InitialContext();
			 Context envContext = (Context) ctx.lookup("java:comp/env");
			 DataSource ds = (DataSource) envContext.lookup("jdbc/sqlLocal");    	    
    	    conn = ds.getConnection();
    	    
    	    
    	    System.out.println("Intento Login usuario="+user + " pwd="+pwd);
    	    
			conexion = validarUsuario(user,pwd);
			this.user = user;
			this.pwd = pwd;
			
		  }catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }catch (NamingException e) {
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
	
	private boolean validarUsuario(String u, String p){
		

		String sql = "SELECT * FROM usuarios where nombre = '" + u + "'";
		try {
			Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultado = sentencia.executeQuery(sql);
			while (resultado.next()) {
				String storedPassword = resultado.getString("pwd");
				String generatedSecuredPasswordHash = BCrypt.hashpw(p, BCrypt.gensalt(12));
				System.out.println("bbdd::validarUsuario generatedSecuredPasswordHash + recoveredSecuredPasswordHash");
				//System.out.println(generatedSecuredPasswordHash);
				//System.out.println(storedPassword);
				
				System.out.println("bbdd::validarUsuario LongitudPasswordHash="+generatedSecuredPasswordHash.length());
				boolean matched = BCrypt.checkpw(p, generatedSecuredPasswordHash);
				System.out.println(matched);
				matched = BCrypt.checkpw(p, storedPassword);
				System.out.println(matched);
				
			}
            return true;
            
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
		}catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
}
