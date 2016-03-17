package com.abedajna.cccmapper.performance;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
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
import com.abedajna.cccmapper.testscenario.domain.Product;
import com.abedajna.cccmapper.testscenario.domain.ProductCFCompositeColumn;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.abedajna.cccmapper.testutils.TestUtils;
import com.abedajna.cccmapper.util.Pair;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { DateToColumnConfig.class, BaseTestConfig.class })
@Ignore
public class YKTProfiling {

	@Autowired
	ConvertDataObjectToColumnNameObjects<ProductCFCompositeColumn> convertDataObjectToColumnNameObjects;

	@Test
	public void testConverter() {

		List<Product> productList = CreateUtils.createProducts(101L, 4, 100, 10);
		List<Pair<ProductCFCompositeColumn, String>> all = null;

		while (true) {
			all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();

			for (Product p : productList) {
				all.addAll(TestUtils.shedTTLs(convertDataObjectToColumnNameObjects.convert(p, ProductCFCompositeColumn.class)));
			}

		}

	}
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class DateToColumnConfig {
}