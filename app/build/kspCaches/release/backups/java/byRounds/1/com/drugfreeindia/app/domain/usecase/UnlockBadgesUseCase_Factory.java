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
public final class UnlockBadgesUseCase_Factory implements Factory<UnlockBadgesUseCase> {
  private final Provider<UserRepository> userRepositoryProvider;

  public UnlockBadgesUseCase_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public UnlockBadgesUseCase get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static UnlockBadgesUseCase_Factory create(
      Provider<UserRepository> userRepositoryProvider) {
    return new UnlockBadgesUseCase_Factory(userRepositoryProvider);
  }

  public static UnlockBadgesUseCase newInstance(UserRepository userRepository) {
    return new UnlockBadgesUseCase(userRepository);
  }
}
