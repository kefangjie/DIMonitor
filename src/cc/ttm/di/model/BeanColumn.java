package cc.ttm.di.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanColumn {

    String name() default "";  //在JTable中的表头名字
    int index()default 0;  //在表头的索引
}