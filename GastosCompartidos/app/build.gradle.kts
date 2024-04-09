plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.strainteam.gastoscompartidos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.strainteam.gastoscompartidos"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //librerias de firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    //livedata
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    //fragment
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    //activity
    implementation("androidx.activity:activity-ktx:1.8.2")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}