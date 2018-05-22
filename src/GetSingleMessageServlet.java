import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import models.Constants;
import models.UserProperty;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;

public class GetSingleMessageServlet  extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    DatastoreService ds = (DatastoreService) req.getAttribute("ds");
    String postId = req.getParameter("post_id");
    Entity singleMessage;
    Entity userInfo;
    PrintStream out = new PrintStream(resp.getOutputStream());
    out.println("<html>\n" +
        "  <head>\n" +
        "    <title>Get Single Message</title>\n" +
        "    <link href=\"css/bootstrap-4.0.0.css\" rel=\"stylesheet\">\n" +
        "    <link href=\"javscript/facebook.js\" rel=\"script\">\n" +
        "    <script src=\"javscript/facebook.js\"></script>\n" +
        "  </head>\n" +
        "  <body>\n" +
        "  <nav id=\"myHeader\" class=\"navbar navbar-expand-lg navbar-dark bg-primary header fixed-top\">\n" +
        "    <a class=\"navbbar-brand\" href=\"#\"><span style=\"color: #000000; font-family: monospace;size: 12px\" ><strong>Facebook<br>Tweet</strong></span></a>\n" +
        "    <div class=\"collapse navbar-collapse\" id=\"navbarSupportedContent\">\n" +
        "      <ul class=\"navbar-nav mr-auto\">\n" +
        "        <li class=\"nav-item\">\n" +
        "          <a class=\"nav-link\" href=\"index.jsp\" >My tweets</a>\n" +
        "        </li>\n" +
        "        <li class=\"nav-item\">\n" +
        "          <a class=\"nav-link active\" href=\"friends.jsp\" >Friends</a>\n" +
        "        </li>\n" +
        "        <li class=\"nav-item\">\n" +
        "          <a class=\"nav-link\" href=\"top.jsp\" >Top tweets</a>\n" +
        "        </li>\n" +
        "      </ul>\n" +
        "    </div>\n" +
        "  </nav>\n" +
        "  <section>\n" +
        "    <div class=\"jumbotron mt-2\">");
    try {
      singleMessage = ds.get(KeyFactory.createKey(Constants.POSTS, postId));
      userInfo = ds.get(KeyFactory.createKey(Constants.USERS, (String)singleMessage.getProperty(UserProperty.UID)));
      out.println("<div class=\"media\">\n" +
          "          <img class=\"mr-3\" src=\"" + userInfo.getProperty(UserProperty.USER_PIC_URL) + "\"/>\n" +
          "          <div class=\"media-body\">\n" +
          "            <h5>" + userInfo.getProperty(UserProperty.USER_NAME) +" </h5>\n" +
          "            <p>" + singleMessage.getProperty(UserProperty.POST_MESSAGE) + "\"<br>@ \"" + singleMessage.getProperty(UserProperty.POST_CREATED_TIME) + "</p>\n" +
          "            <p>Time viewed: " + singleMessage.getProperty(UserProperty.VISIT_COUNTER) + "</p>\n" +
          "          </div>\n" +
          "        </div>");
    } catch (EntityNotFoundException e) {
      //Do nothing
      out.println("<h5>The post does not exist.</h5>");
    }
    out.println("      </div>\n" +
        "    </div>\n" +
        "  </section>\n" +
        "  </body>\n" +
        "</html>");
  }
}
