package controller;

import controller.GetController.HomeController;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import structure.StartLine;

public class ControllerMapper {

    // TODO : 나중에 따로 등록하는 객체 따로 뺴라, 얘 HashMap 안됨? 맘에 안들어, samePath but, different HTTP Method..?
    List<Controller> controllers = new ArrayList<>(List.of((Controller)new HomeController()));
    public Controller getController(BufferedReader br) throws IOException {
        StartLine startLine = new StartLine(br.readLine());

        for(Controller controller : controllers) {
            if(controller.doesFit(startLine)) {
                return controller;
            }
        }
        throw new IOException("잘못된 경로요(URL)");   // 뭘 던질지 몰라 준비 해봤어~
    }

}
