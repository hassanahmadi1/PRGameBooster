package com.prgamebooster.domain.repository

import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.model.GameProfile
import kotlinx.coroutines.flow.Flow

/**
 * مسئول خواندن/نوشتن پروفایل مستقل هر بازی از DataStore واقعی.
 * پیاده‌سازی واقعی: [com.prgamebooster.data.repository.GameProfileRepositoryImpl]
 */
interface GameProfileRepository {
    fun observeProfile(gameId: GameId): Flow<GameProfile>
    fun observeAllProfiles(): Flow<List<GameProfile>>
    suspend fun updateProfile(profile: GameProfile)
    suspend fun resetProfile(gameId: GameId)
    suspend fun resetAllProfiles()
}
