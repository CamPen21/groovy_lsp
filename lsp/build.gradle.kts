plugins {
    id("buildlogic.kotlin-library-conventions")
}

dependencies {
    api("org.codehaus.groovy:groovy:3.0.9")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.22.0")
}

tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}
