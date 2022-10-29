package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private var fileName = ""
    private var statusLoad = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        ok_button.setOnClickListener() {
            returnToMainActivity()
        }
        fileName = intent.getStringExtra("fileName").toString()
        statusLoad = intent.getStringExtra("status").toString()
        file_name.text = fileName
        status.text = statusLoad
        if (statusLoad == DownloadState.SUCCESS.value) {
            status.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
        } else {
            status.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
        }

    }
    private fun returnToMainActivity() {
        val  mainActivity = Intent(this, MainActivity::class.java)
        startActivity(mainActivity)
    }

}
