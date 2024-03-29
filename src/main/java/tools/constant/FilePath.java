package tools.constant;

import java.util.HashMap;
import java.util.Map;

public enum FilePath {

  HOME_PATH("webapp/index.html"),
  REGISTER_PATH("webapp/user/form.html"),
  LOGIN_PATH("webapp/user/login.html"),
  LOGIN_FAILED_PATH("webapp/user/login_failed.html"),
  QNA_PATH("webapp/qna/form.html"),
  QNA_SHOW("webapp/qna/show.html");

  final String key;
  private static final Map<String, FilePath> map = new HashMap<>();

  FilePath(String key) {
    this.key = key;
  }

  static {
    for (FilePath filePath : FilePath.values()) {
      map.put(filePath.key, filePath);
    }
  }

  public String getKey() {
    return this.key;
  }

  public static FilePath get(String htmlPathKey) {
    return map.get(htmlPathKey);
  }
}
