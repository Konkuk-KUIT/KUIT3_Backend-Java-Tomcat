package server.service;

import static tools.constant.FilePath.HOME_PATH;
import static tools.constant.FilePath.LOGIN_FAILED_PATH;
import static tools.constant.FilePath.LOGIN_PATH;
import static tools.constant.FilePath.REGISTER_PATH;
import static tools.constant.StatusCode.BAD_REQUEST;
import static tools.constant.StatusCode.FOUND;
import static tools.constant.StatusCode.OK;
import static tools.constant.Uri.HOME;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import model.entity.User;
import model.repository.MemoryUserRepository;
import model.repository.Repository;
import tools.io.HttpRequest;
import tools.io.HttpResponse;

public class ServiceImpl implements Service {

  private static final Logger log = Logger.getLogger(ServiceImpl.class.getName());

  private final Repository userRepository;


  public ServiceImpl() {
    this.userRepository = MemoryUserRepository.getInstance();
  }

  @Override
  public void home(HttpRequest request, HttpResponse response) throws IOException {
    log.info("<<<<<<<<< 홈화면 >>>>>>>>>");
    byte[] bytes = Files.readAllBytes(Path.of(HOME_PATH.getKey()));

    // TODO : 컨트롤러로 빼고 에러 처리
    response.setStatusCode(OK);
    response.setBody(bytes);
    response.build();
  }

  @Override
  public void registerForm(HttpRequest request, HttpResponse response) throws IOException {
    log.info("<<<<<<<<< 회원가입화면 >>>>>>>>>");
    byte[] bytes = Files.readAllBytes(Path.of(REGISTER_PATH.getKey()));

    response.setStatusCode(OK);
    response.setBody(bytes);
    response.build();
  }

  @Override
  public void loginForm(HttpRequest request, HttpResponse response) throws IOException {
    log.info("<<<<<<<<< 로그인화면 >>>>>>>>>");
    byte[] bytes = Files.readAllBytes(Path.of(LOGIN_PATH.getKey()));

    response.setStatusCode(OK);
    response.setBody(bytes);
    response.build();
  }

  @Override
  public void registerOnGetMethod(HttpRequest request, HttpResponse response) throws IOException {
    log.info("<<<<<<<<< 회원가입 (get) >>>>>>>>>");
    Map<String, String> query = request.getQueryParams();
    userRepository.addUser(new User(
        query.get("userId"),
        query.get("password"),
        query.get("name"),
        query.get("email")
    ));

    response.setStatusCode(FOUND);
    response.setLocation(HOME.getKey());
    response.build();
  }

  @Override
  public void registerOnPostMethod(HttpRequest request, HttpResponse response) throws IOException {
    log.info("<<<<<<<<< 회원가입 요청 (post) >>>>>>>>>");
    Map<String, String> payload = request.getBody();
    userRepository.addUser(new User(
        payload.get("userId"),
        payload.get("password"),
        payload.get("name"),
        payload.get("email")
    ));

    response.setStatusCode(FOUND);
    response.setLocation(HOME.getKey());
    response.build();
  }

  @Override
  public void login(HttpRequest request, HttpResponse response) throws IOException {
    log.info("<<<<<<<<< 로그인 요청 >>>>>>>>>");
    Map<String, String> payload = request.getBody();
    System.out.println(payload.get("userId"));
    System.out.println(payload.get("password"));
    Optional<User> user = userRepository.findUserById(payload.get("userId"));

    if (user.isEmpty()) {
      log.info("<<<<<<<<< 로그인 실패 >>>>>>>>>");
      byte[] bytes = Files.readAllBytes(Path.of(LOGIN_FAILED_PATH.getKey()));
      response.setStatusCode(BAD_REQUEST);
      response.setBody(bytes);
      response.build();
    }

    log.info("<<<<<<<<< 로그인 성공 >>>>>>>>>");
    response.setStatusCode(FOUND);
    response.setAuthorized(true);
    response.setLocation(HOME.getKey());
    response.build();
  }
}
