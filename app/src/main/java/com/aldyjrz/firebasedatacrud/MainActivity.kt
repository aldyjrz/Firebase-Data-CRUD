package com.aldyjrz.firebasedatacrud


import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Adapter
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aldyjrz.Buku
import com.aldyjrz.firebasedatacrud.adapter.AdapterBuku
import com.google.android.gms.tasks.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.*
import com.google.firebase.database.*
import java.util.*

/*
* AldyJrz
*
*/
class MainActivity : AppCompatActivity(), AdapterBuku.FirebaseDataListener {
    //variabel fields
    private var mToolbar: Toolbar? = null
    private var mFloatingActionButton: FloatingActionButton? = null
    private var mEditNama: EditText? = null
    private var mEditMerk: EditText? = null
    private var mEditHarga: EditText? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: AdapterBuku? = null
    private var daftarBarang: ArrayList<Buku>? = null

    //variabel yang merefers ke Firebase Database
    private var mDatabaseReference: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //android toolbar
        setupToolbar(R.id.toolbar)
        mRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.setLayoutManager(LinearLayoutManager(this))
        FirebaseApp.initializeApp(this)
        // mengambil referensi ke Firebase Database
        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabaseReference = mFirebaseInstance!!.getReference("barang")
        mDatabaseReference!!.child("data_barang").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                daftarBarang = ArrayList<Buku>()
                for (mDataSnapshot in dataSnapshot.getChildren()) {
                    val Buku: Buku? = mDataSnapshot.getValue(Buku::class.java)
                    Buku.setKey(mDataSnapshot.getKey())
                    daftarBarang!!.add(com.aldyjrz.Buku)
                }
                //set adapter RecyclerView
                mAdapter = AdapterBuku(this@MainActivity, daftarBuku)
                mRecyclerView.setAdapter(mAdapter)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // TODO: Implement this method
                Toast.makeText(this@MainActivity, databaseError.getDetails().toString() + " " + databaseError.getMessage(), Toast.LENGTH_LONG).show()
            }
        })


        //FAB (FloatingActionButton) tambah Buku
        mFloatingActionButton = findViewById<View>(R.id.tambah_barang) as FloatingActionButton
        mFloatingActionButton!!.setOnClickListener(View.OnClickListener { //tambah Buku
            dialogTambahBarang()
        })
    }

    /* method ketika data di klik
	*/
    override fun onDataClick(buku: Buku?, position: Int) {
         val builder = AlertDialog.Builder(this)
        builder.setTitle("Pilih Aksi")
        builder.setPositiveButton("UPDATE") { dialog, id -> dialogUpdateBarang(buku) }
        builder.setNegativeButton("HAPUS") { dialog, id -> hapusDataBarang(buku) }
        builder.setNeutralButton("BATAL") { dialog, id -> dialog.dismiss() }
        val dialog: Dialog = builder.create()
        dialog.show()
    }

    //setup android toolbar
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupToolbar(id: Int) {
        mToolbar = findViewById<View>(id) as Toolbar
     }

    //dialog tambah Buku / alert dialog
    private fun dialogTambahBarang() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Tambah Data Buku")
        val view: View = layoutInflater.inflate(R.layout.add_buku, null)
        mEditNama = view.findViewById<View>(R.id.nama_barang) as EditText
        mEditMerk = view.findViewById<View>(R.id.merk_barang) as EditText
        mEditHarga = view.findViewById<View>(R.id.harga_barang) as EditText
        builder.setView(view)

        //button simpan Buku / submit Buku
        builder.setPositiveButton("SIMPAN") { dialog, id ->
            val namaBarang = mEditNama!!.text.toString()
            val merkBarang = mEditMerk!!.text.toString()
            val hargaBarang = mEditHarga!!.text.toString()
            if (!namaBarang.isEmpty() && !merkBarang.isEmpty() && !hargaBarang.isEmpty()) {
                submitDataBarang(Buku(judulBuku, penulisBuku, hargaBuku))
            } else {
                Toast.makeText(this@MainActivity, "Data harus di isi!", Toast.LENGTH_LONG).show()
            }
        }

        //button kembali / batal
        builder.setNegativeButton("BATAL") { dialog, id -> dialog.dismiss() }
        val dialog: Dialog = builder.create()
        dialog.show()
    }

    //dialog update Buku / update data Buku
    private fun dialogUpdateBarang(barang: Buku?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Data Buku")
        val view: View = layoutInflater.inflate(R.layout.edit_buku, null)
        mEditNama = view.findViewById<View>(R.id.nama_barang) as EditText
        mEditMerk = view.findViewById<View>(R.id.merk_barang) as EditText
        mEditHarga = view.findViewById<View>(R.id.harga_barang) as EditText
        mEditNama.setText(barang.getNama())
        mEditMerk.setText(barang.getMerk())
        mEditHarga.setText(barang.getHarga())
        builder.setView(view)

        //final Buku mBarang = (Barang)getIntent().getSerializableExtra("
        if (barang != null) {
            builder.setPositiveButton("SIMPAN") { dialog, id ->
                Buku.setNama(mEditNama!!.text.toString())
                Buku.setMerk(mEditMerk!!.text.toString())
                Buku.setHarga(mEditHarga!!.text.toString())
                updateDataBarang(barang)
            }
        }
        builder.setNegativeButton("BATAL") { dialog, id -> dialog.dismiss() }
        val dialog: Dialog = builder.create()
        dialog.show()
    }

    /**
     * submit data Buku
     * ini adalah kode yang digunakan untuk mengirimkan data ke Firebase Realtime Database
     * set onSuccessListener yang berisi kode yang akan dijalankan
     * ketika data berhasil ditambahkan
     */
    private fun submitDataBarang(barang: Buku) {
        mDatabaseReference.child("data_barang").push().setValue(barang).addOnSuccessListener(this, object : OnSuccessListener<Void?>() {
            fun onSuccess(mVoid: Void?) {
                Toast.makeText(this@MainActivity, "Data Buku berhasil di simpan !", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * update/edit data Buku
     * ini adalah kode yang digunakan untuk mengirimkan data ke Firebase Realtime Database
     * set onSuccessListener yang berisi kode yang akan dijalankan
     * ketika data berhasil ditambahkan
     */
    private fun updateDataBarang(barang: Buku?) {
        mDatabaseReference.child("data_barang").child(barang.getKey()).setValue(barang).addOnSuccessListener(object : OnSuccessListener<Void?>() {
            fun onSuccess(mVoid: Void?) {
                Toast.makeText(this@MainActivity, "Data berhasil di update !", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * hapus data Buku
     * ini kode yang digunakan untuk menghapus data yang ada di Firebase Realtime Database
     * set onSuccessListener yang berisi kode yang akan dijalankan
     * ketika data berhasil dihapus
     */
    private fun hapusDataBarang(barang: Buku) {
        if (mDatabaseReference != null) {
            mDatabaseReference.child("data_barang").child(barang.getKey()).removeValue().addOnSuccessListener(object : OnSuccessListener<Void?>() {
                fun onSuccess(mVoid: Void?) {
                    Toast.makeText(this@MainActivity, "Data berhasil di hapus !", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

}
