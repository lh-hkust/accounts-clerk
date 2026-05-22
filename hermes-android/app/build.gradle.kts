import java.util.Base64

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.hermes.app"
    compileSdk = 36
    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "com.hermes.app"
        minSdk = 30
        targetSdk = 36

        // versionCode从环境变量读取，默认为1（必须是整数）
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()

        // versionName从环境变量读取，若未设置则默认为0.1.1
        versionName = System.getenv("VERSION_NAME") ?: "0.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 签名配置：从环境变量读取（支持跳过签名）
    signingConfigs {
        create("release") {
            val keystoreBase64 = System.getenv("KEYSTORE_BASE64")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            val envKeyAlias = System.getenv("KEY_ALIAS")
            val envKeyPassword = System.getenv("KEY_PASSWORD")
            val skipSigning = System.getenv("SKIP_SIGNING")?.toBoolean() ?: false

            if (!skipSigning && keystoreBase64 != null && keystorePassword != null &&
                envKeyAlias != null && envKeyPassword != null) {
                // 从BASE64解码并写入临时文件
                val keystoreFile = File.createTempFile("keystore", ".jks")
                keystoreFile.deleteOnExit()
                keystoreFile.writeBytes(Base64.getDecoder().decode(keystoreBase64))
                storeFile = keystoreFile
                storePassword = keystorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            }
        }
    }

    // ABI拆分：生成arm64-v8a和x86独立APK
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "x86")
            isUniversalApk = false  // 不生成通用APK
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 使用release签名配置（若环境变量未设置或跳过签名则使用debug签名）
            val releaseSigningConfig = signingConfigs.findByName("release")
            val skipSigning = System.getenv("SKIP_SIGNING")?.toBoolean() ?: false
            if (!skipSigning && releaseSigningConfig?.storeFile != null) {
                signingConfig = releaseSigningConfig
            } else {
                println("WARNING: Using debug signing (SKIP_SIGNING=$skipSigning or env vars not set)")
                signingConfig = signingConfigs.getByName("debug")
            }
        }
        debug {
            isMinifyEnabled = false
            // debug使用默认调试签名
        }
    }

    // App Bundle动态分发按架构拆分
    bundle {
        abi {
            enableSplit = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

// 打印版本名的Task（供CI获取版本号）
tasks.register("printVersionName") {
    doLast {
        println(project.android.defaultConfig.versionName)
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":presentation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // WorkManager
    implementation(libs.work.runtime)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}