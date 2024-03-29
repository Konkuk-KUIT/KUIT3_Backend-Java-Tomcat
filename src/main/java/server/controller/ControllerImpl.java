package server.controller;

import static tools.constant.Method.GET;
import static tools.constant.Method.POST;
import static tools.constant.Uri.HOME;
import static tools.constant.Uri.LOGIN;
import static tools.constant.Uri.REGISTER;
import static tools.constant.Uri.REGISTER_FORM;

import java.io.IOException;
import server.service.Service;
import server.service.ServiceImpl;
import tools.io.HttpRequest;
import tools.io.HttpResponse;

public class ControllerImpl implements Controller {

  private final Service service;

  public ControllerImpl(ServiceImpl service) {
    this.service = service;
  }

  @Override
  public void mapping(HttpRequest request, HttpResponse response) throws IOException {

    // @GetMapping("/") 홈화면
    if (request.getMethod() == GET && request.getUri() == HOME) {
      service.home(request, response);
    }

    // @GetMapping("/user/form") 회원가입 페이지
    if (request.getMethod() == GET && request.getUri() == REGISTER_FORM) {
      service.registerForm(request, response);
    }

    // @GetMapping("/user/login") 로그인 페이지
    if (request.getMethod() == GET && request.getUri() == LOGIN) {
      service.loginForm(request, response);
    }

    // @GetMapping("/user/signup") 회원가입 - 1
    if (request.getMethod() == GET && request.getUri() == REGISTER) {
      service.registerOnGetMethod(request, response);
    }

    // @PostMapping("/user/signup") 회원가입 - 2
    if (request.getMethod() == POST && request.getUri() == REGISTER) {
      service.registerOnPostMethod(request,response);
    }

    // @PostMapping("/user/login") 로그인
    if (request.getMethod() == POST && request.getUri() == LOGIN) {
      service.login(request, response);
    }
  }
}
