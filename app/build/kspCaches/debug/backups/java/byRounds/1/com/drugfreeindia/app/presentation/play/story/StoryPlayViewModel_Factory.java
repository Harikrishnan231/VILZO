package com.drugfreeindia.app.presentation.play.story;

import androidx.lifecycle.SavedStateHandle;
import com.drugfreeindia.app.domain.usecase.PlayStoryUseCase;
import com.drugfreeindia.app.domain.usecase.SubmitChoiceUseCase;
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
public final class StoryPlayViewModel_Factory implements Factory<StoryPlayViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<PlayStoryUseCase> playStoryUseCaseProvider;

  private final Provider<SubmitChoiceUseCase> submitChoiceUseCaseProvider;

  public StoryPlayViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<PlayStoryUseCase> playStoryUseCaseProvider,
      Provider<SubmitChoiceUseCase> submitChoiceUseCaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.playStoryUseCaseProvider = playStoryUseCaseProvider;
    this.submitChoiceUseCaseProvider = submitChoiceUseCaseProvider;
  }

  @Override
  public StoryPlayViewModel get() {
    return newInstance(savedStateHandleProvider.get(), playStoryUseCaseProvider.get(), submitChoiceUseCaseProvider.get());
  }

  public static StoryPlayViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<PlayStoryUseCase> playStoryUseCaseProvider,
      Provider<SubmitChoiceUseCase> submitChoiceUseCaseProvider) {
    return new StoryPlayViewModel_Factory(savedStateHandleProvider, playStoryUseCaseProvider, submitChoiceUseCaseProvider);
  }

  public static StoryPlayViewModel newInstance(SavedStateHandle savedStateHandle,
      PlayStoryUseCase playStoryUseCase, SubmitChoiceUseCase submitChoiceUseCase) {
    return new StoryPlayViewModel(savedStateHandle, playStoryUseCase, submitChoiceUseCase);
  }
}
