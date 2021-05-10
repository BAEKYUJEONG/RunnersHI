package com.A306.runnershi.DI

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.R
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(@ApplicationContext app : Context) = FusedLocationProviderClient(app)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext app: Context) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java).also{
            it.action = "ACTION_SHOW_TRACKING_FRAGMENT"
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    fun provideBaseNotificationBuilder(@ApplicationContext app: Context, pendingIntent: PendingIntent) = NotificationCompat.Builder(app, "runnershi_channel")
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
        .setContentTitle("Runners' Hi")
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)

}