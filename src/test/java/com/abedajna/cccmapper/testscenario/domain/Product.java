package com.abedajna.cccmapper.testscenario.domain;

import java.util.*;

import com.abedajna.cccmapper.annotations.*;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.fasterxml.jackson.annotation.JsonIgnore;

@TTL(0)
public class Product extends BaseEntity implements CassandraDataObject {

	@CompositeKeyWithOrdinal(prefix = Constants.PRODUCT_COMPOSITE_KEY_PREFIX, ordinal = 1)
	private UUID productId;

	private Long customerId; // just for convenience

	private Integer intProductAttr;

	private String strProductAttr;

	@JsonIgnore
	@Transient
	private String transientProductAttr;

	@OneToMany()
	private SortedSet<Version> versions = new TreeSet<Version>();

	@JsonIgnore
	public Version getLatestVersion() {
		if (versions.size() == 0l) {
			return null;
		} else {
			return versions.last();
		}
	}
	
	
	
	

	// getters & setters

	public UUID getProductId() {
		return productId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public void setProductId(UUID productId) {
		this.productId = productId;
	}

	public Integer getIntProductAttr() {
		return intProductAttr;
	}

	public void setIntProductAttr(Integer intProductAttr) {
		this.intProductAttr = intProductAttr;
	}

	public String getStrProductAttr() {
		return strProductAttr;
	}

	public void setStrProductAttr(String strProductAttr) {
		this.strProductAttr = strProductAttr;
	}

	public String getTransientProductAttr() {
		return transientProductAttr;
	}

	public void setTransientProductAttr(String transientProductAttr) {
		this.transientProductAttr = transientProductAttr;
	}

	public SortedSet<Version> getVersions() {
		return versions;
	}

	public void setVersions(SortedSet<Version> versions) {
		this.versions = versions;
	}
	
	
}
