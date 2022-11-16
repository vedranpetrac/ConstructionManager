package hr.petrach.constructionmanager.dao

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "contractors")
data class Contractor @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true) var contractorId: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var specialization: String? = null,
    var picturePath: String? = null,

    ){
        override fun toString() = "$name"
    }