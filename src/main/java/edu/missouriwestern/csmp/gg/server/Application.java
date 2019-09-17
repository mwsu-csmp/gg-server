
package edu.missouriwestern.csmp.gg.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication()
@ImportResource({
        "classpath:game-layout.xml",
        "classpath:server-config.xml"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static Application loadMap(String s) {
        return null;
    }

    @Bean("taskExecutor")
    public TaskExecutor getExecutor() {
        return new ThreadPoolTaskExecutor();
    }
}