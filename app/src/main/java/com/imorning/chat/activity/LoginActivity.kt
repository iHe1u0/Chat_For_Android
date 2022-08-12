package com.imorning.chat.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.imorning.chat.databinding.ActivityLoginBinding
import com.imorning.common.action.LoginAction
import com.imorning.common.constant.Config
import com.imorning.common.constant.StatusCode
import com.imorning.common.utils.SessionManager

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

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
            val account = binding.loginAccountEdit.text.toString().trim()
            val password = binding.loginPasswordEdit.text.toString().trim()
            if (account.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "账号密码不能为空", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    this.finish()
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
        }
    }
}