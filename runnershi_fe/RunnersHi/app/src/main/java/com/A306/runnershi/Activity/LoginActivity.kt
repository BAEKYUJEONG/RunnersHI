package com.A306.runnershi.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.A306.runnershi.DB.RunningDB
import com.A306.runnershi.Dao.UserDAO
import com.A306.runnershi.Helper.HttpConnect
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.Token
import com.A306.runnershi.Model.User
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel:UserViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 카카오 초기화
        KakaoSdk.init(this, resources.getString(R.string.kakao_api_key))

        // 회원가입 버튼 클릭
        // 회원가입 페이지로 이동
        socialRegisterButton.setOnClickListener {
            val registerIntent:Intent = Intent(this,RegisterActivity::class.java).apply {
                putExtra("loginType", "Normal")
            }
            startActivity(registerIntent)
            overridePendingTransition(0,0)
        }

        // 로그인 버튼 클릭
        // 서버 통신으로 로그인
        toLoginButton.setOnClickListener {
            val userEmail = emailInput.text.toString()
            val userPwd = pwdInput.text.toString()
            val loginParams = mapOf("email" to userEmail, "pwd" to userPwd)
            // 서버 통신
            val loginConnect = HttpConnect("/login", loginParams)
        }

        // 카카오 로그인 콜백함수
        val kakaoCallBack: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_LONG).show()
            }
            else if (token != null) {
                Timber.e("카카오 토큰? ${token.accessToken}")

//                var httpConnect = HttpConnect("/user/signin/kakao", mapOf("accessToken" to token.accessToken))
//                var result = httpConnect.post()
//                Timber.e(result)

                RetrofitClient.getInstance().kakaoLogin(Token(token.accessToken)).enqueue(object:Callback<ResponseBody>{
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        val result = Gson().fromJson(response.body()?.string(), Map::class.java)
                        if (result["token"] == null){
                            val kakaoIntent:Intent = Intent(applicationContext, RegisterActivity::class.java).apply {
                                putExtra("loginType", "Social")
                                putExtra("userId", result["userId"].toString())
                            }
                            startActivity(kakaoIntent)
                            overridePendingTransition(0, 0)
                        }else{
                            val kakaoIntent:Intent = Intent(applicationContext, RegisterActivity::class.java).apply {
                                putExtra("loginType", "Social")
                                putExtra("userId", result["userId"].toString())
                                putExtra("userName", result["userName"].toString())
                                putExtra("token", result["token"].toString())
                                putExtra("runningType", result["runningType"].toString())
                            }
                            startActivity(kakaoIntent)
                            overridePendingTransition(0, 0)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Timber.e("RETROFIT... : ${call.toString()}")
                    }
                })



//                val registerIntent:Intent = Intent(this,RegisterActivity::class.java).apply {
//                    putExtra("loginType", "Social")
//                }
//                startActivity(registerIntent)
//                overridePendingTransition(0,0)
            }
        }

        // 카카오 로그인 버튼 클릭
        kakaoLogin.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = kakaoCallBack)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoCallBack)
            }
        }

        // 네이버 로그인 클릭
//        naverLogin.setOnClickListener {
//
//        }
    }
}