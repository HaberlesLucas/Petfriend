package com.example.petfriend

import java.io.Serializable

data class Producto(
    var codProducto: Int,
    var codCategoria: Int,
    var categoria: String, //JAJAJA ERA ESTO, PUSE INT
    var nombre: String,
    var descripcion: String,
    var peso: Double,
    var unidades: Int,
    var imagen: String,
    var stock: Int,
    var precio_contado: String,
    var precio_lista: String,
    var precio_suelto: String,
    var edad: String,
    var mascota: String
) : Serializable
