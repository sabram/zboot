package com.shaunabram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GreetingService {

	public static final String CLASSIC_GREETING_CONTENT = "Hello, World!";
	public static final Integer CLASSIC_GREETING_ID = 1;
	public static final Greeting CLASSIC_GREETING = new Greeting(CLASSIC_GREETING_ID, CLASSIC_GREETING_CONTENT);

	private final AtomicInteger counter = new AtomicInteger(CLASSIC_GREETING_ID);

	private Map<Integer, Greeting> greetings = new HashMap<>();

	public GreetingService() {
		greetings.put(CLASSIC_GREETING_ID, CLASSIC_GREETING);
	}

	public Greeting getGreeting(int greetingId) {
		return greetings.get(greetingId);
	}

	public Greeting removeGreeting(int greetingId) {
		return greetings.remove(greetingId);
	}

	public void addGreeting(Greeting greeting) {
		greetings.put(greeting.getId(), greeting);
	}

	public List<Greeting> getAllGreetings() {
		return new ArrayList<>(greetings.values());
	}

	public Greeting createGreeting(String greetingContent) {
		Greeting createdGreeting = new Greeting(counter.incrementAndGet(), greetingContent);
		addGreeting(createdGreeting);
		return createdGreeting;
	}
}
