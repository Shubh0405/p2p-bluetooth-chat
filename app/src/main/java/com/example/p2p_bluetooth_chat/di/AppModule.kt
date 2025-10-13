package com.example.p2p_bluetooth_chat.di

import android.app.Application
import com.example.p2p_bluetooth_chat.presentation.chat.viewmodel.ChatViewModel
import com.example.p2p_bluetooth_chat.presentation.home.viewmodels.HomePageViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideHomePageViewModel(application: Application): HomePageViewModel = HomePageViewModel(application)

    @Provides
    @Singleton
    fun provideChatViewModel(application: Application): ChatViewModel = ChatViewModel(application)

}