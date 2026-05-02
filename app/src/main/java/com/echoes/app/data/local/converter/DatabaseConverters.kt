package com.echoes.app.data.local.converter

import androidx.room.TypeConverter
import com.echoes.app.data.local.model.AffiliationType
import com.echoes.app.data.local.model.CapsuleMediaType
import com.echoes.app.data.local.model.UnlockType

class DatabaseConverters {

    @TypeConverter
    fun fromAffiliationType(value: AffiliationType): String = value.name

    @TypeConverter
    fun toAffiliationType(value: String): AffiliationType = AffiliationType.valueOf(value)

    @TypeConverter
    fun fromCapsuleMediaType(value: CapsuleMediaType): String = value.name

    @TypeConverter
    fun toCapsuleMediaType(value: String): CapsuleMediaType = CapsuleMediaType.valueOf(value)

    @TypeConverter
    fun fromUnlockType(value: UnlockType): String = value.name

    @TypeConverter
    fun toUnlockType(value: String): UnlockType = UnlockType.valueOf(value)
}
