package org.antop.froala.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.tika.Tika;

@Getter
@ToString
public class FileInfo {
	private String name;
	@Setter
	private String mimeType;
	@Setter
	private byte[] bytes;
	@Setter
	private long size;

	public FileInfo(String name) {
		this.name = name;
	}

	public FileInfo(String name, byte[] bytes) {
		this(name);
		this.bytes = bytes;
		this.size = bytes.length;

		Tika tika = new Tika();
		this.mimeType = tika.detect(bytes, name);
	}

	public boolean isImage() {
		return mimeType.startsWith("image/");
	}

	public boolean isVideo() {
		return mimeType.startsWith("video/");
	}

}