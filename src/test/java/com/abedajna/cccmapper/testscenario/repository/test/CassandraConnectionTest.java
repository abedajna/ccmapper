package com.abedajna.cccmapper.testscenario.repository.test;

import static org.junit.Assert.assertNotNull;

import org.cassandraunit.CassandraUnit;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.config.CassandraUnitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CassandraUnitConfig.class, BaseTestConfig.class })
public class CassandraConnectionTest {

	@Autowired
	Keyspace keyspace;

	@Rule
	public CassandraUnit cassandraUnit = new CassandraUnit(new ClassPathJsonDataSet("cassandra-data.json"));

	@Autowired
	ObjectMapper objectMapper;

	@Test
	public void checkKeyspace() throws ConnectionException {
		assertNotNull(keyspace);
	}

}
