package de.ssmits.javaStreamingPipeArchitecture;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@Entity
public class TestEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String SEQ_GEN = "SEQ_GENERATOR_POC";

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator=SEQ_GEN)
	@SequenceGenerator(name=SEQ_GEN, sequenceName="[SEQ_POC]", initialValue=1, allocationSize=1)
	private Long id;
	
	@Version
	@Column(nullable=false, length=19)
	private Long version;
	
	@Column(nullable=false, updatable=false)
	private String uuid = UUID.randomUUID().toString();
	
	@Column(nullable=false, length=2000)
	private String payload;
	
	public TestEntity() {
		// Empty constructor required by JPA Spec.
	}

	public long getId() {
		return id;
	}

	public long getVersion() {
		return version;
	}
	
	public UUID getUuid() {
		return UUID.fromString(uuid);
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return String.format(
			"%s [id='%s', version='%s', payload='%s']", 
			getClass().getName(), 
			getId(), 
			getVersion(),
			getPayload()
		);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((payload == null) ? 0 : payload.hashCode());
		result = prime * result + (int) (version ^ (version >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TestEntity)) {
			return false;
		}
		TestEntity other = (TestEntity) obj;
		if (id != other.id) {
			return false;
		}
		if (payload == null) {
			if (other.payload != null) {
				return false;
			}
		} else if (!payload.equals(other.payload)) {
			return false;
		}
		if (version != other.version) {
			return false;
		}
		return true;
	}
}
