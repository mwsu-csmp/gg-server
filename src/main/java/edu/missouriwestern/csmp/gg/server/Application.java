
package edu.missouriwestern.csmp.gg.server;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.server.controllers.PlayerController;
import edu.missouriwestern.csmp.gg.server.networking.MqttEventPropagator;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.logging.Logger;

@SpringBootApplication()
@ImportResource({
        "classpath:game-layout.xml",
        "classpath:server-config.xml"
})
@ConfigurationProperties(prefix = "gg")
public class Application {

    public static Logger logger = Logger.getLogger(Application.class.getCanonicalName());

    private Game game;

    private String mqttUri="tcp://localhost";  // TODO: draw from properties file
    private String mqttClientId="server";

    private IMqttClient mqttClient;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean("taskExecutor")
    public TaskExecutor getExecutor() {
        return new ThreadPoolTaskExecutor();
    }


    /** loads boards at start of server */
    @org.springframework.context.event.EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) throws MqttException {

        // connect to MQTT server
        var mqttFactory = (MqttPahoClientFactory)event.getApplicationContext().getBean("mqtt-connection-factory");
        mqttClient = mqttFactory.getClientInstance(mqttUri, mqttClientId);
        mqttClient.connect();
        if(!mqttClient.isConnected()) throw new RuntimeException("could not connect to mqtt broker");
        logger.info("Successfully connected to MQTT Broker");

        var maps = event.getApplicationContext().getBeansOfType(Board.class);
        var propagator = (MqttEventPropagator)event.getApplicationContext().getBean("event-propagator");
        var playerController = (PlayerController)event.getApplicationContext().getBean(PlayerController.class);
        for(var mapName : maps.keySet()) {
            var map = maps.get(mapName);
            map.getGame().addBoard(map);
            logger.info("loading map " + mapName + ": \n");
            logger.info(""+map.getGame().getBoard(mapName));
        }

        // register global listeners with all games
        var games = event.getApplicationContext().getBeansOfType(Game.class);
        var listeners = event.getApplicationContext().getBeansOfType(EventListener.class);
        for(var gameName : games.keySet()) {
            logger.info("Starting game " + gameName);
            if(game != null) throw new RuntimeException("Only one game at a time currently supported");
            this.game = games.get(gameName);
            propagator.setMqttClient(mqttClient, game);
            playerController.setMqttClient(mqttClient, game);
            for (var listener : listeners.values()) {
                if(listener == propagator) continue; // avoid creating feedback loop
                game.registerListener(listener);
            }
            game.propagateEvent(new Event(game, "game-start"));     // indicate game is starting
        }
    }
}