package com.example.petfriend

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class NuevoProducto : AppCompatActivity() {

    private val PERMISSION_REQUEST_CODE: Int = 101

    var txt_nombre: TextView? = null
    var txt_descipcion: TextView? = null
    var txt_peso: TextView? = null
    var txt_stock: TextView? = null
    var txt_precioContado: TextView? = null
    var txt_precioLista: TextView? = null

    var txt_precioSuelto: TextView? = null
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


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_producto)

        //quitar barra actionbar
        supportActionBar?.hide()

        // obtener los datos ingresados por el usuario
        txt_nombre = findViewById(R.id.nombreProductoNew)
        txt_descipcion = findViewById(R.id.descripcionProductoNew)
        txt_stock = findViewById(R.id.stockProductoNew)
        txt_precioContado = findViewById(R.id.precioContadoProductoNew)
        txt_precioLista = findViewById(R.id.precioListaProductoNew)
        txt_precioSuelto = findViewById(R.id.precioSueltoProductoNew)
        txt_peso = findViewById(R.id.pesoProductoNew)

        imageView = findViewById(R.id.imageView)

        findViewById<Button>(R.id.btnAgregarFoto).setOnClickListener {
            abrirSelectorDeImagen()
        }

    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Pide el permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // El permiso ya está concedido, puedes proceder con la captura de la foto
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permiso concedido, puedes proceder con la captura de la foto
                    dispatchTakePictureIntent()
                } else {
                    // Permiso denegado, muestra un mensaje al usuario explicando por qué necesitas el permiso
                    Toast.makeText(
                        this,
                        "Permiso de cámara necesario para tomar fotos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            // Puedes tener más casos aquí para otros permisos que tu app pueda necesitar
            else -> {
                // Ignora todos los otros casos de solicitud de permisos
            }
        }
    }

    private fun abrirSelectorDeImagen() {
        val opciones = arrayOf<CharSequence>("Tomar Foto", "Elegir de Galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Imagen")
        builder.setItems(opciones) { dialog, item ->
            when {
                opciones[item] == "Tomar Foto" -> {
                    checkPermission()
                }

                opciones[item] == "Elegir de Galería" -> {
                    dispatchPickImageIntent()
                }

                opciones[item] == "Cancelar" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()

    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @SuppressLint("IntentReset")
    private fun dispatchPickImageIntent() {
        val pickPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.type = "image/*"
        startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            imageView.setImageURI(selectedImage)
        }
    }

    fun clickGuardar(view: View) {
        val apiNuevoProducto = ApiUtils.API_URL_NUEVO
        val colaProceso: RequestQueue = Volley.newRequestQueue(this)

        //trim() se utiliza para eliminar los espacios en blanco al principio y al final de cada cadena
        // isEmpty() verifica si la cadena resultante está vacía después de eliminar los espacios en blanco
        if (txt_nombre?.text.toString().trim().isEmpty() ||
            txt_descipcion?.text.toString().trim().isEmpty() ||
            txt_peso?.text.toString().trim().isEmpty() ||
            txt_stock?.text.toString().trim().isEmpty() ||
            txt_precioContado?.text.toString().trim().isEmpty() ||
            txt_precioLista?.text.toString().trim().isEmpty() ||
            txt_precioSuelto?.text.toString().trim().isEmpty()
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

        val categorias: RadioGroup = findViewById(R.id.categoriaProductoNew)
        val categoriaSeleccionada = categorias.checkedRadioButtonId
        if (categoriaSeleccionada == -1) {
            Toast.makeText(this, "Por favor selecciona una categoría.", Toast.LENGTH_LONG).show()
            return
        }


        //convertir el peso a double
        val txt_peso_double: Double = txt_peso?.text.toString().toDouble()


        val selectedCategoria =
            findViewById<RadioButton>(categorias.checkedRadioButtonId).text.toString()
        val selectedMascotas = mascotasCheckboxes.filter { it.isChecked }.map { it.text.toString() }

        val selectedEdades = edadesCheckboxes.filter { it.isChecked }.map { it.text.toString() }

        Response.ErrorListener { error ->
            val serverError = String(error.networkResponse.data)
            Toast.makeText(this, "Error: $serverError", Toast.LENGTH_LONG).show()
        }

        val resultadoPost = object : JsonObjectRequest(Method.POST, apiNuevoProducto, null,
            Response.Listener { response ->
                Toast.makeText(this, "PRODUCTO INSERTADO", Toast.LENGTH_LONG).show()
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "PRODUCTO NO INSERTADO - Error: $error", Toast.LENGTH_LONG)
                    .show()
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                val parametros = JSONObject()

                //conexion
                parametros.put("dbb", "")
                parametros.put("password", "")
                parametros.put("usuario", "")

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

                parametros.put("nombreProducto", txt_nombre?.text.toString())
                parametros.put("descripcionProducto", txt_descipcion?.text.toString())
                parametros.put("pesoProducto", txt_peso_double)

                // Obtener otros datos ingresados por el usuario
                parametros.put("stockProducto", txt_stock?.text.toString().toInt())
                //println(txt_stock?.text.toString().toInt())
                parametros.put("precioContadoProducto", txt_precioContado?.text.toString())
                parametros.put("precioListaProducto", txt_precioLista?.text.toString())
                parametros.put("precioSueltoProducto", txt_precioSuelto?.text.toString())

                val param = parametros.toString(4)
                println("JSON PRODUCTO AÑADIR: $param")

                return parametros.toString().toByteArray()
            }
        }
        colaProceso.add(resultadoPost)
        //AÑADIR FINALIZACIÓN Y SALIR A LA LISTA DE PRODUCTOS
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ListaProductos::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
