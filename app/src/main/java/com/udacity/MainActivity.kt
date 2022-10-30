package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

enum class DownloadState (val value: String) {
    SUCCESS("Success"),
    FAILURE("Failure")
}
class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedUrl: String? = null
    private var selectedUrlName: String? = null
    lateinit var customButton: LoadingButton

    private lateinit var manager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        customButton = findViewById(R.id.custom_button)
        customButton.setCustomButtonState(ButtonState.Completed)
        customButton.setOnClickListener {
            download()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId:String, channelName:String) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Download complete"
            }
        manager.createNotificationChannel(notificationChannel)

    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent?.action
            if(downloadID==id){
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                    val query = Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0))
                   val manager=context!!.getSystemService(Context.DOWNLOAD_SERVICE)as DownloadManager
                    val cursor:Cursor=manager.query(query)
                    if(cursor.moveToFirst()){
                        if(cursor.count>0){
                            val status=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL){
                                customButton.setCustomButtonState(ButtonState.Completed)
                                this@MainActivity.manager.sendNotification(selectedUrlName.toString(), applicationContext, DownloadState.SUCCESS.value)
                            }
                            else{
                                customButton.setCustomButtonState(ButtonState.Completed)
                                this@MainActivity.manager.sendNotification(selectedUrlName.toString(), applicationContext, DownloadState.FAILURE.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun download() {
        customButton.setCustomButtonState(ButtonState.Clicked)

        if(selectedUrl != null){
            customButton.setCustomButtonState(ButtonState.Loading)
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createChannel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_name))

            val file = File(getExternalFilesDir(null),"/repos")
            if(!file.exists()){
                file.mkdirs()
            }
            val request =
                DownloadManager.Request(Uri.parse(selectedUrl))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/repos/$selectedUrlName.zip")

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        }else{
            customButton.setCustomButtonState(ButtonState.Completed)
            toast(getString(R.string.no_url_selected_text))
        }

        }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            if (view.isChecked) {
                when (view.getId()) {
                    R.id.glide ->
                            setUrlAndName(
                                getString(R.string.glide_url),
                                getString(R.string.glide)
                            )
                    R.id.load_app ->
                            setUrlAndName(
                                getString(R.string.load_app_url),
                                getString(R.string.load_app)
                            )
                    R.id.retrofit ->
                            setUrlAndName(
                                getString(R.string.retrofit_url),
                                getString(R.string.retrofit)
                            )
                }
            }
        }
    }

    private fun setUrlAndName(url: String, name: String){
        selectedUrl = url
        selectedUrlName = name
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}
