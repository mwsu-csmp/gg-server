package hello;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class PlayerController {

    @MessageMapping("/hello")
    @SendTo("/topic/playerPos")
    public Player player(Mapping playerpos) throws Exception{
        Thread.sleep(1000);
        return new Player("~~Coordinates~~: " + HtmlUtils.htmlEscape(playerpos.getCoordinates()));
    }
}