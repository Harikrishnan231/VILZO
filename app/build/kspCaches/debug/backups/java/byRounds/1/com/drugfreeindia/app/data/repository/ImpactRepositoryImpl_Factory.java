package com.drugfreeindia.app.data.repository;

import com.drugfreeindia.app.data.local.AppDatabase;
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
public final class ImpactRepositoryImpl_Factory implements Factory<ImpactRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  public ImpactRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ImpactRepositoryImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static ImpactRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider) {
    return new ImpactRepositoryImpl_Factory(databaseProvider);
  }

  public static ImpactRepositoryImpl newInstance(AppDatabase database) {
    return new ImpactRepositoryImpl(database);
  }
}
