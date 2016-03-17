package com.abedajna.cccmapper.converter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.abedajna.cccmapper.cache.*;
import com.abedajna.cccmapper.cache.CassandraDataObjectMetadataCache.*;
import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache.CompositeClassMetadata;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.PropertySetterAndGetter;
import com.abedajna.cccmapper.util.Triple;

public class ConvertDataObjectToColumnNameObjectsImpl<C extends CompositeColumnName> implements ConvertDataObjectToColumnNameObjects<C> {

	@Autowired
	DomainMetadataCache cache;

	@Autowired
	ConversionService conversionService;

	@Autowired
	PropertySetterAndGetter pgs;

	// please do not create a circular graph using OneToMany!
	@Override
	public List<Triple<C, String, Integer>> convert(Object o, Class<C> classC) {
		List<Triple<C, String, Integer>> lst = new ArrayList<Triple<C, String, Integer>>();
		try {
			convert(o, lst, classC);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ParseException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("conversion failed", e);
		}
		return lst;
	}

	@SuppressWarnings("unchecked")
	private <T extends CassandraDataObject> void convert(Object o, List<Triple<C, String, Integer>> lst, Class<C> classC) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
			SecurityException, ParseException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		Class<T> aClass = (Class<T>) o.getClass();
		CDOClassMetadata m = cache.getCDOClassMetadata(aClass);

		for (Field field : m.attributeFields) {
			if (pgs.getProperty(o, field) != null) {
				lst.add(new Triple<C, String, Integer>(createCompositeColumnNameObject(m, field, o, classC), pgs.getPropertyAsString(o, field), m.ttl));
			}
		}

		for (OneToManyJoinInfo otmMetadata : m.oneToManys) {
			Field field = otmMetadata.otmField;
			Object p = pgs.getProperty(o, field);
			if (p != null) {
				Collection<Object> coll = (Collection<Object>) p;
				for (Object child : coll) {
					convert(child, lst, classC);
				}
			}
		}

	}

	private C createCompositeColumnNameObject(CDOClassMetadata dataObjectMetadata, Field attributeField, Object dataObject, Class<C> classC) throws IllegalArgumentException, IllegalAccessException,
			ParseException, InstantiationException, InvocationTargetException, NoSuchMethodException {

		C compKey = classC.newInstance();

		CompositeClassMetadata ccmetadata = cache.getCompositeClassMetadata(classC);
		List<Object> values = new ArrayList<Object>(ccmetadata.componentDefaultValues);
		values.set(ccmetadata.attributeOrdinal, attributeField.getName());
		for (Key key : dataObjectMetadata.keys) {
			values.set(key.prefixOrdinal, key.prefix);
			values.set(key.valueOrdinal, pgs.getProperty(dataObject, key.f));			
		}

		// use the value array to populate the newly created classC instance
		for (int i = 0; i < ccmetadata.componentFields.size(); i++) {
			Field f = ccmetadata.componentFields.get(i);
			f.setAccessible(true);
			pgs.setProperty(compKey, f, values.get(i));
		}
		return compKey;

	}

}
