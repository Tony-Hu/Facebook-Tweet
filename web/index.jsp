<%@ page import="com.restfb.types.Post" %>
<%@ page import="java.util.List" %>
<%@ page import="com.restfb.Connection" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="models.UserProperty" %>
<%@ page import="models.Constants" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %><%--
  Created by IntelliJ IDEA.
  User: tony
  Date: 5/15/18
  Time: 7:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
  <head>
    <title>My tweets</title>
    <link href="css/bootstrap-4.0.0.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js" async></script>
    <script src="https://connect.facebook.net/en_US/sdk.js" async></script>
    <script src="javscript/facebook.js" async></script>
  </head>
  <%--<body onload="checkLoginState();">--%>
  <body>
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
        <c:choose>
          <c:when test="${token == null}">
            <a href="${login}" class="text-dark">Login</a>
          </c:when><c:otherwise>
            <a class="text-dark" href="/logout">Hello! ${name}</a>
        </c:otherwise>
        </c:choose>
      </ul>
    </div>
  </nav>
  <section>
    <div class="jumbotron mt-2">
      <h4>
        <a href="https://www.facebook.com/dialog/share?app_id=<%out.print(Constants.APP_ID);%>&href=https%3A//www.google.com/&redirect_uri=<%out.print(Constants.REDIRECT_URL);%>post">Post New Tweet</a>
      </h4>
      <div id="my-posts" class="container">
        <% int i = 0;%>
        <% PreparedQuery pq;%>
        <% if ((pq = (PreparedQuery) request.getAttribute("posts")) != null) {%>
          <% for (Entity result : pq.asIterable()){ %>
                <% i++;%>
                <div class="media">
                  <img class="mr-3" src="<% out.print(request.getAttribute("pic"));%>"/>
                  <div class="media-body">
                    <h5><% out.print(request.getAttribute("name"));%></h5>
                    <p><% out.println(i + ". "  + result.getProperty(UserProperty.POST_MESSAGE) + "<br>@ " + result.getProperty(UserProperty.POST_CREATED_TIME));%></p>
                    <div class="container">
                      <div class="row">
                        <div class="col-md-6">
                          <button class="btn btn-primary" type="button" onclick="window.open('https://www.facebook.com/dialog/send?app_id=<%out.print(Constants.APP_ID);%>&link=<% out.print(Constants.REDIRECT_URL + "get?post_id=" + result.getKey().getName());%>&redirect_uri=<%out.print(Constants.REDIRECT_URL);%>')">Share</button>
                        </div>
                        <div class="col-md-6">
                          <button class="btn btn-primary" type="button" onclick="window.open('/delete?id=<%out.print(result.getKey().getName());%>')">Delete</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
            <% } %>
        <% } %>
      </div>
    </div>
  </section>
  </body>
</html>
