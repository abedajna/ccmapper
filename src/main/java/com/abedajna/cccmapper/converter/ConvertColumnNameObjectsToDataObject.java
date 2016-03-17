package com.abedajna.cccmapper.converter;

import java.util.List;

import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.Pair;

//C: Astyanax composite key
//T: object converted from/to
public interface ConvertColumnNameObjectsToDataObject<T extends CassandraDataObject, C extends CompositeColumnName> {

	List<T> convert(List<Pair<C, String>> lst, Class<T> classCDO);

}
