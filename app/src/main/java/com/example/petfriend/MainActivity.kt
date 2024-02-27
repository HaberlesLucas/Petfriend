package com.example.petfriend

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    @SuppressLint("WrongViewCast", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //quitar barra actionbar
        supportActionBar?.hide()
        //no rotate pantasha
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val btnListaProductoEditText = findViewById<AppCompatButton>(R.id.btnVerListaProductos)
        btnListaProductoEditText.setOnClickListener {
            val intent = Intent(this, ListaProductos::class.java)
            startActivity(intent)
        }


        val btnVentaProductoEditText = findViewById<AppCompatButton>(R.id.btnVenta)
        btnVentaProductoEditText.setOnClickListener {
            //startActivity(Intent(this, SeleccionProductosActivity::class.java))
            mostrarAlerta("Funcionalidad de Ventas no está implementada. Pero falta poco!!")
        }

        val btnCompraProductoEditText = findViewById<AppCompatButton>(R.id.btnCompra)
        btnCompraProductoEditText.setOnClickListener {
            mostrarAlerta("Funcionalidad de Compras no está implementada")
        }

        val btnUsuarioEditText = findViewById<AppCompatButton>(R.id.btnUsuario)
        btnUsuarioEditText.setOnClickListener {
            //val intent = Intent(this, AddProducto::class.java)
            //startActivity(intent)
            mostrarAlerta("Funcionalidad de Usuarios no está implementada")
        }
    }

    private fun mostrarAlerta(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Funcionalidad No Implementada")
        builder.setMessage("Lo siento, $mensaje")
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }

}
