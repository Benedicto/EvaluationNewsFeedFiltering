<%-- 
    Document   : home
    Created on : 02-May-2012, 19:59:58
    Author     : XZH
--%>


<%@page import="java.util.Map"%>
<%@page import="org.json.simple.*"%>
<%@page import="rest.Rest"%>
<%
    Rest rest = new Rest("gonadarush@gmail.com", "salesforce63875682");
    Map<String, JSONObject> candidateItems = rest.getFeedItems();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="css/zen-components.css"/>
        <link rel="stylesheet" type="text/css" href="css/zen-normalize.css"/>
        <link rel="stylesheet" type="text/css" href="css/prettify.css"/>
        <link rel="stylesheet" type="text/css" href="css/mycss.css"/>        
        <title>Recommendation</title>
    </head>

    <body class="zen" >
            <%
                for (String id : candidateItems.keySet()) {
                    JSONObject item = candidateItems.get(id);
                    String photoUrl = item.get("photoUrl").toString();
                    
                    rest.getPhoto(photoUrl);
            %>
            <img src="<%=photoUrl%>" alt="sample image">
            <%
                }
            %>
    </body>
</html>
