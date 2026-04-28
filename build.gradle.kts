plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dep.mgmt)
    jacoco
}

group = "com.template"
version = "v0.0.0-a-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot BOM — manages all Spring Boot dependency versions
    implementation(platform(libs.spring.boot.bom))
    annotationProcessor(platform(libs.spring.boot.bom))
    testImplementation(platform(libs.spring.boot.bom))

    // Testcontainers BOM
    testImplementation(platform(libs.testcontainers.bom))

    // Spring Boot Starters
    implementation(libs.spring.boot.web)
    implementation(libs.spring.boot.thymeleaf)
    implementation(libs.spring.boot.jpa)
    implementation(libs.spring.boot.security)
    implementation(libs.spring.boot.validation)
    implementation(libs.spring.boot.actuator)
    implementation(libs.spring.boot.oauth2)

    // Thymeleaf extras
    implementation(libs.thymeleaf.layout.dialect)
    implementation(libs.spring.security.thymeleaf)

    // Database
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    runtimeOnly(libs.postgresql)

    // WebJars — Bootstrap 5
    implementation(libs.bootstrap)
    implementation(libs.bootstrap.icons)
    implementation(libs.webjars.locator)

    // Lombok (must be declared before mapstruct-processor)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.lombok.mapstruct.binding)

    // MapStruct
    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // Dev Tools
    developmentOnly(libs.spring.boot.devtools)

    // Tests
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation("org.testcontainers:testcontainers:2.0.5")
    testImplementation("com.github.docker-java:docker-java-transport-httpclient5:3.4.0")
    testRuntimeOnly(libs.junit.launcher)
    testImplementation(libs.bytebuddy)
    testImplementation(libs.bytebuddy.agent)
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}
