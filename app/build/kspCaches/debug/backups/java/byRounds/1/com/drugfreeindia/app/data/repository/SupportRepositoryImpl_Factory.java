package com.drugfreeindia.app.data.repository;

import com.drugfreeindia.app.data.local.AppDatabase;
import com.drugfreeindia.app.data.secure.SecurePreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class SupportRepositoryImpl_Factory implements Factory<SupportRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<SecurePreferencesManager> securePreferencesProvider;

  public SupportRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider,
      Provider<SecurePreferencesManager> securePreferencesProvider) {
    this.databaseProvider = databaseProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public SupportRepositoryImpl get() {
    return newInstance(databaseProvider.get(), securePreferencesProvider.get());
  }

  public static SupportRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider,
      Provider<SecurePreferencesManager> securePreferencesProvider) {
    return new SupportRepositoryImpl_Factory(databaseProvider, securePreferencesProvider);
  }

  public static SupportRepositoryImpl newInstance(AppDatabase database,
      SecurePreferencesManager securePreferences) {
    return new SupportRepositoryImpl(database, securePreferences);
  }
}
