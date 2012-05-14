<%-- 
    Document   : index
    Created on : May 2, 2012, 10:47:21 AM
    Author     : zxu
--%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="jquery.js"></script>
        <link rel="stylesheet" type="text/css" href="css/zen-components.css"/>
        <link rel="stylesheet" type="text/css" href="css/zen-normalize.css"/>
        <link rel="stylesheet" type="text/css" href="css/prettify.css"/>
        <link rel="stylesheet" type="text/css" href="css/mycss.css"/>
        <title>Recommendation</title>
    </head>
    <body class="zen">
        <div class="zen-callout center" style="width:300px;">
            <b class="zen-arrow"></b>
            <div class="zen-inner">
                <div class="zen-header">
                </div>
                <div class="zen-body">
                    <form class="zen-form zen-labelSmall" action="home_up.jsp" method="post">
                        <label for="username">
                            User Name
                        </label>
                        <input class="zen-input" name="username" id="username" />
                        <br/>
                        <label for="password">
                            Password
                        </label>
                        <input class="zen-input" type="password" name="password" id="password" />
                        <br/>
                        <label for="submit">
                        
                        </label>
                        <input class="zen-btn zen-primaryBtn" type="submit" name="submit" id="submit" value="Login">
                    </form>
                </div>
                <div class="zen-footer">
                </div>
            </div>
        </div>
    </body>
</html>
