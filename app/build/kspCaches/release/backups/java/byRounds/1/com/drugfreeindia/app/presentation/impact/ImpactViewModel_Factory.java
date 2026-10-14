package com.drugfreeindia.app.presentation.impact;

import com.drugfreeindia.app.domain.repository.ExploreRepository;
import com.drugfreeindia.app.domain.repository.ImpactRepository;
import com.drugfreeindia.app.domain.repository.StoryRepository;
import com.drugfreeindia.app.domain.repository.UserRepository;
import com.drugfreeindia.app.domain.usecase.SubmitSurveyUseCase;
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
public final class ImpactViewModel_Factory implements Factory<ImpactViewModel> {
  private final Provider<ImpactRepository> impactRepositoryProvider;

  private final Provider<StoryRepository> storyRepositoryProvider;

  private final Provider<ExploreRepository> exploreRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<SubmitSurveyUseCase> submitSurveyUseCaseProvider;

  public ImpactViewModel_Factory(Provider<ImpactRepository> impactRepositoryProvider,
      Provider<StoryRepository> storyRepositoryProvider,
      Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<SubmitSurveyUseCase> submitSurveyUseCaseProvider) {
    this.impactRepositoryProvider = impactRepositoryProvider;
    this.storyRepositoryProvider = storyRepositoryProvider;
    this.exploreRepositoryProvider = exploreRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.submitSurveyUseCaseProvider = submitSurveyUseCaseProvider;
  }

  @Override
  public ImpactViewModel get() {
    return newInstance(impactRepositoryProvider.get(), storyRepositoryProvider.get(), exploreRepositoryProvider.get(), userRepositoryProvider.get(), submitSurveyUseCaseProvider.get());
  }

  public static ImpactViewModel_Factory create(Provider<ImpactRepository> impactRepositoryProvider,
      Provider<StoryRepository> storyRepositoryProvider,
      Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<SubmitSurveyUseCase> submitSurveyUseCaseProvider) {
    return new ImpactViewModel_Factory(impactRepositoryProvider, storyRepositoryProvider, exploreRepositoryProvider, userRepositoryProvider, submitSurveyUseCaseProvider);
  }

  public static ImpactViewModel newInstance(ImpactRepository impactRepository,
      StoryRepository storyRepository, ExploreRepository exploreRepository,
      UserRepository userRepository, SubmitSurveyUseCase submitSurveyUseCase) {
    return new ImpactViewModel(impactRepository, storyRepository, exploreRepository, userRepository, submitSurveyUseCase);
  }
}
