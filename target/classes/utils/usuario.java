package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import utils.BCrypt;

public class usuario {

	private String id, nombre, idSesion, firma;
	
	private boolean isAdmin = false;	
/*	
	  InitialContext ctx = null;
	  DataSource ds = null;
	  Connection conn = null;
  
	  boolean conexion = false;
*/		  
	  boolean loginOK = false;
	  
	
	public usuario(){
		this.nombre = "";		
		this.id= "";

		
  
	}

	/**
	 * Realiza carga clase a partir credenciales proporcionadas: Login + carga perfilado seguridad
	 * @param user Nombre usuario
	 * @param pwd pwd proporcionada
	 * 
	 */
	public usuario(Statement sentencia, String user, String pwd){
		this.nombre = "";		
		this.id= "";
		//conexionSGBD();
		//if (conexion){
			System.out.println("usuario::INIT ("+user+" , "+pwd+")");
			if(validarUsuario(sentencia,user,pwd)){
				getPerfiladoSeguridad(sentencia, user, pwd);
			}else{
				this.nombre = null;
				this.id=null;
				System.out.println("usuario::INIT ("+user+" , "+pwd+")  -- No se encuentra el usuario en bbdd. LOGIN ERROR");
				//return false;
			}
			
		//}else{
		//	System.out.println("usuario::INIT ("+user+" , "+pwd+")  -- NO HAY CONEXIÓN A BBDDD");
		//}
	}

	

	
	  private void getPerfiladoSeguridad(Statement sentencia, String user, String pwd){
		  

				String sql = "";
				try {
					// DETERMINAMOS SI ES USUARIO ADMINISTRADOR
					//Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);					
					sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario = " + Integer.parseInt(id) + ") and admin = 1";
					ResultSet resultado = sentencia.executeQuery(sql);
					//System.out.println("usuario::getPerfiladoSeguridad --  BUSCAMOS ROLES TIPO ADMIN PARA USUARIO\n  ****************** " + sql);
					resultado.first();
					
					if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
						System.out.println("usuario::getPerfiladoSeguridad --  USUARIO ES ADMIN.   RECUPERAMOS "+Integer.parseInt(resultado.getString("cuenta"))+" ROLES TIPO ADMIN " );
						this.isAdmin = true;
						
					}else{
						System.out.println("usuario::getPerfiladoSeguridad --  USUARIO NO ES TIPO ADMIN " );
					}
				}catch (Exception e){
					System.out.println("usuario::getPerfiladoSeguridad("+user+" , "+pwd+")  -- EXCEPCIÓN AL BUSCAR ROLES USUARIO en bbdd. \n  ****************** " + sql);
					e.printStackTrace();
				}						
				

	  }
	
	  
		private boolean validarUsuario(Statement sentencia, String u, String p){
			

			String sql = "SELECT * FROM usuarios where nombre = '" + u + "'";
			try {
				//Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ResultSet resultado = sentencia.executeQuery(sql);
				String storedPassword = "";
				if (resultado.first()){
					
					do {
						//System.out.println("bbdd::validarUsuario("+u+" , "+p+")  -- Encuentra usuario en bbdd");
						storedPassword = resultado.getString("pwd");
						this.id = resultado.getString("idUsuario");
						this.nombre = u;						

						this.firma = BCrypt.hashpw(p, BCrypt.gensalt(12));
						System.out.println("usuario::validarUsuario FIRMA " + this.firma);

						
						loginOK = BCrypt.checkpw(p, storedPassword);
						System.out.println("usuario::validarUsuario("+u+" , "+p+")  -- Recupera storedPassword = " + storedPassword + " Validación " + loginOK);
					}while (resultado.next());
				}else{
					System.out.println("usuario::validarUsuario("+u+" , "+p+")  -- No se encuentra el usuario. LOGIN ERROR  \n  ****************** " + sql);
					loginOK = false;
				}
			} catch (SQLException e) {
				System.out.println("usuario::validarUsuario("+u+" , "+p+")  -- No se encuentra el usuario. SQLException  \n  ****************** " + sql);
				e.printStackTrace();			
			}catch (Exception e){
				System.out.println("usuario::validarUsuario("+u+" , "+p+")  -- No se encuentra el usuario. Exception  \n  ****************** " + sql);
				e.printStackTrace();
			}
			return loginOK;
		}
		
	
		
		  /**
		   * DETERMINA SI USUARIO (por ID o NOMBRE) TIENE ROLES ASIGNADOS COMO ADMIN
		   * PRIORIDAD AL PARÁMETRO idUsuario
		   * @param idUsuario
		   * @param nombreUsuario
		   * @return boolean
		   * @throws Exception
		   */
		  private boolean checkUserIsAdmin(Statement sentencia, String idUsuario, String nombreUsuario) throws Exception {

				//Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ResultSet resultado ;
				String sql; 
				
			  if(null == idUsuario || "".equalsIgnoreCase(idUsuario)){
				  
				  //REALIZAMOS LA BÚSQUEDA POR nombreUsuario SIEMPRE QUE TENGA VALOR Y NO SEA null			  
				  sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario in(SELECT idUsuario FROM usuarios where nombre = ''"+nombreUsuario+"'"+")) and admin = 1";				
					
			  }else{
				  
				  //REALIZAMOS LA BÚSQUEDA POR idUsuario SIEMPRE QUE SE PUEDA CONVERTIR EL VALOR A INT			  
				  sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario = " + Integer.parseInt(idUsuario) + ") and admin = 1";
					
			  }
			  
			  resultado = sentencia.executeQuery(sql);
			  System.out.println("usuario::checkUserIsAdmin("+idUsuario+","+nombreUsuario+")  --  BUSCAMOS ROLES TIPO ADMIN PARA USUARIO\n  ****************** " + sql);
				resultado.first();
				System.out.println("usuario::checkUserIsAdmin("+idUsuario+","+nombreUsuario+")  --  RECUPERAMOS "+Integer.parseInt(resultado.getString("cuenta"))+" ROLES TIPO ADMIN " );
				
				if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
					return true;
				}else{				
					return false;
				}

		  }
		
		
		private void conexionSGBD(){
			/*
			try{
				ctx = new InitialContext();
				Context envContext = (Context) ctx.lookup("java:comp/env");
				DataSource ds = (DataSource) envContext.lookup("jdbc/sqlLocal");    	    
			    conn = ds.getConnection();
			    conexion=true;
			    System.out.println("usuario::INIT-Conexión a BBDD realizada Ok !!!!");
			}catch(Exception e){
				System.out.println("usuario::INIT-Conexión a BBDD ERRORRRR \n" + e.toString());
			}
			*/	
		}
