<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Gestiones con mySQL</title>
</head>
<body>
<%

if (session.getAttribute("Login")==null){
%>
<h1>Acceso al SGBD</h1>

<form id="loginForm" action="ServMain" method="post">
	<input type="hidden" name="operacion" id="operacion" value="login" />
	<input type="text" name="usuario" id="usuario" value="" />
	<input type="password" name="password" id="password" value="" />
	<input type="submit" value="Entrar"/>
</form>
<%
		if (request.getParameter("loginError")!=null){			
			%><p style="color:red";>Error de acceso</p><%}

} else{
	String user = "";
	String pwd = "";
	String[] dataLogin = session.getAttribute("Login").toString().split("/");
	user=dataLogin[0];
	pwd=dataLogin[1];
%>	
<h1>Acceso CONCEDIDO al SGBD</h1>

<form id="loginForm" action="ServMain" method="post">
	<input type="hidden" name="operacion" id="operacion" value="getSQLschemas" />
	<input type="hidden" name="usuario" id="usuario" value="<%=user%>" />
	<input type="hidden" name="password" id="password" value="<%=pwd%>"/>
	<select id="squemas" name="squemas">
		
		<option name="elija...." value="">Elija esquema</option>
		<%
		String schemaSelected = "";
		if(session.getAttribute("schemas")!=null){
			String str1 = session.getAttribute("schemas").toString();

			if(null != session.getAttribute("schemaSelected"))
				schemaSelected = session.getAttribute("schemaSelected").toString();
			%> <!-- <%=str1 %> --> <%			
			
			String[] schemasSQL = str1.split("/");

			for (int i = 0; i < schemasSQL.length; i++) {%>
				<option name="<%=schemasSQL[i] %>" <%if(schemasSQL[i].equalsIgnoreCase(schemaSelected)){ %> selected="selected"<%}%> > <%=schemasSQL[i] %></option>						
			<%}
		}
		%>
		<!-- schemaSelected = <%=schemaSelected %> -->
	</select>
	<input type="submit" value="Pedir"/>
</form>

<%	String tablaSelected = "";
	
if (session.getAttribute("tablasSchema")!=null){	
	
	if(session.getAttribute("tableSelected")!=null)
		tablaSelected =session.getAttribute("tableSelected").toString();
		
	String str1 = session.getAttribute("tablasSchema").toString();	
	String[] tablasSchema = str1.split("/");	
	%>
	
	<form id="tablaSchema" action="ServMain" method="post">
	<input type="hidden" name="operacion" id="operacion" value="getSQLschemas" />
	<input type="hidden" name="usuario" id="usuario" value="<%=user%>" />
	<input type="hidden" name="password" id="password" value="<%=pwd%>"/>
	<input type="hidden" id="schemaSelected" name="schemaSelected" value="<%=schemaSelected %>">
	<!-- <%=str1 %> --> 
	<select id="squemaTables" name="squemaTables">
	<%	for (int i = 0; i < tablasSchema.length; i++) {%>
		<option name="<%=tablasSchema[i] %>" <%if(tablasSchema[i].equalsIgnoreCase(tablaSelected)){ %> selected="selected"<%}%> > <%=tablasSchema[i] %></option>						
	<%}	%>
	
	
	</select>
	<input type="submit" value="Ver Tabla"/>
	</form>
	
	<%}
}
 %>
</body>
</html>