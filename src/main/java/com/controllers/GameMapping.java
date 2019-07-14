package com.controllers;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Entity;
import edu.missouriwestern.csmp.gg.base.Game;
import edu.missouriwestern.csmp.gg.base.HasProperties;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component("game")
public class GameMapping extends Game {

    private static Logger logger = Logger.getLogger(GameMapping.class.getCanonicalName());

    private TaskExecutor taskExecutor;

    protected static void save(HasProperties object) {
        String properties = "";
        for(Map.Entry properties1: object.getProperties().entrySet()){
            properties += String.format(" %s: ", properties1.getKey()) + properties1.getValue() + ",";
        }

        if (properties.endsWith(",")){
            properties = properties.substring(1, properties.length() - 1);
        }

        String type = "";

        if (object instanceof PlayerController){
            type = "Player";
        } else {
            type = "Entity";
        }

        Application.session.run(
                String.format("CREATE (ns:% { %s })", type, properties)
        );
    }

    public GameMapping() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
    }

    @Override
    public void addEntity(Entity ent) {
        super.addEntity(ent);
        if(ent instanceof Runnable) {
            logger.info("starting thread for executable entity " + ent.getID());
            taskExecutor.execute((Runnable)ent);
        }
    }

    /** loads boards at start of server */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        var maps = event.getApplicationContext().getBeansOfType(Board.class);
        for(var mapName : maps.keySet()) {
            this.addBoard(mapName, maps.get(mapName));
            logger.info("loading map " + mapName + ": \n" + maps.get(mapName));
        }
        registerListener(new EventLogger());  // log all events
        accept(new GameStartEvent(this));     // indicate game is starting
    }

    /** loads a text file resource as a string */
    public static String loadMap(String mapFileName) throws IOException {
        var mapString = new BufferedReader(new InputStreamReader(
                GameMapping.class.getClassLoader()
                        .getResourceAsStream(mapFileName)))
                .lines().collect(Collectors.joining("\n"));
        return mapString;
    }



}
