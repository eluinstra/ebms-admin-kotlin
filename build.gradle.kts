import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.mateo.cxf.codegen.wsdl2java.Wsdl2Java

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.vaadin") version "22.0.3"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	id("io.mateo.cxf-codegen") version "1.0.0"
}

group = "nl.clockwork.ebms.admin"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven {
		url = uri("https://maven.vaadin.com/vaadin-addons")
	}
}

sourceSets {
	main {
		java {
			srcDir("${buildDir}/generated-cpaClient")
			srcDir("${buildDir}/generated-urlMappingClient")
			srcDir("${buildDir}/generated-certificateMappingClient")
			srcDir("${buildDir}/generated-ebMSClient")
		}
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web-services")
	implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.37.3")
	implementation("org.jetbrains.exposed:exposed-java-time:0.37.3")
	implementation("com.vaadin:vaadin-spring-boot-starter")
	implementation("org.apache.commons:commons-csv:1.9.0")
	implementation("org.apache.cxf:cxf-core:3.5.0")
	implementation("org.apache.cxf:cxf-rt-frontend-jaxws:3.5.0")
	implementation("org.apache.cxf:cxf-rt-transports-http:3.5.0")
	implementation("nl.clockwork.ebms:ebms-core:2.18.5") {
		exclude("org.springframework")
		exclude("org.springframework.kafka")
		exclude("org.codehaus.btm")
		exclude("com.atomikos")
		exclude("com.zaxxer")
	}
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.github.mvysny.karibudsl:karibu-dsl:1.1.1")
	implementation("com.github.appreciated:apexcharts:2.0.0-beta13")
	implementation("org.postgresql:postgresql:42.3.2")
	cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
	cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
	implementation("javax.jws:javax.jws-api:1.1")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("com.vaadin:vaadin-bom:22.0.3")
	}
}

tasks.register("generateCpaClient", Wsdl2Java::class) {
	toolOptions {
		wsdl.set(file("${projectDir}/src/main/resources/cpa.wsdl"))
		outputDir.set(file("${buildDir}/generated-cpaClient"))
		markGenerated.set(true)
	}
}

tasks.register("generateUrlMappingClient", Wsdl2Java::class) {
	toolOptions {
		wsdl.set(file("${projectDir}/src/main/resources/urlMapping.wsdl"))
		outputDir.set(file("${buildDir}/generated-urlMappingClient"))
		markGenerated.set(true)
	}
}

tasks.register("generateCertificateMappingClient", Wsdl2Java::class) {
	toolOptions {
		wsdl.set(file("${projectDir}/src/main/resources/certificateMapping.wsdl"))
		outputDir.set(file("${buildDir}/generated-certificateMappingClient"))
		markGenerated.set(true)
	}
}

tasks.register("generateEbMSClient", Wsdl2Java::class) {
	toolOptions {
		wsdl.set(file("${projectDir}/src/main/resources/ebMSMTOM.wsdl"))
		outputDir.set(file("${buildDir}/generated-ebMSClient"))
		markGenerated.set(true)
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
	dependsOn("generateCpaClient")
	dependsOn("generateUrlMappingClient")
	dependsOn("generateCertificateMappingClient")
	dependsOn("generateEbMSClient")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
