package com.controllers;

import edu.missouriwestern.csmp.gg.base.*;
import edu.missouriwestern.csmp.gg.base.events.GameStartEvent;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.summary.StatementType;
import org.neo4j.driver.v1.types.Node;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
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

    private static String[] GetPropertyTable(HasProperties object) {
        String[] array = new String[2];
        String properties = "";
        var id = object instanceof Player ? ((Player) object).getID() : "" + ((Entity) object).getID() + "";
        properties += "id:\'" + id + "\'";
        for (Map.Entry properties1 : object.getProperties().entrySet()) {
            properties += String.format(" %s: ", properties1.getKey()) + properties1.getValue() + ",";
        }

        if (properties.endsWith(",")) {
            properties = properties.substring(1, properties.length() - 1);
        }

        String type = "";

        if (object instanceof Player) {
            type = "Player";
        } else {
            type = "Entity";
        }

        array[0] = type;
        array[1] = properties;
        return array;
    }

    protected static void save(HasProperties object) {

        var id = object instanceof Player ? ((Player) object).getID() : "" + ((Entity) object).getID() + "";

        StatementResult ResultStatus = null;
        try {
            ResultStatus = Application.session.run(
                    String.format("MATCH (n { id: \'%s\' })\nReturn n", id)
            );
        }catch(Error e){logger.info(e.toString());}
        if (!ResultStatus.list().isEmpty()){
            logger.info("Found result for id:" + id);
            String[] newTable = GetPropertyTable(object);
            Application.session.run(String.format("MATCH (n { id: \'%s\' })\nSet n = { %s }", id, newTable[1]));
        }else {
            logger.info("Couldn't find result for id:" + id);
            String[] newTable = GetPropertyTable(object);
            logger.info("" + newTable[0] + "\t" + newTable[1]);
            Application.session.run(String.format("CREATE (n:%s { %s })", newTable[0], newTable[1]));
        }
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
