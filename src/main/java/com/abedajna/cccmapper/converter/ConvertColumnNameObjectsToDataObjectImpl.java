package com.abedajna.cccmapper.converter;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.Pair;

public class ConvertColumnNameObjectsToDataObjectImpl<T extends CassandraDataObject, C extends CompositeColumnName> implements ConvertColumnNameObjectsToDataObject<T, C> {

	@Autowired
	Helper<T, C> helper;

	@SuppressWarnings("unchecked")
	@Override
	public List<T> convert(List<Pair<C, String>> lst, Class<T> classCDO) {

		try {
			/*
			 * construct CDOs and put them in a map keyed by ids
			 */
			Map<String, T> objects = helper.constructCDOs(lst);

			/*
			 * iterate through objects. foreach ->
			 * if class matches classCDO, put them in a list. This list will be returned eventually.
			 * if class has a parent class, derive the id of the parent class. Locate the parent class
			 * object using the id and put this object in the parent's collection. 
			 */
			List<T> ret = helper.join(objects, classCDO, (Class<C>) lst.get(0).u.getClass());

			return ret;

		} catch (IllegalArgumentException | IllegalAccessException | SecurityException | InstantiationException | ClassNotFoundException e) {
			throw new RuntimeException("conversion failed", e);
		}

	}

}
