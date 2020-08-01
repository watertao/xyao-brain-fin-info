package io.github.watertao.xyao.infras;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Instruction {

  MessageEnvironmentEnum msgEnv();

  boolean masterOnly();

  String syntax() default "";

  String description() default "";

}
