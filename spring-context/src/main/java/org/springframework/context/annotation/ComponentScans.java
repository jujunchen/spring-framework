/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Container annotation that aggregates several {@link ComponentScan} annotations.
 *
 * <p>Can be used natively, declaring several nested {@link ComponentScan} annotations.
 * Can also be used in conjunction with Java 8's support for repeatable annotations,
 * where {@link ComponentScan} can simply be declared several times on the same method,
 * implicitly generating this container annotation.
 * <p>
 * 聚合多个{@link ComponentScan}注释的容器注释
 * <p>
 *可以声明几个嵌套的{@link ComponentScan}注释。还可以与Java 8对可重复注释的支持一起使用，
 * 其中{@link ComponentScan}可以简单地在同一方法上声明多次，隐式地生成这个容器注释。
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see ComponentScan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ComponentScans {

	ComponentScan[] value();

}
