package com.A306.runnershi.Fragment.SingleRun

import android.content.Context
import android.hardware.*
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.A306.runnershi.R

class SingleRunFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_single_run, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sensorManager = activity?.applicationContext?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
}