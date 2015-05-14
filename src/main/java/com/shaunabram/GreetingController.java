package com.shaunabram;

import org.springframework.boot.autoconfigure.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * GET: 	localhost/greetings 	- return all greeting			[DONE]
 * GET:  	localhost/greetings/1 	- return greeting with id 1		[DONE]
 * PUT: 	localhost/greetings 	- unsupported. (400))			[DONE]
 * PUT: 	localhost/greetings/1 	- update/create greeting 		[DONE]
 * POST:   	localhost/greetings 	- create greeting				[DONE]
 * POST:   	localhost/greetings/1 	- unsupported (400)				[DONE]
 * DELETE: 	localhost/greetings 	- unsupported (405)				[DONE]
 * DELETE: 	localhost/greetings/1 	- delete greeting with id 1		[DONE]
 *
 * In future could support using Strings too
 * e.g. GET:  	localhost/greetings/World - return all greetings containing World
 *
 * Some points worth documenting:
 * 1) Specifying response codes
 * How to specify a response code? I am adding HttpServletResponse servletResponse as a param, then calling servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
 * I think there are other ways (better?)
 *
 *
 */
@Controller
@EnableAutoConfiguration
public class GreetingController {

	public static final String CLASSIC_GREETING_CONTENT = "Hello, World!";
	public static final Integer CLASSIC_GREETING_ID = 1;
	public static final Greeting CLASSIC_GREETING = new Greeting(CLASSIC_GREETING_ID, CLASSIC_GREETING_CONTENT);

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong(CLASSIC_GREETING_ID);

	private List<Greeting> greetings = new ArrayList<>();

	public GreetingController() {
		greetings.add(CLASSIC_GREETING);
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

	@RequestMapping(value ="/greetings/{id}",  method=RequestMethod.GET)
	public @ResponseBody Greeting greetings(@PathVariable("id") long id, HttpServletResponse servletResponse) {
		for (Greeting greeting : greetings) {
			if (greeting.getId() == id) return greeting;
		}
		servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
		return null;
	}

	@RequestMapping(value ="/greetings/{id}",  method=RequestMethod.DELETE)
	public @ResponseBody void deleteGreeting(@PathVariable("id") long id, HttpServletResponse servletResponse) {
		for (Greeting greeting : greetings) {
			if (greeting.getId() == id) {
				greetings.remove(greeting);
				return;
			}
		}
		servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
	}

	@RequestMapping(value ="/greetings",  method=RequestMethod.POST)
	public @ResponseBody Greeting postGreeting(@RequestParam(required=true) String content) {
		Greeting greeting = new Greeting(counter.incrementAndGet(), content);
		greetings.add(greeting);
		return greeting;
	}

	@RequestMapping(value ="/greetings/{id}",  method=RequestMethod.PUT)
	public @ResponseBody Greeting putGreeting(@PathVariable("id") long id, @RequestBody Greeting greeting, HttpServletResponse servletResponse) {
		if (id != greeting.getId()) {
			servletResponse.setStatus(HttpStatus.CONFLICT.value());
			return greeting;
		}
		boolean updated = false;
		for (int i=0; i<greetings.size(); i++) {
			if (greetings.get(i).getId() == id) {
				//existing greeting, updating
				greetings.set(i, greeting);
				updated = true;
				break;
			}
		}
		if (!updated) {
			//new, creating
			greetings.add(greeting);
			servletResponse.setStatus(HttpStatus.CREATED.value());
		}
		return greeting;
	}

}