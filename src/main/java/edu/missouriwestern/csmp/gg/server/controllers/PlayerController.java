package edu.missouriwestern.csmp.gg.server.controllers;

import com.google.gson.*;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.mwsu.entities.Player;
import edu.missouriwestern.csmp.gg.server.Application;
import edu.missouriwestern.csmp.gg.server.networking.MqttEventPropagator;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller("player-controller")
public class PlayerController implements MqttCallback, AuthenticationSuccessHandler {

    private Gson gson;

    @Autowired private Game game;

    private IMqttClient client;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public PlayerController() {
        var gb = new GsonBuilder();
        gson = gb.create();
    }

    public void setMqttClient(IMqttClient client, Game game) throws MqttException {
        this.game = game;
        this.client = client;
        this.client.setCallback(this);
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
            Principal user) {
        receiveCommand(data, user.getName());
    }

    public void receiveCommand(
            String data,
            String agent) {
        var parser = new JsonParser();
        var element = parser.parse(data).getAsJsonObject();
        if(!element.has("username") || !element.get("username").getAsString().equals(agent)) {
            Application.logger.info("bad command from " + agent + ": " + data);
            return;
        }
        var properties = new HashMap<String,Object>();
        for(String property : element.keySet()) {
            properties.put(property, decodeJSONObject(element.get(property)));
        }
        var event = new Event(game, "command", properties);
        game.getAgent(agent).acceptEvent(event);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Application.logger.info("disconnected from mqtt");
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        try {
            var tokens = topic.split("/");
            if(tokens.length == 3 && tokens[1].equals("agent"))
                receiveCommand(mqttMessage.toString(), tokens[2]);
        } catch(Exception e) {
            Application.logger.info(e.toString());
            Application.logger.info(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        var user = (User)authentication.getPrincipal();
        var agent = game.getAgent(user.getUsername(), "player");
        if(agent == null) {
            Application.logger.info("Could not load agent for " + user.getUsername());
            return;
        }
        game.registerListener(agent);
        Application.logger.info(user.getUsername() + " is connected");
        try {
            client.subscribe(game.getId()+"/agent/"+user.getUsername());
        } catch(MqttException e) {
            Application.logger.info("Could not subscribe to " + user.getUsername() + " client commands");
        }
        httpServletResponse.sendRedirect("/game/client");
    }
}