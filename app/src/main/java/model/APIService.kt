package com.example.petfriend


import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface PetFriendAPI {
    @GET("getDatos.php")
    fun getProductos(): Call<ProductosResponse>

    @PUT("getDatos.php/productos/{codProducto}")
    suspend fun actualizarProducto(@Path("codProducto") codProducto: Int, @Body producto: Producto): Response<Unit>

}

// Define la respuesta de la API
data class ProductosResponse(val productos: List<Producto>)


