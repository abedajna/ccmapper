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
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.abedajna.cccmapper.testscenario.domain.*;
import com.abedajna.cccmapper.util.Pair;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HelperTestConfig.class, BaseTestConfig.class })
public class HelperTest {

	@Autowired
	Helper helper;

	@Test
	public void emitCDOIdentifierStringTest() throws IllegalArgumentException, IllegalAccessException {
		assertEquals("0:P:2:IG:4:IG:", helper.emitCDOClassIdentifierString(createCCO("P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:productId")));
		assertEquals("0:P:2:V:4:IG:", helper.emitCDOClassIdentifierString(createCCO("P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:productId")));
		assertEquals("0:P:2:V:4:A:", helper.emitCDOClassIdentifierString(createCCO("P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:productId")));
		assertEquals("0:A:2:IG:4:IG:", helper.emitCDOClassIdentifierString(createCCO("A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:accountId")));
	}

	@Test
	public void emitCDOIdTest() throws IllegalArgumentException, IllegalAccessException {
		assertEquals("0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:IG:3:-1:4:IG:5:80703740-e434-4051-8e3b-948cb27de485:", helper.emitCDOId(createCCO("P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:productId")));
		assertEquals("0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:V:3:11:4:IG:5:80703740-e434-4051-8e3b-948cb27de485:", helper.emitCDOId(createCCO("P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:productId")));
		assertEquals("0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:V:3:11:4:A:5:d521a637-65d2-4e7b-adcc-eea403615bc4:", helper.emitCDOId(createCCO("P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:productId")));
		assertEquals("0:A:1:0c9a2c5c-2c2c-4337-89b8-162157e2b612:2:IG:3:-1:4:IG:5:80703740-e434-4051-8e3b-948cb27de485:", helper.emitCDOId(createCCO("A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:accountId")));
	}

