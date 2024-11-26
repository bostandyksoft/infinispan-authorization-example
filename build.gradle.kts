plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.asuproject"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.infinispan.protostream:protostream-processor:5.0.12.Final")

    implementation(platform("org.infinispan:infinispan-bom:15.0.11.Final"))
    implementation("org.infinispan:infinispan-core:15.0.11.Final")
    implementation("org.infinispan.protostream:protostream-processor:5.0.12.Final")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
