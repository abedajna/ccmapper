package com.abedajna.cccmapper.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;

import com.abedajna.cccmapper.annotations.StoreAsJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertySetterAndGetter {

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ConversionService conversionService;
	
	@Value("${use.org.apache.commons.beanutils.PropertyUtils:false}")
	Boolean usePropertyUtils;


	
	public void setProperty(Object o, Field property, Object s) {
		try {
			if (usePropertyUtils)
				PropertyUtils.setProperty(o, property.getName(), conversionService.convert(s, property.getType()));
			else
				property.set(o, conversionService.convert(s, property.getType()));
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object getProperty(Object o, Field property) {
		try {
			return usePropertyUtils ? PropertyUtils.getProperty(o, property.getName()) : property.get(o); 
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public void setPropertyFromString(Object o, Field property, String s) {
		try {
			if (usePropertyUtils) {
				if (property.getAnnotation(StoreAsJson.class) != null) {
					PropertyUtils.setProperty(o, property.getName(), objectMapper.readValue(s, property.getType()));
				} else {
					PropertyUtils.setProperty(o, property.getName(), conversionService.convert(s, property.getType()));				
				}				
			} else {
				if (property.getAnnotation(StoreAsJson.class) != null) {
					property.set(o, objectMapper.readValue(s, property.getType()));
					PropertyUtils.setProperty(o, property.getName(), objectMapper.readValue(s, property.getType()));
				} else {
					property.set(o, conversionService.convert(s, property.getType()));				
				}				
			}

		} catch (IOException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public String getPropertyAsString(Object o, Field property) {
		try {
			if (usePropertyUtils) {
				Object val = PropertyUtils.getProperty(o, property.getName());
				if (property.getAnnotation(StoreAsJson.class) != null) {
					return objectMapper.writeValueAsString(val);
				} else {
					return conversionService.convert(val, String.class);
				}
			} else {
				Object val = property.get(o);
				if (property.getAnnotation(StoreAsJson.class) != null) {
					return objectMapper.writeValueAsString(val);
				} else {
					return conversionService.convert(val, String.class);
				}				
			}

		} catch (JsonProcessingException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}
