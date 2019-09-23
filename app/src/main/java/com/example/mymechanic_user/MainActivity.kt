package com.example.mymechanic_user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var locationBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationBtn = findViewById(R.id.setLocationBtn)

        locationBtn.setOnClickListener{
            setLocation()
        }
    }

    private fun setLocation(){
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
