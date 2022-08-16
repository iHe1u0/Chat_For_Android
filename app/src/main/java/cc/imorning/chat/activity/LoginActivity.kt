package cc.imorning.chat.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import cc.imorning.chat.App
import cc.imorning.chat.databinding.ActivityLoginBinding
import cc.imorning.common.action.LoginAction
import cc.imorning.common.constant.Config
import cc.imorning.common.constant.StatusCode
import cc.imorning.common.manager.ConnectionManager
import cc.imorning.common.utils.AvatarUtils
import cc.imorning.common.utils.SessionManager
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jivesoftware.smackx.vcardtemp.VCardManager

private const val TAG = "LoginActivity"

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(Config.LOGIN_INFO)
        if (sessionManager.fetchAccount() != null && sessionManager.fetchAuthToken() != null) {
            binding.loginAccountEdit.setText(sessionManager.fetchAccount())
            binding.loginPasswordEdit.setText(sessionManager.fetchAuthToken())
        }

        binding.loginGoButton.setOnClickListener {
            binding.loginProgress.visibility = View.VISIBLE
            val account = binding.loginAccountEdit.text.toString().trim()
            val password = binding.loginPasswordEdit.text.toString().trim()
            if (account.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "账号密码不能为空", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            MainScope().launch(Dispatchers.IO) {
                val result = LoginAction.run(
                    account = account,
                    password = password
                )
                when (result) {
                    StatusCode.OK -> {
                        if (binding.loginRememberToken.isChecked) {
                            sessionManager.saveAccount(account)
                            sessionManager.saveAuthToken(password)
                        }
                        if (ConnectionManager.isConnectionAuthenticated(App.getTCPConnection())) {
                            val selfVCard =
                                VCardManager.getInstanceFor(App.getTCPConnection()).loadVCard()
                            App.vCard = selfVCard
                            AvatarUtils.instance.cacheAvatar(App.getTCPConnection().user.asEntityBareJidString())
                            Logger.xml(selfVCard.toXML().toString())
                        }
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                        startActivity(intent)
                        this@LoginActivity.finish()
                    }
                    StatusCode.LOGIN_AUTH_FAILED -> {
                        Snackbar.make(binding.root, "登陆失败: 账号或密码错误", Snackbar.LENGTH_SHORT).show()
                    }
                    StatusCode.NETWORK_ERROR -> {
                        Snackbar.make(binding.root, "登陆失败: 网络无连接", Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {
                        Snackbar.make(binding.root, "登陆失败: $result", Snackbar.LENGTH_SHORT).show()
                    }
                }
                withContext(Dispatchers.Main) {
                    binding.loginProgress.visibility = View.GONE
                }
            }
        }
    }
}