import org.jreleaser.model.Active

plugins {
    id("eclipse")
    id("maven-publish")
    id("org.jreleaser") version "1.22.0"
    id("io.github.maamissiniva.distance-field-font") version "0.1.1"
    id("io.github.maamissiniva.assets-library") version "0.1.1"
}


val mlGhBase                   = "github.com/maamissiniva/assets-dff-CaveatBrush-Regular"
val mlArName                   = "maamissiniva-assets-dff-CaveatBrush-Regular"
val mlDesc                     = "Distance field font : CaveatBrush-Regular"
val mlYear                     = "2026"
val groupName                  = "io.github.maamissiniva"
val stagingDir                 = project.layout.buildDirectory.dir("staging-deploy").get()

eclipse.project.name           = "_github_maamissiniva_assets-dff-CaveatBrush-Regular"
group                          = groupName
version                        = "0.1.0"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId    = groupName
            artifactId = mlArName

	    artifact(tasks["buildAssetsJar"])

            pom {
                name          = mlArName
                description   = mlDesc
                url           = "https://${mlGhBase}"
                inceptionYear = mlYear
                licenses {
                    license {
                        name = "LGPL-2.1"
                        url  = "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html"
                    }
                }
                developers {
                    developer {
                        id    = "maamissiniva"
                        name  = "Maamissiniva"
                        email = "maamissiniva@gmail.com"
                    }
                }
                scm {
                    connection          = "scm:git:https://${mlGhBase}.git"
                    developerConnection = "scm:git:ssh://${mlGhBase}.git"
                    url                 = "https://${mlGhBase}"
                }
            }
        }
    }

    repositories {
        maven {
            url = uri(stagingDir.asFile.path)
        }
    }
}

jreleaser {
    signing {
        pgp {
            active  = Active.ALWAYS
            armored = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                register("release-deploy") {
                    active = Active.RELEASE
                    url    = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(stagingDir.asFile.path)
                }
            }
            nexus2 {
                register("snapshot-deploy") {
                    active                 = Active.SNAPSHOT
                    snapshotUrl            = "https://central.sonatype.com/repository/maven-snapshots/"
                    applyMavenCentralRules = true
                    snapshotSupported      = true
                    closeRepository        = true
                    releaseRepository      = true
                    stagingRepository(stagingDir.asFile.path)
                }
            }
        }
    }
}
