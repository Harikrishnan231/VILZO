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
public final class ExploreRepositoryImpl_Factory implements Factory<ExploreRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  public ExploreRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ExploreRepositoryImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static ExploreRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider) {
    return new ExploreRepositoryImpl_Factory(databaseProvider);
  }

  public static ExploreRepositoryImpl newInstance(AppDatabase database) {
    return new ExploreRepositoryImpl(database);
  }
}
