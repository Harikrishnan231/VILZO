package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.SupportRepository;
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
public final class FindCentresUseCase_Factory implements Factory<FindCentresUseCase> {
  private final Provider<SupportRepository> supportRepositoryProvider;

  public FindCentresUseCase_Factory(Provider<SupportRepository> supportRepositoryProvider) {
    this.supportRepositoryProvider = supportRepositoryProvider;
  }

  @Override
  public FindCentresUseCase get() {
    return newInstance(supportRepositoryProvider.get());
  }

  public static FindCentresUseCase_Factory create(
      Provider<SupportRepository> supportRepositoryProvider) {
    return new FindCentresUseCase_Factory(supportRepositoryProvider);
  }

  public static FindCentresUseCase newInstance(SupportRepository supportRepository) {
    return new FindCentresUseCase(supportRepository);
  }
}
