package com.drugfreeindia.app.domain.usecase;

import com.drugfreeindia.app.domain.repository.StoryRepository;
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
public final class PlayStoryUseCase_Factory implements Factory<PlayStoryUseCase> {
  private final Provider<StoryRepository> storyRepositoryProvider;

  public PlayStoryUseCase_Factory(Provider<StoryRepository> storyRepositoryProvider) {
    this.storyRepositoryProvider = storyRepositoryProvider;
  }

  @Override
  public PlayStoryUseCase get() {
    return newInstance(storyRepositoryProvider.get());
  }

  public static PlayStoryUseCase_Factory create(Provider<StoryRepository> storyRepositoryProvider) {
    return new PlayStoryUseCase_Factory(storyRepositoryProvider);
  }

  public static PlayStoryUseCase newInstance(StoryRepository storyRepository) {
    return new PlayStoryUseCase(storyRepository);
  }
}
