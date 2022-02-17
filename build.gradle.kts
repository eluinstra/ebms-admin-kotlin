import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.vaadin") version "22.0.3"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
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

extra["vaadinVersion"] = "22.0.3"

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
	implementation("org.postgresql:postgresql:42.3.2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
	imports {
		mavenBom("com.vaadin:vaadin-bom:${property("vaadinVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
