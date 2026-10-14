package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.StoryRepository;
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
public final class SubmitChoiceUseCase_Factory implements Factory<SubmitChoiceUseCase> {
  private final Provider<StoryRepository> storyRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public SubmitChoiceUseCase_Factory(Provider<StoryRepository> storyRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.storyRepositoryProvider = storyRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public SubmitChoiceUseCase get() {
    return newInstance(storyRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static SubmitChoiceUseCase_Factory create(
      Provider<StoryRepository> storyRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new SubmitChoiceUseCase_Factory(storyRepositoryProvider, userRepositoryProvider);
  }

  public static SubmitChoiceUseCase newInstance(StoryRepository storyRepository,
      UserRepository userRepository) {
    return new SubmitChoiceUseCase(storyRepository, userRepository);
  }
}
