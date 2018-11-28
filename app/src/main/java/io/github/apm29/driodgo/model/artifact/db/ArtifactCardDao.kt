package io.github.apm29.driodgo.model.artifact.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single


@Dao
interface ArtifactCardDao {

    @Query("Select * from artifact_card where card_type = :type")
    fun  getCardByType(type:String):Single<List<CardEntity>>

    @Query("Select * from artifact_card ")
    fun  getCardAll():Single<List<CardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cards:List<CardEntity>):List<Long>
}