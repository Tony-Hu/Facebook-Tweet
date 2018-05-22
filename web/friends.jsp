<%@ page import="java.util.List" %>
<%@ page import="com.restfb.types.User" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="models.UserProperty" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="models.Pair" %>
<%@ page import="models.Constants" %><%--
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
    <title>Friends</title>
    <link href="css/bootstrap-4.0.0.css" rel="stylesheet">
    <link href="javscript/facebook.js" rel="script">
    <script src="javscript/facebook.js"></script>
  </head>
  <body>
  <nav id="myHeader" class="navbar navbar-expand-lg navbar-dark bg-primary header fixed-top">
    <a class="navbbar-brand" href="#"><span style="color: #000000; font-family: monospace;size: 12px" ><strong>Facebook<br>Tweet</strong></span></a>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <a class="nav-link" href="index.jsp" >My tweets</a>
        </li>
        <li class="nav-item">
          <a class="nav-link active" href="friends.jsp" >Friends</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="top.jsp" >Top tweets</a>
        </li>
      </ul>
      <ul class="nav navbar-nav navbar-right">
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
      <div>
        <% List<Pair> friendsPost = (List<Pair>) request.getAttribute("friends");%>
        <% if (friendsPost != null) { %>
            <% for (Pair pair : friendsPost) { %>
              <% int i = 0; %>
              <% for (Entity posts : pair.pq.asIterable()) { %>
                <% i++;%>
                <div class="media">
                  <img class="mr-3" src="<% out.print(pair.user.getPicture().getUrl());%>"/>
                  <div class="media-body">
                    <h5><% out.print(pair.user.getName());%></h5>
                    <p><% out.println(i + ". "  + posts.getProperty(UserProperty.POST_MESSAGE) + "<br>@ " + posts.getProperty(UserProperty.POST_CREATED_TIME));%></p>
                  </div>
                </div>
              <% } %>
            <% } %>
        <% } %>
      </div>
    </div>
  </section>
  </body>
</html>
