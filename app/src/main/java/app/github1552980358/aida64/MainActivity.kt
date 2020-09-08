package app.github1552980358.aida64

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import app.github1552980358.aida64.RemoteActivity.Companion.INTENT_SETTINGS
import kotlinx.android.synthetic.main.activity_main.floatingActionButton
import kotlinx.android.synthetic.main.activity_main.toolbar
import lib.github1552980358.ktExtension.android.content.toast
import lib.github1552980358.ktExtension.android.util.logE

class MainActivity: AppCompatActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        logE(TAG, "Activity: onCreate")
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        setSupportActionBar(toolbar)
        
        val fragment = MainPreference()
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayout, fragment, "MainPreference")
            .show(fragment)
            .commit()
        
        floatingActionButton.setOnClickListener {
            startActivity(Intent(this, RemoteActivity::class.java).putExtra(INTENT_SETTINGS, fragment.getSettings()))
        }
        
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        logE(TAG, "Activity: onCreateOptionsMenu")
        
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        logE(TAG, "Activity: onOptionsItemSelected")
        
        when (item.itemId) {
    
            R.id.menu_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
            
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        logE(TAG, "Activity: onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        toast(
            if (grantResults.first() == PackageManager.PERMISSION_GRANTED) R.string.mainActivity_permission_success
            else R.string.mainActivity_permission_fail
        )
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logE(TAG, "Activity: onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        
        toast(
            if (android.provider.Settings.System.canWrite(this)) R.string.mainActivity_permission_success
            else R.string.mainActivity_permission_fail
        )
    }
    
}