package org.springframework.example.BFPP;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class F2 {

	public F2() {
		System.out.println("F2 create");
	}

	@Bean
	public F3 f3() {
		System.out.println("@Bean F3");
		return new F3();
	}

}
