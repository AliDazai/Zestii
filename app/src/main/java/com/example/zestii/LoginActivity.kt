package com.your.zestii
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
class LoginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.r0g4dak5s65zb))
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.r6tvteotf9m7))
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.r2tv58b8iif5))
        Glide.with(this).load("https://i.imgur.com/1tMFzp8.png").into(findViewById(R.id.r2lxxj3vpjj8))
    }
}