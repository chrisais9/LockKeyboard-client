package kr.koohyongmo.lockkeyboardclient.keyboard.model.service

import kr.koohyongmo.lockkeyboardclient.keyboard.model.response.TokenResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by KooHyongMo on 2020/06/23
 */
interface RetrofitService {

    @GET("gettoken/{username}")
    fun getToken(@Path("username") username: String) : Call<TokenResponse>

}