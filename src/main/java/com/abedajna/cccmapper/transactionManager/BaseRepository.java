package com.abedajna.cccmapper.transactionManager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import com.abedajna.cccmapper.cache.DomainMetadataCache;
import com.abedajna.cccmapper.converter.ConvertColumnNameObjectsToDataObject;
import com.abedajna.cccmapper.converter.ConvertDataObjectToColumnNameObjects;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.util.Pair;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.Serializer;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.*;
import com.netflix.astyanax.serializers.AnnotatedCompositeSerializer;
import com.netflix.astyanax.serializers.CompositeRangeBuilder;

/*
 * K: keyserializer
 * C: columnserializaer
 * T: cassandra data object
 */
public class BaseRepository<C extends CompositeColumnName, T extends CassandraDataObject, K> {

	@Autowired
	Keyspace keyspace;

	@Autowired
	ConvertColumnNameObjectsToDataObject<T, C> convertCCNOToCDO;

	@Autowired
	ConvertDataObjectToColumnNameObjects<C> convertCDOToCCNO;
	
	@Autowired
	DomainMetadataCache cache;
	
	@Autowired(required=false)
	private List<Trigger<C>> triggers = new ArrayList<Trigger<C>>();
	
	private Class<C> classC;
	
	private String columnFamilyName;
	
	private ColumnFamily<K, C> columnFamily;
	
	private Serializer<K> keySerializer;
	
	private AnnotatedCompositeSerializer<C> annotatedCompositeSerializer;

	@PostConstruct
	public void init() {
		Class<?>[] types = ResolvableType.forType(this.getClass().getGenericSuperclass()).resolveGenerics();
		this.classC = (Class<C>) types[0];
		this.annotatedCompositeSerializer = new AnnotatedCompositeSerializer<C>(classC);
		this.columnFamilyName = cache.getCompositeClassMetadata(classC).columnFamilyName;
		try {
			this.keySerializer = (Serializer<K>) Class.forName(cache.getCompositeClassMetadata(classC).keySerializerName).getMethod("get").invoke(null);
			this.columnFamily = ColumnFamily.newColumnFamily(getColumnFamilyName(), getKeySerializer(), annotatedCompositeSerializer);			
		} catch (IllegalArgumentException | SecurityException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Serializer<K> getKeySerializer() {
		return this.keySerializer;		
	}
	
	public AnnotatedCompositeSerializer<C> getAnnotatedCompositeSerializer() {
		return this.annotatedCompositeSerializer;		
	}

	public ColumnFamily<K, C> getColumnFamily() {
		return this.columnFamily;
	}

	public List<Trigger<C>> getTriggers() {
		return this.triggers;
	}

	public String getColumnFamilyName() {
		return this.columnFamilyName;
	}

	public Transaction.Builder<C, K> getTransactionBuilder(K k) {
		return new Transaction.Builder<C, K>(this, k, classC, getTriggers());
	}
	
	
//	public void store(K k, T t) {
//		try {
//			List<Pair<C, String>> lst = new ArrayList<Pair<C, String>>();
//			lst.addAll(convertCDOToCCNO.convert(t, classC));
//
//			MutationBatch batch = keyspace.prepareMutationBatch();
//			for (Pair<C, String> p : lst) {
//				batch.withRow(getColumnFamily(), k).putColumn(p.u, p.v);
//			}
//			batch.execute();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}


	public List<T> query(K k, CompositeRangeBuilder range, Class<T> classT) {
		ColumnList<C> columnList;
		try {
			columnList = keyspace.prepareQuery(getColumnFamily()).getKey(k).withColumnRange(range.build()).execute().getResult();
			List<Pair<C, String>> list = new ArrayList<Pair<C, String>>();
			if (columnList != null) {
				for (Column<C> col : columnList) {
					list.add(new Pair<C, String>(col.getName(), col.getStringValue()));
				}
			}
			return list.size() == 0 ? new ArrayList<T>() : (List<T>) convertCCNOToCDO.convert(list, classT);
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
	}

	
	public T queryForOne(K k, CompositeRangeBuilder range, Class<T> classT)  {
		List<T> lst = (List<T>) query(k, range, classT);
		if (lst.size() > 1)
			throw new TooManyResultsException();
		else {
			return lst.size() == 0 ? null : lst.get(0);
		}
	}
	
	
	public void store(K k, T t) {
		this.getTransactionBuilder(k).with(t).build().commit();		
	}


}
