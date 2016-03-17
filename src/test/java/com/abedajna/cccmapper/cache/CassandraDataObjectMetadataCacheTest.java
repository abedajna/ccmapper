package com.abedajna.cccmapper.cache;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.annotations.EnableCCCMapper;
import com.abedajna.cccmapper.cache.CassandraDataObjectMetadataCache.CDOClassMetadata;
import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.testscenario.domain.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CassandraDataObjectMetadataCacheTestConfig.class, BaseTestConfig.class })
public class CassandraDataObjectMetadataCacheTest {

	@Autowired
	CassandraDataObjectMetadataCache cassandraDataObjectMetadataCache;

	@Test
	public void testMetadata() {

		CDOClassMetadata m = cassandraDataObjectMetadataCache.getClassMetadata(Product.class);

		assertThat(m.allFieldsByName.keySet(), hasItems("strProductAttr", "productId", "transientProductAttr", "versions", "intProductAttr"));

		List<String> attributes = new ArrayList<String>();
		m.attributeFields.stream().forEach(f -> {
			attributes.add(f.getName());
		});
		assertThat(attributes, hasItems("productId", "intProductAttr", "strProductAttr"));

		assertEquals("versions", m.oneToManys.get(0).otmField.getName());
		assertEquals(com.abedajna.cccmapper.testscenario.domain.Version.class, m.oneToManys.get(0).manyClass);

		assertEquals("productId", m.keys.get(0).f.getName());
		assertEquals("P", m.keys.get(0).prefix);
		assertEquals(Integer.valueOf(0), m.keys.get(0).prefixOrdinal);
		assertEquals(Integer.valueOf(1), m.keys.get(0).valueOrdinal);

		assertEquals(1, m.oneToManyFields.size());

		assertEquals("versions", m.oneToManyFields.get("com.abedajna.cccmapper.testscenario.domain.Version").getName());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedOperationException() {
		CDOClassMetadata m = cassandraDataObjectMetadataCache.getClassMetadata(Product.class);
		m.attributeFields.set(0, null);
	}

}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class CassandraDataObjectMetadataCacheTestConfig {
}