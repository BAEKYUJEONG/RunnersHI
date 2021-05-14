package com.A306.runnershi.Helper

import com.A306.runnershi.Model.Token
import com.A306.runnershi.Model.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface RetrofitService {
    // 회원 관련
    // 카카오 로그인
    @Headers("Content-Type: application/json")
    @POST("/user/signin/kakao")
    fun kakaoLogin(@Body body:Token) : Call<ResponseBody>

    // 카카오 가입
    @Headers("Content-Type: application/json")
    @POST("/user/signup/social")
    fun socialRegister(
        @Query("userId") userId:String,
        @Query("userName") userName: String
    ): Call<ResponseBody>

    // 이메일 확인
    @GET("/user/emailchk/{email}")
    fun userEmailCheck(@Path("email") email:String): Call<ResponseBody>

    // 닉네임 확인
    @GET("/user/namechk/{name}")
    fun userNicknameCheck(@Path("name") name:String): Call<ResponseBody>

    // 일반 회원가입
    @Headers("Content-Type: application/json")
    @POST("/user/signup/runhi")
    fun normalRegister(
        @Query("email") email:String,
        @Query("pwd") pwd: String,
        @Query("userName") userName: String
    ): Call<ResponseBody>

    // 일반 로그인
    @Headers("Content-Type: application/json")
    @POST("/user/signin/runhi")
    fun normalLogin(
        @Query("email") email:String,
        @Query("pwd") pwd:String
    ): Call<ResponseBody>

    //-------------------------------------------------//

    // 친구 관련
    // 친구 추가
    // body = {
    //    "friendUserName" : "칭긔맺고싶은 닉네임"
    //}
    @Headers("Content-Type: application/json")
    @POST("/friend/add")
    fun addFriend(
        @Header("token") token:String,
        @Body body:Map<String, String>
    ): Call<ResponseBody>

    // 친구 추가 수락
    // body = {
    //    friendUserId : "신청한 친구 id",
    //    alarmId : 알람id
    //}
    @Headers("Content-Type: application/json")
    @POST("/friend/accept")
    fun acceptFriend(
            @Header("token") token:String,
            @Body body:Map<String, String>
    ): Call<ResponseBody>

    // 친구 추가 거절
    // body = {
    //    alarmId : 알람id
    //}
    @Headers("Content-Type: application/json")
    @POST("/friend/accept")
    fun rejectFriend(
            @Header("token") token:String,
            @Body body:Map<String, String>
    ): Call<ResponseBody>

    // 친구 삭제
    // body = {
    //    friendUserId : "지울 친구 아이디"
    //}
    @DELETE("/friend/delete")
    fun deleteFriend(
        @Header("token") token:String,
        @Body body:Map<String, String>
    ): Call<ResponseBody>

    // 친구 목록
    @GET("/friend/list")
    fun getFriendList(
        @Header("token") token:String
    ): Call<ResponseBody>

    //-------------------------------------------------//

    // 커스텀 관리
    // 커스텀 수정
    @PUT("/custom")
    fun customSetting(
        @Header("token") token:String
    ): Call<ResponseBody>

    //-------------------------------------------------//

    // 랭킹 관리
    // 전체 전체 랭킹
    // body = {
    //    type : "time",
    //    offset : 0
    //}
    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("/ranking/total/all")
    fun totalAllRanking(
        @Header("token") token:String,
        @Body body:Map<String, Any>
    ): Call<ResponseBody>

    // 전체 친구 랭킹
    // body = {
    //    type : "time",
    //    offset : 0
    //}
    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("/ranking/total/friend")
    fun totalFriendRanking(
        @Header("token") token:String,
        @Body body:Map<String, Any>
    ): Call<ResponseBody>

    // 주간 전체 랭킹
    // body = {
    //    type : "pace",
    //    offset : 0
    //}
    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("/ranking/weekly/all")
    fun weeklyAllRanking(
        @Header("token") token:String,
        @Body body:Map<String, Any>
    ): Call<ResponseBody>

    // 주간 친구 랭킹
    // body = {
    //    type : "distance",
    //    offset : 0
    //}
    @JvmSuppressWildcards
    @Headers("Content-Type: application/json")
    @POST("/ranking/weekly/friend")
    fun weeklyFriendRanking(
        @Header("token") token:String,
        @Body body:Map<String, Any>
    ): Call<ResponseBody>

    //-------------------------------------------------//

    // 기록 관리
    // 기록하기
    // body = {
    //    gps_path: file,
    //    distance: double,
    //    start_time: DATETIME,
    //    end_time: DATETIME,
    //    running_time: int,
    //    title: varchar(100)
    //}
    @Headers("Content-Type: application/json")
    @POST("/record/create")
    fun recordCreate(
        @Header("token") token:String,
        @Body body:Map<String, Any>
    ): Call<ResponseBody>

    //------나머지 부분은 SWAGGER가 되는대로 추가하겠습니다.------//

}