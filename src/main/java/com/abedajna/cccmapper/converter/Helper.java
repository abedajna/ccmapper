package com.abedajna.cccmapper.converter;

import java.lang.reflect.Field;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.abedajna.cccmapper.cache.DomainMetadataCache;
import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache.CompositeClassMetadata;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.Pair;
import com.abedajna.cccmapper.util.PropertySetterAndGetter;

public class Helper<T extends CassandraDataObject, C extends CompositeColumnName> {

	@Autowired
	DomainMetadataCache cache;

	@Autowired
	PropertySetterAndGetter pgs;

	@Autowired
	ConversionService conversionService;

	// 0:P:2:IG:4:IG: (Product), 0:P:2:V:4:IG: (Version), 0:P:2:V:4:M: (Milestone)
	public String emitCDOClassIdentifierString(C c) throws IllegalArgumentException, IllegalAccessException {
		CompositeClassMetadata m = cache.getCompositeClassMetadata(c.getClass());
		StringBuilder sb = new StringBuilder();
		for (int ordinal : m.columnKeyOrdinals) {
			sb.append(ordinal).append(":").append(conversionService.convert(pgs.getProperty(c, m.componentFields.get(ordinal)), String.class)).append(":");			
		}
		return sb.toString();
	}

	// 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:IG:3:-1:4:IG:5:-1: (Product)
	// 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:IG:5:-1: (Version)
	// 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:M:5:111: (Milestone)
	public String emitCDOId(C c) throws IllegalArgumentException, IllegalAccessException {
		CompositeClassMetadata m = cache.getCompositeClassMetadata(c.getClass());
		StringBuilder sb = new StringBuilder();
		for (int ordinal = 0; ordinal < m.componentFields.size(); ordinal++) {
			if (ordinal != m.attributeOrdinal) {
				sb.append(ordinal).append(":").append(conversionService.convert(pgs.getProperty(c, m.componentFields.get(ordinal)), String.class)).append(":");
			}
		}
		return sb.toString();
	}

	// create a hashmap (key: id, value: cdo)
	public Map<String, T> constructCDOs(List<Pair<C, String>> lst) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		Map<String, T> objects = new HashMap<String, T>();
		for (Pair<C, String> pair : lst) {
			// identify the cdo class
			Class<T> billingDomainClass = (Class<T>) cache.identifyCDOClass(pair.u.getClass(), emitCDOClassIdentifierString(pair.u));
			// store the object in a hashmap, if not already exists
			String id = emitCDOId(pair.u);
			if (!objects.containsKey(id)) {
				objects.put(id, billingDomainClass.newInstance());
			}
			// set the object field value
			T cdo = objects.get(id);
			String cdoFieldName = pgs.getProperty(pair.u, cache.getCompositeClassMetadata(pair.u.getClass()).attributeField).toString();
			
			Field cdoField = cache.getCDOClassMetadata(billingDomainClass).allFieldsByName.get(cdoFieldName);
			cdoField.setAccessible(true);
			pgs.setPropertyFromString(cdo, cdoField, pair.v);
		}
		return objects;
	}

	/*
	 * iterate through objects. foreach ->
	 * if class matches classCDO, put them in a list. This list will be returned eventually.
	 * if class has a parent class, derive the id of the parent class. Locate the parent class
	 * object using the id and put this object in the parent's collection. 
	 */
	@SuppressWarnings("unchecked")
	public List<T> join(Map<String, T> objects, Class<T> classCDOToReturn, Class<C> classCco)
			throws IllegalArgumentException, IllegalAccessException, ClassNotFoundException {

		List<T> ret = new ArrayList<T>();
		Map<String, String> manyToOneClassMap = cache.getManyToOneClassMap();

		for (String id : objects.keySet()) {

			T cdo = objects.get(id);

			String cdoClassName = cdo.getClass().getName();

			if (cdoClassName.equals(classCDOToReturn.getName())) {
				ret.add((T) cdo);
			}

			if (manyToOneClassMap.containsKey(cdoClassName)) {
				String parentClassName = manyToOneClassMap.get(cdoClassName);
				String parentId = getParentId((Class<T>) Class.forName(parentClassName), classCco, id);
				T parent = objects.get(parentId);
				if (parent != null) {
					Field parentToManyCollectionField = cache.getCDOClassMetadata(parent.getClass()).oneToManyFields.get(cdoClassName);
					((Collection<Object>) parentToManyCollectionField.get(parent)).add(cdo);
				}
			}

		}

		return ret;
	}

	// 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:IG:5:-1: (Version) -> 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:IG:3:-1:4:IG:5:-1: (Product)
	// 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:M:5:111: (Milestone) -> 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:IG:5:-1: (Version)
	public String getParentId(Class<T> parentClass, Class<C> classCco, String childId) {
		CompositeClassMetadata m = cache.getCompositeClassMetadata(classCco);

		// get a list with defaults for all composite keys & values, then,
		// NONE,NONE,IG,-1,IG,-1  --> 0,NONE,1,NONE,2,IG,3,-1,4,IG,5,-1 
		String[] defaults = new String[m.keyDefaultStrings.size() * 2];
		for (int i = 0; i < m.keyDefaultStrings.size(); i++) {
			defaults[i * 2] = i + "";
			defaults[i * 2 + 1] = m.keyDefaultStrings.get((i));
		}

		// get a list representing the child id
		// 0:P:1:2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:M:5:111: -> 0,P,1,2b157a2f-1dad-4178-a61a-51e92c04f9e4,2,V,3,11,4,M,5,111
		String[] sarray = childId.split(":");

		// now you have two arrays:
		// 0,P,1,2b157a2f-1dad-4178-a61a-51e92c04f9e4,2,V,3,11,4,M,5,111     and
		// 0,NONE,1,NONE,2,IG,3,-1,4,IG,5,-1
		// copy the elements corresponding to the parent class "keys" from childIdLst to defaults
		// say you are supposed to copy the ordinals 0,1,2,3 corresponding to P,uuid,V,5 to the default str array
		cache.getCDOClassMetadata(parentClass).keys.stream().forEach(k -> {
			defaults[k.prefixOrdinal * 2 + 1] = sarray[k.prefixOrdinal * 2 + 1];
			defaults[k.valueOrdinal * 2 + 1] = sarray[k.valueOrdinal * 2 + 1];
		});

		// 0,P,1,2b157a2f-1dad-4178-a61a-51e92c04f9e4,2,V,3,11,4,M,5,111 --> 0:P:1,2b157a2f-1dad-4178-a61a-51e92c04f9e4:2:V:3:11:4:IG:5:-1
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < defaults.length; i++) {
			sb.append(defaults[i]).append(":");
		}
		return sb.toString();
	}

}
