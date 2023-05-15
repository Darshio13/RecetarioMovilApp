package com.example.recetariotest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
 import com.example.recetariotest.databinding.ActivitySkillsBinding
 import android.webkit.WebView
import android.webkit.WebViewClient


 import android.content.Context
import android.content.SharedPreferences
 import android.view.View

import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray


class SkillsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySkillsBinding;
    lateinit var sharedPreferences: SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkillsBinding.inflate(layoutInflater);
        setContentView(binding.root)

        //val webView = binding.ytPlayer


        //Query
        sharedPreferences = getSharedPreferences("prefs", Context.MODE_PRIVATE)

        var iduser = sharedPreferences.getString("id_user", null)!!

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://apitest-production-6abd.up.railway.app/comodin/GetSkillsforComodin/"+ iduser

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                if (response.length >10)
                {
                    //Se obtiene el array de recetas
                    val jarray = JSONArray(response.toString());
                    for (nums in 0..jarray.length()-1) {
                        val jsonObj = jarray.getJSONObject(nums).getJSONObject("skills");
                        var url = jsonObj.getString("url")

                        val view: View = layoutInflater.inflate(com.example.recetariotest.R.layout.video_template, null)
                        var webView = view.findViewById<WebView>(R.id.ytPlayer)


                        webView.settings.setJavaScriptEnabled(true)

                        val frameVideo =
                            "<html><body><iframe width=\"340\" height=\"180\" src=\"${url}\" frameborder=\"0\" allowfullscreen></iframe></body></html>"

                        val displayYoutubeVideo = webView
                        displayYoutubeVideo.webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                                return false
                            }
                        }
                        val webSettings = displayYoutubeVideo.settings
                        webSettings.javaScriptEnabled = true
                        displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8")



                        binding.container.addView(view)



                    }





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