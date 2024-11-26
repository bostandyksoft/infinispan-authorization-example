package com.asuproject.infinispan.cache;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

import java.util.List;
import java.util.Set;

public class Authorization {

	@ProtoField(number = 1)
	final String id;

	@ProtoField(number = 2)
	final Set<String> keys;

	@ProtoFactory
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
}
