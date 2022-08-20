package cc.imorning.chat.activity


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import cc.imorning.chat.R
import cc.imorning.chat.databinding.ActivityMainBinding
import cc.imorning.chat.service.NetworkService
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var networkService: NetworkService
    private var isNetworkServiceRunning = false
    private val networkServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as NetworkService.NetworkServiceBinder
            networkService = binder.getService()
            isNetworkServiceRunning = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isNetworkServiceRunning = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        // val appBarConfiguration = AppBarConfiguration(
        //     setOf(
        //         R.id.navigation_message, R.id.navigation_contact, R.id.navigation_profile
        //     )
        // )
        navView.setupWithNavController(navController)

        // Bind Network Service
        Intent(this, NetworkService::class.java).also { intent: Intent ->
            bindService(intent, networkServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDestroy() {
        if (isNetworkServiceRunning) {
            unbindService(networkServiceConnection)
            isNetworkServiceRunning = false
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}