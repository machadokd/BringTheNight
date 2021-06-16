package ipvc.estg.bringthenight

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import android.widget.RadioGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_criar_evento.*
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.HashMap


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val newWordActivityRequestCode = 1
    private var lat: String? = ""
    private  var long: String? = ""

    private var image : Uri? = null

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        onActivityResult(PICK_IMAGE, result)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()


        val button = findViewById<Button>(R.id.button_registo)
        button.setOnClickListener {
            createAccount()
        }


        val radioGroup = findViewById<RadioGroup>(R.id.radio_resgisto)
        radioGroup.check(R.id.empresa)
        clickRadioEmpresa()

        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, checkedId ->
            var checkedId = checkedId
            checkedId = radioGroup.checkedRadioButtonId
            when (checkedId) {
                R.id.empresa -> {
                    clickRadioEmpresa()
                }
                R.id.user -> {
                    clickRadioUser()
                }
            }
        })

        val info_morada = findViewById<TextView>(R.id.informacao_morada_empresa)
        info_morada.setOnClickListener{
            val intent = Intent(this@RegisterActivity, MapaRegistoActivity::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }

        val imagemPerfil = findViewById<ImageView>(R.id.imagem_empresa_registo)
        imagemPerfil.setOnClickListener {
            load_image()
        }

    }



    private fun load_image() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        resultLauncher.launch(gallery)
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        if (result.resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            image = result.data?.data!!

            val imagemP = findViewById<ImageView>(R.id.imagem_empresa_registo)
            imagemP.setImageURI(image)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            lat = data?.getStringExtra(MapaRegistoActivity.EXTRA_REPLY_LAT)
            long = data?.getStringExtra(MapaRegistoActivity.EXTRA_REPLY_LONG)

            val address = getAddress(lat!!.toDouble(), long!!.toDouble())
            val morada= findViewById<EditText>(R.id.morada_empresa_registo)
            morada.setText(address)
        } else{

        }
    }


    private fun getAddress(lat :Double, long: Double):String?{
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, long, 1)
        return list[0].getAddressLine(0)
    }


    private fun createAccount() {
        val email = findViewById<EditText>(R.id.email_registo).text.toString()
        val password = findViewById<EditText>(R.id.pass_registo).text.toString()
        val nome_empresa = findViewById<EditText>(R.id.nome_empresa_registo).text.toString()
        val nickname_user = findViewById<EditText>(R.id.nickname_registo).text.toString()
        val morada = findViewById<EditText>(R.id.morada_empresa_registo).text.toString()
        var tipo = ""
        var nome = ""

        if (!email.isEmpty() && !password.isEmpty() && !nome_empresa.isEmpty() && !morada.isEmpty()|| !email.isEmpty() && !password.isEmpty() && !nickname_user.isEmpty()){
            val empresa = findViewById<RadioButton>(R.id.empresa)
            val nick = findViewById<RadioButton>(R.id.user)

            if(empresa.isChecked){
                tipo = "empresa"
                nome = nome_empresa
            } else if (nick.isChecked){
                tipo = "user"
                nome = nickname_user
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        //Toast.makeText(baseContext, "Registo efetuado com sucesso.", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        //updateUI(user)

                        val userid : String = user!!.uid

                        Log.d("machas", userid)

                        var hashMap : HashMap<String, Any> = HashMap()

                        val now = Date()
                        val filename = now.time.toString()

                        hashMap.put("id", userid)
                        hashMap.put("email", email)
                        hashMap.put("nome", nome)
                        hashMap.put("tipo", tipo)
                        hashMap.put("morada", morada)
                        hashMap.put("imagem", filename)
                        hashMap.put("latitude", lat!!.toDouble())
                        hashMap.put("longitude", long!!.toDouble())


                        val storageReference = FirebaseStorage.getInstance().getReference("images/$userid/perfil/$filename")

                        storageReference.putFile(image!!).addOnSuccessListener {
                            Toast.makeText(this, "Imagem Carregada.", Toast.LENGTH_SHORT).show()
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Falhou a dar upload da imagem.", Toast.LENGTH_SHORT).show()
                        }

                        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference().child(
                            "users"
                        ).child(userid)

                        ref.setValue(hashMap).addOnCompleteListener { task->
                            if(task.isSuccessful){
                                Log.d(ContentValues.TAG, "createUserWithEmail2:success")
                                Toast.makeText(baseContext, "User Criado", Toast.LENGTH_SHORT).show()
                                finish()
                            }else{
                                Toast.makeText(baseContext, "User não Criado", Toast.LENGTH_SHORT).show()
                                Log.w(TAG, "createUserWithEmail2:failure", task.exception)
                            }
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }

        }else{
            Toast.makeText(baseContext, "Campos Vazios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickRadioEmpresa(){
        val nickname = findViewById<EditText>(R.id.nickname_registo)
        nickname.visibility = EditText.GONE

        val nome_empresa = findViewById<EditText>(R.id.nome_empresa_registo)
        nome_empresa.visibility = EditText.VISIBLE

        val morada_empresa = findViewById<EditText>(R.id.morada_empresa_registo)
        morada_empresa.visibility = EditText.VISIBLE

        val info_morada = findViewById<TextView>(R.id.informacao_morada_empresa)
        info_morada.visibility = TextView.VISIBLE

        val imagem = findViewById<ImageView>(R.id.imagem_empresa_registo)
        imagem.visibility = TextView.VISIBLE
    }

    private fun clickRadioUser(){
        val nome_empresa = findViewById<EditText>(R.id.nome_empresa_registo)
        nome_empresa.visibility = EditText.GONE

        val nickname = findViewById<EditText>(R.id.nickname_registo)
        nickname.visibility = EditText.VISIBLE

        val morada_empresa = findViewById<EditText>(R.id.morada_empresa_registo)
        morada_empresa.visibility = EditText.GONE

        val info_morada = findViewById<TextView>(R.id.informacao_morada_empresa)
        info_morada.visibility = TextView.GONE

        val imagem = findViewById<ImageView>(R.id.imagem_empresa_registo)
        imagem.visibility = TextView.GONE
    }



    private fun updateUI(user: FirebaseUser?) {

    }

    companion object {
        private const val TAG = "EmailPassword"
        private const val PICK_IMAGE = 100
    }

}