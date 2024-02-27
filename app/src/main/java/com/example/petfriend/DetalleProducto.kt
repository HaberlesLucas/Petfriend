package com.example.petfriend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class DetalleProducto : AppCompatActivity() {

    @SuppressLint("SourceLockedOrientationActivity", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_producto)

        //quitar barra actionbar
        supportActionBar?.hide()
        //no rotate pantasha
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val producto = intent.getSerializableExtra("producto") as Producto
        mostrarDetalles(producto)

        val btnEditarProductoDetalleEditText = findViewById<TextView>(R.id.btnEditarProductoDetalle)
        btnEditarProductoDetalleEditText.setOnClickListener {
            val intent = Intent(this, EditarProductoActivity::class.java)
            intent.putExtra("producto", producto)
            startActivity(intent)
        }

        //cuando se presiona el btnEliminarProductoDetalle
        val btnEliminar = findViewById<Button>(R.id.btnEliminarProducto)
        btnEliminar.setOnClickListener {
            mostrarAlertaConfirmacion(producto)
        }
    }

    //  ANDA BIEN EL MOSTRAR DETALLES
    private fun mostrarDetalles(producto: Producto) {
        // Inicializa tus TextViews e ImageView
        val nombreProductoTextView = findViewById<TextView>(R.id.nombreProductoDetalle)
        val stockProductoTextView = findViewById<TextView>(R.id.stockProductoDetalle)
        val precioContadoProductoTextView =
            findViewById<TextView>(R.id.precioContadoProductoDetalle)
        val precioListaProductoTextView = findViewById<TextView>(R.id.precioListaProductoDetalle)
        val precioSueltoProductoTextView = findViewById<TextView>(R.id.precioSueltoProductoDetalle)
        val descripcionProductoTextView = findViewById<TextView>(R.id.descripcionProductoDetalle)
        val pesoProductoTextView = findViewById<TextView>(R.id.pesoProductoDetalle)

        val categoriaProductoTextView = findViewById<TextView>(R.id.categoriaProductoDetalle)
        val mascotaProductoTextView = findViewById<TextView>(R.id.mascototaProductoDetalle)
        val edadProductoTextView = findViewById<TextView>(R.id.edadProductoDetalle)

        // Asignar los valores del producto a los TextViews e ImageView
        nombreProductoTextView.text = producto.nombre
        categoriaProductoTextView.text = producto.categoria
        mascotaProductoTextView.text = producto.mascota
        edadProductoTextView.text = producto.edad

        pesoProductoTextView.text = producto.peso.toString()
        descripcionProductoTextView.text = producto.descripcion //new
        stockProductoTextView.text = producto.stock.toString()
        precioContadoProductoTextView.text = "$ ${producto.precio_contado}"
        precioSueltoProductoTextView.text = "$ ${producto.precio_suelto}"
        precioListaProductoTextView.text = "$ ${producto.precio_lista}"

        val stock = producto.stock
        stockProductoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)

        // obtener el Drawable desde los recursos
        val stockVerde = ContextCompat.getDrawable(this, R.drawable.radius_stock_green)
        val stockRojo = ContextCompat.getDrawable(this, R.drawable.radius_stock_red)

        // extraer el color del drawable
        val colorGreen = (stockVerde as? GradientDrawable)?.color?.defaultColor ?: Color.TRANSPARENT
        val colorRed = (stockRojo as? GradientDrawable)?.color?.defaultColor ?: Color.TRANSPARENT

        // establecer el color del fondo según el stock
        if (stock > 0) {
            stockProductoTextView.setBackgroundColor(colorGreen)
        } else {
            stockProductoTextView.setBackgroundColor(colorRed)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ListaProductos::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun mostrarAlertaConfirmacion(producto: Producto) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro que deseas eliminar este producto?")
        builder.setPositiveButton("Sí") { _, _ ->
            eliminarProducto(producto)
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }


    private fun eliminarProducto(producto: Producto) {

        val api_eliminar = ApiUtils.API_URL_ELIMINAR

        val colaProceso: RequestQueue = Volley.newRequestQueue(this)

        val resultadoPost = object : JsonObjectRequest(Method.POST, api_eliminar, null,
            Response.Listener { response ->
                Toast.makeText(this, "PRODUCTO ELIMINADO", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                // Aquí manejas cualquier error que ocurra durante la eliminación del producto
                Toast.makeText(
                    this,
                    "ERROR AL ELIMINAR PRODUCTO - Error: $error",
                    Toast.LENGTH_LONG
                ).show()
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {

                val parametros = JSONObject()

                //conexion
                parametros.put("dbb", "u450356324_petfriend")
                parametros.put("password", "Petfriend0")
                parametros.put("usuario", "u450356324_petfriend")
                ///eliminación
                parametros.put("codProducto", producto.codProducto)

                val param = parametros.toString(4)
                println("JSON PRODUCTO ELIMINAR: $param")
                return parametros.toString().toByteArray()
            }
        }
        colaProceso.add(resultadoPost)
        finish()
    }

}