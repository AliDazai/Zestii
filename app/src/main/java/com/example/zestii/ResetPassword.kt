import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val resetButton = findViewById<Button>(R.id.resetButton)

        resetButton.setOnClickListener {
            // Implement reset password logic here
        }
    }
}
