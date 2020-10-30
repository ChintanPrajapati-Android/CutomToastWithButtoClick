package com.cp.customtoast

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var mToast: ActivityToast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = LayoutInflater.from(this).inflate(R.layout.lsy_toast, null)
        mToast = ActivityToast(this, view)
        mToast?.setGravity(Gravity.TOP)
        mToast?.setLength(5000)
        mToast?.view?.findViewById<Button>(R.id.btnSubmit)?.setOnClickListener {
            mToast?.cancel()
        }


        findViewById<TextView>(R.id.tvClick).setOnClickListener {
            mToast?.show()
        }
    }
}