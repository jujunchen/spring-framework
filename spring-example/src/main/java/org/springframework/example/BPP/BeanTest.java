package org.springframework.example.BPP;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext =
				new AnnotationConfigApplicationContext("org.springframework.example.BPP");
	}
}
