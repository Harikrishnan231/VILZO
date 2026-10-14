package com.drugfreeindia.app.data.seed;

import android.content.Context;
import com.drugfreeindia.app.data.local.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DatabaseInitializer_Factory implements Factory<DatabaseInitializer> {
  private final Provider<Context> contextProvider;

  private final Provider<AppDatabase> databaseProvider;

  public DatabaseInitializer_Factory(Provider<Context> contextProvider,
      Provider<AppDatabase> databaseProvider) {
    this.contextProvider = contextProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DatabaseInitializer get() {
    return newInstance(contextProvider.get(), databaseProvider.get());
  }

  public static DatabaseInitializer_Factory create(Provider<Context> contextProvider,
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseInitializer_Factory(contextProvider, databaseProvider);
  }

  public static DatabaseInitializer newInstance(Context context, AppDatabase database) {
    return new DatabaseInitializer(context, database);
  }
}
