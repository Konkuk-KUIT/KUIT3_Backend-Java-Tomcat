package tools.io;

import tools.constant.StatusCode;
import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {

  private static final String BASE = "http://localhost:80";
  private StatusCode statusCode;
  private String location;
  private String header;
  private boolean isAuthorized;
  private byte[] body = new byte[0];
  private final DataOutputStream dos;

  public HttpResponse(DataOutputStream dos) {
    this.dos = dos;
  }

  public void setStatusCode(StatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public void setLocation(String location) {
    this.location = BASE + location;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public void setAuthorized(boolean isAuthorized) {
    this.isAuthorized = isAuthorized;
  }

  public void build() throws IOException {
    createHeader();
    dos.writeBytes(this.header);
    dos.write(body, 0, body.length);
    dos.flush();
  }

  private void createHeader() {
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP/1.1 ").append(statusCode.getValue()).append(" \r\n");
    sb.append("Content-Type: ").append("text/html;charset=utf-8 \r\n");
    sb.append("Content-Length: ").append(body.length).append(" \r\n");

    addLocation(sb);

    if (isAuthorized) {
      sb.append("Set-Cookie: ").append("logined=true \r\n");
    }

    sb.append("\r\n");
    this.header = String.valueOf(sb);
  }

  private void addLocation(StringBuilder sb) {
    if (location == null) {
      return;
    }
    sb.append("Location: ").append(location).append(" \r\n");
  }
}
