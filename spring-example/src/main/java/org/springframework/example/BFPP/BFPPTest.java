package org.springframework.example.BFPP;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class BFPPTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
		annotationConfigApplicationContext.scan("org.springframework.example.BFPP");
		annotationConfigApplicationContext.addBeanFactoryPostProcessor(new E());
		annotationConfigApplicationContext.addBeanFactoryPostProcessor(new E1());
		annotationConfigApplicationContext.refresh();

		System.out.println(annotationConfigApplicationContext.getBean("f"));
	}
}
