package com.abedajna.cccmapper.converter;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.annotations.EnableCCCMapper;
import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.converter.ConvertColumnNameObjectsToDataObject;
import com.abedajna.cccmapper.converter.ConvertDataObjectToColumnNameObjects;
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.abedajna.cccmapper.testutils.TestUtils;
import com.abedajna.cccmapper.util.Pair;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConvertColumnNameObjectsToDataObjectTestConfig.class, BaseTestConfig.class })
public class ConvertColumnNameObjectsToDataObjectTest {

	@Autowired
	ConvertColumnNameObjectsToDataObject<Product, ProductCFCompositeColumn> convertColumnNameObjectsToDataObject_Product;

	@Autowired
	ConvertColumnNameObjectsToDataObject<Account, ProductCFCompositeColumn> convertColumnNameObjectsToDataObject_Account;

	@Autowired
	ConvertDataObjectToColumnNameObjects<ProductCFCompositeColumn> convertDataObjectToColumnNameObjects;

	@Test
	public void testConverter() {

		List<Pair<ProductCFCompositeColumn, String>> all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();

		List<Product> productList = CreateUtils.createProducts(101L, 2, 2, 2);
		Account a1 = CreateUtils.createType1Account(101L);
		Account a2 = CreateUtils.createType2Account(101L);

		for (Product p : productList) {
			all.addAll(TestUtils.shedTTLs(convertDataObjectToColumnNameObjects.convert(p, ProductCFCompositeColumn.class)));
		}
		all.addAll(TestUtils.shedTTLs(convertDataObjectToColumnNameObjects.convert(a1, ProductCFCompositeColumn.class)));
		all.addAll(TestUtils.shedTTLs(convertDataObjectToColumnNameObjects.convert(a2, ProductCFCompositeColumn.class)));

		List<Product> products = convertColumnNameObjectsToDataObject_Product.convert(all, Product.class);
		assertEquals(2, products.size());
		products.stream().forEach(p -> {
			SortedSet<Version> versions = p.getVersions();
			assertEquals(2, p.getVersions().size());
			versions.stream().forEach(v -> {
				assertEquals(2, v.getAccountAllocations().size());
			});
		});
		List<Account> promos = convertColumnNameObjectsToDataObject_Account.convert(all, Account.class);
		assertEquals(2, promos.size());

	}	
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class ConvertColumnNameObjectsToDataObjectTestConfig {
}