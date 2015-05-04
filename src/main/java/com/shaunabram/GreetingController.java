package com.shaunabram;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GET: 	localhost/greetings - return all greeting
 * GET:  	localhost/greetings/1 - return greeting with id 1
 * PUT: 	localhost/greetings - unsupported. error? (code?)
 * PUT: 	localhost/greetings/1 - update greeting with id 1
 * POST:   	localhost/greetings - create greeting (enclosed greeting should have no id, error if it does?)
 * POST:   	localhost/greetings/1 - unsupported. error? (code?)
 * DELETE: 	localhost/greetings - unsupported. error? (code?)
 * DELETE: 	localhost/greetings/1 - delete greeting with id 1
 *
 * In future could support using Strings too
 * e.g. GET:  	localhost/greetings/World - return all greetings containing World
 */
@Controller
@EnableAutoConfiguration
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	public static final String CLASSIC_GREETING = "Hello, World!";

	private List<Greeting> greetings = new ArrayList<>();

	public GreetingController() {
		Greeting g1 = new Greeting(1, CLASSIC_GREETING);
		greetings.add(g1);
	}

	@RequestMapping("/greeting")
	public @ResponseBody Greeting greeting(
			@RequestParam(value="name", required=false, defaultValue="World") String name) {
		return new Greeting(counter.incrementAndGet(),
				String.format(template, name));
	}

	@RequestMapping(value ="/greetings",  method=RequestMethod.GET)
	public @ResponseBody List<Greeting> greetings() {
		return greetings;
	}

}