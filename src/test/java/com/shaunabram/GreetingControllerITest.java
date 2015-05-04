package com.shaunabram;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import sun.jvm.hotspot.debugger.Page;

import java.util.List;
import static com.shaunabram.GreetingController.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WebConfig.class)
@WebIntegrationTest("server.port:9090")
public class GreetingControllerITest {

//	private final Condition<String> jedi = new Condition<String>("jedi") {
//		private final Set<String> jedis = newLinkedHashSet("Luke", "Yoda", "Obiwan");
//		@Override
//		public boolean matches(String value) {
//			return jedis.contains(value);
//		}
//	};

	@Test
	public void canFetchMickey() {
//		Integer id = mickey.getId();

		RestTemplate restTemplate = new RestTemplate();
		List<Greeting> greetings = restTemplate.getForObject("http://localhost:9090/greetings", List.class);
		assertThat(greetings)
				.isNotEmpty()
				.extracting("content")
				.contains(CLASSIC_GREETING);
	}
}
