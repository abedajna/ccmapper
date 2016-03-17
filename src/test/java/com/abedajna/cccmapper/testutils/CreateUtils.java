package com.abedajna.cccmapper.testutils;

import java.time.ZonedDateTime;
import java.util.*;

import com.abedajna.cccmapper.testscenario.domain.*;

public class CreateUtils {

	public static List<Product> createProducts(Long customerId, int numProducts, int numVersionsInEachProduct, int numAccountsInEachVersion) {

		List<Product> productList = new ArrayList<Product>();
		for (int i = 1; i <= numProducts; i++) {
			Product p = new Product();
			productList.add(p);
			p.setProductId(UUID.randomUUID());
			p.setIntProductAttr(new Random().nextInt(1000));
			p.setStrProductAttr("a string" + new Random().nextInt(1000));
			p.setCustomerId(customerId);
			p.setTransientProductAttr("transient string");
			setBaseEntity(p);
			for (int j = 1; j <= numVersionsInEachProduct; j++) {
				Version v = new Version();
				v.setProductId(p.getProductId());
				String s = i + "" + j + "";
				v.setVersionNum(Integer.parseInt(s));
				v.setAttr1("attr1");
				v.setAttr2(new Random().nextInt(1000));
				p.getVersions().add(v);
				setBaseEntity(v);
				for (int k = 1; k <= numAccountsInEachVersion; k++) {
					AccountAllocation a = new AccountAllocation();
					a.setAccountId(UUID.randomUUID());
					a.setProductId(p.getProductId());
					a.setVersionNum(Integer.parseInt(s));
					a.setAllocAmount(new Random().nextInt(1000));
					a.setAllocReason("this is allocation reason no " + new Random().nextInt(1000));
					v.getAccountAllocations().add(a);
					setBaseEntity(a);
				}
			}
		}
		return productList;
	}

	public static Account createType1Account(Long customerId) {
		return createType1Account(customerId, UUID.randomUUID());
	}

	public static Account createType2Account(Long customerId) {
		return createType2Account(customerId, UUID.randomUUID());
	}

	public static Account createType1Account(Long customerId, UUID uuid) {
		Type1Account m = new Type1Account();
		m.setAttr1("attr1");
		Account a = new Account();
		setBaseEntity(a);
		a.setAcmetadata(m);
		a.setAccountId(uuid);
		a.setStatus(Status.ACTIVE);
		return a;
	}

	public static Account createType2Account(Long customerId, UUID uuid) {
		Type2Account m = new Type2Account();
		m.setAttr1("attr1");
		Account a = new Account();
		setBaseEntity(a);
		a.setAcmetadata(m);
		a.setAccountId(uuid);
		a.setStatus(Status.ACTIVE);
		return a;
	}

	private static void setBaseEntity(BaseEntity b) {
		b.setCreatedBy("amit");
		b.setCreationTs(ZonedDateTime.now());
	}

	public static boolean match(Product p_orig, Product p_retrieved) {
		if (p_retrieved == null && p_orig != null)
			return false;
		if (!p_retrieved.getProductId().equals(p_orig.getProductId()))
			return false;
		if (!p_retrieved.getIntProductAttr().equals(p_retrieved.getIntProductAttr()))
			return false;
		if (!p_retrieved.getStrProductAttr().equals(p_orig.getStrProductAttr()))
			return false;
		if (!p_retrieved.getCustomerId().equals(p_orig.getCustomerId()))
			return false;
		if (!(p_retrieved.getTransientProductAttr() == null))
			return false;
		if (!(p_retrieved.getVersions().size() == (p_orig.getVersions().size())))
			return false;
		int p_retrieved_alloc_cnt = 0, p_orig_alloc_cnt = 0;
		for (Version v : p_retrieved.getVersions()) {
			p_retrieved_alloc_cnt += v.getAccountAllocations().size();
		}
		for (Version v : p_orig.getVersions()) {
			p_orig_alloc_cnt += v.getAccountAllocations().size();
		}
		if (p_retrieved_alloc_cnt != p_orig_alloc_cnt)
			return false;
		if (!p_retrieved.getCreatedBy().equals(p_orig.getCreatedBy()))
			return false;
		if (!p_retrieved.getCreationTs().equals(p_orig.getCreationTs()))
			return false;
		return true;
	}

	public static boolean match(Version v_orig, Version v_retrieved) {
		if (v_retrieved == null && v_orig != null)
			return false;
		if (!v_retrieved.getProductId().equals(v_orig.getProductId()))
			return false;
		if (!v_retrieved.getVersionNum().equals(v_orig.getVersionNum()))
			return false;
		if (!v_retrieved.getAttr1().equals(v_orig.getAttr1()))
			return false;
		if (!v_retrieved.getAttr2().equals(v_orig.getAttr2()))
			return false;
		if (!v_retrieved.getCreatedBy().equals(v_orig.getCreatedBy()))
			return false;
		if (!v_retrieved.getCreationTs().equals(v_orig.getCreationTs()))
			return false;
		if (!(v_retrieved.getAccountAllocations().size() == (v_orig.getAccountAllocations().size())))
			return false;
		return true;
	}

	public static boolean match(AccountAllocation a_orig, AccountAllocation a_retrieved) {
		if (a_retrieved == null && a_orig != null)
			return false;
		if (!a_retrieved.getProductId().equals(a_orig.getProductId()))
			return false;
		if (!a_retrieved.getVersionNum().equals(a_orig.getVersionNum()))
			return false;
		if (!a_retrieved.getAccountId().equals(a_orig.getAccountId()))
			return false;
		if (!a_retrieved.getAllocAmount().equals(a_orig.getAllocAmount()))
			return false;
		if (!a_retrieved.getAllocReason().equals(a_orig.getAllocReason()))
			return false;
		if (!a_retrieved.getCreatedBy().equals(a_orig.getCreatedBy()))
			return false;
		if (!a_retrieved.getCreationTs().equals(a_orig.getCreationTs()))
			return false;
		return true;
	}

	public static boolean match(Account a_orig, Account a_retrieved) {
		if (a_retrieved == null && a_orig != null)
			return false;
		if (!a_retrieved.getAccountId().equals(a_orig.getAccountId()))
			return false;
		if (!a_retrieved.getCreatedBy().equals(a_orig.getCreatedBy()))
			return false;
		if (!a_retrieved.getCreationTs().equals(a_orig.getCreationTs()))
			return false;
		if (!a_retrieved.getAcmetadata().getClass().equals(a_orig.getAcmetadata().getClass()))
			return false;
		return true;
	}

}
