package org.antop.froala.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.UUID;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToUuidConverter());
	}

	public class StringToUuidConverter implements Converter<String, UUID> {
		@Override
		public UUID convert(String source) {
			return UUID.fromString(source);
		}
	}

}