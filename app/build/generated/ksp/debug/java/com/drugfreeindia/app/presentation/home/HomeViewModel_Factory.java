package com.drugfreeindia.app.presentation.home;

import com.drugfreeindia.app.data.local.DfiDataStore;
import com.drugfreeindia.app.domain.repository.ExploreRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<ImpactRepository> impactRepositoryProvider;

  private final Provider<ExploreRepository> exploreRepositoryProvider;

  private final Provider<DfiDataStore> dataStoreProvider;

  public HomeViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<ImpactRepository> impactRepositoryProvider,
      Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<DfiDataStore> dataStoreProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.impactRepositoryProvider = impactRepositoryProvider;
    this.exploreRepositoryProvider = exploreRepositoryProvider;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(userRepositoryProvider.get(), impactRepositoryProvider.get(), exploreRepositoryProvider.get(), dataStoreProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<ImpactRepository> impactRepositoryProvider,
      Provider<ExploreRepository> exploreRepositoryProvider,
      Provider<DfiDataStore> dataStoreProvider) {
    return new HomeViewModel_Factory(userRepositoryProvider, impactRepositoryProvider, exploreRepositoryProvider, dataStoreProvider);
  }

  public static HomeViewModel newInstance(UserRepository userRepository,
      ImpactRepository impactRepository, ExploreRepository exploreRepository,
      DfiDataStore dataStore) {
    return new HomeViewModel(userRepository, impactRepository, exploreRepository, dataStore);
  }
}
