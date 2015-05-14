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

	@Test
	//Spring RestTemplate.getForObject doesn't play well with List return types
	//http://stackoverflow.com/questions/8509060/how-do-i-avoid-compiler-warnings-when-generic-type-information-is-unavailable
	@SuppressWarnings({"unchecked"})
	public void greetings_returns_at_least_classicGreeting() {
		RestTemplate restTemplate = new RestTemplate();
		List<Greeting> greetings = (List<Greeting>)restTemplate.getForObject("http://localhost:9090/greetings", List.class);
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

	@Test
	public void put_greetings_with_new_greeting_returns_CREATED() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		Integer id = 100;
		Greeting greeting = new Greeting(id, "newGreeting");
		HttpEntity<Greeting> entity = new HttpEntity<>(greeting, headers);
		String url = "http://localhost:9090/greetings/" + id;

		Map<String, String> params = new HashMap<>();
		ResponseEntity<Greeting> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Greeting.class, params);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Greeting returnedGreeting = response.getBody();
		System.out.println("returnedGreeting = " + returnedGreeting);
		assertThat(returnedGreeting).isEqualTo(greeting);
	}

	@Test
	public void put_greetings_with_updated_greeting_returns_OK() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		final int existingGreetingID = CLASSIC_GREETING_ID;
		Greeting greeting = new Greeting(existingGreetingID, "updatedGreeting");
		HttpEntity<Greeting> entity = new HttpEntity<>(greeting, headers);
		String url = "http://localhost:9090/greetings/" + existingGreetingID;

		Map<String, String> params = new HashMap<>();
		ResponseEntity<Greeting> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Greeting.class, params);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		Greeting returnedGreeting = response.getBody();
		System.out.println("returnedGreeting = " + returnedGreeting);

		//hack to reset the CLASSIC_GREETING (Could probably better do via a Service or Dao call later)
		greeting = new Greeting(existingGreetingID, CLASSIC_GREETING_CONTENT);
		entity = new HttpEntity<>(greeting, headers);
		restTemplate.exchange(url, HttpMethod.PUT, entity, Greeting.class, params);
	}

	@Test
	public void put_greetings_with_inconsistent_greeting_id_returns_CONFLICT() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		Integer id = 100;
		Greeting greeting = new Greeting(id, "newGreeting");
		HttpEntity<Greeting> entity = new HttpEntity<>(greeting, headers);
		Integer conflictingID = id+1;
		String url = "http://localhost:9090/greetings/" + conflictingID;
		HttpStatus statusCode = null;
		try {
			restTemplate.exchange(url, HttpMethod.PUT, entity, Greeting.class);
		} catch (HttpStatusCodeException ex) {
			statusCode = ex.getStatusCode();
		}
		assertThat(statusCode).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	public void put_greetings_with_missing_greeting_returns_BAD_REQUEST() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");
		HttpEntity<Greeting> entity = new HttpEntity<>(headers); //Note no greeting included
		Integer anyId = 100;
		String url = "http://localhost:9090/greetings/" + anyId;
		HttpStatus statusCode = null;
		try {
			restTemplate.exchange(url, HttpMethod.PUT, entity, Greeting.class);
		} catch (HttpStatusCodeException ex) {
			statusCode = ex.getStatusCode();
		}
		assertThat(statusCode).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}
