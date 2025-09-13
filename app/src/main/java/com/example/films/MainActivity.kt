package com.example.films

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.films.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Используем layout с NavHostFragment
        setContentView(R.layout.activity_main)
    }
}
