package com.aldyjrz.firebasedatacrud.adapter


import android.R
import android.content.Context 
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aldyjrz.Buku
import com.aldyjrz.firebasedatacrud.ItemBukuHolder
import java.util.*


class AdapterBuku(
    private val context: Context,
    daftarBuku: ArrayList<Buku>
) :
    RecyclerView.Adapter<ItemBukuHolder?>() {
    private val daftarBuku: ArrayList<Buku>
    private val listener: FirebaseDataListener
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBukuHolder {
        // TODO: Implement this method
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_barang, parent, false)
        return ItemBukuHolder(view)
    }

    fun onBindViewHolder(holder: ItemB, position: Int) {
        // TODO: Implement this method
        holder.namaBarang.setText("Nama   : " + daftarBuku[position].getNama())
        holder.merkBarang.setText("Merk     : " + daftarBuku[position].getMerk())
        holder.hargaBarang.setText("Harga   : " + daftarBuku[position].getHarga())
        holder.view.setOnClickListener(View.OnClickListener {
            listener.onDataClick(
                daftarBuku[position],
                position
            )
        })
    }

    // TODO: Implement this method
    val itemCount: Int
        get() =// TODO: Implement this method
            daftarBuku.size

    //interface data listener
    interface FirebaseDataListener {
        fun onDataClick(buku: Buku?, position: Int)
    }

    init {
        this.daftarBuku = daftarBuku
        listener = context as FirebaseDataListener
    }
}