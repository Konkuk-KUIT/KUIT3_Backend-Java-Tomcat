package tools.io;

import tools.constant.Method;
import tools.constant.Uri;
import tools.utils.HttpRequestManager;
import java.util.Map;

public class HttpRequest {

  private final Method method;
  private final Uri uri;
  private final Map<String, String> queryParams;
  private final Map<String, String> header;
  private final Map<String, String> body;

  public HttpRequest(HttpRequestManager httpRequestManager) {
    this.method = Method.get(httpRequestManager.getMethod());
    this.uri = Uri.get(httpRequestManager.getOnlyUri());
    this.queryParams = httpRequestManager.getQueryParams();
    this.header = httpRequestManager.getHeader();
    this.body = httpRequestManager.getBody();
  }

  public Method getMethod() {
    return this.method;
  }

  public Uri getUri() {
    return this.uri;
  }

  public Map<String, String> getQueryParams() {
    return this.queryParams;
  }

  public Map<String, String> getHeader() {
    return this.header;
  }

  public Map<String, String> getBody() {
    return this.body;
  }
}
