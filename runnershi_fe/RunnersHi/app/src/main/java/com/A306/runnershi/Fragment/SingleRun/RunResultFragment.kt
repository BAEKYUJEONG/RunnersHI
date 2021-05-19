package com.A306.runnershi.Fragment.SingleRun

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.DI.TrackingUtility
import com.A306.runnershi.Fragment.Home.HomeFragment
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Run
import com.A306.runnershi.R
import com.A306.runnershi.Services.Polyline
import com.A306.runnershi.Services.TrackingService
import com.A306.runnershi.ViewModel.SingleRunViewModel
import com.A306.runnershi.ViewModel.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run_result.*
import kotlinx.android.synthetic.main.item_run.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
//class RunResultFragment(distance:String, time:String, pace:String) : Fragment(R.layout.fragment_run_result) {
class RunResultFragment() : Fragment(R.layout.fragment_run_result) {

    private val userViewModel: UserViewModel by viewModels()
    var token = ""

    private val runViewModel: SingleRunViewModel by viewModels()

    private var map: GoogleMap? = null
    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeMillis = 0L

//    private val resultDistance = run.distance
//    private val resultTime = run.time
//    private val resultPace = run.pace
//    private val resultRoute = run.img

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 맵뷰를 구글맵에 연결하고 polyline 그릴 준비하기
        routeDataView.onCreate(savedInstanceState)
        routeDataView.getMapAsync {
            map = it
            addAllPolylines()
        }

        // polyline 데이터를 받아오기
        val mainActivity = activity as MainActivity
        subscrbeToObservers(mainActivity)

        // SingleRunFragment에서 최종 데이터 받아오기

//        distanceDataView.text = "${resultDistance} K"
//        timeDataView.text = resultTime
//        paceDataView.text = resultPace
//        routeDataView.apply {
////            Glide.with(this).load(resultRoute).into(routeDataView)
//        }


        userViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                Log.e("러닝 기록탭에서 유저 정보 가져오는데", "오류가 날수가 없는데...")
            } else {
                token = it.token.toString()
            }
        })
        userViewModel.userInfo.removeObserver(Observer {  })

        // 기록하고 나면 어디로 보내줄까요?
        // TODO "나중에는 detailview로 보내줄건데 지금은 home으로 보내줄게요"
        val homeFragment = HomeFragment()

//        subscribeToObservers(mainActivity, homeFragment)

        saveRunBtn.setOnClickListener {
            saveToDb(mainActivity)
//            mainActivity.makeCurrentFragment(homeFragment)
        }
    }

    private fun saveToDb(activity: MainActivity) {
        map?.snapshot { bmp ->
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val distanceInMeters = TrackingService.totalDistance.value!!.toInt()
            val avgSpeed = ((distanceInMeters / 1000f) / (curTimeMillis / 1000f / 60 / 60) * 10).roundToInt() / 10f
            val dateTimeSpent = TrackingUtility.getFormattedStopWatchTime(TrackingService.timeRunInMillis.value!!)
            val finalPace = TrackingUtility.getPaceWithMilliAndDistance(TrackingService.totalPace.value!!)

            // 타이틀도 가져올거예요!
            val title = titleTextView.text.toString()

            // 이 run을 우선 DB에 보내서 저장해두겠습니다!
            val run = Run(title, bmp, dateTimestamp, avgSpeed, distanceInMeters, dateTimeSpent, finalPace)
            runViewModel.insertRun(run)

            // 그런데 DB에서 요구하는 모양새가 쫌 다르니까 맞춰서 같이 보내줄게요
            val body = mapOf(
                "distance" to distanceInMeters,
                "endTime" to dateTimestamp,
                "runningTime" to dateTimeSpent,
                "title" to title)
            RetrofitClient.getInstance().recordCreate(token, body).enqueue(afterSaveToDB)
            activity.sendCommandToService("ACTION_STOP_SERVICE")
        }
    }

    private var afterSaveToDB = object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("세이브디비에서", "통신이 안됐답니다!")
        }
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            val result = response.body()?.string().toString()
            if (result == "SUCCESS") {
                Toast.makeText(context, "달리기를 성공적으로 기록했습니다", Toast.LENGTH_LONG).show()
                Log.i("달리기 기록", "성공")
            } else {
                Toast.makeText(context, "달리기를 기록하지 못했습니다", Toast.LENGTH_LONG).show()
                Log.i("달리기 기록", "실패")
            }
        }

    }
//    private fun subscribeToObservers(activity: MainActivity, homeFragment: HomeFragment) {
//        TrackingService.totallyFinished.observe(viewLifecycleOwner, Observer {
//
//        })
//    }

    // 맵뷰 구현하는 과정 :
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions= PolylineOptions()
                    .color(Color.RED)
                    .width(8f)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun subscrbeToObservers(activity: MainActivity) {
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun addLatestPolyline() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                    .color(Color.RED)
                    .width(8f)
                    .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun moveCameraToUser() {
        if ( pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            pathPoints.last().last(), 15f
                    )
            )
        }
    }
}

