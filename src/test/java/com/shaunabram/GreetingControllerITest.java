package com.shaunabram;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

	@Test
	public void delete_greetings_with_no_id_returns_405() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		HttpEntity entity = new HttpEntity(headers);
		Map<String, String> params = new HashMap<>();
		HttpStatus statusCode = null;
		try {
			String url = "http://localhost:9090/greetings";
			restTemplate.exchange(url, HttpMethod.DELETE, entity, Greeting.class, params);
		} catch (HttpStatusCodeException ex) {
			statusCode = ex.getStatusCode();
		}
		assertThat(statusCode).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
	}

	@Test
	public void delete_greetings_with_valid_id_returns_OK() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		HttpEntity entity = new HttpEntity(headers);
		Map<String, String> params = new HashMap<>();
		params.put("id", CLASSIC_GREETING_ID.toString());
		String url = "http://localhost:9090/greetings/" + "{id}";
		ResponseEntity<Greeting> response = restTemplate.exchange(url, HttpMethod.DELETE, entity, Greeting.class, params);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void post_greetings_with_valid_greeting_returns_OK() {
		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		String content = "contenttest";
		map.add("content", content);
		String url = "http://localhost:9090/greetings";

		Greeting greeting = restTemplate.postForObject(url, map, Greeting.class);

		assertThat(greeting.getContent()).isEqualTo(content);
		assertThat(greeting.getId()).isNotNull();
	}

	@Test
	public void post_greetings_with_no_greeting_returns_400() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		HttpEntity entity = new HttpEntity(headers);
		//note no content added
		String url = "http://localhost:9090/greetings";

		HttpStatus statusCode = null;
		try {
			restTemplate.exchange(url, HttpMethod.POST, entity, Greeting.class);
		} catch (HttpStatusCodeException ex) {
			statusCode = ex.getStatusCode();
		}
		assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);

	}

}
