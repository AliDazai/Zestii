package com.your.zestii
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.rrtfpfobhdj))
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.r193ioapmagt))
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.rjzvipqisrsb))
    }
}