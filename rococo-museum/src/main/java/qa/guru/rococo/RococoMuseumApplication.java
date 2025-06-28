package qa.guru.rococo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import qa.guru.rococo.service.PropertiesLogger;

@SpringBootApplication
public class RococoMuseumApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RococoMuseumApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}
