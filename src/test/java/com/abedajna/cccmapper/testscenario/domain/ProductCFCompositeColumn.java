package com.abedajna.cccmapper.testscenario.domain;

import java.util.UUID;

import com.abedajna.cccmapper.annotations.*;
import com.abedajna.cccmapper.domain.CompositeColumnName;
import com.netflix.astyanax.annotations.Component;

@ColumnFamily(name = "ProductCF", keySeralizer="com.netflix.astyanax.serializers.LongSerializer")
public class ProductCFCompositeColumn extends CompositeColumnName {

	@ColumnKey
	@Component(ordinal = 0)
	String key1;

	@ColumnValue
	@Component(ordinal = 1)
	UUID key1Val;

	@ColumnKey
	@Default("IG")
	@Component(ordinal = 2)
	String key2;

	@ColumnValue
	@Default("-1")	
	@Component(ordinal = 3)
	Integer key2Val;

	@ColumnKey
	@Default("IG")
	@Component(ordinal = 4)
	String key3;

	@ColumnValue
	@Default("80703740-e434-4051-8e3b-948cb27de485")
	@Component(ordinal = 5)
	UUID key3Val;
	
	@Attribute
	@Component(ordinal = 6)
	String attributename;

	// getters & setters

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public UUID getKey1Val() {
		return key1Val;
	}

	public void setKey1Val(UUID key1Val) {
		this.key1Val = key1Val;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public Integer getKey2Val() {
		return key2Val;
	}

	public void setKey2Val(Integer key2Val) {
		this.key2Val = key2Val;
	}

	public String getKey3() {
		return key3;
	}

	public void setKey3(String key3) {
		this.key3 = key3;
	}

	public UUID getKey3Val() {
		return key3Val;
	}

	public void setKey3Val(UUID key3Val) {
		this.key3Val = key3Val;
	}

	public String getAttributename() {
		return attributename;
	}

	public void setAttributename(String attributename) {
		this.attributename = attributename;
	}
	

	// debug only
	public String toString() {
		return key1 + ":" + key1Val.toString() + ":" + key2 + ":" + key2Val.toString() + ":" + key3 + ":" + key3Val.toString() + ":" + attributename;
	}


}
