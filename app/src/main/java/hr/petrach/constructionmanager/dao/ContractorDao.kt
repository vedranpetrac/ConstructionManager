package hr.petrach.constructionmanager.dao

import androidx.room.*

@Dao
interface ContractorDao {
    @Query("select * from contractors")
    fun getWorkers(): MutableList<Contractor>
    @Query("select * from contractors where contractorId=:id")
    fun getWorker(id: Long) : Contractor?
    @Insert
    fun insert(contractor: Contractor)
    @Update
    fun update(contractor: Contractor)
    @Delete
    fun delete(contractor: Contractor)
}