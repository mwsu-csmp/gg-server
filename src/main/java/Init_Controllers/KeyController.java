package Init_Controllers;

import KeyboardInput.Keyboard;
import PlayerInfo.Player;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Controller
public class KeyController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @MessageMapping(value = "/KeyboardInput")
    @SendTo("/topic/keyboard")
    public Message readKey(Keyboard key) throws Exception{

        log.info("getting the keyboard input");

        return new Message(HtmlUtils.htmlEscape(key.getKey()));
    }
}
