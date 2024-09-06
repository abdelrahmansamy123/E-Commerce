package com.training.ecommerce.data.repository.user

import com.training.ecommerce.data.dataSource.dataStore.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class UserDataStoreRepositoryImpl(private var context: UserPreferencesDataSource) :
    UserPreferencesRepository {
    //write to DataStore
    override suspend fun saveLoginState(isLoggedIn: Boolean) {

    }

    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return context.isUserLoggedIn
    }

    override suspend fun saveUserID(userId: String) {

    }

    override fun getUserID(): Flow<String?> {
        TODO("Not yet implemented")
    }

    override fun saveUSerEmail(email: String) {
        TODO("Not yet implemented")
    }
}