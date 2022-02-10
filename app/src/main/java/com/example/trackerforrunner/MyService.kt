package com.example.trackerforrunner

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.SystemClock.sleep
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.trackerforrunner.MainActivity.Companion.DISTANCE
import com.example.trackerforrunner.fragments.MainFragment
import kotlin.concurrent.thread

class MyService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager

    private var lastLocation: Location? = null
    private var currentLocation: Location? = null
    private var distance: Int? = 0
    private var speed: Float = 0.0f

    private var seconds: Int = 0
    private var minutes: Int = 0

    private lateinit var thread: Thread

    private var bool: Boolean = true

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        distance = intent?.getIntExtra(DISTANCE, 0)
        val intent = Intent(MainFragment.BROADCAST_ACTION)
        thread = thread {
                while (bool) {
                intent.putExtra(MainFragment.TIME, chronometer(distance!! > 0))
                intent.putExtra(MainFragment.LAST_DISTANCE, distance)
                intent.putExtra(MainFragment.SPEED, speed)
                Log.d("myLog", "onStartCommand: $distance")
                sendBroadcast(intent)
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkPermission()
    }

    private fun checkPermission() {
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 1, 1.0F, this)
        }
    }

    override fun onDestroy() {
        bool = false
        thread.interrupt()
        while (!thread.isInterrupted)
        Log.d("myLog", "onDestroy: ${thread.isInterrupted} ")
        locationManager.removeUpdates(this)
        super.onDestroy()
    }

    override fun onLocationChanged(loc: Location) {
        currentLocation = loc
        if (lastLocation != null && currentLocation?.hasSpeed() == true) {
            distance = distance?.minus(lastLocation?.distanceTo(currentLocation)?.toInt()!!)
            speed = Math.round(loc.speed * 10f) / 10f
        } else {
            speed = 0f
        }
        lastLocation = loc
    }

    private fun chronometer(boolean: Boolean): String {
        sleep(1000)
        if (boolean) {
            if (seconds < 59) {
                seconds += 1
            } else {
                minutes += 1
                seconds = 0
            }
            return if (seconds < 10) {
                "$minutes:0$seconds"
            } else {
                "$minutes:$seconds"
            }
        } else {
            return if (seconds < 10) {
                "$minutes:0$seconds"
            } else {
                "$minutes:$seconds"
            }
        }
    }
}