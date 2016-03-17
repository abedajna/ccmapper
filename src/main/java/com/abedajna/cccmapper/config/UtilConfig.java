package com.abedajna.cccmapper.config;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import com.abedajna.cccmapper.util.PropertySetterAndGetter;

/**
 * SpringConfig for utility beans
 */
@Configuration
public class UtilConfig {

	@Bean
	public PropertySetterAndGetter propertySetterAndGetter() {
		return new PropertySetterAndGetter();
	}

	@Bean
	ConversionService conversionService() {
		DefaultConversionService c = new DefaultConversionService();
		c.addConverter(new Java8ZonedDateTimeToStringConverter());
		c.addConverter(new StringToJava8ZonedDateTimeConverter());
		return c;
	}
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	

}

class Java8ZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String> {

	@Override
	public String convert(ZonedDateTime source) {
		return source.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

}

class StringToJava8ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

	@Override
	public ZonedDateTime convert(String source) {
		return ZonedDateTime.parse(source, DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

}
