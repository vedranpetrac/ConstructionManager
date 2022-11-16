package hr.petrach.constructionmanager

import android.app.Application
import hr.petrach.constructionmanager.dao.ConstructionDao
import hr.petrach.constructionmanager.dao.ConstructionsDatabase
import hr.petrach.constructionmanager.dao.ContractorDao

class App : Application() {

    private lateinit var workerDao: ContractorDao
    private lateinit var constructionDao: ConstructionDao

    fun getWorkerDao() = workerDao
    fun getConstructionDao() = constructionDao

    override fun onCreate() {
        super.onCreate()
        var db = ConstructionsDatabase.getInstance(this)
        workerDao = db.workerDao()
        constructionDao = db.constructionDao()
    }
}