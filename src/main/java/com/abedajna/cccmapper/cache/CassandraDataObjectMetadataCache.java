package com.abedajna.cccmapper.cache;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.abedajna.cccmapper.annotations.*;
import com.abedajna.cccmapper.domain.CassandraDataObject;

public class CassandraDataObjectMetadataCache {

	public static class CDOClassMetadata {
		public final Map<String, Field> allFieldsByName;
		public final List<OneToManyJoinInfo> oneToManys;
		public final Map<String, Field> oneToManyFields;
		public final List<Field> attributeFields;
		public final List<Key> keys;
		public final Integer ttl;

		public CDOClassMetadata(Map<String, Field> allFieldsByName, List<OneToManyJoinInfo> oneToManys, List<Field> attributeFields, List<Key> keys, Map<String, Field> oneToManyFields, Integer ttl) {
			this.allFieldsByName = Collections.unmodifiableMap(new HashMap<String, Field>(allFieldsByName));
			this.oneToManys = Collections.unmodifiableList(new ArrayList<OneToManyJoinInfo>(oneToManys));
			this.oneToManyFields = Collections.unmodifiableMap(oneToManyFields);
			this.attributeFields = Collections.unmodifiableList(new ArrayList<Field>(attributeFields));
			this.keys = Collections.unmodifiableList(new ArrayList<Key>(keys));
			this.ttl = ttl;
		}
	}

	public static class OneToManyJoinInfo {
		public final Field otmField;
		public final Class<CassandraDataObject> manyClass;

		public OneToManyJoinInfo(Field otmField, Class<CassandraDataObject> manyClass) {
			this.otmField = otmField;
			this.manyClass = manyClass;
		}
	}

	public static class Key {
		public final String prefix;
		public final Integer prefixOrdinal;
		public final Integer valueOrdinal;
		public final Field f;

		public Key(String prefix, Integer prefixOrdinal, Integer valueOrdinal, Field f) {
			this.prefix = prefix;
			this.prefixOrdinal = prefixOrdinal;
			this.valueOrdinal = valueOrdinal;
			this.f = f;
		}

	}

	private ConcurrentMap<String, CDOClassMetadata> metadataMap = new ConcurrentHashMap<String, CDOClassMetadata>();

	@SuppressWarnings("unchecked")
	public <T extends CassandraDataObject> CDOClassMetadata getClassMetadata(Class<T> aClass) {
		try {

			if (!metadataMap.containsKey(aClass.getName())) {
				
				Integer ttl = aClass.getAnnotation(TTL.class) != null ? aClass.getAnnotation(TTL.class).value() : 0;

				Map<String, Field> allFieldsByName = new HashMap<String, Field>();
				List<OneToManyJoinInfo> oneToManys = new ArrayList<OneToManyJoinInfo>();
				Map<String, Field> oneToManyFields = new HashMap<String, Field>();
				List<Field> attributeFields = new ArrayList<Field>();
				List<Key> keys = new ArrayList<Key>();

				for (Field f : getAllFields(aClass)) {

					allFieldsByName.put(f.getName(), f);

					f.setAccessible(true);

					if (f.getAnnotation(Transient.class) == null && f.getAnnotation(OneToMany.class) == null) {
						attributeFields.add(f);
					}

					OneToMany otm = f.getAnnotation(OneToMany.class);
					if (otm != null) {

						Field otmField = null;
						Class<CassandraDataObject> manyClass = null;

						otmField = f;
						manyClass = (Class<CassandraDataObject>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];

						oneToManys.add(new OneToManyJoinInfo(otmField, manyClass));

						oneToManyFields.put(manyClass.getName(), otmField);
					}

					CompositeKeyWithOrdinal k = f.getAnnotation(CompositeKeyWithOrdinal.class);
					if (k != null) {

						String prefix = "";
						Integer prefixOrdinal = -1;
						Integer valueOrdinal = -1;
						Field fk = null;

						fk = f;
						valueOrdinal = k.ordinal();
						prefixOrdinal = valueOrdinal - 1;
						prefix = k.prefix();
						keys.add(new Key(prefix, prefixOrdinal, valueOrdinal, fk));
					}

					Collections.sort(keys, (Key k1, Key k2) -> Integer.compare(k1.valueOrdinal, k2.valueOrdinal));

				}

				CDOClassMetadata m = new CDOClassMetadata(allFieldsByName, oneToManys, attributeFields, keys, oneToManyFields, ttl);

				metadataMap.putIfAbsent(aClass.getName(), m);
			}
			return metadataMap.get(aClass.getName());

		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}

	}

	private List<Field> getAllFields(Class<?> aClass) {
		List<Field> lst = new ArrayList<Field>();
		while (aClass != null) {
			lst.addAll(Arrays.asList(aClass.getDeclaredFields()));
			aClass = aClass.getSuperclass();
		}
		return lst;
	}
}
