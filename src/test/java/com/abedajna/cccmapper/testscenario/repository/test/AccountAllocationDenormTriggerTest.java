package com.abedajna.cccmapper.testscenario.repository.test;

import static org.junit.Assert.assertEquals;

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
import com.abedajna.cccmapper.testscenario.domain.Account;
import com.abedajna.cccmapper.testscenario.domain.Product;
import com.abedajna.cccmapper.testscenario.repository.AccountRepository;
import com.abedajna.cccmapper.testscenario.repository.ProductRepository;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AccountAllocationDenormTriggerTestConfig.class, CassandraUnitConfig.class, BaseTestConfig.class })
public class AccountAllocationDenormTriggerTest {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	Keyspace keyspace;
	
	@Rule
	public CassandraUnit cassandraUnit = new CassandraUnit(new ClassPathJsonDataSet("cassandra-data.json"));

	


	@Test
	public void testLatestProduct() throws ConnectionException {

		Long customerId = Long.valueOf(new Random().nextInt(100000));

		// create customer accounts
		UUID accountId1 = UUID.randomUUID();
		UUID accountId2 = UUID.randomUUID();		
		Account a1 = CreateUtils.createType1Account(customerId, accountId1);
		Account a2 = CreateUtils.createType2Account(customerId, accountId2);		
		accountRepository.getTransactionBuilder(customerId).with(a1).with(a2).build().commit();

		// retrieve accounts. Validate no allocation data with accounts
		Account a1Retrieved = accountRepository.getAccount(customerId, accountId1);
		Account a2Retrieved = accountRepository.getAccount(customerId, accountId2);
		assertEquals(0, a1Retrieved.getAccountAllocations().size());
		assertEquals(0, a2Retrieved.getAccountAllocations().size());

		// create products, with account allocations
		Product p = CreateUtils.createProducts(customerId, 1, 2, 2).get(0);
		
		// overwrite the accounts consumed				
		p.getVersions().first().getAccountAllocations().get(0).setAccountId(accountId1);
		p.getVersions().first().getAccountAllocations().get(1).setAccountId(accountId2);
		p.getVersions().last().getAccountAllocations().get(0).setAccountId(accountId1);
		p.getVersions().last().getAccountAllocations().get(1).setAccountId(accountId2);
		
		// store product.
		productRepository.getTransactionBuilder(customerId).with(p).with(a1).with(a2).build().commit();
		
		//TestUtils.printAllCassandraData(customerId, keyspace);
		
		// retrieve accounts. Validate presence of allocation data with accounts
		a1Retrieved = accountRepository.getAccount(customerId, accountId1);
		a2Retrieved = accountRepository.getAccount(customerId, accountId2);
		assertEquals(2, a1Retrieved.getAccountAllocations().size());
		assertEquals(2, a2Retrieved.getAccountAllocations().size());
	}
	

}

@Configuration
@PropertySource(value = { "classpath:test.properties" })
@ComponentScan(basePackages = { "com.abedajna.cccmapper" })
@EnableCCCMapper
class AccountAllocationDenormTriggerTestConfig {
}
