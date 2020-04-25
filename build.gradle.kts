plugins {
    kotlin("jvm") version ("1.3.21")
    id("org.jetbrains.dokka") version "0.10.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("junit:junit:4.12")
}

tasks.test {
    useJUnit()
    maxHeapSize = "1G"
}
tasks.dokka {
    outputFormat = "markdown"
    outputDirectory = "$buildDir/javadoc"
}