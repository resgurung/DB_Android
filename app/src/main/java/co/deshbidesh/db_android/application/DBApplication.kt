package co.deshbidesh.db_android.application

import android.app.Application
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_home_feature.domain.DBHomeRepository
import co.deshbidesh.db_android.db_home_feature.viewmodels.HomeViewModelFactory
import co.deshbidesh.db_android.db_network.domain.DBNewsRepositoryImp
import co.deshbidesh.db_android.db_news_feature.news.presentation.viewmodel.DBNewsArticleViewModelFactory
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration


class DBApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // initial setting for the app
        DBPreferenceHelper.init(this)

        // setup database
        val database by lazy {
            DBDatabase.getDatabase(this)
        }

        // inislize mobile ads module
        MobileAds.initialize(this){}

        // test device for native apps : add as many device here
        val testDeviceIds = listOf("BEEB8D61A78D8C00D045C9CF724955BA")

        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()

        MobileAds.setRequestConfiguration(configuration)

        // load native ads
        //DBNativeAdService.loadAds(this)

        // home repository
        //val homeRepository by lazy {
            HomeViewModelFactory.inject(DBHomeRepository(database))
        //}

        // news view model factory injection
        DBNewsArticleViewModelFactory.inject(DBNewsRepositoryImp(database))

        DBNoteDetailViewModelFactory.inject(
            DBNoteRepository(database.noteDAO()),
            DBImageRepository(database.imageDAO())
        )
    }
}