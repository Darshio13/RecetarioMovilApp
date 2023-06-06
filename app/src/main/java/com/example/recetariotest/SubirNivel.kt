package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recetariotest.databinding.ActivitySubirNivelBinding


class SubirNivel : AppCompatActivity() {
    lateinit var binding : ActivitySubirNivelBinding;
    lateinit var sharedPreferences: SharedPreferences;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubirNivelBinding.inflate(layoutInflater);
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var user_lvl = sharedPreferences.getInt("level_user", 0)!!

        when (user_lvl)
        {
            1 -> binding.nivel.text = "Tu nivel actual es: Sin experiencia.";
            2 -> binding.nivel.text = "Tu nivel actual es: Principiante.";
            3 -> binding.nivel.text = "Tu nivel actual es: Con experiencia.";

        }


        //Subir de nivel
        binding.subir.setOnClickListener {
            //Query
            var iduser = sharedPreferences.getString("id_user", null)!!

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url = "https://apitest-production-3e6f.up.railway.app/user/LevelUpUser/"+ iduser

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    // RESPONSE.
                    if (user_lvl <3 && response.toString() == "1")
                    {
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Felicidades.")
                            .setContentText("Acabas de subir de nivel.").show()

                        user_lvl +=1;
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putInt("level_user",  user_lvl);
                        editor.putInt("recipes_done",  0);
                        editor.commit()

                        when (user_lvl)
                        {
                            1 -> binding.nivel.text = "Tu nivel actual es: Sin experiencia.";
                            2 -> binding.nivel.text = "Tu nivel actual es: Principiante.";
                            3 -> binding.nivel.text = "Tu nivel actual es: Con experiencia.";

                        }
                    }
                    else
                    {
                        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("No puedes subir de nivel.")
                            .setContentText("Revisa si tienes las 5 recetas hechas o si ya eres nivel maximo.").show()

                    }


                },
                {  })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)
        }






    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        finish();
    }
}