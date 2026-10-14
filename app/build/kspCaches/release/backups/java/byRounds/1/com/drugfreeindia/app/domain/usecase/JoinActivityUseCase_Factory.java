package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.ExploreRepository;
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
public final class JoinActivityUseCase_Factory implements Factory<JoinActivityUseCase> {
  private final Provider<ExploreRepository> exploreRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public JoinActivityUseCase_Factory(Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.exploreRepositoryProvider = exploreRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public JoinActivityUseCase get() {
    return newInstance(exploreRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static JoinActivityUseCase_Factory create(
      Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new JoinActivityUseCase_Factory(exploreRepositoryProvider, userRepositoryProvider);
  }

  public static JoinActivityUseCase newInstance(ExploreRepository exploreRepository,
      UserRepository userRepository) {
    return new JoinActivityUseCase(exploreRepository, userRepository);
  }
}
