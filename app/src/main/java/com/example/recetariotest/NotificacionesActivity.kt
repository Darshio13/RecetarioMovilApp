package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recetariotest.databinding.ActivityNotificacionesBinding
import org.json.JSONArray

class NotificacionesActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences;

    lateinit var binding: ActivityNotificacionesBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificacionesBinding.inflate(layoutInflater);
        setContentView(binding.root)

        //Query
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        var iduser = sharedPreferences.getString("id_user", null)!!

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/comodin/GetComodinByUser/"+ iduser

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                if (response.length >10)
                {
                    //Se obtiene el array de recetas
                    val jarray = JSONArray(response.toString());
                    for (nums in 0..jarray.length()-1) {
                        val jsonObj = jarray.getJSONObject(nums);
                        val tipo = jsonObj.getInt("tipo");
                        val visto = jsonObj.getInt("seen");
                        val id_comodin = jsonObj.getInt("id_comodin");
                        val view: View = layoutInflater.inflate(R.layout.notificacion_template, null)
                        val card = view.findViewById<CardView>(R.id.card);
                        val txt = view.findViewById<TextView>(R.id.txt)

                        if (visto==0)
                        {
                            card.setCardBackgroundColor(Color.parseColor("#D3FCD5"))
                            // card.setCardBackgroundColor(Color.parseColor("##D3FCD5"))
                        }

                        when (tipo)
                        {
                            1 ->
                            {
                                txt.text = "\uD83C\uDF81 Desbloqueaste comodin de: Aumentar contador de recetas."
                            }

                            2 ->
                            {
                                txt.text = "\uD83C\uDF81 Desbloqueaste comodin de: Receta de nivel superior"
                            }

                            3 ->
                            {
                                txt.text = "\uD83C\uDF81 Desbloqueaste comodin de: No bajar de nivel"
                            }

                            4 ->
                            {
                                txt.text = "\uD83C\uDF81 Desbloqueaste comodin de: Aprende Skills"
                            }

                        }

                        binding.container.addView(view)

                        card.setOnClickListener {
                            //Dialog
                            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("¿Quieres usar este comodin?" )
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
                                                .setContentText("Tu comodin ha sido usado. Puedes ver tu receta de nivel superior en el inicio. Si eres el nivel maximo, te llegara una receta por correo por uno de nuestros chefs expertos.")
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