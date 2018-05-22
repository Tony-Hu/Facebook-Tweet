package models;

import com.google.appengine.api.datastore.PreparedQuery;
import com.restfb.types.User;

public class Pair {
  public PreparedQuery pq;
  public User user;

  public Pair(User user, PreparedQuery pq){
    this.pq = pq;
    this.user = user;
  }
}
