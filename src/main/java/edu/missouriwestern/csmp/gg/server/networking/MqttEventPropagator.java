package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.server.Application;
import org.eclipse.paho.client.mqttv3.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MqttEventPropagator implements EventListener, MqttCallback {

    private IMqttClient client;
    private Game game;
    private Set<EventListener> listeners = ConcurrentHashMap.newKeySet();

    public void setMqttClient(IMqttClient client, Game game) throws MqttException {
        this.game = game;
        this.client = client;
        this.client.subscribe(game.getId());
        this.client.setCallback(this);
    }

    public void acceptEvent(Event e) {
        try {
            if(client != null) {
                client.publish(e.getGame().getId(), new MqttMessage(e.toString().getBytes(StandardCharsets.UTF_8)));
            }
        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
        }
    }

    public Consumer<EventListener> registerEventReceiver() {
        return listener -> {
            listeners.add(listener);
        };
    }

    @Override
    public void connectionLost(Throwable throwable) {
        Application.logger.info("disconnected from mqtt");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        try {
            var e = Event.fromJson(game, mqttMessage.toString());
            for (var listener : listeners)
                listener.acceptEvent(e);
        } catch(Exception e) {
            Application.logger.info(e.toString());
            Application.logger.info(Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
