package com.docsapp.loginregisterkotlin.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.docsapp.loginregisterkotlin.Preferences.PreferenceHelper
import com.docsapp.loginregisterkotlin.R

class DashboardActivity : AppCompatActivity() {

    private var tvname: TextView? = null
    private var tvhobby:TextView? = null
    private var btnlogout: Button? = null
    private var preferenceHelper: PreferenceHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        preferenceHelper = PreferenceHelper(this)

        tvhobby = findViewById<View>(R.id.tvhobby) as TextView
        tvname = findViewById<View>(R.id.tvname) as TextView
        btnlogout = findViewById<View>(R.id.btn) as Button

        tvname!!.text = "Welcome " + preferenceHelper!!.getNames()
        tvhobby!!.setText("Your hobby is " + preferenceHelper!!.getHobbys())

        btnlogout!!.setOnClickListener {
            preferenceHelper!!.putIsLogin(false)
            val intent = Intent(this@DashboardActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this@DashboardActivity.finish()
        }
    }
}
