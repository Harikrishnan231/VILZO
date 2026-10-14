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
public final class StoryRepositoryImpl_Factory implements Factory<StoryRepositoryImpl> {
  private final Provider<AppDatabase> databaseProvider;

  public StoryRepositoryImpl_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public StoryRepositoryImpl get() {
    return newInstance(databaseProvider.get());
  }

  public static StoryRepositoryImpl_Factory create(Provider<AppDatabase> databaseProvider) {
    return new StoryRepositoryImpl_Factory(databaseProvider);
  }

  public static StoryRepositoryImpl newInstance(AppDatabase database) {
    return new StoryRepositoryImpl(database);
  }
}
