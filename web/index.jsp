<%--
  Created by IntelliJ IDEA.
  User: tony
  Date: 5/15/18
  Time: 7:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>My tweets</title>
    <link href="css/bootstrap-4.0.0.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" async></script>
    <script src="https://connect.facebook.net/en_US/sdk.js" async></script>
    <script src="javscript/facebook.js" async></script>
  </head>
  <body onload="checkLoginState();">
  <nav id="myHeader" class="navbar navbar-expand-lg navbar-dark bg-primary header fixed-top">
    <a class="navbbar-brand" href="#"><span style="color: #000000; font-family: monospace;size: 12px" ><strong>Facebook<br>Tweet</strong></span></a>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <a class="nav-link active" href="index.jsp" >My tweets</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="friends.jsp" >Friends</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="top.jsp" >Top tweets</a>
        </li>
      </ul>
      <ul class="nav navbar-nav navbar-right" id="login-btn">
        <fb:login-button
                scope="public_profile, email, user_birthday, user_posts"
                onlogin="checkLoginState();">
        </fb:login-button>
      </ul>
    </div>
  </nav>
  <section>
    <div class="jumbotron mt-2">
      <div id="my-posts" class="container">

      </div>
    </div>
  </section>
  </body>
</html>
