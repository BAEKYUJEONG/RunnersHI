package com.A306.runnershi.Fragment.Register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.A306.runnershi.Activity.LoginActivity
import com.A306.runnershi.Activity.MainActivity
import com.A306.runnershi.Activity.SettingsActivity
import com.A306.runnershi.Helper.RetrofitClient
import com.A306.runnershi.Model.User
import com.A306.runnershi.Openvidu.Permission.PermissionInfo
import com.A306.runnershi.R
import com.A306.runnershi.ViewModel.UserViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_normal_register.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

@AndroidEntryPoint
class NormalRegisterFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_normal_register, container, false)
    }

    private var emailCheck = false
    private var nicknameCheck = false
//    private var code = ""


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 코드 입력창 가려두기
        codeInput.visibility = View.GONE

        // 로그인 버튼 클릭
        toLoginButton.setOnClickListener {
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.overridePendingTransition(0, 0)
        }

        // 이메일 입력창 변경시
        emailInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && emailInput.text.toString().trim() != "") {
                RetrofitClient.getInstance().userEmailCheck(emailInput.text.toString().trim()).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                    ) {
                        var result = Gson().fromJson(response.body()?.string(), Map::class.java)
                        if (result != null) {
                            emailCheck = result["result"] == "valid"
                            if (!emailCheck) {
                                Toast.makeText(requireContext(), "메일을 확인해주세요.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(requireContext(), "메일을 확인 후 코드를 입력하세요", Toast.LENGTH_LONG).show()
//                                codeInput.visibility = View.VISIBLE
//                                code = result["code"].toString()
                            }
                        }

                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크를 확인해주세요.", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        // 닉네임 입력창 변경시
        nicknameInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && nicknameInput.text.toString().trim() != "") {
                RetrofitClient.getInstance().userNicknameCheck(nicknameInput.text.toString().trim()).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                    ) {
                        var result = Gson().fromJson(response.body()?.string(), Map::class.java)
                        if (result != null) {
                            nicknameCheck = result["result"] == "valid"
                            if (!nicknameCheck) {
                                Toast.makeText(requireContext(), "닉네임을 확인해주세요.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(requireContext(), "네트워크를 확인해주세요.", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        normalRegisterButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val pwd = pwdInput.text.toString().trim()
            val nickname = nicknameInput.text.toString().trim()
            val inputCode = codeInput.text.toString().trim()

            if (emailCheck && nicknameCheck) {
                when {
                    email == "" -> {
                        Toast.makeText(activity, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    nickname == "" -> {
                        Toast.makeText(activity, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
//                    inputCode != code -> {
//                        Toast.makeText(activity, "코드를 확인해주세요.", Toast.LENGTH_SHORT).show()
//                    }
                    pwd == "" -> {
                        Toast.makeText(activity, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        RetrofitClient.getInstance().normalRegister(email, pwd, nickname).enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                Timber.e(response.headers().toString())
                                val result = Gson().fromJson(response.body()?.string(), Map::class.java)
                                if (result != null) {
                                    var resultCheck = result["result"] == "SUCCESS"
                                    if (!resultCheck) {
                                        when {
                                            result["result"] == "invalid pwd" -> {
                                                Toast.makeText(activity, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                                            }
                                            result["result"] == "invalid name" -> {
                                                Toast.makeText(activity, "닉네임을 확인해주세요.", Toast.LENGTH_SHORT).show()
                                            }
                                            result["result"] == "invalid email" -> {
                                                Toast.makeText(activity, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
                                            }
                                            else -> {
                                                Toast.makeText(activity, "관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        var user = User(result["token"].toString(), result["userId"].toString(), result["userName"].toString(), result["runningType"].toString())
                                        userViewModel.insertUser(user)

                                        val permissionInfo = PermissionInfo(requireContext())
                                        permissionInfo.showLocationPermissionInfo(Intent(activity, MainActivity::class.java))

//                                        val intent = Intent(activity, MainActivity::class.java)
//                                        startActivity(intent)
//                                        activity?.overridePendingTransition(0, 0)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Timber.e("shshshshshshshshsh")
                            }
                        })
                    }
                }
            } else {
                Toast.makeText(requireContext(), "한 번 확인해주세요.", Toast.LENGTH_LONG).show()
            }
        }

        // 개인정보 동의 내역 출력해주기
        privacyPolicyInformTextView.setOnClickListener {
            showPrivacyDialog()
        }
    }

    private fun showPrivacyDialog() {
        val dialog = SettingsActivity.SettingsFragment.PrivacyCustomDialog(requireContext())
        dialog.privacyDialog()
    }

}
