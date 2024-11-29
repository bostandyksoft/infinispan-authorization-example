package com.asuproject.infinispan.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.List;
import java.util.Set;

public class Authorization {

	private String id;
	private Set<String> keys;

	public Authorization() {
	}

	public Authorization(String id, Set<String> keys) {
		this.id = id;
		this.keys = keys;
	}

	public void add(String value) {
		keys.add(value);
	}

	public void remove(String value) {
		keys.remove(value);
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setKeys(Set<String> keys) {
		this.keys = keys;
	}

	@ProtoField(number = 1)
	public String getId() {
		return id;
	}

	@ProtoField(number = 2)
	public Set<String> getKeys() {
		return keys;
	}
}
