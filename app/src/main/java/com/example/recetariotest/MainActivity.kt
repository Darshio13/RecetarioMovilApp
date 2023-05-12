package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
 import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
 import org.json.JSONArray
import org.json.JSONObject
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;
 import usuario
import com.example.recetariotest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var user: EditText;
        lateinit var pass: EditText;
        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide();

        val checkLogin = findViewById(R.id.login) as Button
        checkLogin.setOnClickListener {
            user = findViewById(R.id.username)
            pass = findViewById(R.id.password)

            //Se verifica si se ingreso un nombre de usuario
            if (user.text.toString().isEmpty() )
            {
                Toast.makeText(this@MainActivity, "Ingresa tu nombre de usuario", Toast.LENGTH_SHORT).show()
            }
            else {
                //Se verifica si se ingreso una contraseña
                if (pass.text.toString().isEmpty()) {
                    Toast.makeText(this@MainActivity, "Ingresa tu cotraseña", Toast.LENGTH_SHORT) .show()
                    //binding.layoutPassword.setError("Ingresa tu contraseña");
                }
                else{
                    //Se tienen ambos datos
                     //Http response
                    val textView = findViewById<TextView>(R.id.text)
                    // ...

                    // Instantiate the RequestQueue.
                    val queue = Volley.newRequestQueue(this)
                    val url = "https://apitest-production-6abd.up.railway.app/user/login/"+user.text.toString()+"/"+pass.text.toString()

                    // Request a string response from the provided URL.
                    val stringRequest = StringRequest(
                        Request.Method.GET, url,
                        { response ->
                            // RESPONSE.
                            //No hubo sesion
                            if (response.toString().length <30)
                            {
                                //Toast.makeText(this@MainActivity, "Las credenciales no coinciden", Toast.LENGTH_SHORT).show()
                                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText("Las credenciales no coinciden.").show()


                            }
                            else {
                                parseJson(response.toString());
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish()

                            }
                            },
                        { textView.text = "That didn't work!" })

                    // Add the request to the RequestQueue.
                    queue.add(stringRequest)

                }

            }
        }

    }


    override fun onStart() {
        super.onStart()
        lateinit var sharedPreferences: SharedPreferences;
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        //var iduser="";
        //iduser = sharedPreferences.getString("id_user", null)!!
        if (sharedPreferences.contains("id_user")){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun parseJson(data: String) {
        lateinit var sharedPreferences: SharedPreferences;
        var PREFS_KEY = "prefs"

        //Json
        val jarray = JSONArray(data);
        val jsonObj = jarray.getJSONObject(0);
        val nombre = jsonObj.getString("nombre_usuario");
        val user = usuario();
        user.nombre_usuario= nombre;
        user.id_usuario = jsonObj.getInt("id_usuario");


        //Set Session
        sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("id_user", user.id_usuario.toString());
        editor.putInt("level_user", jsonObj.getInt("nivel"));
        editor.putInt("recipes_done",  jsonObj.getInt("contador_recetas") )
        editor.putInt("NoNotificaciones", jsonObj.getInt("numberOfComodines"));

        editor.putString("user_name", user.nombre_usuario);

        editor.apply()



    }
}