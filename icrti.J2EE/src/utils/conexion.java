package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import utils.BCrypt;

public class conexion {

	String idUsuario, nombre, idSesion;

	boolean isAdmin = false;	
	
/*
 * 	  	InitialContext ctx = null;
 * 		DataSource ds = null;
 * 		Connection conn = null;
 * 		boolean conexion = false;
 */
	  
	  
	  
	  boolean loginOK = false;
	  
	
	/**
	 * Constructor Genérico. Inicializa conexión a SGBD
	 * 
	 * @param 
	 * @return 
	 */
	public conexion(){
		this.nombre = "";		
		this.idUsuario= "";
		//conectaSGBD();
		
	}
	
	/**
	 * Inicializa conexión a SGBD y añade nueva conexion a partir de un objeto Usuario.
	 * Si existe sesión con mismo ID no crea la sesión
	 * @param usuario class, String idServidor
	 * @return 
	 */
	  public conexion(Statement sentencia, usuario u, String idServidor){
			this.nombre = "";		
			this.idUsuario= "";
		  //conectaSGBD();
		  addSesion(sentencia, u, idServidor,true);
		  
		  
	  }

		/**
		 * Inicializa conexión a SGBD y añade nueva conexion a partir de un objeto Usuario.
		 * Si la sesión ya existiera con mismo ID, sobreEscribe.
		 * @param usuario class, String idServidor
		 * @return 
		 */
		  public conexion(Statement sentencia, usuario u, String idServidor, boolean sobreEscribe){
				this.nombre = "";		
				this.idUsuario= "";
			  // conectaSGBD();
			  addSesion(sentencia, u, idServidor,sobreEscribe);
			  
			  
		  }
		  
		/**
		 * Inicializa objeto conexión a partir del sello de la sesión 
		 * @param sello
		 */
		  public conexion(Statement sentencia, String sello){
			
			  this.nombre = "";		
			this.idUsuario= "";
			//conectaSGBD();
			 getSesion(sentencia, sello);		 
			  
		  }
		  
