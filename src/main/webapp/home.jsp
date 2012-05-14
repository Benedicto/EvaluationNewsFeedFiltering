<%-- 
    Document   : home
    Created on : 02-May-2012, 19:59:58
    Author     : XZH
--%>

<%@page import="java.util.Map"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.logging.Level"%>
<%@page import="org.json.simple.*"%>
<%@page import="rest.Rest"%>
<%@page import="recommender.Recommender"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Rest rest = (Rest) session.getAttribute("rest");
    if (rest == null) {
        Rest.setTimeFrame(8);
        rest = new Rest(request.getParameter("code"));
        session.setAttribute("rest", rest);
    }
    String userId = rest.getMyId();
    Map<String, JSONObject> candidateItems = rest.getFeedItems();
    Set<String> selected = Recommender.getRecommendation(userId, candidateItems);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="css/zen-components.css"/>
        <link rel="stylesheet" type="text/css" href="css/zen-normalize.css"/>
        <link rel="stylesheet" type="text/css" href="css/prettify.css"/>
        <link rel="stylesheet" type="text/css" href="css/mycss.css"/>
        <script type="text/javascript" src="jquery.js"></script>
        <script type="text/javascript">
            var valid=false;
            
            function more()
            {
                validateData();
                if(valid)
                {
                    submitData();
                    $("#myform").attr("action","home.jsp");
                }                
            }
            
            function finish()
            {
                validateData();
                if(valid)
                {
                    submitData();
                    $("#myform").attr("action","thankyou.html");
                }
                
            }
            
            function submitData()
            {
                var jasonObject={userId:"<%=userId%>",items:[]};
                $(":radio").map(
                function()
                {
                    if(this.checked)
                    {                      
                        jasonObject.items.push({name:this.name,value:this.value});
                    }
                });
                $.post("collectData", JSON.stringify(jasonObject));
            }

            function validate()
            {
                if(!valid)
                    alert("Please select an opinion for every post before submitting!");
                return valid;
            }

            function validateData()
            {
                var checked=0;
                $(":radio").map(
                function()
                {
                    if(this.checked)
                    {                      
                        checked += 1;
                    }
                });
                if(checked < 5)
                    valid = false;
                else
                    valid = true;
            }
        </script>
        <title>Recommendation</title>
    </head>

    <body class="zen" >
        <form method="post" onSubmit="return validate()" id="myform">
            <div class="zen-box zen-themed center" style="width:400px">
                <div class="zen-inner">
                    <div class="zen-header">
                        <h4 align="center">Chatter News Feed Recommender</h4>
                    </div>
                </div>
            </div>
            <%
                for (Map.Entry<String,JSONObject> e : items.entrySet()) {
                    String id = e.getKey();
                    JSONObject item = e.getValue();
                    String photoUrl = item.get("photoUrl").toString();
                    String body = ((JSONObject) item.get("body")).get("text").toString();
            %>
            <div class="zen-box zen-standardBackground zen-simple center" style="width:400px">
                <div class="zen-inner">
                    <div class="zen-body">
                        <div class="zen-media">
                            <a class="zen-img" href="javascript:void(0);">
                                <img src=<%=photoUrl%> alt="sample image">
                            </a>
                            <div class="zen-mediaBody">
                                <%=body%>
                            </div>
                        </div>
                    </div>
                </div>            
            </div>
            <div class="zen-footer">
                <fieldset class="zen-checkGroup">                       
                    <label for="<%=id + "0"%>">
                        <input type="radio" name="<%=id%>" id="<%=id + "0"%>" value="0">
                        Interesting
                    </label>                       
                    <label for="<%=id + "1"%>">
                        <input type="radio" name="<%=id%>" id="<%=id + "1"%>" value="1">
                        Not so interesting
                    </label>
                </fieldset>
            </div>
            <%
                }
            %>
            <br/>
            <div class="zen-footer">
                <input class="zen-btn zen-primaryBtn zen-mhl" type="submit" onclick="more()" value="More" style="width:150px;"/>
                <input class="zen-btn zen-primaryBtn zen-mhl" type="submit" onclick="finish()" value="Finish" style="width:150px;"/>
            </div>
        </form>
    </body>
</html>