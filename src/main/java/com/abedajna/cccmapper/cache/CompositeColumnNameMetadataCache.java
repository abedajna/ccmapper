package com.abedajna.cccmapper.cache;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.abedajna.cccmapper.annotations.*;
import com.abedajna.cccmapper.domain.CompositeColumnName;

public class CompositeColumnNameMetadataCache {

	@Autowired
	ConversionService conversionService;

	public static class CompositeClassMetadata {
		// lists sorted by ordinal
		public final List<Field> componentFields;
		public final List<Integer> columnKeyOrdinals;
		public final List<Integer> columnValueOrdinals;
		public final List<Object> componentDefaultValues;
		public final List<String> keyDefaultStrings;
		public final Field attributeField;
		public final int attributeOrdinal;
		public final String columnFamilyName;
		public final String keySerializerName;


		public CompositeClassMetadata(List<Field> componentFields, List<Integer> columnKeyOrdinals, List<Integer> columnValueOrdinals, List<Object> componentDefaultValues,
				List<String> keyDefaultStrings, Field attributeField, int attributeOrdinal, String columnFamilyName, String keySerializerName) {
			this.componentFields = Collections.unmodifiableList(new ArrayList<Field>(componentFields));
			this.columnKeyOrdinals = Collections.unmodifiableList(new ArrayList<Integer>(columnKeyOrdinals));
			this.columnValueOrdinals = Collections.unmodifiableList(new ArrayList<Integer>(columnValueOrdinals));
			this.componentDefaultValues = Collections.unmodifiableList(new ArrayList<Object>(componentDefaultValues));
			this.keyDefaultStrings = Collections.unmodifiableList(new ArrayList<String>(keyDefaultStrings));
			this.attributeField = attributeField;
			this.attributeOrdinal = attributeOrdinal;
			this.columnFamilyName = columnFamilyName;
			this.keySerializerName = keySerializerName;
		}

	}

	private ConcurrentMap<String, CompositeClassMetadata> metadataMap = new ConcurrentHashMap<String, CompositeClassMetadata>();

	public <C extends CompositeColumnName> CompositeClassMetadata getClassMetadata(Class<C> aClass) {

		if (!metadataMap.containsKey(aClass.getName())) {

			ColumnFamily cf = aClass.getAnnotation(ColumnFamily.class);
			String columnFamilyName = cf.name();
			String keySerializerName = cf.keySeralizer();

			List<Field> componentFields = new ArrayList<Field>();
			List<Integer> columnKeyOrdinals = new ArrayList<Integer>();
			List<Integer> columnValueOrdinals = new ArrayList<Integer>();
			List<Object> componentDefaultValues = new ArrayList<Object>();
			List<String> keyDefaultStrings = new ArrayList<String>();
			Field attributeField = null;
			int attributeOrdinal = -1;

			// get a list of "Component" fields sorted by ordinal
			for (java.lang.reflect.Field f : aClass.getDeclaredFields()) {
				f.setAccessible(true);
				if (f.getAnnotation(com.netflix.astyanax.annotations.Component.class) != null) {
					componentFields.add(f);
				}
			}
			Collections.sort(
					componentFields,
					(Field o1, Field o2) -> Integer.compare(o1.getAnnotation(com.netflix.astyanax.annotations.Component.class).ordinal(),
							o2.getAnnotation(com.netflix.astyanax.annotations.Component.class).ordinal()));

			// now populate fields in CompositeClassMetadata object
			for (int ordinal = 0; ordinal < componentFields.size(); ordinal++) {

				Field f = componentFields.get(ordinal);

				Default def = f.getAnnotation(Default.class);
				componentDefaultValues.add(def == null ? "NONE" : def.value());

				if (f.getAnnotation(ColumnKey.class) != null) {
					columnKeyOrdinals.add(ordinal);
				}

				if (f.getAnnotation(ColumnValue.class) != null) {
					columnValueOrdinals.add(ordinal);
				}

				if (f.getAnnotation(Attribute.class) != null) {
					attributeField = f;
					attributeOrdinal = ordinal;
				}

			}

			for (int i = 0; i < componentDefaultValues.size(); i++) {
				if (i != attributeOrdinal) {
					keyDefaultStrings.add(conversionService.convert(componentDefaultValues.get(i), String.class));
				}
			}

			CompositeClassMetadata m = new CompositeClassMetadata(componentFields, columnKeyOrdinals, columnValueOrdinals, componentDefaultValues, keyDefaultStrings, attributeField, attributeOrdinal, columnFamilyName, keySerializerName);

			metadataMap.putIfAbsent(aClass.getName(), m);
		}
		return metadataMap.get(aClass.getName());
	}
}