	@Test
	public void constructCDOsTest() throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		List<Pair<CompositeColumnName, String>> lst = createPairs();
		Map<String, CassandraDataObject> objects = helper.constructCDOs(lst);
		assertEquals(9, objects.keySet().size());
	}

	@Test
	public void getParentIdTest() {
		assertEquals("0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:IG:3:-1:4:IG:5:80703740-e434-4051-8e3b-948cb27de485:",
				helper.getParentId(Product.class, ProductCFCompositeColumn.class, "0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:V:3:11:4:IG:5:80703740-e434-4051-8e3b-948cb27de485:"));
		assertEquals("0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:V:3:11:4:IG:5:80703740-e434-4051-8e3b-948cb27de485:",
				helper.getParentId(Version.class, ProductCFCompositeColumn.class, "0:P:1:4fe1d917-277a-49e1-8801-0bad939ce0c1:2:V:3:11:4:A:5:d521a637-65d2-4e7b-adcc-eea403615bc4:"));
	}

	@Test
	public void joinTest() throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		List<Pair<CompositeColumnName, String>> lst = createPairs();
		Map<String, CassandraDataObject> objects = helper.constructCDOs(lst);
		List<Product> listProducts = helper.join(objects, Product.class, ProductCFCompositeColumn.class);

		assertEquals(1, listProducts.size());
		Product p = (Product) listProducts.get(0);
		SortedSet<Version> listVersions = p.getVersions();
		assertEquals(2, listVersions.size());
		Version v = listVersions.first();
		List<AccountAllocation> listAllocations = v.getAccountAllocations();
		assertEquals(2, listAllocations.size());

		objects = helper.constructCDOs(lst);
		List<Version> listVersions2 = helper.join(objects, Version.class, ProductCFCompositeColumn.class);
		assertEquals(2, listVersions2.size());
		Version v2 = (Version) listVersions2.get(0);
		List<AccountAllocation> listAllocations2 = v2.getAccountAllocations();
		assertEquals(2, listAllocations2.size());

		objects = helper.constructCDOs(lst);
		List<Account> accounts = helper.join(objects, Account.class, ProductCFCompositeColumn.class);
		assertEquals(2, accounts.size());
	}

	private ProductCFCompositeColumn createCCO(String data) {
		ProductCFCompositeColumn c = new ProductCFCompositeColumn();
		String[] array = data.split(":");
		c.setKey1(array[0]);
		c.setKey1Val(UUID.fromString(array[1]));
		c.setKey2(array[2]);
		c.setKey2Val(Integer.parseInt(array[3]));
		c.setKey3(array[4]);
		c.setKey3Val(UUID.fromString(array[5]));
		c.setAttributename(array[6]);
		return c;
	}

	private List<Pair<CompositeColumnName, String>> createPairs() {

		String[] array = {
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:customerId::101",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:intProductAttr::867",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:strProductAttr::a string629",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:creationTs::2014-06-27T09:20:55.005-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:createdBy::amit",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:versionNum::11",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:attr1::attr1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:attr2::750",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:creationTs::2014-06-27T09:20:55.012-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:IG:80703740-e434-4051-8e3b-948cb27de485:createdBy::amit",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:versionNum::11",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:accountId::d521a637-65d2-4e7b-adcc-eea403615bc4",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:allocReason::this is allocation reason no 266",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:allocAmount::549",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:creationTs::2014-06-27T09:20:55.012-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:d521a637-65d2-4e7b-adcc-eea403615bc4:createdBy::amit",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:versionNum::11",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:accountId::b217dc65-ca63-4684-a848-4dc026893cc2",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:allocReason::this is allocation reason no 945",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:allocAmount::694",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:creationTs::2014-06-27T09:20:55.012-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:11:A:b217dc65-ca63-4684-a848-4dc026893cc2:createdBy::amit",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:IG:80703740-e434-4051-8e3b-948cb27de485:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:IG:80703740-e434-4051-8e3b-948cb27de485:versionNum::12",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:IG:80703740-e434-4051-8e3b-948cb27de485:attr1::attr1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:IG:80703740-e434-4051-8e3b-948cb27de485:attr2::888",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:IG:80703740-e434-4051-8e3b-948cb27de485:creationTs::2014-06-27T09:20:55.012-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:IG:80703740-e434-4051-8e3b-948cb27de485:createdBy::amit",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:versionNum::12",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:accountId::8bdca160-662a-4c73-b074-68571627efa3",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:allocReason::this is allocation reason no 47",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:allocAmount::871",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:creationTs::2014-06-27T09:20:55.012-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:8bdca160-662a-4c73-b074-68571627efa3:createdBy::amit",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:productId::4fe1d917-277a-49e1-8801-0bad939ce0c1",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:versionNum::12",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:accountId::504bbd31-1f58-4197-a914-8678ab251326",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:allocReason::this is allocation reason no 282",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:allocAmount::117",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:creationTs::2014-06-27T09:20:55.012-07:00[America/Los_Angeles]",
				"P:4fe1d917-277a-49e1-8801-0bad939ce0c1:V:12:A:504bbd31-1f58-4197-a914-8678ab251326:createdBy::amit",
				"A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:accountId::0c9a2c5c-2c2c-4337-89b8-162157e2b612",
				"A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:acmetadata::{\"class\":\"com.abedajna.cccmapper.testscenario.domain.Type1Account\",\"attr1\":\"attr1\"}",
				"A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:status::ACTIVE",
				"A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:creationTs::2014-06-27T09:20:55.013-07:00[America/Los_Angeles]",
				"A:0c9a2c5c-2c2c-4337-89b8-162157e2b612:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:createdBy::amit",
				"A:5b8fd8f6-c127-4a6d-baaa-e5a5a978680a:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:accountId::5b8fd8f6-c127-4a6d-baaa-e5a5a978680a",
				"A:5b8fd8f6-c127-4a6d-baaa-e5a5a978680a:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:acmetadata::{\"class\":\"com.abedajna.cccmapper.testscenario.domain.Type2Account\",\"attr1\":\"attr1\"}",
				"A:5b8fd8f6-c127-4a6d-baaa-e5a5a978680a:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:status::ACTIVE",
				"A:5b8fd8f6-c127-4a6d-baaa-e5a5a978680a:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:creationTs::2014-06-27T09:20:55.013-07:00[America/Los_Angeles]",
				"A:5b8fd8f6-c127-4a6d-baaa-e5a5a978680a:IG:-1:IG:80703740-e434-4051-8e3b-948cb27de485:createdBy::amit"};

		List<Pair<CompositeColumnName, String>> lst = new ArrayList<Pair<CompositeColumnName, String>>();
		for (String s : array) {
			String[] keyval = s.split("::");
			lst.add(new Pair<CompositeColumnName, String>(createCCO(keyval[0]), keyval[1]));
		}
		return lst;

	}
}

@Configuration
@EnableCCCMapper
@PropertySource("classpath:test.properties")
class HelperTestConfig {
}