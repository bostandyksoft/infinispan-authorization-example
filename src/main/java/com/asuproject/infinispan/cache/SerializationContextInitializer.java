package com.asuproject.infinispan.cache;

import org.infinispan.protostream.annotations.ProtoSchema;

@ProtoSchema(
		includeClasses = Authorization.class,
		schemaFileName = "authorization.proto",
		schemaFilePath = "proto",
		schemaPackageName = "com.asuproject.infinispan.cache"
)
public interface SerializationContextInitializer extends org.infinispan.protostream.SerializationContextInitializer {
}
