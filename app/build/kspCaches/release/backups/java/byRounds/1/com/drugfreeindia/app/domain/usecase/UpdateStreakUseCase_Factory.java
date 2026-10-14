package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.UserRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class UpdateStreakUseCase_Factory implements Factory<UpdateStreakUseCase> {
  private final Provider<UserRepository> userRepositoryProvider;

  public UpdateStreakUseCase_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public UpdateStreakUseCase get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static UpdateStreakUseCase_Factory create(
      Provider<UserRepository> userRepositoryProvider) {
    return new UpdateStreakUseCase_Factory(userRepositoryProvider);
  }

  public static UpdateStreakUseCase newInstance(UserRepository userRepository) {
    return new UpdateStreakUseCase(userRepository);
  }
}
