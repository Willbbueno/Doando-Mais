plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

}
android {
    namespace = "com.upx.doando_mais"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.upx.doando_mais"
        minSdk = 27
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --- Firebase ---
    // 1. Importa o "Controle de Versões" (BOM) - MANTENHA APENAS UM!
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))

    // 2. Adicione os produtos SEM especificar a versão
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-storage")

    // 3. Adicione a dependência de DEBUG do App Check (também sem versão)
    implementation("com.google.firebase:firebase-appcheck-debug")

    // --- Lifecycle & Navigation ---
    val lifecycleVersion = "2.8.1"
    implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata:$lifecycleVersion")
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment:$navVersion")
    implementation("androidx.navigation:navigation-ui:$navVersion")

    // --- Glide (para fotos) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- Testes ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}