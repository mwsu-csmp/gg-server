package hello;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ClientController {

    private Client client;

    @MessageMapping("/hello")
    @SendTo("/topic/moveClient")
    public JSONObject moveClient(JSONObject jsonObject) throws Exception {
        int dx = jsonObject.getInt("dx");
        int dy = jsonObject.getInt("dy");
        int newX = client.updateX(dx);
        int newY = client.updateY(dy);
        JSONObject json = new JSONObject();
        json.optInt("x", newX);
        json.optInt("y", newY);
        return json;
    }

}