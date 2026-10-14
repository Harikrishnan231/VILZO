package com.drugfreeindia.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.drugfreeindia.app.data.local.AppDatabase;
import com.drugfreeindia.app.data.local.DfiDataStore;
import com.drugfreeindia.app.data.repository.ExploreRepositoryImpl;
import com.drugfreeindia.app.data.repository.ImpactRepositoryImpl;
import com.drugfreeindia.app.data.repository.StoryRepositoryImpl;
import com.drugfreeindia.app.data.repository.SupportRepositoryImpl;
import com.drugfreeindia.app.data.repository.UserRepositoryImpl;
import com.drugfreeindia.app.data.secure.SecurePreferencesManager;
import com.drugfreeindia.app.data.seed.DatabaseInitializer;
import com.drugfreeindia.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.drugfreeindia.app.domain.usecase.JoinActivityUseCase;
import com.drugfreeindia.app.domain.usecase.PlayStoryUseCase;
import com.drugfreeindia.app.domain.usecase.SubmitChoiceUseCase;
import com.drugfreeindia.app.domain.usecase.SubmitSurveyUseCase;
import com.drugfreeindia.app.presentation.explore.ExploreViewModel;
import com.drugfreeindia.app.presentation.explore.ExploreViewModel_HiltModules;
import com.drugfreeindia.app.presentation.help.HelpViewModel;
import com.drugfreeindia.app.presentation.help.HelpViewModel_HiltModules;
import com.drugfreeindia.app.presentation.home.HomeViewModel;
import com.drugfreeindia.app.presentation.home.HomeViewModel_HiltModules;
import com.drugfreeindia.app.presentation.impact.ImpactViewModel;
import com.drugfreeindia.app.presentation.impact.ImpactViewModel_HiltModules;
import com.drugfreeindia.app.presentation.onboarding.OnboardingViewModel;
import com.drugfreeindia.app.presentation.onboarding.OnboardingViewModel_HiltModules;
import com.drugfreeindia.app.presentation.play.PlayHubViewModel;
import com.drugfreeindia.app.presentation.play.PlayHubViewModel_HiltModules;
import com.drugfreeindia.app.presentation.play.story.StoryPlayViewModel;
import com.drugfreeindia.app.presentation.play.story.StoryPlayViewModel_HiltModules;
import com.drugfreeindia.app.presentation.profile.ProfileViewModel;
import com.drugfreeindia.app.presentation.profile.ProfileViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerDfiApplication_HiltComponents_SingletonC {
  private DaggerDfiApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public DfiApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements DfiApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements DfiApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements DfiApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements DfiApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements DfiApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements DfiApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements DfiApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public DfiApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends DfiApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends DfiApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends DfiApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends DfiApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_explore_ExploreViewModel, ExploreViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_help_HelpViewModel, HelpViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_impact_ImpactViewModel, ImpactViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_play_PlayHubViewModel, PlayHubViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_play_story_StoryPlayViewModel, StoryPlayViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectDatabaseInitializer(instance, singletonCImpl.databaseInitializerProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_drugfreeindia_app_presentation_onboarding_OnboardingViewModel = "com.drugfreeindia.app.presentation.onboarding.OnboardingViewModel";

      static String com_drugfreeindia_app_presentation_play_PlayHubViewModel = "com.drugfreeindia.app.presentation.play.PlayHubViewModel";

      static String com_drugfreeindia_app_presentation_profile_ProfileViewModel = "com.drugfreeindia.app.presentation.profile.ProfileViewModel";

      static String com_drugfreeindia_app_presentation_impact_ImpactViewModel = "com.drugfreeindia.app.presentation.impact.ImpactViewModel";

      static String com_drugfreeindia_app_presentation_help_HelpViewModel = "com.drugfreeindia.app.presentation.help.HelpViewModel";

      static String com_drugfreeindia_app_presentation_home_HomeViewModel = "com.drugfreeindia.app.presentation.home.HomeViewModel";

      static String com_drugfreeindia_app_presentation_play_story_StoryPlayViewModel = "com.drugfreeindia.app.presentation.play.story.StoryPlayViewModel";

      static String com_drugfreeindia_app_presentation_explore_ExploreViewModel = "com.drugfreeindia.app.presentation.explore.ExploreViewModel";

      @KeepFieldType
      OnboardingViewModel com_drugfreeindia_app_presentation_onboarding_OnboardingViewModel2;

      @KeepFieldType
      PlayHubViewModel com_drugfreeindia_app_presentation_play_PlayHubViewModel2;

      @KeepFieldType
      ProfileViewModel com_drugfreeindia_app_presentation_profile_ProfileViewModel2;

      @KeepFieldType
      ImpactViewModel com_drugfreeindia_app_presentation_impact_ImpactViewModel2;

      @KeepFieldType
      HelpViewModel com_drugfreeindia_app_presentation_help_HelpViewModel2;

      @KeepFieldType
      HomeViewModel com_drugfreeindia_app_presentation_home_HomeViewModel2;

      @KeepFieldType
      StoryPlayViewModel com_drugfreeindia_app_presentation_play_story_StoryPlayViewModel2;

      @KeepFieldType
      ExploreViewModel com_drugfreeindia_app_presentation_explore_ExploreViewModel2;
    }
  }

  private static final class ViewModelCImpl extends DfiApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ExploreViewModel> exploreViewModelProvider;

    private Provider<HelpViewModel> helpViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<ImpactViewModel> impactViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<PlayHubViewModel> playHubViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<StoryPlayViewModel> storyPlayViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private JoinActivityUseCase joinActivityUseCase() {
      return new JoinActivityUseCase(singletonCImpl.exploreRepositoryImplProvider.get(), singletonCImpl.userRepositoryImplProvider.get());
    }

    private SubmitSurveyUseCase submitSurveyUseCase() {
      return new SubmitSurveyUseCase(singletonCImpl.impactRepositoryImplProvider.get(), singletonCImpl.userRepositoryImplProvider.get());
    }

    private PlayStoryUseCase playStoryUseCase() {
      return new PlayStoryUseCase(singletonCImpl.storyRepositoryImplProvider.get());
    }

    private SubmitChoiceUseCase submitChoiceUseCase() {
      return new SubmitChoiceUseCase(singletonCImpl.storyRepositoryImplProvider.get(), singletonCImpl.userRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.exploreViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.helpViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.impactViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.playHubViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.storyPlayViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_explore_ExploreViewModel, ((Provider) exploreViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_help_HelpViewModel, ((Provider) helpViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_impact_ImpactViewModel, ((Provider) impactViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_play_PlayHubViewModel, ((Provider) playHubViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_drugfreeindia_app_presentation_play_story_StoryPlayViewModel, ((Provider) storyPlayViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_drugfreeindia_app_presentation_play_PlayHubViewModel = "com.drugfreeindia.app.presentation.play.PlayHubViewModel";

      static String com_drugfreeindia_app_presentation_play_story_StoryPlayViewModel = "com.drugfreeindia.app.presentation.play.story.StoryPlayViewModel";

      static String com_drugfreeindia_app_presentation_explore_ExploreViewModel = "com.drugfreeindia.app.presentation.explore.ExploreViewModel";

      static String com_drugfreeindia_app_presentation_help_HelpViewModel = "com.drugfreeindia.app.presentation.help.HelpViewModel";

      static String com_drugfreeindia_app_presentation_home_HomeViewModel = "com.drugfreeindia.app.presentation.home.HomeViewModel";

      static String com_drugfreeindia_app_presentation_impact_ImpactViewModel = "com.drugfreeindia.app.presentation.impact.ImpactViewModel";

      static String com_drugfreeindia_app_presentation_profile_ProfileViewModel = "com.drugfreeindia.app.presentation.profile.ProfileViewModel";

      static String com_drugfreeindia_app_presentation_onboarding_OnboardingViewModel = "com.drugfreeindia.app.presentation.onboarding.OnboardingViewModel";

      @KeepFieldType
      PlayHubViewModel com_drugfreeindia_app_presentation_play_PlayHubViewModel2;

      @KeepFieldType
      StoryPlayViewModel com_drugfreeindia_app_presentation_play_story_StoryPlayViewModel2;

      @KeepFieldType
      ExploreViewModel com_drugfreeindia_app_presentation_explore_ExploreViewModel2;

      @KeepFieldType
      HelpViewModel com_drugfreeindia_app_presentation_help_HelpViewModel2;

      @KeepFieldType
      HomeViewModel com_drugfreeindia_app_presentation_home_HomeViewModel2;

      @KeepFieldType
      ImpactViewModel com_drugfreeindia_app_presentation_impact_ImpactViewModel2;

      @KeepFieldType
      ProfileViewModel com_drugfreeindia_app_presentation_profile_ProfileViewModel2;

      @KeepFieldType
      OnboardingViewModel com_drugfreeindia_app_presentation_onboarding_OnboardingViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.drugfreeindia.app.presentation.explore.ExploreViewModel 
          return (T) new ExploreViewModel(singletonCImpl.exploreRepositoryImplProvider.get(), viewModelCImpl.joinActivityUseCase());

          case 1: // com.drugfreeindia.app.presentation.help.HelpViewModel 
          return (T) new HelpViewModel(singletonCImpl.supportRepositoryImplProvider.get());

          case 2: // com.drugfreeindia.app.presentation.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.userRepositoryImplProvider.get(), singletonCImpl.impactRepositoryImplProvider.get(), singletonCImpl.exploreRepositoryImplProvider.get(), singletonCImpl.dfiDataStoreProvider.get());

          case 3: // com.drugfreeindia.app.presentation.impact.ImpactViewModel 
          return (T) new ImpactViewModel(singletonCImpl.impactRepositoryImplProvider.get(), singletonCImpl.storyRepositoryImplProvider.get(), singletonCImpl.exploreRepositoryImplProvider.get(), singletonCImpl.userRepositoryImplProvider.get(), viewModelCImpl.submitSurveyUseCase());

          case 4: // com.drugfreeindia.app.presentation.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.userRepositoryImplProvider.get(), singletonCImpl.dfiDataStoreProvider.get());

          case 5: // com.drugfreeindia.app.presentation.play.PlayHubViewModel 
          return (T) new PlayHubViewModel(singletonCImpl.storyRepositoryImplProvider.get(), singletonCImpl.userRepositoryImplProvider.get());

          case 6: // com.drugfreeindia.app.presentation.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.userRepositoryImplProvider.get(), singletonCImpl.dfiDataStoreProvider.get());

          case 7: // com.drugfreeindia.app.presentation.play.story.StoryPlayViewModel 
          return (T) new StoryPlayViewModel(viewModelCImpl.savedStateHandle, viewModelCImpl.playStoryUseCase(), viewModelCImpl.submitChoiceUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends DfiApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends DfiApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends DfiApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<DatabaseInitializer> databaseInitializerProvider;

    private Provider<ExploreRepositoryImpl> exploreRepositoryImplProvider;

    private Provider<DfiDataStore> dfiDataStoreProvider;

    private Provider<SecurePreferencesManager> securePreferencesManagerProvider;

    private Provider<UserRepositoryImpl> userRepositoryImplProvider;

    private Provider<SupportRepositoryImpl> supportRepositoryImplProvider;

    private Provider<ImpactRepositoryImpl> impactRepositoryImplProvider;

    private Provider<StoryRepositoryImpl> storyRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.databaseInitializerProvider = DoubleCheck.provider(new SwitchingProvider<DatabaseInitializer>(singletonCImpl, 0));
      this.exploreRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ExploreRepositoryImpl>(singletonCImpl, 2));
      this.dfiDataStoreProvider = DoubleCheck.provider(new SwitchingProvider<DfiDataStore>(singletonCImpl, 4));
      this.securePreferencesManagerProvider = DoubleCheck.provider(new SwitchingProvider<SecurePreferencesManager>(singletonCImpl, 5));
      this.userRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<UserRepositoryImpl>(singletonCImpl, 3));
      this.supportRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SupportRepositoryImpl>(singletonCImpl, 6));
      this.impactRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<ImpactRepositoryImpl>(singletonCImpl, 7));
      this.storyRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<StoryRepositoryImpl>(singletonCImpl, 8));
    }

    @Override
    public void injectDfiApplication(DfiApplication dfiApplication) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.drugfreeindia.app.data.seed.DatabaseInitializer 
          return (T) new DatabaseInitializer(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideDatabaseProvider.get());

          case 1: // com.drugfreeindia.app.data.local.AppDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.drugfreeindia.app.data.repository.ExploreRepositoryImpl 
          return (T) new ExploreRepositoryImpl(singletonCImpl.provideDatabaseProvider.get());

          case 3: // com.drugfreeindia.app.data.repository.UserRepositoryImpl 
          return (T) new UserRepositoryImpl(singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.dfiDataStoreProvider.get(), singletonCImpl.securePreferencesManagerProvider.get());

          case 4: // com.drugfreeindia.app.data.local.DfiDataStore 
          return (T) new DfiDataStore(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.drugfreeindia.app.data.secure.SecurePreferencesManager 
          return (T) new SecurePreferencesManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.drugfreeindia.app.data.repository.SupportRepositoryImpl 
          return (T) new SupportRepositoryImpl(singletonCImpl.provideDatabaseProvider.get(), singletonCImpl.securePreferencesManagerProvider.get());

          case 7: // com.drugfreeindia.app.data.repository.ImpactRepositoryImpl 
          return (T) new ImpactRepositoryImpl(singletonCImpl.provideDatabaseProvider.get());

          case 8: // com.drugfreeindia.app.data.repository.StoryRepositoryImpl 
          return (T) new StoryRepositoryImpl(singletonCImpl.provideDatabaseProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
