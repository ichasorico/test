package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import utils.usuario;


public class conexionTest {

		private String usuario1 = "ingo";
		private String usuario2 = "macario";
		private String pwdUsuario1 = "asteroide";
		private String pwdUsuario2 = "asteroida";
		private boolean usuario1IsAdmin = false;
		private boolean usuario2IsAdmin = false;
		
		// SELLOS
		private String selloUsuario1 = "";
		
		private String idServidor = "tomcat1";
		
		// USUARIO TEST ERROR
		private String usuario3 = "felipe";
		private String pwdUsuario3 = "asteroidX";

	
		private String idUsuario1 = "";
		private String idUsuario2 = "";
		
		
		  InitialContext ctx = null;
		  DataSource ds = null;
		  Connection conn = null;       
		  Statement sentencia = null;
		  
	    @Before	    
	    public void initialize()	     
	    {
	    	
	    	String url = "jdbc:mysql://localhost:3306/icrti.testing";
	    	String username = "root";
	    	String password = "asteroide";
	    	try  {
	    		Connection connection = DriverManager.getConnection(url, username, password);
	    		sentencia = connection.createStatement();
	    	}catch(Exception e){
	    		
	    	}	    	
	    }
	    
	    
	    @Test 
	    public void testConexion()
	    {
	    	
	    	usuario u1 = new usuario(sentencia,usuario1, pwdUsuario1);
	    	selloUsuario1 = u1.getFirma();
	    	conexion c = new conexion(sentencia, u1,idServidor,true);
	    	// VERFICIACIÓN SGBD
	    	String sql = "SELECT * FROM conexionesactivas where idSesion = '"+selloUsuario1+"'";	    	
	    	String selloTest = ""; 
    		try{
    			ResultSet resultado = sentencia.executeQuery(sql);
	    		resultado.first();	    		
	    		selloTest = resultado.getString("idSesion");	    		
    		}catch(Exception e){
    			
    		}
    		assertEquals(selloUsuario1, selloTest);
	    }
}
