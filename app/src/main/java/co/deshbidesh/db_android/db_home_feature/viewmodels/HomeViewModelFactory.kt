package co.deshbidesh.db_android.db_home_feature.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_home_feature.domain.DBHomeRepository

object HomeViewModelFactory:  ViewModelProvider.Factory {

    private lateinit var repository: DBHomeRepository

    fun inject(repository: DBHomeRepository) {
        HomeViewModelFactory.repository = repository
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}