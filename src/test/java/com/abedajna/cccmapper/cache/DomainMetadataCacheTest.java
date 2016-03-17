package com.abedajna.cccmapper.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.annotations.EnableCCCMapper;
import com.abedajna.cccmapper.cache.CassandraDataObjectMetadataCache.CDOClassMetadata;
import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache.CompositeClassMetadata;
import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.testscenario.domain.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DomainMetadataCacheTestConfig.class, BaseTestConfig.class })
public class DomainMetadataCacheTest {

	@Autowired
	DomainMetadataCache cache;

	@SuppressWarnings("unchecked")
	@Test
	public void testMetadata() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {

		// ensure that the postconstruct really loaded the caches!
		Field ccnof = cache.ccnoCache.getClass().getDeclaredField("metadataMap");
		ccnof.setAccessible(true);
		ConcurrentMap<String, CompositeClassMetadata> ccoMetadataMap = (ConcurrentMap<String, CompositeClassMetadata>) ccnof.get(cache.ccnoCache);
		assertNotNull(ccoMetadataMap.get(ProductCFCompositeColumn.class.getName()));

		Field cdof = cache.cdoCache.getClass().getDeclaredField("metadataMap");
		cdof.setAccessible(true);
		ConcurrentMap<String, CDOClassMetadata> cdoMetadataMap = (ConcurrentMap<String, CDOClassMetadata>) cdof.get(cache.cdoCache);
		assertNotNull(cdoMetadataMap.get(Product.class.getName()));

		// some basic testing
		CompositeClassMetadata m1 = cache.getCompositeClassMetadata(ProductCFCompositeColumn.class);
		assertNotNull(m1);

		CDOClassMetadata m2 = cache.getCDOClassMetadata(Product.class);
		assertNotNull(m2);

		// test identifyCDOClass
		assertEquals(cache.identifyCDOClass(ProductCFCompositeColumn.class, "0:P:2:IG:4:IG:"), Product.class);
		assertEquals(cache.identifyCDOClass(ProductCFCompositeColumn.class, "0:P:2:V:4:IG:"), Version.class);
		assertEquals(cache.identifyCDOClass(ProductCFCompositeColumn.class, "0:P:2:V:4:A:"), AccountAllocation.class);
		assertEquals(cache.identifyCDOClass(ProductCFCompositeColumn.class, "0:A:2:IG:4:IG:"), Account.class);

		// test manyToOneClassMap
		assertEquals(3, cache.getManyToOneClassMap().keySet().size());
		assertEquals("com.abedajna.cccmapper.testscenario.domain.Product", cache.getManyToOneClassMap().get("com.abedajna.cccmapper.testscenario.domain.Version"));
		assertEquals("com.abedajna.cccmapper.testscenario.domain.Version", cache.getManyToOneClassMap().get("com.abedajna.cccmapper.testscenario.domain.AccountAllocation"));
		assertEquals("com.abedajna.cccmapper.testscenario.domain.Account", cache.getManyToOneClassMap().get("com.abedajna.cccmapper.testscenario.domain.AccountAllocationDenorm"));
		
		// test ttls
		assertEquals(6, cache.getTTLs().keySet().size());
		assertEquals(new Integer(0), cache.getTTLs().get("com.abedajna.cccmapper.testscenario.domain.Product"));
		assertEquals(new Integer(63244800), cache.getTTLs().get("com.abedajna.cccmapper.testscenario.domain.Version"));
		assertEquals(new Integer(63244800), cache.getTTLs().get("com.abedajna.cccmapper.testscenario.domain.AccountAllocation"));
		assertEquals(new Integer(0), cache.getTTLs().get("com.abedajna.cccmapper.testscenario.domain.AccountAllocationDenorm"));
		assertEquals(new Integer(0), cache.getTTLs().get("com.abedajna.cccmapper.testscenario.domain.LastUpdatedProduct"));
		assertEquals(new Integer(0), cache.getTTLs().get("com.abedajna.cccmapper.testscenario.domain.Account"));
	}
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class DomainMetadataCacheTestConfig {
}
