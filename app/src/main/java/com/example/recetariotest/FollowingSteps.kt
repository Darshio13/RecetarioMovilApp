package com.example.recetariotest

 import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
 import com.example.recetariotest.databinding.ActivityFollowingStepsBinding
import android.speech.tts.TextToSpeech

import org.json.JSONArray
import java.util.Locale;

import android.os.CountDownTimer

import java.util.concurrent.TimeUnit

import android.os.SystemClock
import android.widget.Chronometer
import android.widget.Chronometer.OnChronometerTickListener
 import android.widget.Toast
 import cn.pedant.SweetAlert.SweetAlertDialog


class FollowingSteps : AppCompatActivity() {
    lateinit var binding : ActivityFollowingStepsBinding;
    var timePerStep: Long = 0;
    var textToSpeech: TextToSpeech? = null
    var timerTTS: CountDownTimer? = null
    var playing = false;
    lateinit var clockSound: MediaPlayer;
    lateinit var finishedTimeSound : MediaPlayer;
    lateinit var stepChronometer: Chronometer;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowingStepsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var speaking= false;
        supportActionBar?.hide();


        //Obtener Json de pasos
        val StepsString = intent.getStringExtra("STEPS_RECIPE");
        val JsonStepsArray = JSONArray(StepsString);
        //Administrar proceso de pasos
         var pasoActual = 1;
        //Get step data
        binding.stepTurn.text = JsonStepsArray.getJSONObject(0).getString("descripcion") + " (" + JsonStepsArray.getJSONObject(0).getInt("tiempo") +" min)."
         var especificacion = when (JsonStepsArray.getJSONObject(pasoActual-1).getString("especificacion"))
        {
            "1" -> "Dinámico"
            "2" -> "Estático"
            else -> ""
        }
        binding.stepNumber.text = "Paso "+ pasoActual +" (" + especificacion+ ")."
        //Arreglo de tiempos
        val userTimes = LongArray(JsonStepsArray.length()) //equivalent in Java: new int[size]


        // Timer for TTS
        fun TimerTTS () {
            val timerTTS = object : CountDownTimer(20000, 10) {
                override fun onTick(millisUntilFinished: Long) {
                    if (!textToSpeech!!.isSpeaking())
                    {
                        if (!JsonStepsArray.getJSONObject(pasoActual-1).getString("especificacion").equals("2") && !speaking &&playing)
                        {
                            binding.nexStep.isEnabled = true;

                        }
                        if (speaking)
                        {
                            binding.btnText.text= "Narrar Paso";
                            speaking = false;
                        }

                         timerTTS?.cancel()

                    }
                }
                override fun onFinish() {
                    if (textToSpeech!!.isSpeaking())
                    {
                        TimerTTS();
                    }
                }
            }
            timerTTS.start()
        }

        //Text to speech

        // Definir Text to speech
        textToSpeech = TextToSpeech(applicationContext) { i ->
            // if No error is found then only it will run
            if (i != TextToSpeech.ERROR) {
                // To Choose language of speech
                val locSpanish = Locale("spa", "MEX")
                textToSpeech?.setLanguage(locSpanish);
            }
        }
        //Funcion para reproducir Texto
        fun SpeakStep(step: String)
        {
            textToSpeech!!.speak(step, TextToSpeech.QUEUE_FLUSH, null)
        }

        //Reproducir texto o detenerlo
        binding.btnText.setOnClickListener{
               if (playing == false)
               {
                   binding.btnText.text="Narrar paso";
                   setTimerStep(JsonStepsArray.getJSONObject(pasoActual - 1).getInt("tiempo"))
                   playing = true;
                   if (!JsonStepsArray.getJSONObject(pasoActual-1).getString("especificacion").equals("2"))
                   {
                       TimerTTS();
                   }
               }
            if (!speaking)
            {
                speaking = true;
                TimerTTS();
                binding.btnText.text="Detener narracion";
                SpeakStep(JsonStepsArray.getJSONObject(pasoActual - 1).getString("descripcion"));


            }
            else
            {
                textToSpeech!!.stop();
            }

        }

        //Ir al siguiente paso
        binding.nexStep.setOnClickListener {
            if (JsonStepsArray.getJSONObject(pasoActual-1).getString("especificacion").equals("2") && playing)
            {
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Espera.")
                    .setContentText("El paso actual es de tipo estatico. No se proseguira al siguiente paso hasta que el tiempo se acabe.").show()
            }
            else {

                if (JsonStepsArray.length() == pasoActual)
                {
                    //Se terminan los pasos
                    userTimes[pasoActual-1] = timePerStep;

                    val intent = Intent(baseContext, RetroalimentacionActivity::class.java)
                    intent.putExtra("STEPS_RECIPE", JsonStepsArray.toString())
                    intent.putExtra("string-array", userTimes);
                    startActivity(intent)
                    finish();

                }
                else {
                    //Se registra el tiempo
                    userTimes[pasoActual-1] = timePerStep;

                    //Se va al siguiente paso
                    pasoActual = pasoActual + 1;
                    //Set time lmao
                    val t = JsonStepsArray.getJSONObject(pasoActual - 1).getInt("tiempo")
                    timerTTS?.cancel();
                    //Restart button
                    binding.btnText.text = "Comenzar paso";
                    playing = false;

                    //Stop tts
                    textToSpeech!!.stop();
                    //Update step data
                    binding.stepTurn.text = JsonStepsArray.getJSONObject(pasoActual - 1)
                        .getString("descripcion") + " (" + JsonStepsArray.getJSONObject(pasoActual - 1)
                        .getInt("tiempo") + " min)."
                    especificacion = when (JsonStepsArray.getJSONObject(pasoActual - 1)
                        .getString("especificacion")) {
                        "1" -> "Dinámico"
                        "2" -> "Estático"
                        else -> ""
                    }
                    binding.stepNumber.text = "Paso " + pasoActual + " (" + especificacion + ")."

                    //Stop Sounds
                    finishedTimeSound?.stop()
                    clockSound?.stop()

                    //Reset Chronometer
                    stepChronometer.setBase(SystemClock.elapsedRealtime());
                    stepChronometer.stop()
                    //Disable button
                    binding.nexStep.isEnabled = false;
                }


            }






        }





    }




    fun setTimerStep(minuteStep: Int) {
         //Timer
        finishedTimeSound = MediaPlayer.create(this, R.raw.over);
        stepChronometer = binding.StepChronometer
        stepChronometer.setBase(SystemClock.elapsedRealtime());
        stepChronometer.start();
        clockSound = MediaPlayer.create(this, R.raw.tick);

        stepChronometer.setOnChronometerTickListener(OnChronometerTickListener {
            // do something when chronometer changes
            clockSound?.start();
            val elapsedMillis: Long = SystemClock.elapsedRealtime() - stepChronometer.getBase()
            timePerStep =  elapsedMillis
             if (TimeUnit.MINUTES.toMillis(minuteStep.toLong()) < elapsedMillis)
            {
                binding.nexStep.isEnabled= true;
                playing=false;
            }

        })

    }

    override fun onBackPressed() {
        //this is only needed if you have specific things

        /* your specific things...*/

        SweetAlertDialog(this@FollowingSteps, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("¿Deseas Regresar?")
            .setContentText("Tu progreso no se podrá recuperar.")
            .setConfirmText("Sí.")
            .setConfirmClickListener { sDialog -> sDialog.dismissWithAnimation()
                //timer?.cancel()
                finish();
            }
            .setCancelButton(
                "Cancel"
            ) { sDialog -> sDialog.dismissWithAnimation()
            }
            .show()
    }



}