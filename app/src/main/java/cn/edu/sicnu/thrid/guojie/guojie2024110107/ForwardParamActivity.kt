package cn.edu.sicnu.thrid.guojie.guojie2024110107

import android.app.Activity
import android.os.Bundle
import cn.edu.sicnu.thrid.guojie.guojie2024110107.databinding.ActivityForwardParamBinding

/**
 * 子任务1：通过显式Intent接收MainActivity正向传递的参数并显示。
 */
class ForwardParamActivity : Activity() {

    private lateinit var binding: ActivityForwardParamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForwardParamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 通过与发送端一致的字符串资源key取出参数
        val param = intent.getStringExtra(getString(R.string.forward_param_key))
            ?: getString(R.string.tv_result_default)
        binding.tvReceivedParam.text = param
    }
}
