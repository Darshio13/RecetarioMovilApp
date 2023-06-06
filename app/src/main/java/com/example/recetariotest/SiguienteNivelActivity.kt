package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recetariotest.databinding.ActivitySiguienteNivelBinding
import org.json.JSONArray
import cn.pedant.SweetAlert.SweetAlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener
import com.squareup.picasso.Picasso


class SiguienteNivelActivity : AppCompatActivity() {
    lateinit var binding : ActivitySiguienteNivelBinding;
    lateinit var sharedPreferences: SharedPreferences;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiguienteNivelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var nivelUser = sharedPreferences.getInt("level_user", 0);
        nivelUser+=1;

        var url = ""
        val queue = Volley.newRequestQueue(this)

        url = "https://apitest-production-3e6f.up.railway.app/receta/Siguiente/"+nivelUser


        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                //No hay conexion no se encntro el API
                if (response.toString().length <30)
                {



                }
                else {

                    //Parsear
                    //Se obtiene el array de recetas
                    val jarray = JSONArray(response.toString());
                    //Insertar recetas
                    for (nums in 0..jarray.length()-1) {

                        //Se obtienen los atributos por el indice de jsonObj
                        val jsonObj = jarray.getJSONObject(nums);
                        val nombre = jsonObj.getString("titulo");
                        val ImageArray = jsonObj.getJSONArray("Imagenes")
                        val ImageObject = ImageArray.getJSONObject(0);
                        val urlImg = ImageObject.getString("nombre_archivo")
                        val idReciope = jsonObj.getInt("id_receta")
                        insertRecipe(nombre, urlImg, idReciope, false)
                    }


                }
            },
            {

            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)


    }

    private fun insertRecipe (title:String, url:String, id:Int, experto: Boolean)
    {
        val view: View = layoutInflater.inflate(R.layout.recipe_card_emplate, null)

        val img = view.findViewById<ImageView>(R.id.img)
        val txt = view.findViewById<TextView>(R.id.txt)
        val card = view.findViewById<LinearLayout>(R.id.card)
        val actualcard= view.findViewById<CardView>(R.id.actualcard);

        txt.text = title
        Picasso.get().load(url).into(img);
        if (experto == true)
        {
            binding.container.addView(view,0)
            actualcard.setCardBackgroundColor(Color.parseColor("#FED766"))



        }
        else
        {
            binding.container.addView(view)

        }

    }



    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        finish();
    }
}