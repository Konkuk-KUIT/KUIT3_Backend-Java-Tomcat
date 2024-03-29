package tools.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class HttpRequestManager {

  private final String httpStartLien;
  private final Map<String, String> header;
  private final Map<String, String> body;

  private final static String PATH_SPLITTER = " ";
  private final static String URI_SPLITTER = "?";

  private HttpRequestManager(String httpStartLine, Map<String, String> header, Map<String, String> body) {
    this.httpStartLien = httpStartLine;
    this.header = header;
    this.body = body;
  }

  public String getMethod() {
    return httpStartLien.split(PATH_SPLITTER)[0];
  }

  public String getUri() {
    return httpStartLien.split(PATH_SPLITTER)[1].replace(".html", "");
  }

  public String getOnlyUri() {
    if (!httpStartLien.contains(URI_SPLITTER)) {
      return getUri();
    }
    String raw = httpStartLien.split(PATH_SPLITTER)[1];
    return raw.substring(0, raw.indexOf(URI_SPLITTER));
  }

  private String getQueryString() {
    return httpStartLien.substring(httpStartLien.indexOf(URI_SPLITTER) + 1);
  }

  public Map<String, String> getQueryParams() {
    return QueryParamParser.read(getQueryString().split(PATH_SPLITTER)[0]);
  }

  public Map<String, String> getHeader() {
    return this.header;
  }

  public Map<String, String> getBody() {
    return this.body;
  }

  public static HttpRequestManager of(BufferedReader br) throws IOException {
    String firstLien = br.readLine();
    Map<String, String> header = HttpRequestHeaderParser.read(br);
    Map<String, String> body = HttpRequestBodyParser.read(br, header.get("Content-Length"));

    return new HttpRequestManager(firstLien, header, body);
  }
}
