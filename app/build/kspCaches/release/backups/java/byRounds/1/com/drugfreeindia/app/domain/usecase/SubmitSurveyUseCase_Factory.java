package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.ImpactRepository;
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
public final class SubmitSurveyUseCase_Factory implements Factory<SubmitSurveyUseCase> {
  private final Provider<ImpactRepository> impactRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public SubmitSurveyUseCase_Factory(Provider<ImpactRepository> impactRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.impactRepositoryProvider = impactRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public SubmitSurveyUseCase get() {
    return newInstance(impactRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static SubmitSurveyUseCase_Factory create(
      Provider<ImpactRepository> impactRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new SubmitSurveyUseCase_Factory(impactRepositoryProvider, userRepositoryProvider);
  }

  public static SubmitSurveyUseCase newInstance(ImpactRepository impactRepository,
      UserRepository userRepository) {
    return new SubmitSurveyUseCase(impactRepository, userRepository);
  }
}
