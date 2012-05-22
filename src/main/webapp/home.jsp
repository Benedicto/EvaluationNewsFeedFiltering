<%-- 
    Document   : home
    Created on : 02-May-2012, 19:59:58
    Author     : XZH
--%>

<%@page import="java.util.Set"%>
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
    Map<String, JSONObject> candidateItems = (Map<String, JSONObject>) session.getAttribute("candidates");
    String userId = rest.getMyId();
    Set<String> selected = Recommender.getRecommendation(userId, candidateItems, 15);
    if (selected.size() == 0) {
        response.sendRedirect("thankyouverymuch.html");
    }
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
                submitData();
                $("#myform").attr("action","home.jsp");                           
            }
            
            function finish()
            {
                submitData();
                $("#myform").attr("action","thankyou.html");                           
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
                    else
                    {
                        jasonObject.items.push({name:this.name,value:2});
                    }
                });
                $.ajaxSetup({async:false});
                $.post("collectData", JSON.stringify(jasonObject));
                $.ajaxSetup({async:true});
            }
    
            $(document).ready(function(){
                $('#feedback').on('click',changeSelection);
            });
 
            function changeSelection()
            {
                this.innerHTML="what";
            }
        </script>
        <title>Recommendation</title>
    </head>

    <body class="zen" >
        <form method="post" id="myform">
            <div class="zen-box zen-themed center" style="width:400px">
                <div class="zen-inner">
                    <div class="zen-header">
                        <h4 align="center">Chatter News Feed Recommender</h4>
                    </div>
                </div>
            </div>
            <div class="zen-box zen-simple center" style="width:400px">
                <div class="zen-inner">
                    <div class="zen-body">
                        <p><span style="font-weight:bold">Note</span>: If the user images are not displayed properly or not displayed at all, please log in GUS in a separate page and refresh this page.</p>
                        <p><span style="font-weight:bold">Note</span>: You can skip an item if you cannot decide whether it is interesting or not.</p>
                    </div>
                </div>
            </div>
            <%
                for (String id : selected) {
                    JSONObject item = candidateItems.get(id);
                    String photoUrl = item.get("photoUrl").toString();
                    String body = ((JSONObject) item.get("body")).get("text").toString();
                    String actorName = ((JSONObject) item.get("actor")).get("name").toString();
            %>
            <div class="zen-box zen-standardBackground zen-simple center" style="width:400px">
                <div class="zen-inner">
                    <div class="zen-body">
                        <div class="zen-media">
                            <a class="zen-img" href="javascript:void(0);">
                                <img src="<%=photoUrl%>">
                            </a>
                            <div class="zen-mediaBody" style="max-width:321px; word-wrap:break-word">
                                <span class="actor"> <%=actorName%> </span>  <%=body%>
                                <%
                                    JSONArray comments = (JSONArray) (((JSONObject) item.get("comments")).get("comments"));
                                    if (comments.size() != 0) {
                                %>                                
                                <div class="zen-commentList">
                                    <b class="zen-arrowUp"></b>
                                    <ul>
                                        <%
                                            for (Object o : comments) {
                                                JSONObject comment = (JSONObject) o;
                                                String commentBody = ((JSONObject) comment.get("body")).get("text").toString();
                                                JSONObject user = (JSONObject) comment.get("user");
                                                String commenterPhotoUrl = ((JSONObject) user.get("photo")).get("smallPhotoUrl").toString();
                                                String commenterName = user.get("name").toString();
                                        %>
                                        <li>
                                            <div class="zen-media">
                                                <a class="zen-img" href="javascript:void(0);" >
                                                    <img src="<%=commenterPhotoUrl%>" style="width:30px;height:30px">
                                                </a>
                                                <div class="zen-mediaBody" style="max-width:271px; word-wrap:break-word">
                                                    <span class="actor"> <%=commenterName%> </span> <%=commentBody%>
                                                </div>
                                            </div>
                                        </li>
                                        <%
                                            }
                                        %>
                                    </ul>
                                </div>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
                <hr class="zen-divider" />
                <div class="zen-footer" style="position:relative; height:3em;">
                    <fieldset class="zen-checkGroup radioBig" style="line-height:3em; ">                       
                        <label for="<%=id + "0"%>">
                            <input type="radio" name="<%=id%>" id="<%=id + "0"%>" value=0 vertical-align: middle>
                            Interesting
                        </label>                       
                        <label for="<%=id + "1"%>">
                            <input type="radio" name="<%=id%>" id="<%=id + "1"%>" value=1>
                            Not so interesting
                        </label>
                    </fieldset>
                </div>
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
