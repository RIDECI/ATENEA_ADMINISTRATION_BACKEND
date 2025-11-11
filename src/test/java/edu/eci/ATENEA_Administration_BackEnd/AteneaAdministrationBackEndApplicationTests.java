package edu.eci.ATENEA_Administration_BackEnd;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
class AteneaAdministrationBackEndApplicationTests {

	@Test
	void contextLoads() {
	}

}
