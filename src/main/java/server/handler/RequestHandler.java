package server.handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.controller.Controller;
import server.controller.ControllerImpl;
import server.service.ServiceImpl;
import tools.io.HttpRequest;
import tools.io.HttpResponse;
import tools.utils.HttpRequestManager;

public class RequestHandler implements Runnable {


  private final Socket connection;
  private final Controller controller;

  private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

  public RequestHandler(Socket connection) {
    this.connection = connection;
    this.controller = new ControllerImpl(new ServiceImpl());
  }

  @Override
  public void run() {
    log.log(Level.INFO,
        "New Client Connect! Connected IP : " + connection.getInetAddress()
            + ", Port : " + connection.getPort());

    try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      DataOutputStream dos = new DataOutputStream(out);

      HttpRequest request = new HttpRequest(HttpRequestManager.of(br));
      HttpResponse response = new HttpResponse(dos);

      controller.mapping(request, response);

    } catch (IOException e) {
      log.log(Level.SEVERE, e.getMessage());
    }
  }
}