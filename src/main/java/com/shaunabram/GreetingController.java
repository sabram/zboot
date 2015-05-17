package com.shaunabram;

import org.springframework.boot.autoconfigure.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

	private GreetingService service = new GreetingService();

	@RequestMapping(value ="/greetings",  method=RequestMethod.GET)
	public @ResponseBody List<Greeting> greetings() {
		return service.getAllGreetings();
	}

	@RequestMapping(value ="/greetings/{id}",  method=RequestMethod.GET)
	public @ResponseBody Greeting greetings(@PathVariable("id") int id, HttpServletResponse servletResponse) {
		Greeting greeting = service.getGreeting(id);
		if (greeting == null) {
			servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
		return greeting;
	}

	@RequestMapping(value ="/greetings/{id}",  method=RequestMethod.DELETE)
	public @ResponseBody void deleteGreeting(@PathVariable("id") int id, HttpServletResponse servletResponse) {
		Greeting removedGreeting = service.removeGreeting(id);
		if (removedGreeting == null) {
			servletResponse.setStatus(HttpStatus.NOT_FOUND.value());
		}
	}

	@RequestMapping(value ="/greetings",  method=RequestMethod.POST)
	public @ResponseBody Greeting postGreeting(@RequestParam(required=true) String content) {
		return service.createGreeting(content);
	}

	@RequestMapping(value ="/greetings/{id}",  method=RequestMethod.PUT)
	public @ResponseBody Greeting putGreeting(@PathVariable("id") int id, @RequestBody Greeting greeting, HttpServletResponse servletResponse) {
		if (id != greeting.getId()) {
			servletResponse.setStatus(HttpStatus.CONFLICT.value());
			return greeting;
		}
		Greeting existingGreeting = service.getGreeting(id);
		if (existingGreeting != null) {
			//existing, updating
			service.removeGreeting(id);
			service.addGreeting(greeting);
		} else {
			//new, creating
			service.addGreeting(greeting);
			servletResponse.setStatus(HttpStatus.CREATED.value());
		}
		return greeting;
	}

}