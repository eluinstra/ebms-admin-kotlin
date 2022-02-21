import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.vaadin") version "22.0.3"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	id("org.openapi.generator") version "5.3.1"
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
			srcDir("${buildDir}/generated-cpaClient/src/gen/java")
			srcDir("${buildDir}/generated-urlMappingClient/src/gen/java")
			srcDir("${buildDir}/generated-certificateMappingClient/src/gen/java")
//			srcDir("${buildDir}/generated-ebMSClient/src/gen/java")
		}
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.37.3")
	implementation("org.jetbrains.exposed:exposed-java-time:0.37.3")
	implementation("com.vaadin:vaadin-spring-boot-starter")
	implementation("org.apache.commons:commons-csv:1.9.0")
	implementation("org.apache.cxf:cxf-core:3.5.0")
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
	implementation("org.apache.cxf:cxf-rt-rs-client:3.5.0")
	implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.13.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
	implementation("org.postgresql:postgresql:42.3.2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("com.vaadin:vaadin-bom:22.0.3")
	}
}

tasks.register("generateCpaClient", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
	generateApiClient(
		apiSpecification = "src/main/resources/cpas.json",
		apiOutputDir = project.layout.buildDirectory.dir("generated-cpaClient"),
		apiPackage = "nl.clockwork.ebms.service.cpa"
	)
}

tasks.register("generateUrlMappingClient", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
	generateApiClient(
		apiSpecification = "src/main/resources/urlMappings.json",
		apiOutputDir = project.layout.buildDirectory.dir("generated-urlMappingClient"),
		apiPackage = "nl.clockwork.ebms.service.mapping.url")
}

tasks.register("generateCertificateMappingClient", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
	generateApiClient(
		apiSpecification = "src/main/resources/certificateMappings.json",
		apiOutputDir = project.layout.buildDirectory.dir("generated-certificateMappingClient"),
		apiPackage = "nl.clockwork.ebms.service.mapping.certificate"
	)
}

tasks.register("generateEbMSClient", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
	generateApiClient(
		apiSpecification = "src/main/resources/ebms.json",
		apiOutputDir = project.layout.buildDirectory.dir("generated-ebMSClient"),
		apiPackage = "nl.clockwork.ebms.service.ebms"
	)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
//	dependsOn("generateCpaClient")
//	dependsOn("generateUrlMappingClient")
//	dependsOn("generateCertificateMappingClient")
//	dependsOn("generateEbMSClient")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

fun GenerateTask.generateApiClient(
	apiSpecification: String,
	apiOutputDir: Provider<Directory>,
	apiPackage: String
) {
	input = project.file(apiSpecification).path
	outputDir.set(apiOutputDir.get().toString())
	modelPackage.set("$apiPackage.model")
	this.apiPackage.set("$apiPackage.api")
	invokerPackage.set("$apiPackage.invoker")
	packageName.set(apiPackage)
	generatorName.set("jaxrs-cxf")
	configOptions.set(
		mapOf(
			"interfaceOnly" to "true",
			"useSwaggerAnnotations" to "false",
			"dateLibrary" to "java8"
		)
	)
}