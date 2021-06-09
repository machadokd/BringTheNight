package ipvc.estg.bringthenight

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.*
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var database: DatabaseReference

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

    }

    private fun createAccount() {
        val email = findViewById<EditText>(R.id.email_registo).text.toString()
        val password = findViewById<EditText>(R.id.pass_registo).text.toString()
        val nome_empresa = findViewById<EditText>(R.id.nome_empresa_registo).text.toString()
        val nickname_user = findViewById<EditText>(R.id.nickname_registo).text.toString()
        var tipo = ""
        var nome = ""

        if (!email.isEmpty() && !password.isEmpty() && !nome_empresa.isEmpty() || !email.isEmpty() && !password.isEmpty() && !nickname_user.isEmpty()){
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

                        hashMap.put("id", userid)
                        hashMap.put("email", email)
                        hashMap.put("nome", nome)
                        hashMap.put("tipo", tipo)



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
    }

    private fun clickRadioUser(){
        val nome_empresa = findViewById<EditText>(R.id.nome_empresa_registo)
        nome_empresa.visibility = EditText.GONE

        val nickname = findViewById<EditText>(R.id.nickname_registo)
        nickname.visibility = EditText.VISIBLE
    }



    private fun updateUI(user: FirebaseUser?) {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}