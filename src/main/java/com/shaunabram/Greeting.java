package com.shaunabram;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Greeting {

	private final int id;
	private final String content;

	//Using @JsonProperty to avoid the need for an empty, default constructor
	//see
	// http://stackoverflow.com/questions/27928588/post-to-spring-mvc-controller-results-in-httpmessagenotreadableexception-could
	// http://stackoverflow.com/questions/7625783/jsonmappingexception-no-suitable-constructor-found-for-type-simple-type-class
	public Greeting(@JsonProperty("name")int id, @JsonProperty("content")String content) {
		this.id = id;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public String getContent() {
		return content;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Greeting greeting = (Greeting) o;

		if (id != greeting.id) return false;
		if (content != null ? !content.equals(greeting.content) : greeting.content != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (content != null ? content.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Greeting{" +
				"id=" + id +
				", content='" + content + '\'' +
				'}';
	}
}