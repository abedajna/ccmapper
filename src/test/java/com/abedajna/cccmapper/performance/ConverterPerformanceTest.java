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
import com.abedajna.cccmapper.converter.ConvertColumnNameObjectsToDataObject;
import com.abedajna.cccmapper.converter.ConvertDataObjectToColumnNameObjects;
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.testutils.CreateUtils;
import com.abedajna.cccmapper.testutils.TestUtils;
import com.abedajna.cccmapper.util.Pair;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConverterPerformanceTestConfig.class, BaseTestConfig.class })
@Ignore
public class ConverterPerformanceTest {

	@Autowired
	ConvertColumnNameObjectsToDataObject<Product, ProductCFCompositeColumn> convertColumnNameObjectsToDataObject_Product;

	@Autowired
	ConvertColumnNameObjectsToDataObject<Account, ProductCFCompositeColumn> convertColumnNameObjectsToDataObject_Account;

	@Autowired
	ConvertDataObjectToColumnNameObjects<ProductCFCompositeColumn> convertDataObjectToColumnNameObjects;

	@Test
	public void testConverter() {

		long cdoToColTime = 0;
		long colToCdoTime = 0;
		List<Product> productList = null;
		List<Product> products = null;
		List<Pair<ProductCFCompositeColumn, String>> all = null;
		int N = 100;
		int IG = 50; //ignore the first IG values for calculating average

		for (int i = 1; i <= N; i++) {
			all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();

			productList = CreateUtils.createProducts(101L, 2, 24, 2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long start = System.nanoTime();
			for (Product p : productList) {
				all.addAll(TestUtils.shedTTLs(convertDataObjectToColumnNameObjects.convert(p, ProductCFCompositeColumn.class)));
			}
			long end = System.nanoTime();

			if (i > IG)
				cdoToColTime += (end - start);
			System.out.print(((double) (end - start) / (1000000.0)));

			start = System.nanoTime();
			products = convertColumnNameObjectsToDataObject_Product.convert(all, Product.class);
			end = System.nanoTime();

			if (i > IG)
				colToCdoTime += (end - start);
			System.out.print(" : ");
			System.out.println(((double) (end - start) / (1000000.0)));

		}

		System.out.println("no of CompositeColumnName objects created: " + all.size() + " average time (millisec) taken: " + ((double) cdoToColTime / (1000000.0 * (N - IG))));
		System.out.println("no of CDO objects created: " + products.size() + " average time (millisec) taken: " + ((double) colToCdoTime / (1000000.0 * (N - IG))));

	}
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class ConverterPerformanceTestConfig {
}