		  /**
		   * Da de baja una conexión a partir del sello
		   * @param sello
		   */
		  public void removeSesion(Statement sentencia, String sello){
			  
			  String sql = "select count(*) as cuenta from conexionesactivas where idSesion = '" + sello + "'";
			  try {
					//Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					ResultSet resultado = sentencia.executeQuery(sql);
					resultado.first();
					if((1 <= Integer.parseInt(resultado.getString("cuenta")))){
						int c = Integer.parseInt(resultado.getString("cuenta"));
						sql = "delete from conexionesactivas where idSesion = '" + sello + "'";						
						sentencia.execute(sql);
						
						System.out.println("conexion::removeSesion("+sello+") -- Se han borrado " + c  + " sesiones con el sello " + sello);							
					}else{
						System.out.println("conexion::removeSesion("+sello+") -- ");						
						System.out.println("conexion::removeSesion("+sello+") -- Error al borrar sesión con sello " + sello + "\nNO SE ENCUENTRA SESIÓN");
						System.out.println("conexion::removeSesion("+sello+") -- ");
					}
					
			  }catch (Exception e){
				  // TODO 
				  e.printStackTrace();
			  }
					
		  }
		  
		  
		  /**
		   * Obtiene de la tabla de sesiones activas la sesión con sello indicado
		   * @param sello
		   */
		  private void getSesion(Statement sentencia, String sello){
			  String sql = "select * from conexionesactivas where idSesion = '" + sello + "'";
			  try {
				
				ResultSet resultado = sentencia.executeQuery(sql);
				if(resultado.first()){
					this.idUsuario=resultado.getString("idUsuario");
					sql = "select * from usuarios where idUsuario = '" + this.idUsuario + "'";
					ResultSet resultado1 = sentencia.executeQuery(sql);
					if(resultado1.first()){
						this.nombre = resultado1.getString("nombre");
						this.isAdmin = checkUserIsAdmin(sentencia, this.idUsuario); 
						System.out.println("conexion::getSesion("+sello+") -- Se recupera al usuario " + this.nombre + " id-" + this.idUsuario + " isAdmin = " + this.isAdmin);
					}else{
						System.out.println("conexion::getSesion("+sello+") -- El usuario dado para esta sesión no existe");
						  this.nombre = "";		
							this.idUsuario= "";
					}
				}else{
					System.out.println("conexion::getSesion("+sello+") -- No se encuentra sesión");
					  this.nombre = "";		
						this.idUsuario= "";
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				  this.nombre = "";		
					this.idUsuario= "";
			}		
			  
			  
		  }
		  
	  private void conectaSGBD(){
		  /*
			try{
				ctx = new InitialContext();
				Context envContext = (Context) ctx.lookup("java:comp/env");
				DataSource ds = (DataSource) envContext.lookup("jdbc/sqlLocal");    	    
			    conn = ds.getConnection();
			    conexion=true;
			    System.out.println("conexion::INIT-Conexión a BBDD realizada Ok");
			}catch(Exception e){
				System.out.println("conexion::INIT-ERRORRRR al intentar Conexión a BBDD " + e.toString());
			}
			*/			
	  }
	  
	  private void addSesion(Statement sentencia, usuario u, String idServ, boolean sobreEscribe){
		  
		  if (null != u){
			  
			  System.out.println("conexion::addSesion() --  Intento añadir sesión para usuario="+u.getNombre() + " en la sesión "+u.getFirma());	    	    

					String sql = "";
					try {
						// DETERMINAMOS SI EXISTE SESIÓN ANTERIORMENTE INSERTADA
						//Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);					
						sql = "SELECT count(*) as cuenta FROM conexionesactivas where idSesion = '"+u.getFirma()+"'";
						ResultSet resultado = sentencia.executeQuery(sql);
						System.out.println("conexion::addSession --  BUSCAMOS SESIÓN PREVIAMENTE GUARDADA PARA idSesión " + u.getIdSesion());
						resultado.first();
						
						if((1 <= Integer.parseInt(resultado.getString("cuenta")) && sobreEscribe)){
							System.out.println("conexion::addSession --  SE IDENTIFICA INCONSISTENCIA DE DATOS. \nFLAG SOBREESCRIBIR = "+sobreEscribe+"\nBORRAMOS "+ resultado.getString("cuenta")+ " REGISTRO(S) CON MISMO IDSESION " + u.getIdSesion() );
							sql = "delete from conexionesactivas where idSesion = '"+u.getIdSesion()+"'";
							resultado.close();
							//sentencia.close();
							//sentencia = conn.createStatement();
							sentencia.execute(sql);
							// return false;							
						}

						// resultado.close();
						// sentencia.close();

						System.out.println("conexion::addSession  --  AÑADIMOS SESIÓN " );
						int myInt = (u.isAdmin()) ? 1 : 0;
						
						String timeStamp = String.valueOf(System.currentTimeMillis());
						
						sql = "insert into conexionesactivas (idSesion, idUsuario, isAdmin,idServidor, timeStamp) VALUES ('"+u.getFirma()+"', "+Integer.parseInt(u.getIdUsuario())+", " + myInt + ",'"+idServ+"','"+timeStamp+"')";
						// Statement sentencia1 = conn.createStatement();
						sentencia.execute(sql);
					
						
					}catch (Exception e){
						System.out.println("conexion::addSession ()  -- EXCEPCIÓN AL DETERMINAR INCONSISTENCIA DE DATOS SESIONES ALMACENADAS");
						e.printStackTrace();
					}					
					
		  }else{
			  System.out.println("conexion::addSession()  --  No se puede añadir sesión");
		  }		  
		  
		  
	  }

/*
		private boolean validarUsuario(String u, String p){
			

			String sql = "SELECT * FROM usuarios where nombre = '" + u + "'";
			try {
				Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				ResultSet resultado = sentencia.executeQuery(sql);
				String storedPassword = "";
				if (resultado.first()){
					
					do {
						//System.out.println("bbdd::validarUsuario("+u+" , "+p+")  -- Encuentra usuario en bbdd");
						storedPassword = resultado.getString("pwd");
						this.idUsuario = resultado.getString("idUsuario");
						this.nombre = u;
						//String generatedSecuredPasswordHash = BCrypt.hashpw(p, BCrypt.gensalt(12));
						//System.out.println("bbdd::validarUsuario generatedSecuredPasswordHash + recoveredSecuredPasswordHash");
						//System.out.println(generatedSecuredPasswordHash);
						//System.out.println(storedPassword);				
						//System.out.println("bbdd::validarUsuario LongitudPasswordHash="+generatedSecuredPasswordHash.length());
						//boolean matched = BCrypt.checkpw(p, generatedSecuredPasswordHash);
						//System.out.println(matched);
						
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
		
*/	
		
		  /**
		   * DETERMINA SI USUARIO (por ID o NOMBRE) TIENE ROLES ASIGNADOS COMO ADMIN
		   * PRIORIDAD AL PARÁMETRO idUsuario
		   * @param idUsuario
		   * @param nombreUsuario
		   * @return boolean
		   * @throws Exception
		   */
		  private boolean checkUserIsAdmin(Statement sentencia, String idUsuario, String nombreUsuario) throws Exception {

				// Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
			  //System.out.println("usuario::checkUserIsAdmin("+idUsuario+","+nombreUsuario+")  --  BUSCAMOS ROLES TIPO ADMIN PARA USUARIO\n  ****************** " + sql);
				resultado.first();
				//System.out.println("usuario::checkUserIsAdmin("+idUsuario+","+nombreUsuario+")  --  RECUPERAMOS "+Integer.parseInt(resultado.getString("cuenta"))+" ROLES TIPO ADMIN " );
				
				if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
					return true;
				}else{				
					return false;
				}

		  }
		
		
		  /**
		   * DETERMINA SI USUARIO (por ID ) TIENE ROLES ASIGNADOS COMO ADMIN
		   * 
		   * @param idUsuario
		   * @param nombreUsuario
		   * @return boolean
		   * @throws Exception
		   */
		  private boolean checkUserIsAdmin(Statement sentencia, String idUsuario) {

		  
			  try {
					//Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					ResultSet resultado ;
					String sql; 
					  //REALIZAMOS LA BÚSQUEDA POR idUsuario SIEMPRE QUE SE PUEDA CONVERTIR EL VALOR A INT			  
					  sql = "SELECT count(*) as cuenta FROM roles where idroles in (select idRol from userroles where idUsuario = " + Integer.parseInt(idUsuario) + ") and admin = 1";
				
				resultado = sentencia.executeQuery(sql);
				  System.out.println("usuario::checkUserIsAdmin("+idUsuario+")  --  BUSCAMOS ROLES TIPO ADMIN PARA USUARIO\n  ****************** " + sql);
					resultado.first();
					System.out.println("usuario::checkUserIsAdmin("+idUsuario+")  --  RECUPERAMOS "+Integer.parseInt(resultado.getString("cuenta"))+" ROLES TIPO ADMIN " );
					
					if(1 <= Integer.parseInt(resultado.getString("cuenta"))){
						return true;
					}else{				
						
					}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  return false;

		  }
		
		
	public String getIdUsuario() {
		return idUsuario;
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




	/**
	 * DETERMINA SI UN USUARIO EST CORRECTAMENTE FORMADO. PARA ELLO VALIDA SI DISPONE DE 
	 * 		1.- NOMBRE E ID_USUARIO
	 * 		2.- ROLES ASIGNADOS
	 * @return
	 */
	public boolean isUserOk(){
		if(	null != nombre && !"".equalsIgnoreCase(nombre) &&
			null != idUsuario && !"".equalsIgnoreCase(idUsuario)){
			
			System.out.println("usuario::isUserOk  --  Validación corecta objeto: nombre " + this.nombre + " idUsuario = " + this.idUsuario + " isAdmin " + this.isAdmin);
			return true;
			
			
		}else{
			System.out.println("usuario::isUserOk  --  ERRORRRR Validación objeto: nombre " + this.nombre + " idUsuario = " + this.idUsuario + " isAdmin " + this.isAdmin);
			return false;
		}
	}
	
	




}
