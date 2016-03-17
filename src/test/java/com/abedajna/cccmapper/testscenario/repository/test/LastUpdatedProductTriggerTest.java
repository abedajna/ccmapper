package com.abedajna.cccmapper.testscenario.repository.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import java.util.UUID;

import org.cassandraunit.CassandraUnit;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.annotations.EnableCCCMapper;
import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.config.CassandraUnitConfig;
import com.abedajna.cccmapper.testscenario.domain.Product;
import com.abedajna.cccmapper.testscenario.repository.ProductRepository;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.abedajna.cccmapper.transactionManager.TooManyResultsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { LastUpdatedProductTriggerTestConfig.class, CassandraUnitConfig.class, BaseTestConfig.class })
public class LastUpdatedProductTriggerTest {

	@Autowired
	Keyspace keyspace;

	@Rule
	public CassandraUnit cassandraUnit = new CassandraUnit(new ClassPathJsonDataSet("cassandra-data.json"));

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ColumnFamily<Long, String> counterColumnFamily;
	
	@Autowired
	ColumnFamily<Long, UUID> auditLogColumnFamily;
	


	@Test
	public void testLatestProduct() throws ConnectionException, JsonProcessingException, TooManyResultsException {

		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Product p1 = CreateUtils.createProducts(customerId, 1, 2, 2).get(0);

		productRepository.getTransactionBuilder(customerId).with(p1).build().commit();

		Product p11 = productRepository.getLastUpdatedProduct(customerId);
		assertTrue(CreateUtils.match(p1, p11));



		Product p2 = CreateUtils.createProducts(customerId, 1, 2, 2).get(0);

		productRepository.getTransactionBuilder(customerId).with(p2).build().commit();

		Product p21 = productRepository.getLastUpdatedProduct(customerId);
		assertTrue(CreateUtils.match(p2, p21));

		
	}
	
	@Test
	public void testCounterUpdate() throws ConnectionException {
		
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Product p = CreateUtils.createProducts(customerId, 1, 2, 2).get(0);
		
		productRepository.store(customerId, p);
		
		Column<String> result = keyspace.prepareQuery(counterColumnFamily).getKey(customerId).getColumn("TotalNoProducts").execute().getResult();
		Long counterValue = result.getLongValue();
		assertEquals(new Long(1), counterValue);
		
	}
	
	@Test
	public void testAuditLog() throws ConnectionException, JsonProcessingException {
		
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Product p = CreateUtils.createProducts(customerId, 1, 2, 2).get(0);
		
		productRepository.store(customerId, p);
				
		String actual = keyspace.prepareQuery(auditLogColumnFamily).getKey(customerId).execute().getResult().getColumnByIndex(0).getStringValue();
		String expected = objectMapper.writeValueAsString(p);
		
		assertEquals(expected, actual);
	}
	
}

@Configuration
@PropertySource(value = { "classpath:test.properties" })
@ComponentScan(basePackages = { "com.abedajna.cccmapper" })
@EnableCCCMapper
class LastUpdatedProductTriggerTestConfig {
}
