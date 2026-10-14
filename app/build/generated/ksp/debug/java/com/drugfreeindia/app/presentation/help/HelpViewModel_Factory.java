package com.drugfreeindia.app.presentation.help;

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
public final class HelpViewModel_Factory implements Factory<HelpViewModel> {
  private final Provider<SupportRepository> supportRepositoryProvider;

  public HelpViewModel_Factory(Provider<SupportRepository> supportRepositoryProvider) {
    this.supportRepositoryProvider = supportRepositoryProvider;
  }

  @Override
  public HelpViewModel get() {
    return newInstance(supportRepositoryProvider.get());
  }

  public static HelpViewModel_Factory create(
      Provider<SupportRepository> supportRepositoryProvider) {
    return new HelpViewModel_Factory(supportRepositoryProvider);
  }

  public static HelpViewModel newInstance(SupportRepository supportRepository) {
    return new HelpViewModel(supportRepository);
  }
}
