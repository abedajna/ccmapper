package com.abedajna.cccmapper.testscenario.domain;

import java.util.*;

import com.abedajna.cccmapper.annotations.*;
import com.abedajna.cccmapper.domain.CassandraDataObject;

@TTL(63244800)
public class Version extends BaseEntity implements CassandraDataObject, Comparable<Version> {

	@CompositeKeyWithOrdinal(prefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX, ordinal = 1)
	private UUID productId;

	@CompositeKeyWithOrdinal(prefix = Constants.VERSION_COMPOSITE_KEY_PREFIX, ordinal = 3)
	private Integer versionNum;

	@OneToMany()
	private List<AccountAllocation> accountAllocations = new ArrayList<AccountAllocation>();

	private String attr1;
	
	private Integer attr2;


	@Override
	public int compareTo(Version o) {
		return this.versionNum.compareTo(o.versionNum);
	}

	
	
	
	// getters & setters

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(Integer versionNum) {
		this.versionNum = versionNum;
	}


	public List<AccountAllocation> getAccountAllocations() {
		return accountAllocations;
	}

	public void setAccountAllocations(List<AccountAllocation> accountAllocations) {
		this.accountAllocations = accountAllocations;
	}

	public String getAttr1() {
		return attr1;
	}

	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	public Integer getAttr2() {
		return attr2;
	}

	public void setAttr2(Integer attr2) {
		this.attr2 = attr2;
	}


}
