package com.afa.devicer.back;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.NoArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@Configuration
@EnableCaching
@EnableScheduling
@NoArgsConstructor
public class BackOfficeConfig {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Get rid of all leading & trailing spaces from JSON String fields
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringWithoutSpaceDeserializer(String.class));
        objectMapper.registerModule(module);

        return objectMapper;
    }

    public static class StringWithoutSpaceDeserializer extends StdDeserializer<String> {
        protected StringWithoutSpaceDeserializer(final Class<String> vc) {
            super(vc);
        }

        @Override
        public String deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
            return parser.getText() != null
                    ? parser.getText().trim().replaceAll(" +", " ")
                    : null;
        }
    }

}
