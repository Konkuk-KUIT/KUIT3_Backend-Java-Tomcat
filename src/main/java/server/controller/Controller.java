package server.controller;

import tools.io.HttpRequest;
import tools.io.HttpResponse;
import java.io.IOException;

public interface Controller {

  void mapping(HttpRequest request, HttpResponse response) throws IOException;
}
