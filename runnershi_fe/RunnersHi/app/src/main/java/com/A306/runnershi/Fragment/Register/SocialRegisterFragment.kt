package com.A306.runnershi.Fragment.Register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.A306.runnershi.Activity.LoginActivity
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Activity.RegisterActivity
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.User
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_normal_register.*
import kotlinx.android.synthetic.main.fragment_social_register.*
import kotlinx.android.synthetic.main.fragment_social_register.nicknameInput
import kotlinx.android.synthetic.main.fragment_social_register.toLoginButton
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class SocialRegisterFragment : Fragment() {

    private val viewModel:UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_social_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var registerActivity = activity as RegisterActivity
        var userId = registerActivity.userId

        // 로그인 버튼 클릭
        toLoginButton.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.overridePendingTransition(0,0)
        }

        // 가입 완료 버튼 클릭
        socialRegisterButton.setOnClickListener {
            var nickname = nicknameInput.text.toString().trim()
            when {
                nickname == "" -> {
                    Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    RetrofitClient.getInstance().socialRegister(userId, nickname).enqueue(object:Callback<ResponseBody>{
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            val result = Gson().fromJson(response.body()?.string(), Map::class.java)
                            if (result != null){
                                if (result["result"] == "invalid name"){
                                    Toast.makeText(context, "다른 닉네임을 사용해주세요", Toast.LENGTH_LONG).show()
                                }else{
                                    viewModel.deleteAllUser()
                                    Timber.e("해치웠나")
                                    var user = User(result["token"].toString(), result["userId"].toString(), result["userName"].toString(), result["runningType"].toString())
                                    viewModel.insertUser(user)
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                    activity?.overridePendingTransition(0, 0)
                                }
                            } else{
                                Timber.e("FUCK")
                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                        }
                    })
                }
            }
        }
    }
}