/* Current project structure:
 * each input/output classes have their own package.
 * KeyboardInput reads keyboard input and PlayerInfo holds
 * x coordinate (soon to also hold y).
 *
 * Each Controller Class logs what the class is for when it is called.
 * (i.e Player controller logs it's getting player position)
 *
 * TODO: Current issue: Application and WebsocketConfig must be in the same
 * TODO: File. Otherwise the client will throw a 404 when connecting, or
 * TODO: the server will fail to receive any information.
 *
 */


package Init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}