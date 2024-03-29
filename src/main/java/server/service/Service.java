package server.service;

import tools.io.HttpRequest;
import tools.io.HttpResponse;
import java.io.IOException;

public interface Service {

  void home(HttpRequest request, HttpResponse response) throws IOException;

  void registerForm(HttpRequest request, HttpResponse response) throws IOException;

  void loginForm(HttpRequest request, HttpResponse response) throws IOException;

  void registerOnGetMethod(HttpRequest request, HttpResponse response) throws IOException;

  void registerOnPostMethod(HttpRequest request, HttpResponse response) throws IOException;

  void login(HttpRequest request, HttpResponse response) throws IOException;
}
