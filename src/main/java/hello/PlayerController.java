package hello;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class PlayerController {


    @MessageMapping(value = "/hello")
    @SendTo("/topic/moveto")
    public Mapping map(Player player) throws Exception{
        return new Mapping(HtmlUtils.htmlEscape(Integer.toString(player.getX())));
    }

}