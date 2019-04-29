package org.antop.froala.unit.entity;

import com.jparams.verifier.tostring.ToStringVerifier;
import org.antop.froala.DefaultTests;
import org.antop.froala.entity.StoredFile;
import org.junit.jupiter.api.Test;

class StoredFileTest extends DefaultTests {

	@Test
	void hasString() {
		ToStringVerifier.forClass(StoredFile.class).verify();
	}

}