package cn.edu.sicnu.thrid.guojie.guojie2024110107

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import cn.edu.sicnu.thrid.guojie.guojie2024110107.databinding.ActivityReturnResultBinding

/**
 * 子任务2：在EditText中输入内容，点击"确认返回"后通过setResult
 * 将数据回传给MainActivity，实现两个Activity之间的双向通信。
 */
class ReturnResultActivity : Activity() {

    private lateinit var binding: ActivityReturnResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener {
            val input = binding.etInput.text.toString()
            if (input.isBlank()) {
                Toast.makeText(this, R.string.et_input_hint, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val resultIntent = Intent().apply {
                putExtra(getString(R.string.return_data_key), input)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
