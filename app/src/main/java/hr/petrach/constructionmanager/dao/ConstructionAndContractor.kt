package hr.petrach.constructionmanager.dao

import androidx.room.Embedded
import androidx.room.Relation

data class ConstructionAndContractor (
    @Embedded val construction: Construction,
    @Relation(
        parentColumn = "contractorSignedId",
        entityColumn = "contractorId"
    )
    val contractor: Contractor? = null
)