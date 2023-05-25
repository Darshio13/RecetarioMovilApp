package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recetariotest.databinding.ActivityRetroalimentacionBinding
import org.json.JSONArray
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit

class RetroalimentacionActivity : AppCompatActivity() {
    lateinit var binding : ActivityRetroalimentacionBinding;
    lateinit var sharedPreferences: SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetroalimentacionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var userTotal: Long = 0;
        var stepTotal = 0;
        val f: NumberFormat = DecimalFormat("00")
        supportActionBar?.hide();


        //Obtener Json de pasos
        val StepsString = intent.getStringExtra("STEPS_RECIPE");
        val JsonStepsArray = JSONArray(StepsString);
        //Obtener array de los tiempos del usuario
        val UserDataArray = intent.getLongArrayExtra("string-array")



        //Insertar Datos
        for (num in 0..JsonStepsArray.length()-1)
        {
            val view: View = layoutInflater.inflate(R.layout.ingredients_template, null)

            //User time
            userTotal += UserDataArray?.get(num)!!;

            val user = view.findViewById<TextView>(R.id.ingredient);
            var time = UserDataArray?.get(num)?.let { formatMilliseconds(it) }
            user.text = time;

            //Step time
            val step = view.findViewById<TextView>(R.id.number)
            stepTotal += JsonStepsArray.getJSONObject(num).getInt("tiempo");
            step.text= f.format(JsonStepsArray.getJSONObject(num).getInt("tiempo")) + ":00"
            binding.Tiempos.addView(view)

        }

        //Insertar total
        val viewTotal: View = layoutInflater.inflate(R.layout.ingredients_template, null)
        val userTotalInsert = viewTotal.findViewById<TextView>(R.id.ingredient);
        userTotalInsert.text = formatMilliseconds(userTotal);
        val recipeTotal =  viewTotal.findViewById<TextView>(R.id.number);
        recipeTotal.text =  f.format(stepTotal) + ":00"
        binding.total.addView(viewTotal)

        //Get percentages
        var anticipacion = userTotal.toFloat()/ TimeUnit.MINUTES.toMillis(stepTotal.toLong())
        anticipacion = anticipacion * 100;

        //Se hace el registro
        if (anticipacion >=80)
        {
            //Poner historial
            ObtenerHistorial(JsonStepsArray.getJSONObject(0).getInt("id_receta"))
            //Se hace el historial lmao
            Agregarhistorial(JsonStepsArray.getJSONObject(0).getInt("id_receta"), userTotal);


            //Se actualiza la informacion del usuario
            actualizarUserData(JsonStepsArray.getJSONObject(0).getInt("id_receta"))

            //Comodin receta hecha, Hecho en el tiempo correcto TIPO 1
            if (anticipacion < 120 )
            {
                //Toast.makeText(this@RetroalimentacionActivity, "Comodin 1 ", Toast.LENGTH_SHORT).show()
                CreateComodin(JsonStepsArray.getJSONObject(0).getInt("id_receta"), 1)

            }

            //Comodin receta superior 20 % de ventaja TIPO 2
            if (anticipacion < 85)
            {
                //Toast.makeText(this@RetroalimentacionActivity, "Comodin 2 ", Toast.LENGTH_SHORT).show()
                CreateComodin(JsonStepsArray.getJSONObject(0).getInt("id_receta"), 2)

            }

            //Comodin no bajar de nivel, 15% de ventaja TIPO 3
            if (anticipacion < 100 && anticipacion >=85)
            {
                //Toast.makeText(this@RetroalimentacionActivity, "Comodin 3 ", Toast.LENGTH_SHORT).show()
                CreateComodin(JsonStepsArray.getJSONObject(0).getInt("id_receta"), 3)

            }

            //Comodin aprende skills TIPO 4
            CreateComodin(JsonStepsArray.getJSONObject(0).getInt("id_receta"), 4)

            SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Felicidades")
                .setContentText("Has completado la receta en un tiempo valido. Se sumara a tu contador de recetas hechas.").show()



        }
        else {

            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("La receta hecha no es valida.")
                .setContentText("Has completado la receta en un tiempo invalido. No se te sumara a tu contador de recetas hechas.").show()

        }


        //Toast.makeText(this@RetroalimentacionActivity, anticipacion.toString(), Toast.LENGTH_SHORT).show()









    }

    fun formatMilliseconds(milliseconds: Long): String {
        val f: NumberFormat = DecimalFormat("00")
        val totalSeconds = milliseconds / 1000

        val minutes = totalSeconds / 60

        val seconds = totalSeconds % 60

        return f.format(minutes) + ":" + f.format(seconds)

    }

    fun actualizarUserData(receta: Int)
    {
        //Session
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var iduser = sharedPreferences.getString("id_user", null)!!

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/user/UpdateUserAppData/"+ iduser+"/"+receta

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                //Toast.makeText(this@RetroalimentacionActivity, response.toString(), Toast.LENGTH_SHORT) .show()

            },
            {  })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun CreateComodin(receta: Int, tipo: Int)
    {
        //Session
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var iduser = sharedPreferences.getString("id_user", null)!!


        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/comodin/NuevoComodin/"+ iduser+"/"+receta+"/"+tipo

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                //Toast.makeText(this@RetroalimentacionActivity, response.toString(), Toast.LENGTH_SHORT) .show()

            },
            {  })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }


    fun ObtenerHistorial(receta: Int)
    {
        //Session
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        var iduser = sharedPreferences.getString("id_user", null)!!
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/user/GetHistorial/"+ iduser+"/"+receta

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->

                if (response.toString().length >10)
                {
                    val viewC: View = layoutInflater.inflate(R.layout.ingredients_template, null)
                    viewC.findViewById<TextView>(R.id.ingredient).text = "Dia:"
                    viewC.findViewById<TextView>(R.id.number).text ="tiempo";

                    val jarray = JSONArray(response.toString());
                    var record = 0;
                    for (nums in 0..jarray.length()-1) {
                        val jsonObj = jarray.getJSONObject(nums);
                        val tiempo = formatMilliseconds(jsonObj.getInt("tiempo").toLong())
                        val dia = nums +1;
                        val view: View = layoutInflater.inflate(R.layout.ingredients_template, null)
                        record = record + jsonObj.getInt("tiempo");
                        val userTotalInsert = view.findViewById<TextView>(R.id.ingredient);
                        userTotalInsert.text = dia.toString();
                        val recipeTotal =  view.findViewById<TextView>(R.id.number);
                        recipeTotal.text =  tiempo
                        binding.historialtxt.text = "Registros semanal:"
                        binding.historial.addView(view)

                    }
                    var promedio = record / jarray.length();
                    binding.promediotxt.text = "Promedio de preparacion semanal:" + formatMilliseconds(promedio.toLong());

                }

                // RESPONSE.
                //Toast.makeText(this@RetroalimentacionActivity, response.toString(), Toast.LENGTH_SHORT).show()

            },
            {  })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun Agregarhistorial(receta: Int, tiempo: Long)
    {
        //Session
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        //Actualizar contador de recetas
        var recetasHechas = sharedPreferences.getInt("recipes_done", 0)
        if (recetasHechas < 5)
        {
            editor.putInt("recipes_done", recetasHechas+1 );
            editor.commit()
        }


        var iduser = sharedPreferences.getString("id_user", null)!!
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/user/addHistorial/"+ iduser+"/"+receta+"/"+tiempo

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                //Toast.makeText(this@RetroalimentacionActivity, response.toString(), Toast.LENGTH_SHORT).show()

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