package com.example.trackerforrunner

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.trackerforrunner.ciceroneNavigation.MyRouter
import com.example.trackerforrunner.ciceroneNavigation.Screens
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.androidx.AppNavigator

class MainActivity : AppCompatActivity() {

    companion object {
        internal lateinit var router: Router

        internal lateinit var sharedPreferences: SharedPreferences
        internal lateinit var editor: SharedPreferences.Editor

        const val SETTING = "SETTING"
        const val DISTANCE = "distance"
        const val START_CLICKED = "start_clicked"
        const val DISTANCE_SET = "distance_set"
    }

    private val myRouter: MyRouter = MyRouter()
    private val navigator: Navigator = AppNavigator(this, R.id.container)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        router = myRouter.router
        router.newRootScreen(Screens.mainFragment())

        sharedPreferences = getSharedPreferences(SETTING, MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if(sharedPreferences.getBoolean(START_CLICKED, false)){
            editor.putBoolean(START_CLICKED, false).apply()
        }

        if(sharedPreferences.getBoolean(DISTANCE_SET, false)){
            editor.putBoolean(DISTANCE_SET, false).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        myRouter.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        myRouter.navigatorHolder.removeNavigator()
        editor.putInt(DISTANCE, 0).apply()
    }

    override fun onDestroy() {
        stopService(Intent(this, MyService::class.java))
        super.onDestroy()
    }

}