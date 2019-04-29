package org.antop.froala;

import com.github.javafaker.Faker;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DefaultTests {

	protected final Faker faker = new Faker();

}
