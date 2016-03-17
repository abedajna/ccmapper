package com.abedajna.cccmapper.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;

import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache.CompositeClassMetadata;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;

public class ClassMatchingRulesCache {

	@Autowired
	CompositeColumnNameMetadataCache ccnoCache;

	@Autowired
	CassandraDataObjectMetadataCache cdoCache;

	@Autowired
	ConversionService conversionService;

	// ProductCFCompositeColumn::0:P:2:IG:4:IG: --> Product
	// ProductCFCompositeColumn::0:P:2:V:4:IG: --> Version
	// ProductCFCompositeColumn::0:P:2:V:4:A: --> AccountAllocation
	// ProductCFCompositeColumn::0:A:2:IG:4:IG: --> Account
	private ConcurrentMap<String, Class<CassandraDataObject>> cdoCcnoMap = new ConcurrentHashMap<String, Class<CassandraDataObject>>();

	public void buildCdoCcnoMap(Class<CompositeColumnName> ccno, Class<CassandraDataObject> cdo) {

		CompositeClassMetadata m = ccnoCache.getClassMetadata(ccno);
		// get defaults
		List<Object> ccnoDefaults = new ArrayList<Object>(m.componentDefaultValues);
		// inject key prefixes
		cdoCache.getClassMetadata(cdo).keys.stream().forEach(key -> {
			ccnoDefaults.set(key.prefixOrdinal, key.prefix);
		});
		// ccnoDefaults contains values for both @ColumnKey as well as @ColumnValue
		// create a string containing the values for @ColumnKeys only
		StringBuilder sb = new StringBuilder();
		for (int ordinal : m.columnKeyOrdinals) {
			sb.append(ordinal).append(":").append(conversionService.convert(ccnoDefaults.get(ordinal), String.class)).append(":");
		}
		cdoCcnoMap.put(ccno.getName() + "::" + sb.toString(), cdo);
	}

	public Class<CassandraDataObject> identifyCDOClass(Class<?> ccnoClass, String cDOIdentifierString) {
		return cdoCcnoMap.get(ccnoClass.getName() + "::" + cDOIdentifierString);
	}

	public void setCcnoCache(CompositeColumnNameMetadataCache ccnoCache) {
		this.ccnoCache = ccnoCache;
	}

	public void setCdoCache(CassandraDataObjectMetadataCache cdoCache) {
		this.cdoCache = cdoCache;
	}
}
