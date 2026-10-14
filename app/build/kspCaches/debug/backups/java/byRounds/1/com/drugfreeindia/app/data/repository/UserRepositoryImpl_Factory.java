package com.drugfreeindia.app.data.repository;

import com.drugfreeindia.app.data.local.AppDatabase;
import com.drugfreeindia.app.data.local.DfiDataStore;
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
public final class UserRepositoryImpl_Factory implements Factory<UserRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<DfiDataStore> dataStoreProvider;

  private final Provider<SecurePreferencesManager> securePreferencesProvider;

  public UserRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider,
      Provider<DfiDataStore> dataStoreProvider,
      Provider<SecurePreferencesManager> securePreferencesProvider) {
    this.databaseProvider = databaseProvider;
    this.dataStoreProvider = dataStoreProvider;
    this.securePreferencesProvider = securePreferencesProvider;
  }

  @Override
  public UserRepositoryImpl get() {
    return newInstance(databaseProvider.get(), dataStoreProvider.get(), securePreferencesProvider.get());
  }

  public static UserRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider,
      Provider<DfiDataStore> dataStoreProvider,
      Provider<SecurePreferencesManager> securePreferencesProvider) {
    return new UserRepositoryImpl_Factory(databaseProvider, dataStoreProvider, securePreferencesProvider);
  }

  public static UserRepositoryImpl newInstance(AppDatabase database, DfiDataStore dataStore,
      SecurePreferencesManager securePreferences) {
    return new UserRepositoryImpl(database, dataStore, securePreferences);
  }
}
