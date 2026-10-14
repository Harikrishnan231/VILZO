package com.drugfreeindia.app.presentation.onboarding;

import com.drugfreeindia.app.data.local.DfiDataStore;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<DfiDataStore> dataStoreProvider;

  public OnboardingViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<DfiDataStore> dataStoreProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(userRepositoryProvider.get(), dataStoreProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<DfiDataStore> dataStoreProvider) {
    return new OnboardingViewModel_Factory(userRepositoryProvider, dataStoreProvider);
  }

  public static OnboardingViewModel newInstance(UserRepository userRepository,
      DfiDataStore dataStore) {
    return new OnboardingViewModel(userRepository, dataStore);
  }
}
