plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlinx.kover")
}

android {
    namespace = "com.example.walmart.presentation"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        // This tells the runner which app to "watch"
        testInstrumentationRunnerArguments["targetPackage"] = "com.example.wallmartexample"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }


}

dependencies {
    implementation(project(":domain"))
    implementation(
        project(":data")
    )
    androidTestImplementation( "androidx.fragment:fragment-testing:1.6.2")
    androidTestImplementation( "androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-contrib:3.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.coroutines.get()}")
    androidTestImplementation(project(mapOf("path" to ":app")))
    androidTestImplementation(project(mapOf("path" to ":app")))
//    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2") {
//        exclude(mapOf("group" to "com.android.support", "module" to "support-annotations"))
//    }
//    androidTestImplementation("com.android.support.test:rules:1.0.2")
//    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:3.0.2") {
//        exclude(mapOf("group" to "com.android.support", "module" to "support-v4"))
//        exclude(mapOf("group" to "com.android.support", "module" to "design"))
//        exclude(mapOf("group" to "com.android.support", "module" to "recyclerview-v7"))
//    }

    val navVersion = "2.5.3"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.robolectric:robolectric:4.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation("androidx.fragment:fragment-testing:1.5.7")
    testImplementation("androidx.navigation:navigation-testing:$navVersion")

    val espressoVersion = "3.5.1"
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-accessibility:$espressoVersion")
    testImplementation("androidx.test.espresso:espresso-web:$espressoVersion")
    testImplementation("androidx.test.espresso.idling:idling-concurrent:$espressoVersion")

    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.0")

    // Standard Unit Test dependencies
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // If you are using MockK
    testImplementation("io.mockk:mockk:1.13.3")
}

koverReport {
    // filters for all report types of all build variants
    filters {
        excludes {
            classes(
                "*.databinding.*",
                "*.BuildConfig"
            )
        }
    }
}