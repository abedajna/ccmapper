package com.abedajna.cccmapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.abedajna.cccmapper.cache.*;

/**
 * SpringConfig for cache beans
 */
@Configuration
public class CacheConfig {

	@Bean
	public CassandraDataObjectMetadataCache cassandraDataObjectMetadataCache() {
		return new CassandraDataObjectMetadataCache();
	}

	@Bean
	public ClassMatchingRulesCache classMatchingRulesCache() {
		return new ClassMatchingRulesCache();
	}

	@Bean
	public CompositeColumnNameMetadataCache compositeColumnNameMetadataCache() {
		return new CompositeColumnNameMetadataCache();
	}

	@Bean
	public DomainMetadataCache domainMetadataCache() {
		return new DomainMetadataCache();
	}

	@Bean
	public DomainScanner domainScanner() {
		return new DomainScanner();
	}
}
