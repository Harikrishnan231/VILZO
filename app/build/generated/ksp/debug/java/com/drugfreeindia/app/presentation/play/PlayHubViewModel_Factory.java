package com.drugfreeindia.app.presentation.play;

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
public final class PlayHubViewModel_Factory implements Factory<PlayHubViewModel> {
  private final Provider<StoryRepository> storyRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public PlayHubViewModel_Factory(Provider<StoryRepository> storyRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.storyRepositoryProvider = storyRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public PlayHubViewModel get() {
    return newInstance(storyRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static PlayHubViewModel_Factory create(Provider<StoryRepository> storyRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new PlayHubViewModel_Factory(storyRepositoryProvider, userRepositoryProvider);
  }

  public static PlayHubViewModel newInstance(StoryRepository storyRepository,
      UserRepository userRepository) {
    return new PlayHubViewModel(storyRepository, userRepository);
  }
}
