/* Current project structure:
 * Package Init_Controllers holds the code that is called on
 * program start up and also the controllers.
 *
 * KeyboardInput and PlayerInfo packages were left containing
 * Keyboard and Player respectively as more might be added.
 *

 */


package Init_Controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}