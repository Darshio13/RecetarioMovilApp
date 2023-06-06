package com.example.recetariotest

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recetariotest.databinding.ActivityHomeBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray


class HomeActivity : AppCompatActivity() {
    lateinit var userTV: TextView;
    lateinit var sharedPreferences: SharedPreferences;

    lateinit var binding: ActivityHomeBinding;



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)
        //Binding set up
        binding = ActivityHomeBinding.inflate(layoutInflater);
        setContentView(binding.root)
        supportActionBar?.hide();

         setSupportActionBar(binding.toolbar)


        //Session
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var nameuser="";
        nameuser = sharedPreferences.getString("user_name", null)!!
        //binding.welcome.text= "Bienvenido "+nameuser;
        var nivelUser = sharedPreferences.getInt("level_user", 0);






        //Query
        var url = ""
        val queue = Volley.newRequestQueue(this)
        if (nivelUser == 1 )
        {
            url = "https://apitest-production-3e6f.up.railway.app/receta/NivelUno"

        }
        else
        {
            url = "https://apitest-production-3e6f.up.railway.app/receta/Nivel/"+nivelUser

        }

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                //No hay conexion no se encntro el API
                if (response.toString().length <30)
                {
                    //Toast.makeText(this@MainActivity, "Las credenciales no coinciden", Toast.LENGTH_SHORT).show()
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText("Error. Parece que no hay recetas de este nivel").show()


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
                Toast.makeText(this@HomeActivity, "No se pudo completar la request", Toast.LENGTH_SHORT)
                    .show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)


        InsertNextLevelRecipes();









    }


    private fun InsertNextLevelRecipes ()
    {
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var idUser = sharedPreferences.getString("id_user", null)!!
        var url = ""
        val queue = Volley.newRequestQueue(this)

        url = "https://apitest-production-3e6f.up.railway.app/comodin/GetRecipeForComodin/"+idUser


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
                        insertRecipe(nombre, urlImg, idReciope, true)
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

        card.setOnClickListener{
            //Toast.makeText(this@HomeActivity, "Se toco la receta "+ id.toString(), Toast.LENGTH_SHORT) .show()
            val intent = Intent(baseContext, ViewRecipe::class.java)
            intent.putExtra("ID_RECIPE", id.toString())
            startActivity(intent)

        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //Establecer contador
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var recetasHechas = sharedPreferences.getInt("recipes_done", 0)
        var iduser = sharedPreferences.getString("id_user", null)!!


        val item = menu.findItem(R.id.RecipesDone)
        val item2 = menu.findItem(R.id.Notis)

        item.title = "\uD83C\uDF73"+recetasHechas

        //Query para las notificaciones

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-3e6f.up.railway.app/comodin/GetComodinNotis/"+iduser

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                // RESPONSE.
                item2.title = "\uD83D\uDD14"+ response



            },
            {                item2.title = "\uD83D\uDD14"+ 0
            })
        queue.add(stringRequest)


        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId)
        {
            R.id.logout ->
            {
                sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().commit();
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish();
                return true;
            }


            R.id.Notis-> {
                val intent = Intent(this, NotificacionesActivity::class.java);
                startActivity(intent);
                finish();
                return true;
            }

            R.id.ApSkills ->
            {
                val intent = Intent(this, SkillsActivity::class.java);
                startActivity(intent);
                finish();
                return true;
            }

            R.id.Snivel ->{
                sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                var nivelUser = sharedPreferences.getInt("level_user", 0);
                if (nivelUser>2)
                {
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Espera")
                        .setContentText("Has alcanzado el nivel maximo, nada que ver aqui.").show()

                }
                else {
                    val intent = Intent(this, SubirNivel::class.java);
                    startActivity(intent);
                    finish();
                }
                return true;
            }

            R.id.Nextnivel ->{
                sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                var nivelUser = sharedPreferences.getInt("level_user", 0);
                if (nivelUser>2)
                {
                    SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Espera")
                        .setContentText("Has alcanzado el nivel maximo, nada que ver aqui.").show()

                }
                else
                {

                val intent = Intent(this, SiguienteNivelActivity::class.java);
                startActivity(intent);
                finish();
                }

                return true;
            }


        }

        return super.onOptionsItemSelected(item)
    }
}