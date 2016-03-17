package com.abedajna.cccmapper.cache;

import java.util.*;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.abedajna.cccmapper.cache.CassandraDataObjectMetadataCache.CDOClassMetadata;
import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache.CompositeClassMetadata;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;

public class DomainMetadataCache {

	@Autowired
	DomainScanner scanner;

	@Autowired
	CompositeColumnNameMetadataCache ccnoCache;

	@Autowired
	CassandraDataObjectMetadataCache cdoCache;

	@Autowired
	ClassMatchingRulesCache rulesCache;

	private Set<Class<CassandraDataObject>> allCDOs;

	private Set<Class<CompositeColumnName>> allCCNOs;

	// Version	:	Product
	// Milestone:	Version
	private Map<String, String> manyToOneClassMap;
	
	private Map<String, Integer> ttls;

	@PostConstruct
	public void constructCache() {
		allCDOs = scanner.cassandraDataObjects();
		allCDOs.stream().forEach((clazz) -> {
			cdoCache.getClassMetadata(clazz);
		});

		allCCNOs = scanner.compositeColumnNameObjects();
		allCCNOs.stream().forEach((clazz) -> {
			ccnoCache.getClassMetadata(clazz);
		});
		allCCNOs.stream().forEach((ccno) -> {
			allCDOs.stream().forEach((cdo) -> {
				rulesCache.buildCdoCcnoMap(ccno, cdo);
			});
		});
		constructManyToOneMap();
		constructTTLMap();
	}

	public Map<String, String> getManyToOneClassMap() {
		return manyToOneClassMap;
	}

	public Map<String, Integer> getTTLs() {
		return ttls;
	}

	public <C extends CompositeColumnName> CompositeClassMetadata getCompositeClassMetadata(Class<C> aClass) {
		return ccnoCache.getClassMetadata(aClass);
	}

	public <T extends CassandraDataObject> CDOClassMetadata getCDOClassMetadata(Class<T> aClass) {
		return cdoCache.getClassMetadata(aClass);
	}

	public Class<CassandraDataObject> identifyCDOClass(Class<?> ccnoClass, String cDOIdentifierString) {
		return rulesCache.identifyCDOClass(ccnoClass, cDOIdentifierString);
	}

	public void setScanner(DomainScanner scanner) {
		this.scanner = scanner;
	}

	public void setCcnoCache(CompositeColumnNameMetadataCache ccnoCache) {
		this.ccnoCache = ccnoCache;
	}

	public void setCdoCache(CassandraDataObjectMetadataCache cdoCache) {
		this.cdoCache = cdoCache;
	}

	public void setRulesCache(ClassMatchingRulesCache rulesCache) {
		this.rulesCache = rulesCache;
	}

	private void constructManyToOneMap() {
		Map<String, String> manyToOneClassMap = new HashMap<String, String>();
		allCDOs.stream().forEach((one) -> {
			CDOClassMetadata meta = cdoCache.getClassMetadata(one);
			meta.oneToManys.stream().forEach(j -> {
				manyToOneClassMap.put(j.manyClass.getName(), one.getName());
			});
		});
		this.manyToOneClassMap = Collections.unmodifiableMap(manyToOneClassMap);
	}
	
	private void constructTTLMap() {
		Map<String, Integer> ttls = new HashMap<String, Integer>();
		allCDOs.stream().forEach((one) -> {
			ttls.put(one.getName(), cdoCache.getClassMetadata(one).ttl);
		});
		this.ttls = Collections.unmodifiableMap(ttls);
	}

}
