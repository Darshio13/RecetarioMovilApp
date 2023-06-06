package com.example.recetariotest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.recetariotest.databinding.ActivityViewRecipeBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray


class ViewRecipe : AppCompatActivity() {
    lateinit var binding: ActivityViewRecipeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityViewRecipeBinding.inflate(layoutInflater);
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide();




        //Query

        val queue = Volley.newRequestQueue(this)
        val IdRecipe = intent.getStringExtra("ID_RECIPE")
        val url = "https://apitest-production-3e6f.up.railway.app/receta/RecetaEspecifica/" + IdRecipe

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
                        .setContentText("Error. No se pudo cargar el contenido").show()


                }
                else {

                    //Parsear
                    //Se obtiene el array de recetas
                    val jarray = JSONArray(response.toString());

                    //Insertar datos


                    //Se obtienen los atributos por el indice de jsonObj
                    val jsonObj = jarray.getJSONObject(0);
                    val nombre = jsonObj.getString("titulo");
                    val ImageArray = jsonObj.getJSONArray("Imagenes")
                    val ImageObject = ImageArray.getJSONObject(0);
                    val urlImg = ImageObject.getString("nombre_archivo")
                    val idRecipe = jsonObj.getInt("id_receta")
                    //Titulo
                    binding.recipeTitle.text=nombre;
                    //Imagen
                    Picasso.get()
                        .load(urlImg)
                        .into(binding.recipeImage);
                    //Categoria
                    val CategoryJson = jsonObj.getJSONObject("Cate");
                    val cateRecipe = CategoryJson.getString("nombre_categoria");
                    binding.recipeCategory.text="Categoria: "+ cateRecipe;
                    //Dificultad
                    val nivel = jsonObj.getString("nivel");
                    when (nivel) {
                        "1" -> binding.recipeDifficulty.text="Dificultad: Sencilla"
                        "2" -> binding.recipeDifficulty.text="Dificultad: Regular"
                        "3" -> binding.recipeDifficulty.text ="Dificultad: Complicada"
                    }

                    //Ingrendientes
                    val ingredientsJsonArray =  jsonObj.getJSONArray("Ingredientes")
                    for (num in 0..ingredientsJsonArray.length()-1 )
                    {
                        val view: View = layoutInflater.inflate(R.layout.ingredients_template, null)
                        //Porciones
                        val numero = view.findViewById<TextView>(R.id.number);
                        numero.text =  ingredientsJsonArray.getJSONObject(num).getInt("porciones").toString()
                        //Nombre
                        val nombre = view.findViewById<TextView>(R.id.ingredient);
                        nombre.text= (num+1).toString() +". " +ingredientsJsonArray.getJSONObject(num).getJSONObject("alimento").getString("nombre");
                        binding.recipeIngredients.addView(view);
                    }
                    //Pasos
                    val PasosJsonArray= jsonObj.getJSONArray("Paso")
                    for (num in 0..PasosJsonArray.length()-1)
                    {
                        val view: View = layoutInflater.inflate(R.layout.ingredients_template, null)
                        //Porciones
                        val numero = view.findViewById<TextView>(R.id.number);
                        numero.text = PasosJsonArray.getJSONObject(num).getInt("tiempo").toString()+ " min"
                        //Nombre
                        val nombre = view.findViewById<TextView>(R.id.ingredient);
                        val tipo = PasosJsonArray.getJSONObject(num).getString("especificacion")

                        val especificacion = when (tipo)
                        {
                            "1" -> "Dinámico"
                            "2" -> "Estático"
                            else -> ""
                        }

                        nombre.text= (num+1).toString() +". " +PasosJsonArray.getJSONObject(num).getString("descripcion") + " ("+especificacion + ").";
                        binding.recipeSteps.addView(view);

                    }

                    //Start Recipe proccess
                    var btnStart = binding.start;
                    btnStart.setOnClickListener{
                        val intent = Intent(this, FollowingSteps::class.java);
                        intent.putExtra("STEPS_RECIPE",PasosJsonArray.toString())

                        startActivity(intent);
                    }






                }
            },
            {
                Toast.makeText(this@ViewRecipe, "No se pudo completar la request", Toast.LENGTH_SHORT)
                    .show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)




    }


}