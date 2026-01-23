plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.secrets.gradle.plugin)  // secret 설정 시 해당 내용 추가

    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.perf)
}

android {
    namespace = "com.sesac.android7hours"
    compileSdk = 36

    defaultConfig {
//        applicationId = "com.sesac.android7hours"
        applicationId = "com.naver.maps.map.demo" // 여기 부분이 네이버 등록된 패키지명이 같아야 함
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true // secret 설정 시 해당 내용 추가
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

    implementation(project(":feature:common"))
    implementation(project(":feature:home"))
    implementation(project(":feature:community"))
    implementation(project(":feature:trail"))
    implementation(project(":feature:monitor"))
    implementation(project(":feature:mypage"))
    implementation(project(":feature:auth"))
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.bundles.navigation)
    implementation(libs.bundles.coil)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.perf)
    implementation(libs.play.services.measurement.api)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.map.sdk)  // 해당 부분 넣어야됨
    implementation(libs.play.services.location)
    implementation(libs.threetenabp) // localDate 사용하려면 sdk 26 이하에서는 이렇게 써야됨
    implementation(libs.bundles.kakao) // (libs.versions.toml에 정의된)


    // MinIO Client
    implementation("io.minio:minio:8.5.10")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    implementation("io.coil-kt:coil-compose:2.4.0")
}