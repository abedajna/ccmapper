package com.abedajna.cccmapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.abedajna.cccmapper.converter.*;
import com.abedajna.cccmapper.domain.CassandraDataObject;
import com.abedajna.cccmapper.domain.CompositeColumnName;

/**
 * SpringConfig for convert beans
 */
@Configuration
public class ConverterConfig {

    @Bean
    public <T extends CassandraDataObject, C extends CompositeColumnName> ConvertColumnNameObjectsToDataObjectImpl<T, C> convertColumnNameObjectsToDataObjectImpl(){
        return new ConvertColumnNameObjectsToDataObjectImpl<T, C>();
    }

    @Bean
    public <C extends CompositeColumnName> ConvertDataObjectToColumnNameObjectsImpl<C> convertDataObjectToColumnNameObjectsImpl(){
        return new ConvertDataObjectToColumnNameObjectsImpl<C>();
    }

    @Bean
    public <T extends CassandraDataObject, C extends CompositeColumnName> Helper<T,C> helper(){
        return new Helper<T,C>();
    }
}
