package com.abedajna.cccmapper.converter;

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
import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.converter.ConvertDataObjectToColumnNameObjects;
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.abedajna.cccmapper.util.Triple;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConvertDataObjectToColumnNameObjectsImplTestConfig.class, BaseTestConfig.class })
public class ConvertDataObjectToColumnNameObjectsTest {

	@Autowired
	ConvertDataObjectToColumnNameObjects<ProductCFCompositeColumn> convertDataObjectToColumnNameObjects;

	@Test
	public void testConverter() {

		List<String> results = new ArrayList<String>();
		List<Triple<ProductCFCompositeColumn, String, Integer>> all = new ArrayList<Triple<ProductCFCompositeColumn, String, Integer>>();

		List<Product> productList = CreateUtils.createProducts(101L, 1, 2, 2);
		Account a1 = CreateUtils.createType1Account(101L);
		Account a2 = CreateUtils.createType2Account(101L);

		for (Product p : productList) {
			all.addAll(convertDataObjectToColumnNameObjects.convert(p, ProductCFCompositeColumn.class));
		}
		all.addAll(convertDataObjectToColumnNameObjects.convert(a1, ProductCFCompositeColumn.class));
		all.addAll(convertDataObjectToColumnNameObjects.convert(a2, ProductCFCompositeColumn.class));

		for (Triple<ProductCFCompositeColumn, String, Integer> p : all) {
			results.add(p.u + ":" + p.v + ":" + p.w);
			System.out.println(p.u + ":" + p.v + ":" + p.w);
		}
		String[] array = results.toArray(new String[results.size()]);
		assertEquals(56, array.length);
		assertThat(results, hasItems(array));

	}
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class ConvertDataObjectToColumnNameObjectsImplTestConfig {
}