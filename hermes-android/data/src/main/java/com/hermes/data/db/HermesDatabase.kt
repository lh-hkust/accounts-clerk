package com.hermes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hermes.data.dao.*
import com.hermes.data.entity.*

@Database(
    entities = [
        IdentityIdentifierEntity::class,
        ApplicationEntity::class,
        ApplicationAccountEntity::class,
        IdentifierBindingEntity::class,
        AccountExtensionEntity::class,
        IdentifierDeactivationEntity::class,
        WarningRecordEntity::class,
        BindingHistoryRecordEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HermesDatabase : RoomDatabase() {
    abstract fun identityIdentifierDao(): IdentityIdentifierDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun applicationAccountDao(): ApplicationAccountDao
    abstract fun identifierBindingDao(): IdentifierBindingDao
    abstract fun accountExtensionDao(): AccountExtensionDao
    abstract fun identifierDeactivationDao(): IdentifierDeactivationDao
    abstract fun warningRecordDao(): WarningRecordDao
    abstract fun bindingHistoryRecordDao(): BindingHistoryRecordDao
}