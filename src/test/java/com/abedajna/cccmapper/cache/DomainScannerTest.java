package com.abedajna.cccmapper.cache;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.annotations.EnableCCCMapper;
import com.abedajna.cccmapper.cache.DomainScannerTest.DomainObjectFinderTestConfig;
import com.abedajna.cccmapper.config.BaseTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DomainObjectFinderTestConfig.class, BaseTestConfig.class})
public class DomainScannerTest {

	@Autowired
	DomainScanner scanner;

	@Test
	public void allCDOsScanned() {
		Set<String> cdoFound = new HashSet<String>();
		scanner.cassandraDataObjects().stream().forEach((clazz) -> {
			cdoFound.add(clazz.getName());
		});
		assertThat(
				cdoFound,
				hasItems("com.abedajna.cccmapper.testscenario.domain.Product", 
						"com.abedajna.cccmapper.testscenario.domain.Version",
						"com.abedajna.cccmapper.testscenario.domain.AccountAllocation",
						"com.abedajna.cccmapper.testscenario.domain.AccountAllocationDenorm",
						"com.abedajna.cccmapper.testscenario.domain.Account"));

		Set<String> ccoFound = new HashSet<String>();
		scanner.compositeColumnNameObjects().stream().forEach((clazz) -> {
			ccoFound.add(clazz.getName());
		});
		assertThat(ccoFound, hasItems("com.abedajna.cccmapper.testscenario.domain.ProductCFCompositeColumn"));

	}

	@Configuration
    @EnableCCCMapper
	@PropertySource("classpath:test.properties")
	@ComponentScan(excludeFilters = @ComponentScan.Filter(value = Configuration.class, type = FilterType.ANNOTATION))
	static class DomainObjectFinderTestConfig {

	}

}
