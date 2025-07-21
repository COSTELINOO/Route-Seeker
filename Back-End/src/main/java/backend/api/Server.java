package backend.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Server {
    public static void main(String[] args) {
        System.setProperty("spring.output.ansi.enabled", "ALWAYS");
        SpringApplication.run(Server.class, args);
    }
}
