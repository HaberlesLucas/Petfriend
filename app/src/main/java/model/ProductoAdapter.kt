package modal


import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriend.DetalleProducto
import com.example.petfriend.Producto
import com.example.petfriend.R


class ProductoAdapter(private val productos: List<Producto>) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>(), Filterable{

    private var productosFiltrados: List<Producto> = productos

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filtro = constraint.toString().toLowerCase().trim()
                productosFiltrados = if (filtro.isEmpty()) {
                    productos
                } else {
                    //aca puedo agregar otro filtro
                    productos.filter {it.nombre.toLowerCase().contains(filtro) || it.mascota.toLowerCase().contains(filtro)
                    }
                }
                val resultados = FilterResults()
                resultados.values = productosFiltrados
                return resultados
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productosFiltrados = results?.values as List<Producto>
                notifyDataSetChanged()
            }
        }
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.nombreProducto)
        val stockProducto: TextView = itemView.findViewById(R.id.stockProducto)
        //función para configurar los colores del fondo segun sea el stock
        fun configurarColoresFondo(stock: Int) {
            stockProducto.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            if (stock > 0) {
                stockProducto.setBackgroundResource(R.drawable.radius_stock_green)
            } else {
                stockProducto.setBackgroundResource(R.drawable.radius_stock_red)
            }
        }

        val precioContadoProducto: TextView = itemView.findViewById(R.id.precioContadoItem)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val productoActual = productosFiltrados[position]
        holder.nombreProducto.text = productoActual.nombre

        // Llamar a la función para configurar los colores del fondo
        val stock = productoActual.stock
        holder.configurarColoresFondo(stock)
        holder.stockProducto.text = "STOCK: ${productoActual.stock}"

        holder.precioContadoProducto.text = "$ ${productoActual.precio_contado}"


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_producto, parent, false)
        val holder = ProductoViewHolder(view)

        // Configurar el clic en el elemento
        view.setOnClickListener {
            val position = holder.adapterPosition
            val producto = productosFiltrados[position]

            // Iniciar la actividad DetalleProducto con el producto seleccionado
            val intent = Intent(view.context, DetalleProducto::class.java)
            intent.putExtra("producto", producto)
            view.context.startActivity(intent)
        }
        return holder
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    override fun getItemCount() = productosFiltrados.size
}