plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    id("maven-publish")
}

android {
    namespace = "kiwi.liam.crystal"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    publishing {
        singleVariant("release") {}
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.contentnegotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.kotlinx.serialization)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "kiwi.liam"
            artifactId = "crystal"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "CrystalBallFeedback"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

tasks.register<Zip>("generateRepo") {
    val publishTask = tasks.named(
        "publishReleasePublicationToCrystalBallFeedbackRepository",
        PublishToMavenRepository::class.java
    )
    from(publishTask.map { it.repository.url })
    into("crystal")
    archiveFileName.set("crystal.zip")
}