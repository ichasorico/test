package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import com.mysql.jdbc.PreparedStatement;

public class conexiones {

	/*
	  InitialContext ctx = null;
	  DataSource ds = null;
	  Connection conn = null;
	  */
	  boolean conexion = false;
	  boolean loginOK = false;
	  
	
	public conexiones(){
		/*
		try{
			ctx = new InitialContext();
			Context envContext = (Context) ctx.lookup("java:comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/sqlLocal");    	    
		    conn = ds.getConnection();
		    conexion=true;
		    System.out.println("conexiones::INIT-Conexión a BBDD realizada Ok");
		}catch(Exception e){
			System.out.println("conexiones::INIT-ERRORRRR al intentar Conexión a BBDD " + e.toString());
		}
		*/	    
	  }
	
	public void clearAll(Statement sentencia, String idServidor){
		
		String sql = "select count(*) as cuenta from conexionesactivas where idServidor = '" + idServidor + "'";
		
		ResultSet resultado;
		try {
			// sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultado = sentencia.executeQuery(sql);
			resultado.first();
			System.out.println("conexiones::clearAll  -  Se borrarán " + resultado.getString("cuenta") + " conexiones activas");
			resultado.close();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "delete from conexionesactivas where idServidor = '" + idServidor + "'";
		try {
			// sentencia = conn.createStatement();
			sentencia.execute(sql);
			System.out.println("conexiones::clearAll  - Borrado de conexiones activas instancia + " + idServidor + "  OK !!!");
			sentencia.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/*	
	public void cierra(){
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("conexiones::cierra()  - Problema al cerrar conexión !!!");
			e.printStackTrace();
		}
	}
*/	
	
}
