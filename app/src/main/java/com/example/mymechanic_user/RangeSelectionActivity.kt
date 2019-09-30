package com.example.mymechanic_user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng

class RangeSelectionActivity : AppCompatActivity() {

    private lateinit var seekbar: SeekBar
    private lateinit var srcDistanceBar: TextView
    private lateinit var search: Button
    internal var srcDist = 0
    var currentLatitude: Double = 0.0
    var currentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_range_selection)

        currentLatitude = intent.getDoubleExtra("CurrentLatitude", 0.0)
        currentLongitude = intent.getDoubleExtra("CurrentLongitude", 0.0)

        seekbar = findViewById(R.id.seekBar)
        srcDistanceBar = findViewById(R.id.displayDistance)

        seekBar()

        search = findViewById(R.id.searchBtn)
        search.setOnClickListener { openMechanicList() }
    }

    private fun openMechanicList() {
        val intent = Intent(this, ListMechanicActivity::class.java)
        intent.putExtra("CurrentLatitude", currentLatitude)
        intent.putExtra("CurrentLongitude", currentLongitude)
        intent.putExtra("SearchDistance", srcDist)
        startActivity(intent)
    }

    fun seekBar() {

        srcDistanceBar.text = "Range Covered : " + seekbar!!.progress + "/" + 10 + " km"

        seekbar.setOnSeekBarChangeListener(

            object : SeekBar.OnSeekBarChangeListener {

                var progress_value: Int = 1

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    progress_value = progress
                    srcDist = progress_value / 10 + 1
                    srcDistanceBar.text = "Range Covered : " + ((progress_value / 11) + 1) + "/" + 10 + " km"

                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    //Toast.makeText(ListMech.this, "Select your Range", Toast.LENGTH_LONG).show();

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                    srcDistanceBar.text = "Range Covered : " + ((progress_value / 11) + 1)  + "/" + 10 + " km"
                    //Toast.makeText(ListMech.this, "Select your Range", Toast.LENGTH_LONG).show();

                }

            })

    }

}
