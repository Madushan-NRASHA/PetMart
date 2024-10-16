//buildscript {
//    dependencies {
//        classpath("com.google.gms:google-services:4.4.2")
//    }
//}
//// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    id("com.android.application") version "8.1.1" apply false
//}


buildscript {
    dependencies {
        // Use the latest version of the Google Services plugin
        classpath("com.google.gms:google-services:4.3.15")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("com.android.library") version "8.6.0" apply false
}
