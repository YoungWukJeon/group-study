package tacos.bootadmin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class BootAdminServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BootAdminServerApplication.class, args);
	}

}
