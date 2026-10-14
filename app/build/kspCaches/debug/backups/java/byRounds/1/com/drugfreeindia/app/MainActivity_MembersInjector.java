package com.drugfreeindia.app;

import com.drugfreeindia.app.data.seed.DatabaseInitializer;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<DatabaseInitializer> databaseInitializerProvider;

  public MainActivity_MembersInjector(Provider<DatabaseInitializer> databaseInitializerProvider) {
    this.databaseInitializerProvider = databaseInitializerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<DatabaseInitializer> databaseInitializerProvider) {
    return new MainActivity_MembersInjector(databaseInitializerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectDatabaseInitializer(instance, databaseInitializerProvider.get());
  }

  @InjectedFieldSignature("com.drugfreeindia.app.MainActivity.databaseInitializer")
  public static void injectDatabaseInitializer(MainActivity instance,
      DatabaseInitializer databaseInitializer) {
    instance.databaseInitializer = databaseInitializer;
  }
}
