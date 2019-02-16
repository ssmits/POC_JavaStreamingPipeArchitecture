package de.ssmits.javaStreamingPipeArchitecture;

import java.io.Serializable;

public class TestDto implements Serializable {
	private static final long serialVersionUID = 1L;

	public Long id;
	public Long version;
	public String uuid;
	public String payload;
}
