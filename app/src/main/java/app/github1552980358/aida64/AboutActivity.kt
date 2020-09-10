package app.github1552980358.aida64

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about.toolbar
import lib.github1552980358.ktExtension.android.util.logE

class AboutActivity: AppCompatActivity() {
    
    companion object {
        
        private const val TAG = "AboutActivity"
        
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        logE(TAG, "Activity: onCreate")
        
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_about)
        
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
        
    }
    
}