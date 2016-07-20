<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Administración Seguridad de la Aplicación</title>
</head>
<body>
<%

if (session.getAttribute("Login")==null){
%>
<h1>Acceso al SGBD</h1>

<form id="loginForm" action="ServMain" method="post">
	<input type="hidden" name="operacion" id="operacion" value="login4Admin" />
	<input type="text" name="usuario" id="usuario" value="" />
	<input type="password" name="password" id="password" value="" />
	<input type="submit" value="Entrar"/>
</form>
<%
		if (request.getParameter("loginError")!=null){			
			%><p style="color:red";>Error de acceso</p><%}

} else{
	String rol = session.getAttribute("userIsAdmin").toString();
	Boolean isAdmin = Boolean.valueOf(rol);
	if (isAdmin){
		String user = "";
		String pwd = "";
		String[] dataLogin = session.getAttribute("Login").toString().split("/");
		user=dataLogin[0];
		pwd=dataLogin[1];
		%>	
		<h1>Administración Seguridad Aplicación</h1>

		<form id="loginForm" action="ServMain" method="post">
			<input type="hidden" name="operacion" id="operacion" value="getSQLschemas" />
			<input type="hidden" name="usuario" id="usuario" value="<%=user%>" />
			<input type="hidden" name="password" id="password" value="<%=pwd%>"/>
		</form>
			
	<%}else{
		%><p style="color:red";>Usuario no dispone de Privilegios</p><%
	}
	}%>

	

</body>
</html>