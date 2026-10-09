package cn.edu.sicnu.thrid.guojie.guojie2024110107

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import cn.edu.sicnu.thrid.guojie.guojie2024110107.databinding.ActivityMainBinding

/**
 * 作业三 多Activity —— 主界面（1个MainActivity + 4个二级Activity）。
 *
 * 任务1：显式Intent正向传参，启动 ForwardParamActivity。
 * 任务2：通过 startActivityForResult 启动 ReturnResultActivity，
 *        并在 onActivityResult 中接收其返回结果，实现双向通信。
 * 任务3：启动 CameraActivity，由该Activity以隐式Intent调用系统相机。
 * 任务4：启动 StopWatchActivity，实现可在横屏/竖屏下连续计时的秒表。
 *
 * 全部界面文字均通过字符串资源引用，未在代码中硬编码显示字符串。
 */
class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        /** 任务2返回数据的请求码 */
        private const val REQUEST_CODE_RETURN_RESULT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 任务1：显式Intent正向传参
        binding.btnTask1.setOnClickListener {
            val intent = Intent(this, ForwardParamActivity::class.java).apply {
                putExtra(
                    getString(R.string.forward_param_key),
                    getString(R.string.forward_param_value)
                )
            }
            startActivity(intent)
        }

        // 任务2：启动子Activity并等待返回结果
        binding.btnTask2.setOnClickListener {
            val intent = Intent(this, ReturnResultActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_RETURN_RESULT)
        }

        // 任务3：进入相机拍照子Activity
        binding.btnTask3.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }

        // 任务4：进入秒表子Activity
        binding.btnTask4.setOnClickListener {
            startActivity(Intent(this, StopWatchActivity::class.java))
        }

        Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show()
    }

    /**
     * 任务2：接收 ReturnResultActivity 回传的输入内容并显示。
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RETURN_RESULT && resultCode == RESULT_OK) {
            val result = data?.getStringExtra(getString(R.string.return_data_key))
            binding.tvResult.text = result ?: getString(R.string.tv_result_default)
        }
    }
}
