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




    }



    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        finish();
    }
}