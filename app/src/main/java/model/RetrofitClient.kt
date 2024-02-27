package model

import com.example.petfriend.PetFriendAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                //https://sistema.parodilucas.com/getDatos.php
                .baseUrl("https://sistema.parodilucas.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getPetFriendAPI(): PetFriendAPI {
        return getRetrofit().create(PetFriendAPI::class.java)
    }
}
