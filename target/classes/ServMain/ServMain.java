package ServMain;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import utils.usuario;
import utils.conexion;
import utils.conexiones;
/**
 * Servlet implementation class ServMain
 */
@WebServlet(description = "Servlet principal", urlPatterns = { "/ServMain" })

public class ServMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static List<usuario> sesiones = new ArrayList <usuario>();
	private static String cfgIdSevidor = "tomcatId4Persistencia";
	private static String idSevidor = "";
	
	  InitialContext ctx = null;
	  DataSource ds = null;
	  Connection conn = null;       
	  
	  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServMain() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public void init() throws ServletException{
    	
    	System.out.println("ServMain::INIT  -- CONFIGURACIONES SERVIDOR AL ARRANCAR");    	
		
    	
		Context envContext;
		try {
			ctx = new InitialContext();
			envContext = (Context) ctx.lookup("java:comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/sqlLocal");    	
			conn = ds.getConnection();
			System.out.println("ServMain::INIT  -- CONEXIÓN A LA BBDD REALIZADA");    	
		} catch (NamingException | SQLException e) {
			
			System.out.println("ServMain::INIT  -- ERROR AL REALIZAR CONEXIÓN A LA BBDD ");    
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
        ServletContext servletContext = getServletContext();
        idSevidor = servletContext.getInitParameter(cfgIdSevidor);        
        System.out.println("ServMain::INIT cfgIdSevidor = " + idSevidor  );
        System.out.println("ServMain::INIT  -- CARGA INICIALIZACIÓN WEB.XML");  
        
    	
        limpiaConexionesActivas(idSevidor);
    	
    	
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 

		//response.getWriter().append("Served at: ").append(request.getContextPath());
		if(validarRequest(request)){
			
		// IDEA a ver qué hacemos
			
	        if(request.getParameter("operacion") != null){
	        
	        	if("pingLogin".equals(request.getParameter("operacion"))){
	        		 
	        		response.getOutputStream().write("LoginOK!!".getBytes());
	        		 
	        	}else if("logOut".equals(request.getParameter("operacion"))){
	        		logOUT(request);
	        		response.sendRedirect("logOut.html");
	        	}else{
	        		response.sendRedirect("index.jsp");
	        	}	        
	        }
		}else{
			if("pingLogin".equals(request.getParameter("operacion"))){
	    		//logOUT(request);
        		response.getOutputStream().write("logOut.html".getBytes());
			}else{
				
			}
    		//response.sendRedirect("logOut.html");
			
			//response.getOutputStream().write("logOut.html".getBytes());
		}
		//response.sendRedirect("index.jsp");
        //request.getRequestDispatcher("index.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	

    private boolean login(HttpServletRequest request){
    	
    	System.out.println("ServMain::login SOLICITUD DE LOGIN");
    	
    	if(request.getParameter("usuario") != null && request.getParameter("password") != null ){
    		
    		try {
				Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				usuario usr = new usuario(sentencia, request.getParameter("usuario").toString(),request.getParameter("password").toString());
				
	    		if (usr.isUserOk()){
	    			HttpSession curSesion = request.getSession();
		        	String idSesion = curSesion.getId();
		        	usr.setIdSesion(idSesion);
	    			sesiones.add(usr);
	        		System.out.println("ServMain::login  -- Login ok");
	        		curSesion.setAttribute("Login", "true");
	        		
	        		//NUEVA VERSIÓN CONTROL SESIONES CON PERSISTENCIA
	        		conexion c = new conexion(sentencia, usr,idSevidor,true);
	        		curSesion.setAttribute("sello", usr.getFirma());
	        		sentencia.close();	
	        		return true;

	    		}else{
	    			// return false;
	    		}
	    		
	    		sentencia.close();
	    		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
   		 	
   		 	


		}else{
			//request.setAttribute("loginError","error");
			System.out.println("ServMain::login  -- Login errorrrr. FALTAN CREDENCIALES\nUsuario=" + request.getParameter("usuario")  + " pwd=" + request.getParameter("password") );
			//redirect+="?loginError";
		}		
    	
    	//response.sendRedirect(redirect);
    	return false;
    }
           
    /**
     * DETERMINAMOS SI EL USUARIO ESTÁ LOGADO. 
     * PARA ELLO DETERMINAMOS 
     * 		SI TIENE EN LA REQUEST EL PARÁMETRO USUARIO
     * 			BUSCAMOS EL USUARIO EN LA LISTA DE sesiones Y VALIDAMOS EL ID_SESIÓN
     * 		SI NO LO TIENE DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN (obligatorio)
     * 
     * @param request
     * @return boolean
     * @since 05/07/2016
     */
    private boolean validarRequest(HttpServletRequest request){    	
    
    	HttpSession session = request.getSession(true);
    	// DETERMINAMOS LLEGADA DEL SELLO
    	if (null != session.getAttribute("sello") && !"".equalsIgnoreCase(session.getAttribute("sello").toString())){
        	
    		//BÚSQUEDA POR PERSISTENCIA
    		try {
				Statement sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				conexion c = new conexion(sentencia, session.getAttribute("sello").toString());
				sentencia.close();
	    		if (c.isUserOk()){
	    			return true;
	    		}else{
	    			System.out.println("ServMain::validarRequest  -- No se identifia REQUEST recibida");
	    		}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    	
    	

    	//LLEGADOS A ESTE PUNTO NO SE ENCUENTRA AL USUARIO LOGADO POR SESIÓN.
    	//DETERMINAMOS SI NOS ESTÁ PIDIENDO LOGIN. CASO CONTRARIO, PASAMOS DE LA PETICIÓN
    	
    	System.out.println("ServMain::validarRequest  -- Acceso no logado. Se determinará si pide Login");

    	if(request.getParameter("operacion") != null && !"".equalsIgnoreCase(request.getParameter("operacion"))){
    		String operacion = request.getParameter("operacion");		        	
    		if(operacion.equals("login") || operacion.equals("login4Admin")){ // NOPMD by Íñigo on 4/07/16 16:19
    			return login(request);		        			
    		}else{
    			System.out.println("ServMain::validarRequest  -- Usuario No Logado y Request operación que no es de LOGIN");
    		}
    	}else{
        	System.out.println("ServMain::validarRequest  -- Usuario No Logado y Request sin solicitud de LOGIN");
    	}	        	

	  
	      return false;
    }
    
    
    /** 
     * ELIMINA SESIÓN ACTUAL DE LA LISTA DE SESIONES
     * @param request
     */
    private void logOUT(HttpServletRequest request){

    	conexion c = new conexion();
    	Statement sentencia;
		try {
			if (null != request.getSession(true).getAttribute("sello") && !"".equalsIgnoreCase(request.getSession(true).getAttribute("sello").toString())){			
				sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				c.removeSesion(sentencia, request.getSession(true).getAttribute("sello").toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	request.getSession(true).setMaxInactiveInterval(1);
    	request.getSession(true).invalidate();    	
    	
    }
    
	private void limpiaConexionesActivas(String idServidor){
		
		String sql = "select count(*) as cuenta from conexionesactivas where idServidor = '" + idServidor + "'";
		Statement sentencia;
		ResultSet resultado;
		try {
			sentencia = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultado = sentencia.executeQuery(sql);
			resultado.first();
			System.out.println("ServMain::limpiaConexionesActivas("+idServidor+")  -  Se borrarán " + resultado.getString("cuenta") + " conexiones activas");
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
			sentencia = conn.createStatement();
			sentencia.execute(sql);
			System.out.println("ServMain::limpiaConexionesActivas("+idServidor+")  - Borrado de conexiones activas instancia + " + idServidor + "  OK !!!");
			sentencia.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
