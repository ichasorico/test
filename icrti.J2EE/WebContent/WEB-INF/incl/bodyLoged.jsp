	<%
	String isAdmin;
	if(null == session.getAttribute("isAdmin")){
		isAdmin = "false";
	}else{
		isAdmin = "true";
	}
	String sello = null;
	if(null != session.getAttribute("sello")){
		sello = session.getAttribute("sello").toString();
	}

	%>
	
	<h1>Acceso CONCEDIDO al Sistema</h1>
	<h2>El usuario es admin = <%=isAdmin %></h2>

	
	
	<form id="logedForm" action="ServMain" method="post">
		<input type="hidden" name="operacion" id="operacion" value="logOut" />
		<input type="hidden" name="sello" id="sello" value="<%=sello%> %>" />
		<input type="submit" value="LogOUT"/>
	</form>
	
	
    <script>

    var synccounter = 0; //if you plant to sync with server
    var timeleft = 120 * 60; //initialize the regressive counter


    // check the state every 30 seconds
    setInterval(function(){checkcounter();}, 10 *1000);

    function checkcounter(){
       var params="operacion=pingLogin&sello=<%=sello%>";       
       $.ajax({url:"ServMain",
           type:"POST",
           data:params,
           success: function(result){
              if("LoginOK!!" != result){
        	   	window.location.href=result;
           		}              
           }
       });
    }
       
    
    </script>
