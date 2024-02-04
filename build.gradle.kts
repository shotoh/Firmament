plugins {
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application.mainClass = "io.github.shotoh.firmament.Firmament" //
group = "org.example"
version = "1.0.0"

val jdaVersion = "5.0.0-beta.20"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:$jdaVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("org.incendo:cloud-jda5:1.0.0-beta.1")
    implementation("me.nullicorn:Nedit:2.2.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true

    // Set this to the version of java you want to use,
    // the minimum required for JDA is 1.8
    sourceCompatibility = "21"
}