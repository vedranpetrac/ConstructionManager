package hr.petrach.constructionmanager.dao

import androidx.room.*
import java.time.LocalDate

@Entity(tableName = "constructions")
data class Construction(
    @PrimaryKey(autoGenerate = true) var constructionId: Long? = null,
    var projectName: String? = null,
    var address: String? = null,
    var city: String? = null,
    var picturePath: String? = null,
    var startDate: LocalDate = LocalDate.now(),
    var endDate: LocalDate = LocalDate.now(),
    var contractorSignedId: Long? = null,

    //var workers: MutableList<Worker>? = null
){
    override fun toString() = "$projectName"
}