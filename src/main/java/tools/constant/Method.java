package tools.constant;

import java.util.HashMap;
import java.util.Map;

public enum Method {

  GET("GET"),
  POST("POST");

  private final String key;
  private static final Map<String, Method> map = new HashMap<>();

  Method(String key) {
    this.key = key;
  }

  static {
    for (Method method : Method.values()) {
      map.put(method.key, method);
    }
  }

  public String getKey() {
    return this.key;
  }

  public static Method get(String key) {
    return map.get(key);
  }
}
