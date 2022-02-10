package com.example.trackerforrunner.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.trackerforrunner.MainActivity.Companion.DISTANCE
import com.example.trackerforrunner.MainActivity.Companion.DISTANCE_SET
import com.example.trackerforrunner.MainActivity.Companion.START_CLICKED
import com.example.trackerforrunner.MainActivity.Companion.editor
import com.example.trackerforrunner.MainActivity.Companion.router
import com.example.trackerforrunner.MainActivity.Companion.sharedPreferences
import com.example.trackerforrunner.MyService
import com.example.trackerforrunner.R
import com.example.trackerforrunner.ciceroneNavigation.Screens


class MainFragment : Fragment(), View.OnClickListener {

    companion object {
        const val BROADCAST_ACTION = "com.example.trackerforrunner"
        const val LAST_DISTANCE = "lastDistance"
        const val SPEED = "speed"
        const val TIME = "time"


    }

    private lateinit var broadcastReceiver: BroadcastReceiver

    private var distance: Int? = 0
    private var speed: Float? = 0f
    private var time: String? = "0:00"

    private lateinit var tvDistance: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvSpeed: TextView

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var btnSetDistance: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        tvDistance = view.findViewById<TextView>(R.id.tvDistance)
        tvSpeed = view.findViewById<TextView>(R.id.tvSpeed)
        tvTime = view.findViewById<TextView>(R.id.tvTime)

        btnStart = view.findViewById<Button>(R.id.btnStart)
        btnStop = view.findViewById<Button>(R.id.btnStop)
        btnSetDistance = view.findViewById<Button>(R.id.btnSetDistance)

        btnStart.setOnClickListener(this)
        btnStop.setOnClickListener(this)
        btnSetDistance.setOnClickListener(this)

        tvDistance.text = "0 м"
        tvSpeed.text = "0.0 м/c"

        return view
    }

    override fun onResume() {
        super.onResume()
        tvDistance.text = "${sharedPreferences.getInt(DISTANCE, 0)}"
    }

    override fun onDestroy() {
        context?.unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnStart -> {
                if (!sharedPreferences.getBoolean(START_CLICKED,false)) {
                    if (sharedPreferences.getBoolean(DISTANCE_SET, false)) {
                        if (onGPSEnabled()) {
                            context?.startService(
                                Intent(activity, MyService::class.java)
                                    .putExtra(DISTANCE, sharedPreferences.getInt(DISTANCE, 0))
                            )

                            registerReceiver()

                            editor.putBoolean(START_CLICKED, true).apply()
                        } else {
                            Toast.makeText(context, "Включите геолокацию!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "Задайте дистанцию!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Вы уже нажали на кнопку!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnStop -> {
                if (sharedPreferences.getBoolean(START_CLICKED, false)) {
                    editor.putBoolean(START_CLICKED, false).apply()
                    editor.putBoolean(DISTANCE_SET, false).apply()
                    editor.putInt(DISTANCE, 0).apply()
                    context?.unregisterReceiver(broadcastReceiver)
                    context?.stopService(Intent(context, MyService::class.java))
                    tvSpeed.text = "0.0 м/с"
                    tvDistance.text = "0 м"
                } else {
                    Toast.makeText(context, "Задайте дистанцию и нажмите кнопку СТАРТ!", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnSetDistance -> {
                if (!sharedPreferences.getBoolean(START_CLICKED, false)) {
                    router.navigateTo(Screens.setDistanceFragment())
                } else {
                    Toast.makeText(context, "Нажмите кнопку стоп, чтоб изменить дистанцию!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onGPSEnabled() : Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun registerReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            override fun onReceive(context: Context?, intent: Intent?) {
                distance = intent?.getIntExtra(LAST_DISTANCE, 0)
                if (distance!! > 0) {
                    speed = intent?.getFloatExtra(SPEED, 0f)
                    time = intent?.getStringExtra(TIME)
                    tvDistance.text = "$distance м"
                    tvSpeed.text = "$speed м/c"
                    tvTime.text = time
                } else {
                    Toast.makeText(context, "Поздравляю! Вы пробежали дистанцию!", Toast.LENGTH_LONG).show()
                    tvDistance.text = "0 м"
                    tvSpeed.text = "0 м/c"
                }
            }
        }

        context?.registerReceiver(
            broadcastReceiver,
            IntentFilter(BROADCAST_ACTION)
        )
    }
}
