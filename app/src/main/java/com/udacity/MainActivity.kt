package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.Query
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

enum class DownloadState (val value: String) {
    SUCCESS("Success"),
    FAILURE("Failure")
}
class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var selectedGitHubRepository: String? = null
    private var selectedGitHubFileName: String? = null
    lateinit var loadingButton: LoadingButton

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    companion object {
        private const val CHANNEL_ID = "channelId"

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton = findViewById(R.id.custom_button)
        loadingButton.setLoadButtonState(ButtonState.Completed)
        loadingButton.setOnClickListener {
            download()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId:String, channelName:String) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Download complete"
        notificationManager.createNotificationChannel(notificationChannel)

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
                                loadingButton.setLoadButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(selectedGitHubFileName.toString(), applicationContext, DownloadState.SUCCESS.value)
                            }
                            else{
                                loadingButton.setLoadButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(selectedGitHubFileName.toString(), applicationContext, DownloadState.FAILURE.value)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun download() {
        loadingButton.setLoadButtonState(ButtonState.Clicked)

        if(selectedGitHubRepository != null){
            loadingButton.setLoadButtonState(ButtonState.Loading)
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            createChannel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_name))

            val file = File(getExternalFilesDir(null),"/repos")

            if(!file.exists()){
                file.mkdirs()
            }
            val request =
                DownloadManager.Request(Uri.parse(selectedGitHubRepository))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/repos/$selectedGitHubFileName.zip")

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadID = downloadManager.enqueue(request)
        }else{
            loadingButton.setLoadButtonState(ButtonState.Completed)
            showToast(getString(R.string.noRepotSelectedText))
        }

        }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.glide ->
                    if (checked) {
                        selectedGitHubRepository = getString(R.string.glideGithubURL)
                        selectedGitHubFileName = getString(R.string.glide)
                    }
                R.id.load_app ->
                    if (checked) {
                        selectedGitHubRepository = getString(R.string.loadAppGithubURL)
                        selectedGitHubFileName = getString(R.string.load_app)
                    }
                R.id.retrofit ->
                    if (checked) {
                        selectedGitHubRepository = getString(R.string.retrofitGithubURL)
                        selectedGitHubFileName = getString(R.string.retrofit)
                    }
            }
        }
    }

    private fun showToast(text: String) {
        val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
    }

}
