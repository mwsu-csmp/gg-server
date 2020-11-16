package edu.missouriwestern.csmp.gg.server.controllers;

import com.google.gson.*;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("player-controller")
public class PlayerController {

    private Gson gson;

    @Autowired private Game game;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlayerController() {
        var gb = new GsonBuilder();
        gson = gb.create();
    }


    public Object decodeJSONObject(JsonElement o) {
        if(o.isJsonPrimitive()) {
            var p = (JsonPrimitive)o;
            if(p.isBoolean()) {
                return p.getAsBoolean();
            } else if(p.isNumber()) {
                return p.getAsInt();
            } else if(p.isString()) {
                return p.getAsString();
            } else throw new IllegalArgumentException("unexpected JSON primitive value type");
        } else if(o.isJsonArray()) {
            var ja = o.getAsJsonArray();
            List ls = new ArrayList<Object>();
            for (var v : ja)
                ls.add(decodeJSONObject(v.getAsJsonObject()));
            return ls;
        } else if(o.isJsonNull()) {
            return null;
        } else if(o.isJsonObject()) {
            var jsobj = o.getAsJsonObject();

            var newObj = new HashMap<String,Object>();
            for(var key : jsobj.keySet())
                newObj.put(key, decodeJSONObject(jsobj.get(key)));
            return newObj;
        }
        throw new IllegalArgumentException("unexpected JSON value type");
    }

    @MessageMapping("/gg/command")
    public void receiveCommand(
            String data,
            Principal user
    ){
        var parser = new JsonParser();
        var element = parser.parse(data).getAsJsonObject();

        var properties = new HashMap<String,Object>();
        for(String property : element.keySet()) {
            properties.put(property, decodeJSONObject(element.get(property)));
        }
        game.propagateEvent(new Event(game, "command", properties));
    }
}