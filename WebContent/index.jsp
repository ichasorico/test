<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Gestiones con mySQL</title>
<script src="http://code.jquery.com/jquery-latest.js">
</script>
</head>
<body>
<%

if (session.getAttribute("Login")==null){
	%>

	
	<%@ include file="WEB-INF/incl/loginform.html" %>	

	<%
}else{
	

	
	%>		
	
	<%@ include file="WEB-INF/incl/bodyLoged.jsp" %>	
	
		
	<%
} 
	%>


</body>
</html>