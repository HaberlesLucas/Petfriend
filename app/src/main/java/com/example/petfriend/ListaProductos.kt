package com.example.petfriend

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import modal.ProductoAdapter
import model.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ListaProductos : AppCompatActivity() {

    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var searchView: SearchView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_productos)

        //inicializar SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        //quitar barra actionbar
        supportActionBar?.hide()

        //no rotate pantasha
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // inicializar el RecyclerView y el adaptador
        val rvListaProductos = findViewById<RecyclerView>(R.id.rvListaProductos)
        rvListaProductos.layoutManager = LinearLayoutManager(this)

        productoAdapter = ProductoAdapter(listOf())
        rvListaProductos.adapter = productoAdapter

        // obtiener la referencia del SearchView
        searchView = findViewById(R.id.buscarProductos)

        // configurar el listener para la búsqueda en tiempo real
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // aplica el filtro al adaptador al cambiar el texto de búsqueda
                productoAdapter.filter.filter(newText)
                return false
            }
        })

        //listener para el evento de actualización
        swipeRefreshLayout.setOnRefreshListener {
            cargarDatos()
        }

        //muestra la animación de actualización y carga los datos
        swipeRefreshLayout.post {
            swipeRefreshLayout.isRefreshing = true
            cargarDatos()
        }

        val btnNuevoProducto = findViewById<Button>(R.id.btnAddProducto)
        btnNuevoProducto.setOnClickListener {
            // Crear un Intent para abrir la nueva actividad
            val intent = Intent(this, NuevoProducto::class.java)
            startActivity(intent)
        }

    }

    private fun cargarDatos() {

        val api = RetrofitClient.getPetFriendAPI()
        //val api = retrofit.create(PetFriendAPI::class.java)

        api.getProductos().enqueue(object : Callback<ProductosResponse> {
            override fun onResponse(
                call: Call<ProductosResponse>,
                response: Response<ProductosResponse>
            ) {
                val productos = response.body()?.productos
                // Actualiza el adaptador con los nuevos productos
                productoAdapter = ProductoAdapter(productos ?: listOf())
                val rvListaProductos = findViewById<RecyclerView>(R.id.rvListaProductos)
                rvListaProductos.adapter = productoAdapter
            }

            override fun onFailure(call: Call<ProductosResponse>, t: Throwable) {
                // se podría maneja el error
            }
        })
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}