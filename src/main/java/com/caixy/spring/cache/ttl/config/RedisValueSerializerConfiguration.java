package com.caixy.spring.cache.ttl.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisValueSerializerConfiguration {

  @Bean
  public RedisSerializer<Object> redisValueSerializer() {
    ObjectMapper objectMapper = new ObjectMapper();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Module dateTimeModule =
        new JavaTimeModule()
            .addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter))
            .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter))
            .addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter))
            .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
    objectMapper.registerModule(dateTimeModule);
    Jackson2JsonRedisSerializer<Object> serializer =
        new Jackson2JsonRedisSerializer<>(Object.class);
    serializer.setObjectMapper(objectMapper);
    return serializer;
  }
}
