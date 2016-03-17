package com.abedajna.cccmapper.testscenario.repository;

import java.util.*;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

import org.cassandraunit.CassandraUnit;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.transactionManager.Trigger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.ColumnFamily;

@Component
public class LastUpdatedProductTrigger implements Trigger<ProductCFCompositeColumn> {

	@Autowired
	Keyspace keyspace;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	ColumnFamily<Long, UUID> auditLogColumnFamily;
	
	@Autowired
	ColumnFamily<Long, String> counterColumnFamily;

	@Rule
	public CassandraUnit cassandraUnit = new CassandraUnit(new ClassPathJsonDataSet("cassandra-data.json"));

	@Override
	public List<CassandraDataObject> execute(CassandraDataObject source) {
		
		try {
			auditLog(source);
		} catch (JsonProcessingException | ConnectionException e) {
			throw new RuntimeException(e);
		}
		
		try {
			updateCounter(source);
		} catch (ConnectionException e) {
			throw new RuntimeException(e);
		}
		
		return markLastUpdatedProduct(source);
		
	}

	private List<CassandraDataObject> markLastUpdatedProduct(CassandraDataObject source) {
		
		List<CassandraDataObject> lst = new ArrayList<CassandraDataObject>();
		if (source instanceof Product) {
			Product p = (Product) source;
			LastUpdatedProduct lat = new LastUpdatedProduct();
			lat.setProductId(p.getProductId());
			lst.add(lat);
		}
		return lst;
	}

	
	private void updateCounter(CassandraDataObject source) throws ConnectionException {
		
		if (source instanceof Product) {
			Product p = (Product) source;
			keyspace.prepareColumnMutation(counterColumnFamily, p.getCustomerId(), "TotalNoProducts").incrementCounterColumn(1).execute();
		}		
	}
	

	private void auditLog(CassandraDataObject source) throws JsonProcessingException, ConnectionException {
		
		if (source instanceof Product) {
			Product p = (Product) source;
			MutationBatch m = keyspace.prepareMutationBatch();
			String s = objectMapper.writeValueAsString(p);
			m.withRow(auditLogColumnFamily, p.getCustomerId()).putColumn(TimeUUIDUtils.getUniqueTimeUUIDinMillis(), s);
			m.execute();
		}
	}

}
