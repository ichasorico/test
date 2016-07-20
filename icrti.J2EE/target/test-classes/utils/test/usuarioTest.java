package utils.test;

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


public class usuarioTest {

		private String usuario1 = "ingo";
		private String usuario2 = "macario";
		private String pwdUsuario1 = "asteroide";
		private String pwdUsuario2 = "asteroida";
		private boolean usuario1IsAdmin = false;
		private boolean usuario2IsAdmin = false;
		
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
	    		// OBTENEMOS idUsuario PARA USUARIO1
	    		String sql = "select * from usuarios where nombre = '" + usuario1 +"'";
	    		ResultSet resultado = sentencia.executeQuery(sql);
	    		resultado.first();
	    		idUsuario1 = resultado.getString("idUsuario");
	    		//resultado.close();
	    		// OBTENEMOS idUsuario PARA USUARIO2
	    		sql = "select * from usuarios where nombre = '" + usuario2 +"'";
	    		resultado = sentencia.executeQuery(sql);
	    		resultado.first();
	    		idUsuario2 = resultado.getString("idUsuario");
/*
	    		resultado.close();
	    		sentencia.close();
	    		sentencia = connection.createStatement();
	    		*/
	    		// OBTENEMOS isUserAdmin PARA USUARIO1
	    		sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario in(SELECT idUsuario FROM usuarios where nombre = '"+ usuario1 +"'"+")) and admin = 1";
	    		resultado = sentencia.executeQuery(sql);
	    		try{
		    		resultado.first();
		    		if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
						usuario1IsAdmin = true;
					}else{				
						usuario1IsAdmin = false;
					}
	    		}catch(Exception e){
	    			usuario1IsAdmin = false;
	    		}
	    		
	    		// OBTENEMOS isUserAdmin PARA USUARIO2
	    		sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario in(SELECT idUsuario FROM usuarios where nombre = '"+ usuario2 +"'"+")) and admin = 1";
	    		resultado = sentencia.executeQuery(sql);
	    		try{
		    		resultado.first();
		    		if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
						usuario2IsAdmin = true;
					}else{				
						usuario2IsAdmin = false;
					}
	    			
	    		}catch(Exception e){
	    			usuario2IsAdmin = false;
	    		}
	    		

	    	} catch (SQLException e) {
	    	    throw new IllegalStateException("Cannot connect the database!", e);
	    	}			

	    	

	    }
	    
	    @Test 
	    public void testUsers()
	    {
	    	

	    	usuario u1 = new usuario(sentencia,usuario1, pwdUsuario1);
    		usuario u2 = new usuario(sentencia, usuario2, pwdUsuario2);	  
	    	//assertEquals(u1,u2);
	    	//assertEquals(u1.getIdUsuario(), u2.getIdUsuario());
	    	assertEquals(u1.isUserOk(), u2.isUserOk());

	    }
	    
	    @Test 
	    public void testUser1()
	    {	    	

    		usuario u1 = new usuario(sentencia,usuario1, pwdUsuario1);	    	
    		assertEquals( "validación idUsuario para Usuario.u1 ", u1.getIdUsuario(), idUsuario1);	       
	    }
	    
	    @Test 
	    public void testUser1isAdmin()
	    {	    	

    		usuario u1 = new usuario(sentencia,usuario1, pwdUsuario1);	    	
    		assertEquals( "validación userIsAdmin para Usuario.u1 ", u1.isAdmin(), usuario1IsAdmin);	       
	    }
	    
	    @Test 
	    public void testUser2isAdmin()
	    {	    	

    		usuario u2 = new usuario(sentencia,usuario2, pwdUsuario2);	    	
    		assertEquals( "validación userIsAdmin para Usuario.u2 ", u2.isAdmin(), usuario2IsAdmin);	       
	    }
	    
	    @Test 
	    public void testUser2()
	    {

    		usuario u2 = new usuario(sentencia, usuario2, pwdUsuario2);
    		assertEquals( "validación idUsuario para Usuario.u2 ", u2.getIdUsuario(), idUsuario2);
	       
	    }

	    @Test 
	    public void testUserLoginFail()
	    {

    		usuario u3 = new usuario(sentencia, usuario3, pwdUsuario3);
	    	assertEquals(u3.isUserOk(), false);
	       
	    }

}
