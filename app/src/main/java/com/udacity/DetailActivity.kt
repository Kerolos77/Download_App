package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        btn_ok.setOnClickListener() {
           startActivity(Intent(this, MainActivity::class.java))
        }

        file_name.text = intent.getStringExtra("fileName").toString()
        status.text = intent.getStringExtra("status").toString()

        if (status.text == DownloadState.SUCCESS.value) {
            status.setTextColor(Color.GREEN)
        } else {
            status.setTextColor(Color.RED)
        }

    }
}
