package com.abedajna.cccmapper.annotations;

import java.lang.annotation.*;

 /*
  * Maps to the defined BillingCompositeColumnKey
  * Ordinal value is zero indexed
  */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface CompositeKeyWithOrdinal {
	public String prefix();
	public int ordinal();
}
