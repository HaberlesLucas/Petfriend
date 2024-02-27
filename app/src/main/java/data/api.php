
<?php

if ($_SERVER["REQUEST_METHOD"] == "POST") {


    $data = json_decode(file_get_contents('php://input'), true);

    // Ahora puedes acceder a los datos así:
    $servidor = 'localhost';
    $usuario = $data['usuario'];
    $password = $data['password'];
    $bdd = $data['bdd'];

    //$usuario = $_POST['usuario'];
    //$password = $_POST['password'];
    //$bdd = $_POST['bdd'];

    //$usuario = 'u450356324_petfriend';
    //$password = 'Petfriend0';
    //$bdd = 'u450356324_petfriend';

    $db = new mysqli($servidor, $usuario, $password, $bdd);

    if ($db->connect_error) {
        die('Conexion fallida: ' . $db->connect_error);
        return;
    }

    echo $categoria = mysqli_real_escape_string($data['categoriaProducto']); // INT 
    echo $nombre = mysqli_real_escape_string($data['nombreProducto']); // String 
    echo $peso = mysqli_real_escape_string($data['pesoProducto']); // INT 
    echo $descripcion = mysqli_real_escape_string($data['descripcionProducto']); // String 
    echo $cantidad = mysqli_real_escape_string($data['cantidadProducto']); // INT ( opcional )
    echo $stock = mysqli_real_escape_string($data['stockProducto']); // INT 
    echo $imagen = 'asdasda'; // NO IMPORTA
    echo $precioContado = mysqli_real_escape_string($data['precioContadoProducto']); // STRING
    echo $precioLista = mysqli_real_escape_string($data['precioListaProducto']); // STRING
    echo $precioSuelto = mysqli_real_escape_string($data['precioSueltoProducto']); // STRING
    echo $unidades = 1; // NO IMPORTA


    $query = $db->query("INSERT INTO producto (nombre, codCategoria, descripcion, peso, unidades, imagen, stock, precio_contado, precio_lista, precio_suelto)
                    VALUES ( $nombre, $categoria, $descripcion, $peso, $unidades, $imagen, $stock, $precioContado, $precioLista, $precioSuelto )");

    if($query) {
        $id_edad = $db->insert_id;
        $query = $db->query("INSERT INTO producto_edad (codProducto, codEdad)
                            VALUES($id_edad ,1)");
    }
    if($query) {
        $id_masc = $db->insert_id;
        $query = $db->query("INSERT INTO producto_mascota (codProducto, codMascota)
                            VALUES($id_masc ,1)");
    }

}

