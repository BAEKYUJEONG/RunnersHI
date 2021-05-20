package com.A306.runnershi.Fragment.SingleRun

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Fragment.Home.HomeFragment
import com.A306.runnershi.Fragment.Profile.ProfileFragment
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Run
import com.A306.runnershi.R
import com.A306.runnershi.Services.Polyline
import com.A306.runnershi.Services.TrackingService
import com.A306.runnershi.ViewModel.SingleRunViewModel
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run_result.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
//class RunResultFragment(distance:String, time:String, pace:String) : Fragment(R.layout.fragment_run_result) {
class RunResultFragment(var runResult: Run, val runningDate: Calendar, val timeSpentInSeconds:Long) : Fragment(R.layout.fragment_run_result) {

    companion object{
        var TITLE = ""
    }

    private val userViewModel: UserViewModel by viewModels()
    var token = ""

    private val runViewModel: SingleRunViewModel by viewModels()

    private var map: GoogleMap? = null
    private var pathPoints = mutableListOf<Polyline>()

    private var curTimeMillis = 0L

    private val resultDistance = runResult.distance.toString()
    private val resultTime = runResult.time.toString()
    private val resultPace = runResult.pace.toString()
    private var resultRoute = runResult.img


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
        val profileFragment = ProfileFragment()

        subscribeToObservers(mainActivity)

        distanceDataView.text = "${resultDistance} K"
        timeDataView.text = resultTime
        paceDataView.text = resultPace

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

        saveRunBtn.setOnClickListener {
            generateRunTitle()
            generateRunImg()
        }
    }

    private fun generateRunTitle() {
        // 타이틀도 가져올거예요!
        val newtitle = generateTitleView.text.toString()
        Timber.e("타이틀은요")
        Log.e("널체크전 타이틀", "${newtitle}입니다")

        if (newtitle != "") {
            runResult.title = newtitle
        }
        Log.e("널체크후 타이틀", "${runResult.title.toString()}")

        Timber.e("여기가 먼저뜨나요?")
    }

    private fun generateRunImg() {
        // 우선 사진을 저장해주고요
        map?.snapshot { bmp ->
            Timber.e("사진 저장")
            Timber.e(map?.toString())
            resultRoute = bmp
            runResult.img = resultRoute
            Timber.e("사진 저장된거같은데요?")
            Timber.e(resultRoute.toString())
            saveToDB()
        }
    }

    private fun saveToDB() {
        Timber.e("디비는 아예 안뜨나요?")
        Log.i("런데이터 검증", "${runResult.img.toString()}")
        Log.i("런데이터 제목 검증", "${runResult.title.toString()}")
        runViewModel.insertRun(runResult)
        saveToServer()
    }

    private fun saveToServer() {
        val distance = runResult.distance.toDouble()
//        val endTime = runResult.timestamp.toString()
        val endTime = runningDate.time.toString()
        val runningTime = timeSpentInSeconds.toInt()
        val runningTitle = runResult.title.toString()

        TITLE = runningTitle

        Timber.e("Distance : ${distance}, EndTime: ${endTime}, runningTime : ${runningTime}, runningTitle: $runningTitle")

//        val body = mapOf(
//            "distance" to distance,
//            "endTime" to endTime,
//            "runningTime" to runningTime,
//            "title" to runningTitle)
        RetrofitClient.getInstance().recordCreate(token, distance, endTime, runningTime, runningTitle).enqueue(afterSaveToDB)
    }

    private var afterSaveToDB = object: Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.e("세이브디비에서", "통신이 안됐답니다!")
        }
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            val mainActivity = activity as MainActivity
            val profileFragment = ProfileFragment()

            val result = response.body()?.string().toString()
            if (result == "SUCCESS") {
                Toast.makeText(context, "달리기를 성공적으로 기록했습니다", Toast.LENGTH_LONG).show()
                Log.i("달리기 기록", "성공")
                mainActivity.sendCommandToService("ACTION_STOP_SERVICE")
                mainActivity.makeCurrentFragment(profileFragment)

            } else {
                Toast.makeText(context, "달리기를 기록하지 못했습니다", Toast.LENGTH_LONG).show()
                Log.i("달리기 기록", "실패")
                Log.i("달리기 기록", "$result")
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
//    val mainActivity = activity as MainActivity
//    val link = SingleRunFragment.mapFragmentToSingleRunFragment()
//    private val mapFragment = MapFragment(link)
//    private val subscribeToObservers = mapFragment.subscribeToObservers(mainActivity)

    private fun subscribeToObservers(activity: MainActivity) {
        TrackingService.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
        TrackingService.totallyFinished.observe(viewLifecycleOwner, Observer {
            if (it > 0) {
                if (map != null) {
                    zoomToSeeWholeTrack()
                } else {
                    TrackingService.totallyFinished.postValue(it+1)
                }
            }
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

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                routeDataView.width,
                routeDataView.height,
                (routeDataView.height * 0.05f).toInt()
            )
        )
    }


}

