import os
import shutil

base = "college_staff_app"
os.makedirs(f"{base}/app/src/main/java/com/vilzo/staff", exist_ok=True)
os.makedirs(f"{base}/app/src/main/res/values", exist_ok=True)
os.makedirs(f"{base}/app/src/main/res/drawable", exist_ok=True)

# 1. settings.gradle.kts
with open(f"{base}/settings.gradle.kts", "w", encoding="utf-8") as f:
    f.write('''pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\\\.android.*")
                includeGroupByRegex("com\\\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "VilzoCollegeStaff"
include(":app")
''')

# 2. root build.gradle.kts
with open(f"{base}/build.gradle.kts", "w", encoding="utf-8") as f:
    f.write('''plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}
''')

# 3. gradle.properties
with open(f"{base}/gradle.properties", "w", encoding="utf-8") as f:
    f.write('''org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
''')

# Copy gradle folder & wrapper from root
if os.path.exists("gradle"):
    shutil.copytree("gradle", f"{base}/gradle", dirs_exist_ok=True)
if os.path.exists("gradlew"):
    shutil.copy("gradlew", f"{base}/gradlew")
if os.path.exists("gradlew.bat"):
    shutil.copy("gradlew.bat", f"{base}/gradlew.bat")
if os.path.exists("local.properties"):
    shutil.copy("local.properties", f"{base}/local.properties")
if os.path.exists("local.defaults.properties"):
    shutil.copy("local.defaults.properties", f"{base}/local.defaults.properties")

# 4. strings.xml
with open(f"{base}/app/src/main/res/values/strings.xml", "w", encoding="utf-8") as f:
    f.write('''<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">VILZO Staff Portal</string>
</resources>
''')

print("Created standalone college_staff_app foundation")
