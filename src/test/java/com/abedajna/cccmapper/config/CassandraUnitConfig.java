package com.abedajna.cccmapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.model.ConsistencyLevel;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

@Configuration
public class CassandraUnitConfig {

	@Bean
	public Keyspace inMemoryKeySpace() {

		String keySpaceName = "productKS";

		Keyspace keyspace = null;
		AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
				.forKeyspace(keySpaceName)
				.withAstyanaxConfiguration(
						new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.NONE).setCqlVersion("3.0.0").setDefaultWriteConsistencyLevel(ConsistencyLevel.CL_ONE)
								.setDefaultReadConsistencyLevel(ConsistencyLevel.CL_ONE))
				.withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("MyConnectionPool").setPort(9171).setMaxConnsPerHost(1).setSeeds("127.0.0.1:9171"))
				.withConnectionPoolMonitor(new CountingConnectionPoolMonitor()).buildKeyspace(ThriftFamilyFactory.getInstance());
		context.start();
		keyspace = context.getEntity();

		return keyspace;
	}
}
