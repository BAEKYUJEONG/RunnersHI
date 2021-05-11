package com.A306.runnershi.Services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.absoluteValue

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService:LifecycleService() {

    var isFirstRun = true
    var buttonText = "일시정지"
    var serviceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val timeRunInSeconds = MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder:NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val totalDistance = MutableLiveData<Float>()
        val totalPace = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val totallyFinished = MutableLiveData<Int>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        totalDistance.postValue(0f)
        totalPace.postValue(0L)
        totallyFinished.postValue(0)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
            setButtonText(it)
        })
    }

    private fun setButtonText(b:Boolean){
        if (b){
            buttonText = "일시정지"
        }else{
            buttonText = "다시 달리기"
        }
    }

    private fun killService(){
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let{
            when(it.action){
                "ACTION_START_OR_RESUME_SERVICE" -> {
                    if(isFirstRun){
                        startForegroundService()
                        Timber.e("다시 시작?")
                        isFirstRun = false
                    }else{
                        Timber.d("RESUMING SERVICE")
                        startTimer()
                        startPace()
                    }
                }
                "ACTION_PAUSE_SERVICE" -> {
                    pauseService()
//                    Timber.d("Pause service")
                }
                "ACTION_STOP_SERVICE" -> {
//                    Timber.d("Stop service")
                    killService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch{
            while(isTracking.value!!){
                // 시작 시간과 현재 시간 차이
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                if(timeRunInMillis.value!! >= lastSecondTimestamp + 1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }

                delay(500L)
            }
            timeRun += lapTime
        }
    }

    private fun startPace(){
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                if (totalDistance.value!! > 0f && timeRunInMillis.value!! > 0L){
                    totalPace.postValue((timeRunInMillis.value!! / totalDistance.value!!).toLong() * 1000L)
                }
                delay(3000L)
            }
        }
    }


    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "일시정지" else "계속하기"
        val pendingIntent = if(isTracking){
            val pauseIntent = Intent(this, TrackingService::class.java).apply{
                action = "ACTION_PAUSE_SERVICE"
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply{
                action = "ACTION_START_OR_RESUME_SERVICE"
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if(!serviceKilled){
            curNotificationBuilder = baseNotificationBuilder
                    .addAction(R.drawable.ic_baseline_directions_run_24, notificationActionText, pendingIntent)
            notificationManager.notify(NotificationManager.IMPORTANCE_LOW, curNotificationBuilder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request = LocationRequest().apply{
                    interval = 5000L
                    fastestInterval = 2000L
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            }
        } else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations.let{ locations ->
                    for(location in locations){
                        addPathPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?){
        location?.let{
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply{
                if (last().size > 1) {
                    val result = FloatArray(1)
                    Location.distanceBetween(last().last().latitude, last().last().longitude, location.latitude, location.longitude, result)
                    totalDistance.postValue(
                        totalDistance.value?.plus(result[0])
                    )
                }
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply{
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService(){
        startTimer()
        startPace()
        isTracking.postValue(true)
        totallyFinished.postValue(0)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

//        val notificationBuilder = NotificationCompat.Builder(this, "runnershi_channel")
//            .setAutoCancel(false)
//            .setOngoing(true)
//            .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
//            .setContentTitle("Runners' Hi")
//            .setContentText("00:00:00")
//            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NotificationManager.IMPORTANCE_LOW, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer {
            if (!serviceKilled){
                val notification = curNotificationBuilder
                        .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))
                notificationManager.notify(NotificationManager.IMPORTANCE_LOW, notification.build())
            }
        })

    }

//    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
//        this,
//        0,
//        Intent(this, MainActivity::class.java).also{
//            it.action = "ACTION_SHOW_TRACKING_FRAGMENT"
//        },
//        FLAG_UPDATE_CURRENT
//    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel("runnershi_channel", "RunnersHi", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}