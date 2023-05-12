package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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


class SiguienteNivelActivity : AppCompatActivity() {
    lateinit var binding : ActivitySiguienteNivelBinding;
    lateinit var sharedPreferences: SharedPreferences;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiguienteNivelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var iduser = sharedPreferences.getString("id_user", null)!!

        //Obtener los comodines
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/comodin/GetComodinSinusar/"+ iduser

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                if (response.toString().length >10)
                {
                    //Se obtiene el array de los comodines
                    val jarray = JSONArray(response.toString());
                    for (nums in 0..jarray.length()-1) {

                        //Se obtienen los atributos por el indice de jsonObj

                        val jsonObj = jarray.getJSONObject(nums);
                        val id_comodin = jsonObj.getInt("id_comodin");
                        val tipo = jsonObj.getInt("tipo")
                         //Se crea lar card  del comodin
                        val view: View = layoutInflater.inflate(R.layout.comodin_template, null)
                        binding.container.addView(view)

                        val txt = view.findViewById<TextView>(R.id.txt)
                        val card = view.findViewById<CardView>(R.id.card)

                        when (tipo)
                        {
                            1 -> { txt.text = "\uD83C\uDF1F Aumentar contador de recetas." }
                            2 -> { txt.text = "\uD83C\uDF1F Receta de nivel superior." }
                            3 -> { txt.text = "\uD83C\uDF1F No bajar de nivel." }
                            4 -> { txt.text = "\uD83C\uDF1F Aprende Skills." }
                        }
                        card.setOnClickListener {
                            //Dialog
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("¿Quieres usar este comodin? " + id_comodin)
                                .setContentText("Este comodin dejara de estar disponible al usarlo.")
                                .setConfirmText("Sí.")
                                .setCancelText("Cancelar")
                                .showCancelButton(true)
                                .setCancelClickListener { sDialog -> sDialog.cancel();
                                }
                                .setConfirmClickListener { sDialog ->

                                    binding.container.removeView(view)
                                    //Query
                                    QueryUseComodin(id_comodin, tipo);

                                    when (tipo) {
                                        1 -> {

                                            sDialog
                                                .setTitleText("Listo.")
                                                .setContentText("Tu comodin ha sido usado. Se ha agregado una unidad a tu contador de recetaas hechas.")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        }
                                        2 -> {

                                            sDialog
                                                .setTitleText("Listo.")
                                                .setContentText("Tu comodin ha sido usado. Puedes ver tu receta de nivel superior en el inicio.")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        }
                                        3 -> {

                                            sDialog
                                                .setTitleText("Listo.")
                                                .setContentText("Tu comodin ha sido usado. No bajaras de nivel al terminar el mes.")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        }
                                        4 -> {

                                            sDialog
                                                .setTitleText("Listo.")
                                                .setContentText("Tu comodin ha sido usado. Podras ver el contenido en el apartado Aprender skills.")
                                                .setConfirmText("OK")
                                                .showCancelButton(false)
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                                        }
                                    }



                                }
                                .show()

                        }

                    }

                }

            },
            {  })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)



    }

    fun QueryUseComodin(comodin: Int, tipo: Int)
    {
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        var recetasHechas = sharedPreferences.getInt("recipes_done", 0)

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)

        val url = "https://apitest-production-6abd.up.railway.app/comodin/UseComodin/"+ comodin

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.


                if (tipo== 1 && recetasHechas < 5)
                {
                    editor.putInt("recipes_done", recetasHechas+1 );
                    editor.commit()

                }


            },
            {  })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        finish();
    }
}