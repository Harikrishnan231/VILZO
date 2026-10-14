package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.ImpactRepository;
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
public final class ComputeConfidenceScoreUseCase_Factory implements Factory<ComputeConfidenceScoreUseCase> {
  private final Provider<ImpactRepository> impactRepositoryProvider;

  public ComputeConfidenceScoreUseCase_Factory(
      Provider<ImpactRepository> impactRepositoryProvider) {
    this.impactRepositoryProvider = impactRepositoryProvider;
  }

  @Override
  public ComputeConfidenceScoreUseCase get() {
    return newInstance(impactRepositoryProvider.get());
  }

  public static ComputeConfidenceScoreUseCase_Factory create(
      Provider<ImpactRepository> impactRepositoryProvider) {
    return new ComputeConfidenceScoreUseCase_Factory(impactRepositoryProvider);
  }

  public static ComputeConfidenceScoreUseCase newInstance(ImpactRepository impactRepository) {
    return new ComputeConfidenceScoreUseCase(impactRepository);
  }
}
