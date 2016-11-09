package com.waitme.sharding.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.waitme.sharding.db.route.DataSourceRoutingHandler;
import com.waitme.sharding.db.route.EmptyDataSourceRoutingHandler;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceRouting {
	String value();
	Class<? extends DataSourceRoutingHandler> handler() default EmptyDataSourceRoutingHandler.class;
}
