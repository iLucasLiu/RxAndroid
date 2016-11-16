package com.sunnybear.library.widget.recycler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置ItemViewLayoutId的注解
 * Created by chenkai.gu on 2016/11/7.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemViewLayoutId {

    int value() default 0;
}
