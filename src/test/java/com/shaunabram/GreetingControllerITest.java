package com.shaunabram;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shaunabram.GreetingController.*;

import static org.assertj.core.api.Assertions.assertThat;

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
	public void greetings_returns_at_least_classicGreeting() {
		RestTemplate restTemplate = new RestTemplate();
		List<Greeting> greetings = restTemplate.getForObject("http://localhost:9090/greetings", List.class);
		assertThat(greetings)
				.isNotEmpty()
				.extracting("content")
				.contains(CLASSIC_GREETING_CONTENT);
	}

	@Test
	public void greetings_with_id() {
		RestTemplate restTemplate = new RestTemplate();
		int id = 1;
		Greeting expectedGreeting = new Greeting(id, CLASSIC_GREETING_CONTENT);
		Greeting greeting = restTemplate.getForObject("http://localhost:9090/greetings/" + "{id}", Greeting.class, id);
		assertThat(greeting).isEqualTo(expectedGreeting);

	}

	@Test
	public void greetings_with_id_404() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		HttpEntity entity = new HttpEntity(headers);
		Map<String, String> params = new HashMap<>();
		params.put("id", CLASSIC_GREETING_ID.toString());
		try {
			String url = "http://localhost:9090/greetings/" + "{id}";
			restTemplate.exchange(url, HttpMethod.GET, entity, Greeting.class, params);
		} catch (HttpStatusCodeException ex) {
			HttpStatus statusCode = ex.getStatusCode();
			assertThat(statusCode).isEqualTo(HttpStatus.NOT_FOUND);
		}
	}
}
