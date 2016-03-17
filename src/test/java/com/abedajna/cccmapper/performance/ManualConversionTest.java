package com.abedajna.cccmapper.performance;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.abedajna.cccmapper.config.BaseTestConfig;
import com.abedajna.cccmapper.config.UtilConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseTestConfig.class, UtilConfig.class })
@Ignore
public class ManualConversionTest {

//	@Autowired
//	ConversionService conversionService;
//
//	@Test
//	public void testConverter() throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException {

//		long cdoToColTime = 0;
//		long colToCdoTime = 0;
//		List<Product> productList = null;
//		List<Product> products = null;
//		List<Pair<ProductCFCompositeColumn, String>> all = null;
//		int N = 100;
//		int IG = 50; //ignore the first IG values for calculating average
//
//		for (int i = 1; i <= N; i++) {
//			all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();
//
//			productList = CreateUtils.createProducts(101L, 2, 24, 2);
//			//			try {
//			//				Thread.sleep(1);
//			//			} catch (InterruptedException e) {
//			//				// TODO Auto-generated catch block
//			//				e.printStackTrace();
//			//			}
//
//			long start = System.nanoTime();
//			for (Product p : productList) {
//				all.addAll(createCCO(p));
//				for (Version v : p.getVersions()) {
//					all.addAll(createCCO(v));
//					for (AccountAllocation m : v.getMilestones()) {
//						all.addAll(createCCO(m));
//					}
//				}
//			}
//			long end = System.nanoTime();
//
//			if (i > IG)
//				cdoToColTime += (end - start);
//			System.out.print(((double) (end - start) / (1000000.0)));
//
//			start = System.nanoTime();
//			products = assemble(all);
//			end = System.nanoTime();
//
//			if (i > IG)
//				colToCdoTime += (end - start);
//			System.out.print(" : ");
//			System.out.println(((double) (end - start) / (1000000.0)));
//
//		}
//
//		System.out.println("no of CompositeColumnName objects created: " + all.size() + " average time (millisec) taken: " + ((double) cdoToColTime / (1000000.0 * (N - IG))));
//		System.out.println("no of CDO objects created: " + products.size() + " average time (millisec) taken: " + ((double) colToCdoTime / (1000000.0 * (N - IG))));
//
//	}
//
//	private List<Pair<ProductCFCompositeColumn, String>> createCCO(Product p) {
//		List<Pair<ProductCFCompositeColumn, String>> all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "productId"), p.getProductId().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "customerId"), p.getCustomerId().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "intProductAttr"), p.getIntProductAttr().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "strProductAttr"), p.getStrProductAttr().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "dt"), p.getDt().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "aLong"), p.getaLong().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "aInt"), p.getaInt().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "aBool"), p.getaBool().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "e1"), p.getE1().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "e2"), p.getE2().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "aStr1"), p.getaStr1().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", p.getProductId(), "IG", -1, "IG", -1, "aStr2"), p.getaStr2().toString()));
//
//		return all;
//	}
//
//	private List<Pair<ProductCFCompositeColumn, String>> createCCO(Version v) {
//		List<Pair<ProductCFCompositeColumn, String>> all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "productId"), v.getProductId().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "versionNum"), v.getVersionNum().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "name"), v.getName().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "dt"), v.getDt().format(
//				DateTimeFormatter.ISO_ZONED_DATE_TIME)));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "aLong"), v.getaLong().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "aInt"), v.getaInt().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "aBool"), v.getaBool().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "e1"), v.getE1().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "e2"), v.getE2().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "aStr1"), v.getaStr1().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", v.getProductId(), "V", v.getVersionNum(), "IG", -1, "aStr2"), v.getaStr2().toString()));
//
//		return all;
//	}
//
//	private List<Pair<ProductCFCompositeColumn, String>> createCCO(AccountAllocation m) {
//		List<Pair<ProductCFCompositeColumn, String>> all = new ArrayList<Pair<ProductCFCompositeColumn, String>>();
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "productId"), m.getProductId()
//				.toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "milestoneNum"), m
//				.getMilestoneNum().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "versionNum"), m.getVersionNum()
//				.toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "name"), m.getName().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "aLong"), m.getaLong().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "aInt"), m.getaInt().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "aBool"), m.getaBool().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "e1"), m.getE1().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "e2"), m.getE2().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "aStr1"), m.getaStr1().toString()));
//		all.add(new Pair<ProductCFCompositeColumn, String>(createNewProductCFCompositeColumn("P", m.getProductId(), "V", m.getVersionNum(), "M", m.getMilestoneNum(), "aStr2"), m.getaStr2().toString()));
//
//		return all;
//	}
//
//	private ProductCFCompositeColumn createNewProductCFCompositeColumn(String key1, UUID key1Val, String key2, Integer key2Val, String key3, Integer key3Val, String attributename) {
//		ProductCFCompositeColumn p = new ProductCFCompositeColumn();
//		p.setKey1(key1);
//		p.setKey1Val(key1Val);
//		p.setKey2(key2);
//		p.setKey2Val(key2Val);
//		p.setKey3(key3);
//		p.setKey3Val(key3Val);
//		p.setAttributename(attributename);
//		return p;
//	}
//
//	private List<Product> assemble(List<Pair<ProductCFCompositeColumn, String>> all) throws InstantiationException, IllegalAccessException, SecurityException, NoSuchFieldException {
//		List<Product> ret = new ArrayList<Product>();
//
//		Map<String, Object> objects = new HashMap<String, Object>();
//
//		for (Pair<ProductCFCompositeColumn, String> p : all) {
//
//			ProductCFCompositeColumn c = p.u;
//			String val = p.v;
//
//			Class<?> type = null;
//			// determine the type of c
//			if (c.getKey1().equals("P") && c.getKey2().equals("V") && c.getKey3().equals("M")) {
//				type = AccountAllocation.class;
//			} else if (c.getKey1().equals("P") && c.getKey2().equals("V") && c.getKey3().equals("IG")) {
//				type = Version.class;
//			} else if (c.getKey1().equals("P") && c.getKey2().equals("IG") && c.getKey3().equals("IG")) {
//				type = Product.class;
//			}
//
//			// get the id of c
//			String id = c.getKey1() + ":" + c.getKey1Val() + ":" + c.getKey2() + ":" + c.getKey2Val() + ":" + c.getKey3() + ":" + c.getKey3Val();
//
//			// store the object
//			if (!objects.containsKey(id)) {
//				objects.put(id, type.newInstance());
//			}
//			Object o = objects.get(id);
//
//			// set attribute value
//			Field f;
//			try {
//				f = o.getClass().getDeclaredField(c.getAttributename());
//			} catch (NoSuchFieldException e) {
//				f = o.getClass().getSuperclass().getDeclaredField(c.getAttributename());
//			}
//			f.setAccessible(true);
//			f.set(o, conversionService.convert(val, f.getType()));
//
//		}
//
//		for (String s : objects.keySet()) {
//
//			Object o = objects.get(s);
//			if (o.getClass().getName().equals(Product.class.getName())) {
//				ret.add((Product) o);
//			}
//
//			if (o.getClass().getName().equals(Version.class.getName())) {
//				Version v = (Version) o;
//				String parentId = "P:" + v.getProductId() + ":IG:-1:IG:-1";
//				Product parent = (Product) objects.get(parentId);
//				parent.getVersions().add(v);
//			}
//
//			if (o.getClass().getName().equals(AccountAllocation.class.getName())) {
//				AccountAllocation m = (AccountAllocation) o;
//				String parentId = "P:" + m.getProductId() + ":V:" + m.getVersionNum() + ":IG:-1";
//				Version parent = (Version) objects.get(parentId);
//				parent.getMilestones().add(m);
//			}
//
//		}
//
//		return ret;
//	}
}
