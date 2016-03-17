package com.abedajna.cccmapper.annotations;

import java.lang.annotation.*;

 /*
  * Maps to the defined BillingCompositeColumnKey
  * Ordinal value is zero indexed
  */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ColumnFamily {
	public String name();
	public String keySeralizer();
}
