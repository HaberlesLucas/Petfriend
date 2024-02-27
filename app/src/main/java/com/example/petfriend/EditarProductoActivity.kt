package com.example.petfriend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.RetrofitClient
import org.json.JSONArray
import org.json.JSONObject

class EditarProductoActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var stockEditText: EditText
    private lateinit var precioContadoEditText: EditText
    private lateinit var precioListaEditText: EditText
    private lateinit var precioSueltoEditText: EditText
    private lateinit var pesoProductoEditText: EditText

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2

    private lateinit var imageView: ImageView
    val categoriaIdMap = mapOf(
        "Alim." to 1,
        "Juguete" to 2,
        "Med." to 3,
        "Otro" to 4,
        "Accesorios" to 5
    )
    val mascotaIdMap = mapOf(
        "Perro" to 1,
        "Gato" to 2,
        "Ave" to 3,
        "Otros" to 4
    )
    val edadIdMap = mapOf(
        "Cachorro" to 1,
        "Adulto" to 2,
        "Mordida Pequeña" to 3,
        "Joven" to 4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_producto)

        //quitar barra actionbar
        supportActionBar?.hide()

        // Obtén el producto pasado desde la actividad anterior
        val producto = intent.getSerializableExtra("producto") as Producto
        //INICIAR VAR
        nombreEditText = findViewById<EditText>(R.id.nombreProductoNew)
        descripcionEditText = findViewById<EditText>(R.id.descripcionProductoNew)
        stockEditText = findViewById<EditText>(R.id.stockProductoNew)
        precioContadoEditText = findViewById<EditText>(R.id.precioContadoProductoNew)
        precioListaEditText = findViewById<EditText>(R.id.precioListaProductoNew)
        precioSueltoEditText = findViewById<EditText>(R.id.precioSueltoProductoNew)
        pesoProductoEditText = findViewById<EditText>(R.id.pesoProductoNew)

        verDatosActuales(producto)

        val btnGuardar = findViewById<Button>(R.id.btnConfirmarEdicion)
        btnGuardar.setOnClickListener {

            editarProducto(producto)

            // Cierra la actividad de edición y vuelve a la actividad de detalle
            //val intent = Intent(this, ListaProductos::class.java)
            //intent.putExtra("producto", producto)
            //startActivity(intent)
        }
    }

    private fun editarProducto(producto: Producto) {
        val apiEditar = ApiUtils.API_URL_EDITAR
        val colaProceso: RequestQueue = Volley.newRequestQueue(this)

        if (nombreEditText.text.toString().trim().isEmpty() ||
            descripcionEditText.text.toString().trim().isEmpty() ||
            pesoProductoEditText.text.toString().trim().isEmpty() ||
            stockEditText.text.toString().trim().isEmpty() ||
            precioContadoEditText.text.toString().trim().isEmpty() ||
            precioListaEditText.text.toString().trim().isEmpty() ||
            precioSueltoEditText.text.toString().trim().isEmpty()
        ) {
            Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_LONG).show()
            return
        }

        val mascotasCheckboxes = listOf(
            findViewById<CheckBox>(R.id.perroCheckboxNew),
            findViewById<CheckBox>(R.id.gatoCheckboxNew),
            findViewById<CheckBox>(R.id.aveCheckboxNew),
            findViewById<CheckBox>(R.id.otrosCheckboxNew)
        )
        val atLeastOneMascotaChecked = mascotasCheckboxes.any { it.isChecked }
        if (!atLeastOneMascotaChecked) {
            Toast.makeText(this, "Por favor selecciona al menos una mascota.", Toast.LENGTH_LONG)
                .show()
            return
        }

        val edadesCheckboxes = listOf(
            findViewById<CheckBox>(R.id.cachorroCheckboxNew),
            findViewById<CheckBox>(R.id.adultoCheckboxNew),
            findViewById<CheckBox>(R.id.mordidaPequenaCheckboxNew),
            findViewById<CheckBox>(R.id.jovenCheckboxNew)
        )
        val atLeastOneEdadChecked = edadesCheckboxes.any { it.isChecked }
        if (!atLeastOneEdadChecked) {
            Toast.makeText(this, "Por favor selecciona al menos una edad.", Toast.LENGTH_LONG)
                .show()
            return
        }
        //convertir el peso a double
        val txt_peso_double: String = pesoProductoEditText.text.toString()
        val txt_stock_str = pesoProductoEditText.text.toString()
        val txt_stock_int = if (txt_stock_str.isEmpty() || txt_stock_str == "null") {
            0 // valor por defecto
        } else {
            txt_stock_str.toDouble()
        }

        val categorias: RadioGroup = findViewById(R.id.categoriaProductoNew)
        val selectedCategoria =
            findViewById<RadioButton>(categorias.checkedRadioButtonId).text.toString()

        val selectedMascotas = mascotasCheckboxes.filter { it.isChecked }.map { it.text.toString() }
        val selectedEdades = edadesCheckboxes.filter { it.isChecked }.map { it.text.toString() }

        Response.ErrorListener { error ->
            val serverError = String(error.networkResponse.data)
            Toast.makeText(this, "Error: $serverError", Toast.LENGTH_LONG).show()
        }

        val resultadoPost = object : JsonObjectRequest(Method.POST, apiEditar, null,
            Response.Listener { response ->
                Toast.makeText(this, "PRODUCTO MODIFICADO", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "PRODUCTO NO MODIFICADO - Error: $error", Toast.LENGTH_LONG)
                .show()
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
                parametros.put("codProducto", producto.codProducto)

                // Obtener los IDs de las selecciones del usuario
                val selectedCategoriaId = categoriaIdMap[selectedCategoria]
                val selectedMascotaIds =
                    JSONArray(selectedMascotas.map { mascota -> mascotaIdMap[mascota] })
                val selectedEdadIds = JSONArray(selectedEdades.map { edad -> edadIdMap[edad] })

                // Convertir los JSONArray a listas de JSONObject
                val mascotaIdsJson = JSONArray()
                for (i in 0 until selectedMascotaIds.length()) {
                    val obj = JSONObject()
                    obj.put("codMascotaProducto", selectedMascotaIds.get(i))
                    mascotaIdsJson.put(obj)
                }

                val edadIdsJson = JSONArray()
                for (i in 0 until selectedEdadIds.length()) {
                    val obj = JSONObject()
                    obj.put("codEdadProducto", selectedEdadIds.get(i))
                    edadIdsJson.put(obj)
                }

                // Agregar los IDs al JSON
                parametros.put("categoriaProducto", selectedCategoriaId)

                // Agregar los objetos JSON a 'parametros'
                parametros.put("mascotaIds", mascotaIdsJson)
                parametros.put("edadIds", edadIdsJson)

                parametros.put("nombreProducto", nombreEditText?.text.toString())
                parametros.put("descripcionProducto", descripcionEditText?.text.toString())
                parametros.put("pesoProducto", txt_peso_double)

                // Obtener otros datos ingresados por el usuario
                parametros.put("stockProducto", txt_stock_int)
                //println(txt_stock?.text.toString().toInt())
                parametros.put("precioContadoProducto", precioContadoEditText.text.toString())
                parametros.put("precioListaProducto", precioListaEditText.text.toString())
                parametros.put("precioSueltoProducto", precioSueltoEditText.text.toString())

                val param = parametros.toString(4)
                println("JSON PRODUCTO EDITAR: $param")

                return parametros.toString().toByteArray()

            }
        }
        colaProceso.add(resultadoPost)
        //AÑADIR FINALIZACIÓN Y SALIR A LA LISTA DE PRODUCTOS
        finish()
    }


    @SuppressLint("WrongViewCast")
    fun verDatosActuales(producto: Producto) {
        nombreEditText.setText(producto.nombre)
        descripcionEditText.setText(producto.descripcion)
        stockEditText.setText(producto.stock.toString())
        precioContadoEditText.setText(producto.precio_contado)
        precioListaEditText.setText(producto.precio_lista)
        precioSueltoEditText.setText(producto.precio_suelto)
        pesoProductoEditText.setText(producto.peso.toString())

        when (producto.categoria) {
            "Alimento" -> findViewById<RadioButton>(R.id.alimentoRadioButtonNew).isChecked = true
            "Juguete" -> findViewById<RadioButton>(R.id.jugueteRadioButtonNew).isChecked = true
            "Medicamento" -> findViewById<RadioButton>(R.id.medicamentoRadioButtonNew).isChecked =
                true

            "Otro" -> findViewById<RadioButton>(R.id.otroCategoriaRadioButtonNew).isChecked = true
            "Accesorios" -> findViewById<RadioButton>(R.id.accesoriosRadioButtonNew).isChecked =
                true
        }
        //MASCOTAS
        if (producto.mascota.contains("Perro")) {
            findViewById<CheckBox>(R.id.perroCheckboxNew).isChecked = true
        }
        if (producto.mascota.contains("Gato")) {
            findViewById<CheckBox>(R.id.gatoCheckboxNew).isChecked = true
        }
        if (producto.mascota.contains("Ave")) {
            findViewById<CheckBox>(R.id.aveCheckboxNew).isChecked = true
        }
        if (producto.mascota.contains("Otros")) {
            findViewById<CheckBox>(R.id.otrosCheckboxNew).isChecked = true
        }
        //EDADES
        if (producto.edad.contains("Cachorro")) {
            findViewById<CheckBox>(R.id.cachorroCheckboxNew).isChecked = true
        }
        if (producto.edad.contains("Adulto")) {
            findViewById<CheckBox>(R.id.adultoCheckboxNew).isChecked = true
        }
        if (producto.edad.contains("Mordida Pequeña")) {
            findViewById<CheckBox>(R.id.mordidaPequenaCheckboxNew).isChecked = true
        }
        if (producto.edad.contains("Joven")) {
            findViewById<CheckBox>(R.id.jovenCheckboxNew).isChecked = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ListaProductos::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

