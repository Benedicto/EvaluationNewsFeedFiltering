<%-- 
    Document   : index
    Created on : May 2, 2012, 10:47:21 AM
    Author     : zxu
--%>
<%@page import="static rest.RestConfig.*"%>
<html>
    <body onLoad="document.authorizationForm.submit()">
    <form action="<%=currentREST.getAuthURL()%>" method="post" name="authorizationForm">    
      <input type="hidden" name="response_type" value="code"/>    
      <input type="hidden" name="client_id" value="<%=currentREST.getClient_id()%>"/>
      <input type="hidden" name="redirect_uri" value="<%=currentREST.getRedirect_uri()%>"/>
    </form>
    </body>
</html>
