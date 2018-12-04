package io.github.apm29.driodgo.model.artifact.db

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.apm29.driodgo.model.artifact.bean.*

@TypeConverters(ArtifactCardDataBase.TagsConverter::class)
@Database(entities = [CardEntity::class], version = 8, exportSchema = true)
abstract class ArtifactCardDataBase : RoomDatabase() {

    abstract fun getArtifactCardDao(): ArtifactCardDao


    companion object {

        private const val DATABASE_NAME = "artifact-db"

        // For Singleton instantiation
        @Volatile
        private var instance: ArtifactCardDataBase? = null

        fun getInstance(context: Context): ArtifactCardDataBase {
            return instance ?: synchronized(this) {
                instance ?: ArtifactCardDataBase.buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): ArtifactCardDataBase {
            return Room.databaseBuilder(
                context, ArtifactCardDataBase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }

    }
    object TagsConverter {

        @TypeConverter
        @JvmStatic
        fun fromString(value: String?): List<CardReference>? {
            if(value == null)
                return emptyList()
            val listType = object : TypeToken<List<CardReference>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromArrayList(list: List<CardReference>?): String? {
            val gson = Gson()
            return gson.toJson(list)
        }

        @TypeConverter
        @JvmStatic
        fun fromLargeImage(value: LargeImage?):String?{
            return value?.schinese
        }

        @TypeConverter
        @JvmStatic
        fun toLargeImage(value: String?): LargeImage {
            return LargeImage(
                schinese = value
            )
        }

        @TypeConverter
        @JvmStatic
        fun toCardText(value: String?): CardText {
            return CardText(
                schinese = value
            )
        }

        @TypeConverter
        @JvmStatic
        fun fromCardText(value: CardText?):String?{
            return value?.schinese
        }


        @TypeConverter
        @JvmStatic
        fun toCardName(value: String?): CardName {
            return CardName(
                schinese = value
            )
        }

        @TypeConverter
        @JvmStatic
        fun fromCardName(value: CardName?):String?{
            return value?.schinese
        }


        @TypeConverter
        @JvmStatic
        fun toIngameImage(value: String?): InGameImage {
            return InGameImage(
                url = value
            )
        }

        @TypeConverter
        @JvmStatic
        fun fromInGameImage(value: InGameImage?):String?{
            return value?.url
        }

        @TypeConverter
        @JvmStatic
        fun toMiniImage(value: String?): MiniImage {
            return MiniImage(
                default = value
            )
        }

        @TypeConverter
        @JvmStatic
        fun fromMiniImage(value: MiniImage?):String?{
            return value?.default
        }
    }
}
