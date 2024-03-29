package tools.constant;

import java.util.HashMap;
import java.util.Map;

public enum Uri {

  HOME("/"),
  REGISTER_FORM("/user/form"),
  REGISTER("/user/signup"),
  LOGIN("/user/login"),
  LOGIN_FAILED("/user/login/fail");

  private final String key;
  private static final Map<String, Uri> map = new HashMap<>();

  Uri(String key) {
    this.key = key;
  }

  static {
    for (Uri uri : Uri.values()) {
      map.put(uri.key, uri);
    }
  }

  public String getKey() {
    return this.key;
  }

  public static Uri get(String uriKey) {
    return map.get(uriKey);
  }
}
