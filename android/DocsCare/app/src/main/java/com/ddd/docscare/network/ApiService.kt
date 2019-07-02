package com.ddd.docscare.network

import com.ddd.docscare.model.CategoryResponse
import com.ddd.docscare.model.ImageResponse
import com.ddd.docscare.model.UserInfo
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    /**
     * 이미지 관련 api
     */
    @Multipart
    @POST("/images")
    fun uploadImage(@Part("user_id") user_id: String,
                    @Part file: MultipartBody.Part,
                    @Part("image_name") image_name: String ): Single<ImageResponse>

    @GET("/images")
    fun getImages(@Field("user_id") user_id: String,
                  @Field("text") text: String): Single<List<ImageResponse>>


    /**
     * 유저 회원 관련 api
     */
    @POST("/users/{user_id}")
    fun addUser(@Path("user_id") user_id: String,
                @Body userInfo: UserInfo): Single<UserInfo>

    @DELETE("/users/{user_id}")
    fun deleteUser(@Path("user_id") user_id: String)


    /**
     * 유저 카테고리 관련 api
     */
    @GET("/categories")
    fun getCategories(@Field("user_id") user_id: String): Observable<List<CategoryResponse>>
}