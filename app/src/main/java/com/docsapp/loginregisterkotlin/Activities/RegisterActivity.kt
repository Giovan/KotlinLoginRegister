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
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.IllegalArgumentException

class RegisterActivity : AppCompatActivity() {

    internal var RegisterURL = "https://demonuts.com/Demonuts/JsonTest/Tennis/simpleregister.php"
    private var etname: EditText? = null
    private var ethobby: EditText? = null
    private var etusername:EditText? = null
    private var etpassword:EditText? = null
    private var btnregister: Button? = null
    private var tvlogin: TextView? = null
    private var preferenceHelper: PreferenceHelper? = null
    private val RegTask = 1
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        preferenceHelper = PreferenceHelper(this)

        if (preferenceHelper!!.getIsLogin()) {
            val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            this.finish()
        }

        FuelManager.instance.basePath = "https://demonuts.com";

        etname = findViewById<View>(R.id.etname) as EditText
        ethobby = findViewById<View>(R.id.ethobby) as EditText
        etusername = findViewById<View>(R.id.etusername) as EditText
        etpassword = findViewById<View>(R.id.etpassword) as EditText
        btnregister = findViewById<View>(R.id.btn) as Button
        tvlogin = findViewById<View>(R.id.tvlogin) as TextView

        tvlogin!!.setOnClickListener {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
        }

        btnregister!!.setOnClickListener {
            try {
                register()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class, JSONException::class)
    private fun register() {

        showSimpleProgressDialog(this@RegisterActivity, null, "Loading...", false)

        try {
          /* Fuel.post(RegisterURL, listOf("name" to etname!!.text.toString()
                                        , "hobby" to ethobby!!.text.toString()
                                        , "username" to etusername!!.text.toString()
                                        , "password" to etpassword!!.text.toString()
            )).responseJson { request, response, result ->
              Log.d("plzzzzzz request", request.toString())
              Log.d("plzzzzzz response", response.toString())
              Log.d("plzzzzzz", result.get().content)
              onTaskCompleted(result.get().content, RegTask)
          } */
            val formData = listOf("name" to etname!!.text.toString() , "hobby" to ethobby!!.text.toString(),
                "username" to etusername!!.text.toString(), "password" to etpassword!!.text.toString())

            Fuel.post("/Demonuts/JsonTest/Tennis/simpleregister.php", formData)
                .responseJson { request, response, result ->
                    Log.d("plzzzzzz", result.get().content)
                    onTaskCompleted(result.toString(), RegTask)
                }

            /* Fuel.upload("/post", parameters = formData)
                //Upload normally requires a file, but we can give it an empty list of `DataPart`
                .dataParts { request, url -> listOf<DataPart>() }
                .responseString { request, response, result ->
                    Log.d("plzzzzzz", result.toString())
                    onTaskCompleted(result.toString(), RegTask)
                }
            */
        } catch (e: Exception) {
            Log.d("zzzzzz it dies", e.toString())
        } finally {
            Log.d("zzzzzz it dies", "finally")
        }
    }


    private fun onTaskCompleted(response: String, task: Int) {
        Log.d("responesjson", response)
        removeSimpleProgressDialog()
        when (task) {
            RegTask -> if (isSuccess(response)) {
                saveInfo(response)
                Toast.makeText(this@RegisterActivity, "Registered Successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                this.finish()
            } else {
                Toast.makeText(this@RegisterActivity, getErrorMessage(response), Toast.LENGTH_SHORT).show()
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
                    preferenceHelper!!.putHobby(dataobj.getString("hobby"))
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
        } catch(ie:IllegalArgumentException) {
            ie.printStackTrace()
        } catch(re: RuntimeException) {
            re.printStackTrace()
        } catch(e: Exception) {
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
        } catch(ie: IllegalArgumentException) {
            ie.printStackTrace()
        } catch(re: RuntimeException) {
            re.printStackTrace()
        } catch(e: Exception) {
            e.printStackTrace()
        }
    }
}