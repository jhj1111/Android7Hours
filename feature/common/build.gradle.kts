plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.secrets.gradle.plugin)
}

android {
    namespace = "com.sesac.common"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin{
    jvmToolchain(21)
}

secrets {
    // 사용할 프로퍼티 파일이름을 선언(선언하지 않으면기본"local.properties")
    propertiesFileName = "secret.properties"
    // CI/CD 환경을 위한 기본 프로퍼티 파일을 지정
// 이 파일은 버전 관리에 포함될 수 있음
//defaultPropertiesFileName = "secrets.defaults.properties"

// Secrets Plug-In이 무시할 키의 목록을 정규 표현식으로 지정가능
// "sdk.dir"은 기본적으로 무시
//ignoreList.add("debug.*")  : "debug"로 시작하는 모든 키 무시
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.coil)
//    implementation(libs.androidx.room.runtime)
//    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    ksp(libs.hilt.compiler)
    // WebRTC
    implementation(libs.stream.webrtc.android)

    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.map.sdk)  // 해당 부분 넣어야됨
    implementation(libs.play.services.location)
}
