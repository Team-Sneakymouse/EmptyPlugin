// Template-only build logic for Team-Sneakymouse/EmptyPlugin.
// Applied by build.gradle.kts only while this file exists. scripts/rename-template.sh deletes it,
// so plugins created from the template start without any of these checks.

val templateRepository = "Team-Sneakymouse/EmptyPlugin"

// A repository created from this template would publish (and deploy) a jar named EmptyPlugin on
// its first push to main. While this file exists, only the template repository itself may publish.
tasks.withType<PublishToMavenRepository>().configureEach {
    doFirst {
        if (System.getenv("GITHUB_REPOSITORY") != templateRepository) {
            throw GradleException(
                "Refusing to publish: this checkout is still the EmptyPlugin template (outside $templateRepository). " +
                    "Run `bash scripts/rename-template.sh <PluginName>` and commit the result first.",
            )
        }
    }
}

val templateRenameSmoke by tasks.registering(Exec::class) {
    group = "verification"
    description = "Run scripts/rename-template.sh on a scratch copy and check nothing template-specific is left."
    workingDir = rootDir
    // Reuse publishPreflight's bash (Git Bash on Windows, not WSL).
    val bash = checkNotNull(tasks.named<Exec>("publishPreflight").get().executable)
    commandLine(bash, "scripts/rename-template.sh", "--self-test")
}

tasks.named("check") {
    dependsOn(templateRenameSmoke)
}
