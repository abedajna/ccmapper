package com.abedajna.cccmapper.converter;

import java.util.List;

import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.Triple;

//C: Astyanax composite key
//T: object converted from/to
public interface ConvertDataObjectToColumnNameObjects<C extends CompositeColumnName> {

	List<Triple<C, String, Integer>> convert(Object o, Class<C> classC);

}
