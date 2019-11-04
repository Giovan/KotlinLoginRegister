package com.docsapp.loginregisterkotlin.Activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.docsapp.loginregisterkotlin.Preferences.PreferenceHelper
import com.docsapp.loginregisterkotlin.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    internal var LoginURL = "https://demonuts.com/Demonuts/JsonTest/Tennis/simplelogin.php"
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var tvReg: TextView? = null
    private val LoginTask = 1
    private var preferenceHelper: PreferenceHelper? = null
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferenceHelper = PreferenceHelper(this)

        etUsername = findViewById<View>(R.id.etusername) as EditText
        etPassword = findViewById<View>(R.id.etpassword) as EditText

        btnLogin = findViewById<View>(R.id.btn) as Button
        tvReg = findViewById<View>(R.id.tvreg) as TextView

        btnLogin!!.setOnClickListener {
            try {
                login()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun login() {

        showSimpleProgressDialog(this@LoginActivity, null, "Loading...", false)

        try {
            Fuel.post(LoginURL, listOf(
                "" to etUsername!!.text.toString()
                , "password" to etPassword!!.text.toString()
            )).responseJson { request, response, result ->
                Log.d("plzzzz", result.get().content)
                onTaskCompleted(result.get().content, LoginTask)
            }
        } catch (e: Exception) {

        } finally {

        }
    }

    private fun onTaskCompleted(response: String, task: Int) {
        Log.d("responsejson", response)
        removeSimpleProgressDialog() // Will remove progress dialog
        when (task) {
            LoginTask -> if (isSuccess(response)) {
                saveInfo(response)
                Toast.makeText(this@LoginActivity, "Login Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            } else {
                Toast.makeText(this@LoginActivity, getErrorMessage(response), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveInfo(response: String) {
        preferenceHelper!!.putIsLogin(true)
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.getString("status") == "true") {
                val dataArray = jsonObject.getJSONArray("data")
                for (i in 0 until dataArray.length()) {

                    val dataobj = dataArray.getJSONObject(i)
                    preferenceHelper!!.putName(dataobj.getString("name"))
                    preferenceHelper!!.putName(dataobj.getString("hobby"))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun isSuccess(response: String): Boolean {
        try {
            val jsonObject = JSONObject(response)
            return if (jsonObject.optString("status") == "true") {
                true
            } else {
                false
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return false
    }

    fun getErrorMessage(response: String): String {
        try {
            val jsonObject = JSONObject(response)
            return jsonObject.getString("message")
        } catch (e: JSONException) {
          e.printStackTrace()
        }

        return "No data"
    }

    fun showSimpleProgressDialog(context: Context, title: String?, msg: String, isCancelable: Boolean) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg)
                mProgressDialog!!.setCancelable(isCancelable)
            }
            if (!mProgressDialog!!.isShowing) {
                mProgressDialog!!.show()
            }

        } catch (ie: IllegalArgumentException) {
            ie.printStackTrace()
        } catch (re: RuntimeException) {
            re.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog!!.isShowing) {
                    mProgressDialog!!.dismiss()
                    mProgressDialog = null
                }
            }
        } catch (ie: IllegalArgumentException) {
            ie.printStackTrace()

        } catch (re: RuntimeException) {
            re.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
