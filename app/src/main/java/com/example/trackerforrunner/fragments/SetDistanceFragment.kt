package com.example.trackerforrunner.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.trackerforrunner.MainActivity.Companion.DISTANCE
import com.example.trackerforrunner.MainActivity.Companion.DISTANCE_SET
import com.example.trackerforrunner.MainActivity.Companion.editor
import com.example.trackerforrunner.MainActivity.Companion.router
import com.example.trackerforrunner.MainActivity.Companion.sharedPreferences
import com.example.trackerforrunner.R
import com.example.trackerforrunner.ciceroneNavigation.Screens

class SetDistanceFragment : Fragment(), View.OnClickListener {

    private lateinit var btnSubmit: Button
    private lateinit var etDistance: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_set_distance, container, false)

        btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        btnSubmit.setOnClickListener(this)

        etDistance = view.findViewById<EditText>(R.id.etDistance)

        return view
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.btnSubmit){
            if(Regex("^[0-9]+\$").matches(etDistance.text.toString())
                && etDistance.text.toString().length <= 5) {
                editor.putInt(DISTANCE, etDistance.text.toString().toInt()).apply()
                editor.putBoolean(DISTANCE_SET, true).apply()
                router.backTo(Screens.mainFragment())
            } else {
                Toast.makeText(context,
                    "Введённое значение должно быть натуральным числом и не больше пятизначного числа!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}