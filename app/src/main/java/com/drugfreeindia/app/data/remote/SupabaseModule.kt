package com.drugfreeindia.app.data.remote

import com.drugfreeindia.app.BuildConfig
import com.drugfreeindia.app.data.secure.EncryptedSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/** True when the build has real Supabase credentials (see local.properties). */
val isSupabaseConfigured: Boolean
    get() = BuildConfig.SUPABASE_URL.startsWith("https://") &&
        !BuildConfig.SUPABASE_URL.contains("YOUR-PROJECT") &&
        BuildConfig.SUPABASE_PUBLISHABLE_KEY.length > 20

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabase(sessionManager: EncryptedSessionManager): SupabaseClient {
        // When unconfigured, we still create a client against a harmless URL so DI works;
        // every repository checks `isSupabaseConfigured` and falls back to local data.
        val url = if (isSupabaseConfigured) BuildConfig.SUPABASE_URL else "https://example.invalid"
        val key = if (isSupabaseConfigured) BuildConfig.SUPABASE_PUBLISHABLE_KEY else "unconfigured-key-placeholder"
        return createSupabaseClient(supabaseUrl = url, supabaseKey = key) {
            defaultSerializer = io.github.jan.supabase.serializer.KotlinXSerializer(
                Json { ignoreUnknownKeys = true; explicitNulls = false; encodeDefaults = true }
            )
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "com.drugfreeindia.app"
                host = "login-callback"
                this.sessionManager = sessionManager
                alwaysAutoRefresh = true
                autoLoadFromStorage = true
            }
            install(Postgrest)
            install(Storage)
        }
    }
}
