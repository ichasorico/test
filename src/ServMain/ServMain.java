package ServMain;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sgbd.bbdd;

/**
 * Servlet implementation class ServMain
 */
@WebServlet(description = "Servlet principal", urlPatterns = { "/ServMain" })

public class ServMain extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServMain() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
        if(request.getParameter("operacion") != null){
        	String operacion = request.getParameter("operacion");
            if(operacion.equals("login"))
                login(request, response);
            else if(operacion.equals("getSQLschemas"))
            	getSchemas(request, response);
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
	
	private void getSchemas(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		String redirect = "index.jsp";
		//DETERMINA SI VIENE DEL PROCESO ANTERIOR DE LOGIN
		HttpSession session = request.getSession(true);
		session.removeAttribute("tablasSchema");
		//DEBERÁ ENCONTRAR EN LA SESIÓN UNA ENTRADA DENOMINADA "Login" QUE CONTIENE USUARIO/PWD DADO EN LA REQUEST
		
		if(request.getParameter("usuario") != null && request.getParameter("password") != null ){
			//ENCONTRAMOS USUARIO Y PWD. NECESITAMOS VALIDARLO CONTRA EL DATO EN LA SESIÓN
			String usrSession = (String)session.getAttribute("Login");			
			if(null != usrSession && usrSession.equals(request.getParameter("usuario") + "/" + request.getParameter("password"))){
				//SUPERA LA VALIDACIÓN DE LA SESSÓN
				bbdd db = new bbdd();
				if (db.Login(request.getParameter("usuario").toString(),request.getParameter("password").toString())){
					List<String> tablas = db.getListaTablasDatabase(request.getParameter("squemas"));
					if(null != tablas && tablas.size()>0){
						String strTablas = "";
						for (int i = 0; i < tablas.size(); i++) {
							strTablas += tablas.get(i) + "/";
						}
						
	    				System.out.println("schemaTables de " +request.getParameter("squemas")+ " -- " + strTablas.substring(0, strTablas.length()-1));
	    				session.setAttribute("schemaSelected",request.getParameter("squemas"));
	    				session.setAttribute("tablasSchema", strTablas.substring(0, strTablas.length()-1));
					}else{
						
						//NO SE RECUPERAN TABLAS PARA SCHEMA DADO
						System.out.println("getSchemas:NO SE RECUPERAN TABLAS PARA schema pedido -> " + request.getParameter("squemas") + ".");
						redirect+="?loginError";	
					}
				}else{
					
					//LE ENVIAMOS A LA PANTALLA DE ERROR AL NO VALIDAR LA BBDD LAS CREDENCIALES
					System.out.println("getSchemas::BBDD NO VALIDA LOGIN. " + request.getParameter("usuario") + "-" + request.getParameter("password"));
					redirect+="?loginError";					
				}
			}else{
				
				//LE ENVIAMOS A LA PANTALLA DE ERROR AL NO VALIDAR LOS DATOS DE SESIÓN CON EL LOGIN DE LA REQUEST
				System.out.println("getSchemas::NO VALIDA LOGIN. Datos sesión = " + usrSession + " datos request = " + request.getParameter("usuario") + "/" + request.getParameter("password"));
				redirect+="?loginError";				
			}
		}else{
			//LE ENVIAMOS A LA PANTALLA DE ERROR AL NO ENCONTRAR LOS PARÁMETROS NECESARIOS
			System.out.println("getSchemas::No se dectecta usr + pwd.Login errorrrr");
			redirect+="?loginError";
		}
		response.sendRedirect(redirect);
	}
	
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
    	
		String redirect = "index.jsp";
    	if(request.getParameter("usuario") != null && request.getParameter("password") != null ){

    		HttpSession session = request.getSession(true);
    		bbdd db = new bbdd();
    		if (db.Login(request.getParameter("usuario").toString(),request.getParameter("password").toString())){
    			// GUARDAMOS SESI�N M�S USR + PWD M�S ESTADO
    			session.setAttribute("Login", db.getUser() + "/" + db.getPwd());
    			System.out.println("Login ok");
    			List<String> squemas = db.getListaSqchemas();
    			if(null != squemas && squemas.size() > 0){
    				String strSchemas = "";
    				for (int i = 0; i < squemas.size(); i++) {
						strSchemas+=squemas.get(i)+"/";
					}
    				System.out.println("schemas " + strSchemas.substring(0, strSchemas.length()-1));
    				session.setAttribute("schemas", strSchemas.substring(0, strSchemas.length()-1));
    				
    			}
    		}else{
    			//request.setAttribute("loginError","error");
    			System.out.println("Login errorrrr");
    			redirect+="?loginError";
    		}		
    	}
    	response.sendRedirect(redirect);
    }
            

}
