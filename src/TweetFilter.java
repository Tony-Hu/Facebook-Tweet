import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.scope.FacebookPermissions;
import com.restfb.scope.ScopeBuilder;
import com.restfb.types.Post;
import com.restfb.types.User;
import models.Constants;
import models.Pair;
import models.UserProperty;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TweetFilter implements Filter {
  AccessToken token;
  User user;
  Connection<Post> posts;
  Connection<User> friends;
  boolean forceUpdate = false;
  DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
  @Override
  public void init(FilterConfig filterConfig) {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    ScopeBuilder permissions = new ScopeBuilder();
    permissions.addPermission(FacebookPermissions.USER_POSTS).addPermission(FacebookPermissions.EMAIL)
        .addPermission(FacebookPermissions.USER_GENDER).addPermission(FacebookPermissions.USER_FRIENDS);

    DefaultFacebookClient facebookClient = new DefaultFacebookClient(Version.VERSION_3_0);
    String loginURL = facebookClient.getLoginDialogUrl(Constants.APP_ID, Constants.REDIRECT_URL, permissions);
    request.setAttribute("login", loginURL);
    String code = request.getParameter("code");
    checkURIRequest(request, response);
    if (token == null && code != null) {
      token = facebookClient.obtainUserAccessToken(Constants.APP_ID, Constants.APP_SECRET, Constants.REDIRECT_URL, code);
      ((HttpServletResponse)response).sendRedirect("/");
      System.out.println("Token is: " + token.getAccessToken());
    }
    if (token != null){
      request.setAttribute("token", token);
      facebookClient = new DefaultFacebookClient(token.getAccessToken(), Version.VERSION_3_0);
      user = facebookClient.fetchObject("me", User.class, Parameter.with("fields", "name, gender, picture, email"));
      System.out.println("User get: " + user);
      posts = facebookClient.fetchConnection("me/feed", Post.class);
      System.out.println(posts);

      saveUserInfo();
      friends = facebookClient.fetchConnection("me/friends", User.class);
      List<Pair> friendsPosts = queryFriendsPost();
      System.out.println(friendsPosts);

      PreparedQuery postsQuery = queryDataStore(user.getId());
      request.setAttribute("name", user.getName());
      request.setAttribute("pic", user.getPicture().getUrl());
      request.setAttribute("posts", postsQuery);
      request.setAttribute("ds", ds);
      if (friends != null) {
        request.setAttribute("friends", friendsPosts);
      }
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {

  }

  private void saveUserInfo(){
    Key userKey = KeyFactory.createKey(Constants.USERS, user.getId());
    Entity userEntity;
    try {
      userEntity = ds.get(userKey);
      forceUpdate = forceUpdate || isOutOfDate((Date) userEntity.getProperty(UserProperty.TIME_STAMP));
      if (forceUpdate){
        refreshUserInfoDataStore(userEntity);
        ds.put(userEntity);
      }
      System.out.println("Result = " + userEntity);
    } catch (EntityNotFoundException e) {//If this is first time use the app, record data
      userEntity = new Entity(userKey);
      System.out.println("[Exception]Result = " + userEntity);
      refreshUserInfoDataStore(userEntity);
      ds.put(userEntity);
    }

    if (posts == null || !forceUpdate){
      forceUpdate = false;
      return;
    }
    for (List<Post> postList : posts){
      if (postList == null){
        forceUpdate = false;
        return;
      }
      for (Post post : postList){
        if (post.getMessage() == null){
          continue;
        }
        Key postKey = KeyFactory.createKey(Constants.POSTS, post.getId());
        Entity postEntity;
        try{
          postEntity = ds.get(postKey);
          if (forceUpdate || isOutOfDate((Date) postEntity.getProperty(UserProperty.TIME_STAMP))){
            refreshPostDataStore(postEntity, post);
            ds.put(postEntity);
          }
          System.out.println("Result = " + postEntity);
        } catch (EntityNotFoundException e){
          postEntity = new Entity(postKey);
          System.out.println("[Exception]Result = " + postEntity);
          refreshPostDataStore(postEntity, post);
          ds.put(postEntity);
        }
      }
    }
    forceUpdate = false;
  }

  private void refreshUserInfoDataStore(Entity entity){
    entity.setProperty(UserProperty.TIME_STAMP, new Date());//Update time stamp
    entity.setProperty(UserProperty.USER_NAME, user.getName());
    entity.setProperty(UserProperty.USER_EMAIL, user.getEmail());
    entity.setProperty(UserProperty.USER_PIC_URL, user.getPicture().getUrl());
    entity.setProperty(UserProperty.USER_GENDER, user.getGender());
  }

  private void refreshPostDataStore(Entity entity, Post post){
    entity.setProperty(UserProperty.TIME_STAMP, new Date());
    entity.setProperty(UserProperty.UID, user.getId());
    entity.setProperty(UserProperty.POST_MESSAGE, post.getMessage());
    entity.setProperty(UserProperty.POST_CREATED_TIME, post.getCreatedTime());
  }

  private boolean isOutOfDate(Date entityDate){
    return (new Date().getTime() - entityDate.getTime()) > Constants.FRESH_THRESHOLD;
  }

  private void checkURIRequest(ServletRequest request, ServletResponse response) throws IOException {

    switch(((HttpServletRequest)request).getRequestURI()){
      case "/":
      case "/index.jsp":
      case "/top.jsp":
      case "/friends.jsp":
        //Do nothing
        break;
      case "/delete":
        String id = request.getParameter("id");
        System.out.println("Post #: " + id + " is going to be deleted");
        Key key = KeyFactory.createKey(Constants.POSTS, id);
        ds.delete(key);
        ((HttpServletResponse)response).sendRedirect("/");
        break;
      case "/logout":
        token = null;
        user = null;
        posts = null;
        forceUpdate = false;
        friends = null;
        ((HttpServletResponse)response).sendRedirect("/");
      case "/post":
        forceUpdate = true;
      default:
        ((HttpServletResponse)response).sendRedirect("/");
    }
  }

  private PreparedQuery queryDataStore(String userID){
    Query.Filter propertyFilter =
        new Query.FilterPredicate(UserProperty.UID, Query.FilterOperator.EQUAL, userID);
    Query q = new Query(Constants.POSTS).setFilter(propertyFilter);
    return ds.prepare(q);
  }

  private List<Pair> queryFriendsPost(){
    List<Pair> result = new ArrayList<>();
    if (friends == null){
      return result;
    }

    for (User friend : friends.getData()){
      result.add(new Pair(friend, queryDataStore(friend.getId())));
    }
    return result;
  }

}
