// Build de nivel raíz: solo declara los plugins, no los aplica aquí.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
