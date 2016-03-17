package com.abedajna.cccmapper.cache;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.annotations.EnableCCCMapper;
import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache;
import com.abedajna.cccmapper.cache.CompositeColumnNameMetadataCache.CompositeClassMetadata;
import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.testscenario.domain.ProductCFCompositeColumn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CompositeColumnNameMetadataCacheTestConfig.class, BaseTestConfig.class })
public class CompositeColumnNameMetadataCacheTest {

	@Autowired
	CompositeColumnNameMetadataCache compositeColumnNameMetadataCache;

	@Test
	public void testMetadata() {

		CompositeClassMetadata m = compositeColumnNameMetadataCache.getClassMetadata(ProductCFCompositeColumn.class);
		
		assertEquals("ProductCF", m.columnFamilyName);
		assertEquals("com.netflix.astyanax.serializers.LongSerializer", m.keySerializerName);
				
		assertEquals("attributename", m.attributeField.getName());
		assertEquals(6, m.attributeOrdinal);

		assertEquals(7, m.componentFields.size());
		assertEquals("key1", m.componentFields.get(0).getName());
		assertEquals("key1Val", m.componentFields.get(1).getName());
		assertEquals("key2", m.componentFields.get(2).getName());
		assertEquals("key2Val", m.componentFields.get(3).getName());
		assertEquals("key3", m.componentFields.get(4).getName());
		assertEquals("key3Val", m.componentFields.get(5).getName());
		assertEquals("attributename", m.componentFields.get(6).getName());

		assertEquals(Integer.valueOf(0), m.columnKeyOrdinals.get(0));
		assertEquals(Integer.valueOf(2), m.columnKeyOrdinals.get(1));
		assertEquals(Integer.valueOf(4), m.columnKeyOrdinals.get(2));

		assertEquals(Integer.valueOf(1), m.columnValueOrdinals.get(0));
		assertEquals(Integer.valueOf(3), m.columnValueOrdinals.get(1));
		assertEquals(Integer.valueOf(5), m.columnValueOrdinals.get(2));

		String[] defaults = new String[7];
		defaults[0] = "NONE";
		defaults[1] = "NONE";
		defaults[2] = "IG";
		defaults[3] = "-1";		
		defaults[4] = "IG";
		defaults[5] = "80703740-e434-4051-8e3b-948cb27de485";
		defaults[6] = "NONE";
		assertArrayEquals(defaults, m.componentDefaultValues.toArray());

		String[] defaultKeys = new String[6];
		defaultKeys[0] = "NONE";
		defaultKeys[1] = "NONE";
		defaultKeys[2] = "IG";
		defaultKeys[3] = "-1";		
		defaultKeys[4] = "IG";
		defaultKeys[5] = "80703740-e434-4051-8e3b-948cb27de485";
		assertArrayEquals(defaultKeys, m.keyDefaultStrings.toArray());

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnsupportedOperationException() {
		CompositeClassMetadata m = compositeColumnNameMetadataCache.getClassMetadata(ProductCFCompositeColumn.class);
		m.columnKeyOrdinals.set(0, 6);
	}
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class CompositeColumnNameMetadataCacheTestConfig {
}