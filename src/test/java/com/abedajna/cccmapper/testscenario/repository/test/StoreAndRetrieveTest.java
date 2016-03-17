package com.abedajna.cccmapper.testscenario.repository.test;

import static org.junit.Assert.*;

import java.util.*;

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
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.testscenario.repository.*;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.abedajna.cccmapper.testutils.TestUtils;
import com.abedajna.cccmapper.transactionManager.TooManyResultsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BasicStoreAndRetrieveTestConfig.class, CassandraUnitConfig.class, BaseTestConfig.class })
public class StoreAndRetrieveTest {

	@Autowired
	Keyspace keyspace;

	@Rule
	public CassandraUnit cassandraUnit = new CassandraUnit(new ClassPathJsonDataSet("cassandra-data.json"));

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	VersionRepository versionRepository;

	@Autowired
	AccountRepository accountRepository;

	@Test
	public void checkProduct() throws ConnectionException {
		
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		List<Product> productList = CreateUtils.createProducts(customerId, 1, 2, 3);
		productRepository.getTransactionBuilder(customerId).with(productList.get(0)).build().commit();
		
		TestUtils.printAllCassandraData(customerId, keyspace);

		Product p_retrieved = productRepository.getProduct(customerId, productList.get(0).getProductId());
		assertTrue(CreateUtils.match(productList.get(0), p_retrieved));
	}
	
	
	@Test
	public void checkAccount() {
		
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Account a1 = CreateUtils.createType1Account(customerId);
		Account a2 = CreateUtils.createType2Account(customerId);
		accountRepository.getTransactionBuilder(customerId).with(a1).with(a2).build().commit();
		
		//TestUtils.printAllCassandraData(customerId, keyspace);

		Account a1_retrieved = accountRepository.getAccount(customerId, a1.getAccountId());		
		assertTrue(CreateUtils.match(a1, a1_retrieved));

		Account a2_retrieved = accountRepository.getAccount(customerId, a2.getAccountId());		
		assertTrue(CreateUtils.match(a2, a2_retrieved));
	}

	
	@Test
	public void checkVersion() throws ConnectionException {
		
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		List<Product> productList = CreateUtils.createProducts(customerId, 1, 2, 3);
		productRepository.getTransactionBuilder(customerId).with(productList.get(0)).build().commit();
		
		//TestUtils.printAllCassandraData(customerId, keyspace);

		Version v_retrieved = versionRepository.getProductVersion(customerId, productList.get(0).getProductId(), 11);
		assertTrue(CreateUtils.match(productList.get(0).getVersions().first(), v_retrieved));
	}

	
	@Test
	public void retrieveSingle() throws ConnectionException, JsonProcessingException, TooManyResultsException {

		Long customerId = Long.valueOf(new Random().nextInt(100000));

		// store 3 products
		List<Product> productList = CreateUtils.createProducts(customerId, 3, 2, 2);
		productRepository.getTransactionBuilder(customerId).with(productList.get(0)).with(productList.get(1)).with(productList.get(2)).build().commit();
		
		//TestUtils.printAllCassandraData(customerId, keyspace);

		// retrieve and check data for a product
		Product p_retrieved = productRepository.getProduct(customerId, productList.get(1).getProductId());
		assertTrue(CreateUtils.match(productList.get(1), p_retrieved));
	}

	
	@Test
	public void retrieveList() throws ConnectionException, JsonProcessingException {

		Long customerId = Long.valueOf(new Random().nextInt(100000));

		// store 3 products & 2 accounts
		List<Product> productList = CreateUtils.createProducts(customerId, 3, 2, 2);
		Account a1 = CreateUtils.createType1Account(customerId);
		Account a2 = CreateUtils.createType2Account(customerId);		
		productRepository.getTransactionBuilder(customerId).with(productList.get(0)).with(productList.get(1)).with(productList.get(2)).with(a1).with(a2).build().commit();

		// retrieve and check data for product
		List<Product> p = productRepository.getAllProducts(customerId);
		assertEquals(3, p.size());

		// retrieve and check data for versions for a product
		List<Version> v = versionRepository.getAllProductVersions(customerId, productList.get(1).getProductId());
		assertEquals(2, v.size());
		
		// retrieve and check data for account
		List<Account> a = accountRepository.getAllAccounts(customerId);
		assertEquals(2, a.size());
	}
	

	@Test
	public void bareMinimumAttributes() throws ConnectionException, JsonProcessingException, TooManyResultsException {

		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Product p = new Product();
		p.setProductId(UUID.randomUUID());
		p.setCustomerId(customerId);
		productRepository.getTransactionBuilder(customerId).with(p).build().commit();
		Product p1 = productRepository.getProduct(customerId, p.getProductId());
		assertEquals(p1.getProductId(), p.getProductId());
		assertEquals(p1.getCustomerId(), p.getCustomerId());
		assertEquals(0, p1.getVersions().size());

		Version v = new Version();
		v.setProductId(UUID.randomUUID());
		v.setVersionNum(1);
		productRepository.getTransactionBuilder(customerId).with(v).build().commit();
		Version v1 = versionRepository.getProductVersion(customerId, v.getProductId(), v.getVersionNum());
		assertEquals(v1.getProductId(), v.getProductId());
		assertEquals(Integer.valueOf(1), v1.getVersionNum());
		assertEquals(0, v1.getAccountAllocations().size());		
	}

	
	@Test
	public void notFoundTest() throws ConnectionException, JsonProcessingException, TooManyResultsException {
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Product p = new Product();
		p.setProductId(UUID.randomUUID());
		p.setCustomerId(customerId);
		productRepository.getTransactionBuilder(customerId).with(p).build().commit();
		assertNull(productRepository.getProduct(customerId, UUID.randomUUID()));

		assertNull(productRepository.getProduct(11111L, UUID.randomUUID()));

		List<Version> v1 = versionRepository.getAllProductVersions(customerId, UUID.randomUUID());
		assertEquals(0, v1.size());

		List<Version> v2 = versionRepository.getAllProductVersions(11111111L, UUID.randomUUID());
		assertEquals(0, v2.size());
	}
	
	
	@Test
	public void storeProduct() throws ConnectionException, JsonProcessingException {
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Product p = CreateUtils.createProducts(customerId, 1, 2, 2).get(0);
		productRepository.store(customerId, p);

		Product pRetrieved = productRepository.getProduct(customerId, p.getProductId());
		assertTrue(CreateUtils.match(p, pRetrieved));
	}
	
	
	@Test
	public void storeAccount() throws ConnectionException, JsonProcessingException {
		Long customerId = Long.valueOf(new Random().nextInt(100000));

		Account a = CreateUtils.createType1Account(customerId);
		accountRepository.store(customerId, a);

		Account aRetrieved = accountRepository.getAccount(customerId, a.getAccountId());
		assertTrue(CreateUtils.match(a, aRetrieved));

	}


}

@Configuration
@PropertySource(value = { "classpath:test.properties" })
@ComponentScan(basePackages = { "com.abedajna.cccmapper" })
@EnableCCCMapper
class BasicStoreAndRetrieveTestConfig {
}
