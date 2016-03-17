package com.abedajna.cccmapper.config;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.*;

@Configuration
public class BaseTestConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		SimpleModule module = new SimpleModule("Java8ZonedDateTimeModule");
		module.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
		module.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeSerializer());
		mapper.registerModule(module);
		return mapper;
	}

	@Bean
	public ColumnFamily<Long, UUID> auditLogColumnFamily() {
		return new ColumnFamily<Long, UUID>("AuditLog", LongSerializer.get(), UUIDSerializer.get());
	}
	
	@Bean
	public ColumnFamily<Long, String> counterColumnFamily() {
		return new ColumnFamily<Long, String>("CustomerProductCounter", LongSerializer.get(), StringSerializer.get());
	}
		

}

class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

	@Override
	public void serialize(ZonedDateTime zonedDateTime, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {

		jgen.writeStartObject();
		jgen.writeStringField("zonedDateTime", zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
		jgen.writeEndObject();

	}

}

class ZonedDateTimeDeSerializer extends JsonDeserializer<ZonedDateTime> {

	@Override
	public ZonedDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		JsonNode node = mapper.readTree(jp);
		String dateAsString = node.get("zonedDateTime").asText();
		return ZonedDateTime.parse(dateAsString, DateTimeFormatter.ISO_ZONED_DATE_TIME);

	}

}


