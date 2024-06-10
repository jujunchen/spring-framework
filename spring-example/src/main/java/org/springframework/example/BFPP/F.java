package org.springframework.example.BFPP;

import org.springframework.stereotype.Component;

@Component
public class F {
	private String name;

	@Override
	public String toString() {
		return "F{" +
				"name='" + name + '\'' +
				'}';
	}
}
