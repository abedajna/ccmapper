package com.abedajna.cccmapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.abedajna.cccmapper.config.*;

import org.springframework.context.annotation.Import;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value={CacheConfig.class, ConverterConfig.class, UtilConfig.class})
public @interface EnableCCCMapper {
}
