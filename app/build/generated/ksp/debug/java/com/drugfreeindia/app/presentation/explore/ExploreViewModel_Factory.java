package com.drugfreeindia.app.presentation.explore;

import com.drugfreeindia.app.domain.repository.ExploreRepository;
import com.drugfreeindia.app.domain.usecase.JoinActivityUseCase;
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
public final class ExploreViewModel_Factory implements Factory<ExploreViewModel> {
  private final Provider<ExploreRepository> exploreRepositoryProvider;

  private final Provider<JoinActivityUseCase> joinActivityUseCaseProvider;

  public ExploreViewModel_Factory(Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<JoinActivityUseCase> joinActivityUseCaseProvider) {
    this.exploreRepositoryProvider = exploreRepositoryProvider;
    this.joinActivityUseCaseProvider = joinActivityUseCaseProvider;
  }

  @Override
  public ExploreViewModel get() {
    return newInstance(exploreRepositoryProvider.get(), joinActivityUseCaseProvider.get());
  }

  public static ExploreViewModel_Factory create(
      Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<JoinActivityUseCase> joinActivityUseCaseProvider) {
    return new ExploreViewModel_Factory(exploreRepositoryProvider, joinActivityUseCaseProvider);
  }

  public static ExploreViewModel newInstance(ExploreRepository exploreRepository,
      JoinActivityUseCase joinActivityUseCase) {
    return new ExploreViewModel(exploreRepository, joinActivityUseCase);
  }
}
