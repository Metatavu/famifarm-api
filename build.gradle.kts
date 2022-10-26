import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.allopen") version "1.7.20"
    id("io.quarkus")
    id("org.openapi.generator") version "5.2.0"
    id("org.jetbrains.kotlin.kapt") version "1.4.30"
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val poiVersion: String by project
val moshiVersion: String by project
val testcontainersVersion: String by project
val keycloakTestcontainerVersion: String by project
val jsonAssertVersion: String by project
val feignUmaVersion: String by project
val feignVersion: String by project
val feignFormVersion: String by project
val openApiToolsJacksonDatabindNullableVersion: String by project
val swaggerAnnotationsVersion: String by project
val javaxAnnotationApiVersion: String by project
val jsr305Version: String by project
val jpaModelGenVersion: String by project
val scribeJavaCoreVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-keycloak-admin-client")
    implementation("io.quarkus:quarkus-oidc")
    implementation("io.quarkus:quarkus-liquibase")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-jdbc-mysql")
    implementation("io.quarkus:quarkus-resteasy-jackson")
    implementation("io.quarkus:quarkus-resteasy")
    implementation("org.apache.commons:commons-lang3")
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")

    kapt("org.hibernate:hibernate-jpamodelgen:$jpaModelGenVersion")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:mysql:$testcontainersVersion")
    testImplementation("com.github.dasniko:testcontainers-keycloak:$keycloakTestcontainerVersion")
    testImplementation("org.skyscreamer:jsonassert:$jsonAssertVersion")
    testImplementation("fi.metatavu.feign:feign-uma:$feignUmaVersion")
    testImplementation("io.github.openfeign:feign-core:$feignVersion")
    testImplementation("io.github.openfeign:feign-jackson:$feignVersion")
    testImplementation("io.github.openfeign:feign-slf4j:$feignVersion")
    testImplementation("io.github.openfeign.form:feign-form:$feignFormVersion")
    testImplementation("io.github.openfeign:feign-okhttp:$feignVersion")
    testImplementation("org.openapitools:jackson-databind-nullable:$openApiToolsJacksonDatabindNullableVersion")
    testImplementation("io.swagger:swagger-annotations:$swaggerAnnotationsVersion")
    testImplementation("com.google.code.findbugs:jsr305:$jsr305Version")
    testImplementation("com.github.scribejava:scribejava-core:$scribeJavaCoreVersion")
}

group = "fi.metatavu.famifarm"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

sourceSets["main"].java {
    srcDir("build/generated/api-spec/src/gen/java")
}

sourceSets["test"].java {
    srcDir("build/generated/api-client/src/main/java")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

val generateApiSpec = tasks.register("generateApiSpec", GenerateTask::class) {
    setProperty("generatorName", "jaxrs-spec")
    setProperty("inputSpec", "$rootDir/src/main/resources/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-spec")
    setProperty("apiPackage", "${project.group}.rest.api")
    setProperty("modelPackage", "${project.group}.rest.model")

    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("interfaceOnly", "true")
    this.configOptions.put("returnResponse", "true")
    this.configOptions.put("useSwaggerAnnotations", "false")
    this.configOptions.put("prependFormOrBodyParameters", "true")
}

val generateApiClient = tasks.register("generateApiClient", GenerateTask::class){
    setProperty("generatorName", "java")
    setProperty("library", "feign")
    setProperty("inputSpec", "$rootDir/src/main/resources/swagger.yaml")
    setProperty("outputDir", "$buildDir/generated/api-client")
    setProperty("packageName", "fi.metatavu.famifarm.client")
    setProperty("modelPackage", "${project.group}.client.model")
    setProperty("apiPackage", "${project.group}.client.api")
    setProperty("invokerPackage", "${project.group}.client")
    setProperty("groupId", "${project.group}.client")

    this.configOptions.put("java8", "true")
    this.configOptions.put("dateLibrary", "java8")
    this.configOptions.put("prependFormOrBodyParameters", "true")
}

tasks.named("compileJava") {
    dependsOn("generateApiSpec")
}

tasks.named("compileTestJava") {
    dependsOn("generateApiClient")
}