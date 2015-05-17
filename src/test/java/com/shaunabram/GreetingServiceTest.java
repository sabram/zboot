package com.shaunabram;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static com.shaunabram.GreetingService.*;

public class GreetingServiceTest {

	GreetingService service = new GreetingService();

	@Test
	public void getGreeting() {
		Greeting greeting = service.getGreeting(CLASSIC_GREETING_ID);
		assertThat(greeting).isEqualTo(CLASSIC_GREETING);
	}

	@Test
	public void removeGreeting() {
		int greetingId = CLASSIC_GREETING_ID;
		Greeting removedGreeting = service.removeGreeting(greetingId);
		assertThat(removedGreeting).isEqualTo(CLASSIC_GREETING);
	}

	@Test
	public void addGreeting() {
		int newID = 2;
		Greeting newGreeting = new Greeting(newID, "anything");
		service.addGreeting(newGreeting);
		Greeting retrievedGreeting = service.getGreeting(newID);
		assertThat(retrievedGreeting).isEqualTo(newGreeting);
	}

	@Test
	public void getAllGreetings() {
		List<Greeting> greetings = service.getAllGreetings();
		assertThat(greetings).contains(CLASSIC_GREETING);
	}

	@Test
	public void createGreeting() {
		Greeting newGreeting = service.createGreeting(CLASSIC_GREETING_CONTENT);
		assertThat(newGreeting).isNotNull();
		assertThat(newGreeting.getId()).isNotNull();
		assertThat(newGreeting.getContent()).isEqualTo(CLASSIC_GREETING_CONTENT);
	}
}
