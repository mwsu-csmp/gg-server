package hello;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
public class PlayerController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @MessageMapping(value = "/hello")
    @SendTo("/topic/moveto")
    public Mapping map(Player player) throws Exception{
        log.info("getting the player position");
        return new Mapping(HtmlUtils.htmlEscape(Integer.toString(player.getX())));
    }

}