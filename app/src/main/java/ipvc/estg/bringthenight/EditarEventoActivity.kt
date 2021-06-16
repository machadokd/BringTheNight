package ipvc.estg.bringthenight

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.bringthenight.models.Empresa
import ipvc.estg.bringthenight.models.Evento
import kotlinx.android.synthetic.main.activity_criar_evento.*
import kotlinx.android.synthetic.main.activity_criar_evento.imageView
import kotlinx.android.synthetic.main.activity_criar_evento.title_edit_text
import kotlinx.android.synthetic.main.activity_editar_evento.*
import kotlinx.android.synthetic.main.empresa_post.view.*
import java.io.File
import java.util.*

class EditarEventoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    
    private lateinit var db_reference : DatabaseReference
    private lateinit var evento: Evento
    private var data : Date? = null

    private var image : Uri? = null
//    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        onActivityResult(PICK_IMAGE, result)
//    }

    private val newWordActivityRequestCode = 1

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_evento)

        val id_evento = intent.getStringExtra("evento")
        
        db_reference = FirebaseDatabase.getInstance().reference.child("events").child(id_evento!!)


        db_reference.get().addOnCompleteListener {
            evento = it.getResult()!!.getValue(Evento::class.java)!!
            Log.i("editar_evento", "evento ${evento}")
            title_edit_text.setText(evento.titulo)
            dateEditTextView.text = evento.date.toLocaleString()
            data = evento.date

            val address = getAddress(evento.latitude, evento.longitude)
            locationEditTextView.text = address

            val storageRef = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")
            val localFile = File.createTempFile("temp_file", "png")
            var bitmap: Bitmap? = null

            storageRef.getFile(localFile).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Log.i("firebase_image", "Image $it")
                imageView.setImageBitmap(bitmap!!)
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Evento não existe", Toast.LENGTH_SHORT).show()
            finish()
        }

        edit_image.setOnClickListener { load_image() }

        edit_button.setOnClickListener { edit_event() }

        date_picker_button_edit.setOnClickListener {
            pick_date()
        }

        locationEdit.setOnClickListener{
            val intent = Intent(this@EditarEventoActivity, MapsActivityEditEvent::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
        

    }

    private fun getAddress(lat :Double, long: Double):String?{
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, long, 1)
        return list[0].getAddressLine(0)
    }

    private fun load_image() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

//        resultLauncher.launch(gallery)
        startActivityForResult(gallery, PICK_IMAGE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            evento.latitude = data?.getStringExtra(MapsActivityEditEvent.EXTRA_REPLY_LAT)!!.toDouble()
            evento.longitude = data?.getStringExtra(MapsActivityEditEvent.EXTRA_REPLY_LONG)!!.toDouble()

            val address = getAddress(evento.latitude, evento.longitude)
            locationEditTextView.text = address
        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            image = data!!.data!!
            imageView.setImageURI(image)
        }
    }


    private fun edit_event() {
        val title = title_edit_text.text

        if (!title.isNullOrBlank() && data != null){

            val storageReference = FirebaseStorage.getInstance().getReference("images/${evento.estabelecimento}/${evento.imagem}")


            evento.apply {
                titulo = title.toString()!!
                date = data as Date
            }


            db_reference.setValue(evento).addOnSuccessListener {

                if (image != null){
                    storageReference.putFile(image!!).addOnSuccessListener {
                        Toast.makeText(this, "Imagem Trocada.", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Falhou a dar upload da imagem.", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Falhou a criar evento.", Toast.LENGTH_SHORT).show()
            }

        }
        else {
            Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE = 100
    }


    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    private fun pick_date() {
        getDateTimeCalendar()
        DatePickerDialog(this, this, year, month, day).show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year

        getDateTimeCalendar()
        TimePickerDialog(this, this, hour, minute, true).show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute

        data = Date(savedYear-1900, savedMonth, savedDay, savedHour, savedMinute)

        dateEditTextView.text = data!!.toLocaleString()

    }

}