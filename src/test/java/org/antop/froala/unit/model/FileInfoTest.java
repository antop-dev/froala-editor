package org.antop.froala.unit.model;

import com.jparams.verifier.tostring.ToStringVerifier;
import org.antop.froala.DefaultTests;
import org.antop.froala.model.FileInfo;
import org.junit.jupiter.api.Test;

class FileInfoTest extends DefaultTests {

	@Test
	void hasString() {
		ToStringVerifier.forClass(FileInfo.class).verify();
	}

}