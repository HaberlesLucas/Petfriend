package com.example.petfriend

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class NuevaVenta: AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nueva_venta)

        //quitar barra actionbar
        supportActionBar?.hide()

        // Obtén el producto pasado desde la actividad anterior
        val producto = intent.getSerializableExtra("producto") as Producto
        enviarVenta(producto)
    }


    fun enviarVenta(producto : Producto) {
        val apiNuevaVenta = ApiUtils.API_URL_VENTA
        val colaProceso: RequestQueue = Volley.newRequestQueue(this)

        Response.ErrorListener { error ->
            val serverError = String(error.networkResponse.data)
            Toast.makeText(this, "Error: $serverError", Toast.LENGTH_LONG).show()
        }

        val resultadoPost = object : JsonObjectRequest(Method.POST, apiNuevaVenta, null,
            Response.Listener { response ->
                Toast.makeText(this, "VENTA REALIZADA", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "VENTA NO REALIZADA - Error: $error", Toast.LENGTH_LONG)
                    .show()
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                val parametros = JSONObject()
                val cantidad = findViewById<EditText>(R.id.editTextCantidad)

                //conexion
                parametros.put("dbb", "u450356324_petfriend")
                parametros.put("password", "Petfriend0")
                parametros.put("usuario", "u450356324_petfriend")
                parametros.put("codProducto", producto.codProducto)
                parametros.put("cantidad", cantidad.toString().toInt())
                val param = parametros.toString(4)
                println("JSON PRODUCTO EDITAR: $param")

                return parametros.toString().toByteArray()
            }
        }
        colaProceso.add(resultadoPost)
        finish()
    }
}
