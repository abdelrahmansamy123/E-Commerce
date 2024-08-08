package com.training.ecommerce.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.training.ecommerce.data.dataSource.dataStore.DataStoreKeys.IS_USER_LOGGED_IN
import com.training.ecommerce.data.dataSource.dataStore.DataStoreKeys.USER_ID
import com.training.ecommerce.data.dataSource.dataStore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private var context: Context) : UserPreferencesRepository {


    //write to DataStore
    override suspend fun saveLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_USER_LOGGED_IN] = isLoggedIn
        }
    }

    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            //Return the logged-in state, defaulting to false if not found
            preferences[IS_USER_LOGGED_IN] ?: false
        }
    }

    override suspend fun saveUserID(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }
}