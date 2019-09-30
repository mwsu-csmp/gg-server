
package edu.missouriwestern.csmp.gg.server;

import edu.missouriwestern.csmp.gg.base.Board;
import edu.missouriwestern.csmp.gg.base.Event;
import edu.missouriwestern.csmp.gg.base.EventListener;
import edu.missouriwestern.csmp.gg.base.Game;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.logging.Logger;

@SpringBootApplication()
@ImportResource({
        "classpath:game-layout.xml",
        "classpath:server-config.xml"
})
public class Application {

    private static Logger logger = Logger.getLogger(Application.class.getCanonicalName());

    private Game game;

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @Bean("taskExecutor")
    public TaskExecutor getExecutor() {
        return new ThreadPoolTaskExecutor();
    }


    /** loads boards at start of server */
    @org.springframework.context.event.EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        var maps = event.getApplicationContext().getBeansOfType(Board.class);
        var propagator = event.getApplicationContext().getBean("event-propagator");
        for(var mapName : maps.keySet()) {
            var map = maps.get(mapName);
            map.getGame().addBoard(mapName, map);
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
            for (var listener : listeners.values()) {
                if(listener == propagator) continue; // avoid creating feedback loop
                game.registerListener(listener);
            }
            game.propagateEvent(new Event(game, "game-start"));     // indicate game is starting
        }
    }
}