/*		
		public void desConecta(){
			try {
				ctx.close();
				conn.close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(SQLException s){
				// TODO Auto-generated catch block
				s.printStackTrace();
			}catch(Exception x){
				// TODO Auto-generated catch block
				x.printStackTrace();				
			}
		}
*/			
			    	    
			

		
	public String getIdUsuario() {
		return id;
	}
	
	public boolean isAdmin() {
		return isAdmin;
	}


	public String getNombre() {
		return nombre;
	}

	public String getIdSesion() {
		return idSesion;
	}


	public void setIdSesion(String idSesion) {
		this.idSesion = idSesion;
	}

	public String getFirma() {
		return firma;
	}


	/**
	 * DETERMINA SI UN USUARIO EST CORRECTAMENTE FORMADO. PARA ELLO VALIDA SI DISPONE DE 
	 * 		1.- NOMBRE E ID_USUARIO
	 * 		2.- ROLES ASIGNADOS
	 * @return
	 */
	public boolean isUserOk(){
		if(	null != nombre && !"".equalsIgnoreCase(nombre) &&
			null != id && !"".equalsIgnoreCase(id)  ){
			
			System.out.println("usuario::isUserOk  --  Validación corecta objeto: nombre " + this.nombre + " idUsuario = " + this.id + " isAdmin " + this.isAdmin);
			return true;
			
			
		}else{
			System.out.println("usuario::isUserOk  --  ERRORRRR Validación objeto: nombre " + this.nombre + " idUsuario = " + this.id + " isAdmin " + this.isAdmin);
			return false;
		}
	}
	
	




}
