package com.example.petfriend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import modal.ProductoAdapter

class SeleccionProductosActivity : AppCompatActivity() {

    //val listaProductos: List<Producto> = ListaProductos.obtenerListaProductos()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_venta_seleccionar)

        // Configurar el RecyclerView y el adaptador
        //val adaptador = ProductoAdapter(listaProductos)
        //recyclerViewProductos.layoutManager = LinearLayoutManager(this)
        //recyclerViewProductos.adapter = adaptador
    }
}
