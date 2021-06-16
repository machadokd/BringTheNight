package ipvc.estg.bringthenight

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ipvc.estg.bringthenight.models.Empresa
import ipvc.estg.bringthenight.models.Evento
import ipvc.estg.bringthenight.models.User
import kotlinx.android.synthetic.main.activity_criar_evento.*
import java.util.*

class CriarEventoActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var userId : String
    private lateinit var user : String
    private lateinit var events : DatabaseReference
    private var image : Uri? = null

    private var latitude : Double? = null
    private var longitude : Double? = null

    private val newWordActivityRequestCode = 1

//    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        onActivityResult(PICK_IMAGE, result)
//    }

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

    private var date : Date? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_evento)

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        events = FirebaseDatabase.getInstance().getReference("events")

        FirebaseDatabase.getInstance().reference.child("users").child(userId).get().addOnSuccessListener {
            Log.i("firebase_create_event", "Got value ${it.getValue(User::class.java)}")
            user = it.getValue(Empresa::class.java)!!.nome
            latitude = it.getValue(Empresa::class.java)!!.latitude
            longitude = it.getValue(Empresa::class.java)!!.longitude

            val address = getAddress(latitude!!, longitude!!)
            locationTextView.text = address

            Log.i("firebase_create_event", "Got value ${user}")
        }.addOnFailureListener{
            Log.e("firebase_create_event", "Error getting data", it)
        }

        create_button.setOnClickListener {
            create_event()
        }

        add_image.setOnClickListener {
            load_image()
        }

        date_picker_button.setOnClickListener {
            pick_date()
        }

        location.setOnClickListener{
            val intent = Intent(this@CriarEventoActivity, MapsActivityCreateEvent::class.java)
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
        startActivityForResult(gallery, PICK_IMAGE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            latitude = data?.getStringExtra(MapsActivityCreateEvent.EXTRA_REPLY_LAT)!!.toDouble()
            longitude = data?.getStringExtra(MapsActivityCreateEvent.EXTRA_REPLY_LONG)!!.toDouble()

            val address = getAddress(latitude!!.toDouble(), longitude!!.toDouble())
//            val morada= findViewById<EditText>(R.id.morada_empresa_registo)
            locationTextView.text = address
        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            image = data!!.data!!
            imageView.setImageURI(image)
        }
    }

    private fun create_event() {
        val title = title_edit_text.text

        if (!title.isNullOrBlank() && image != null && date != null){

            val now = Date()
            val filename = now.time.toString()
            val storageReference = FirebaseStorage.getInstance().getReference("images/$userId/$filename")

            val key = events.push().key

            var evento = Evento(titulo = title.toString(), estabelecimento = userId,  imagem = filename, nome_establecimento = user, id = key!!, date = date!!)

            if (longitude != null && latitude != null) {
                evento.latitude = latitude!!.toDouble()
                evento.longitude = longitude!!.toDouble()
            }


            events.child(key).setValue(evento).addOnSuccessListener {

                storageReference.putFile(image!!).addOnSuccessListener {
                    Toast.makeText(this, "Imagem Carregada.", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Falhou a dar upload da imagem.", Toast.LENGTH_SHORT).show()
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

        date = Date(savedYear-1900, savedMonth, savedDay, savedHour, savedMinute)

        dateTextView.text = date!!.toLocaleString()

    }
}