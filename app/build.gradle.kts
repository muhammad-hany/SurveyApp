import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

val configFile = rootProject.file("config.properties")
val configProperties = Properties()
if (configFile.exists()) {
    configProperties.load(FileInputStream(configFile))
}

android {
    namespace = "com.muhammad.hany.surveyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.muhammad.hany.surveyapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "BASE_URL", configProperties["BASE_URL"] as String)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity)

    implementation(libs.koinCore)
    implementation(libs.koin)
    implementation(libs.retrofit)
    implementation(libs.moshiConverterFactory)
    implementation(libs.moshKotlin)
    implementation(libs.loggingInterceptor)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.trading.point.kotlin.composable.architecture)
    implementation(libs.retrofit.rx)
    implementation(libs.rxkotlin)
    implementation(libs.rxandroid)
    implementation(libs.androidx.runtime.rxjava3)


    testImplementation(libs.junit)
    testImplementation (libs.mockwebserver)
    testImplementation (libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}