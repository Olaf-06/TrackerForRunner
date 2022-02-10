package com.example.trackerforrunner.ciceroneNavigation

import com.example.trackerforrunner.fragments.MainFragment
import com.example.trackerforrunner.fragments.SetDistanceFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {
    fun mainFragment() = FragmentScreen {
        MainFragment()
    }

    fun setDistanceFragment() = FragmentScreen {
        SetDistanceFragment()
    }
}