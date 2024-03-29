package tools.constant;

public enum StatusCode {
  OK(" 200 OK "),
  FOUND(" 302 FOUND "),
  CREATED(" 201 CREATED "),
  BAD_REQUEST(" 400 Bad Request ");

  private final String value;

  StatusCode(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
