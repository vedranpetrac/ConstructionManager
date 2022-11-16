package hr.petrach.constructionmanager.dao

import androidx.room.*

@Dao
interface ConstructionDao {

    @Query("select * from constructions")
    fun getAllConstructionAndContractor(): MutableList<ConstructionAndContractor>
    @Query("select * from constructions where constructionId=:id")
    fun getConstructionAndContractor(id: Long): ConstructionAndContractor?
    @Insert
    fun insert(construction: Construction)
    @Update
    fun update(construction: Construction)
    @Delete
    fun delete(construction: Construction)

}