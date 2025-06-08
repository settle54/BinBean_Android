package com.binbean.binbean_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binbean.login.LoginActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(
            this,
            LoginActivity::class.java
        )
        startActivity(intent)
    }
}