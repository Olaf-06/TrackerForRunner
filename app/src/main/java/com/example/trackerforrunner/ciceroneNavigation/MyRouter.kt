package com.example.trackerforrunner.ciceroneNavigation

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Router

class MyRouter {
    private val cicerone: Cicerone<Router> = Cicerone.create()
    val router = cicerone.router
    val navigatorHolder = cicerone.getNavigatorHolder()
}