package cn.edu.sicnu.thrid.guojie.guojie2024110107

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import cn.edu.sicnu.thrid.guojie.guojie2024110107.databinding.ActivityStopwatchBinding
import java.util.Locale

/**
 * 子任务4：StopWatch 秒表。
 *
 * 关键要求：横屏/竖屏切换时计时器仍能连续计时。
 * 实现方式：在 onSaveInstanceState 中保存"已计时长 + 是否正在运行"，
 * 屏幕旋转导致Activity重建后，在 onCreate 中恢复状态；
 * 若旋转前正在运行，则以 SystemClock.elapsedRealtime() 为基准重新锚定起点，
 * 从而保证计时连续不中断。
 */
class StopWatchActivity : Activity() {

    private lateinit var binding: ActivityStopwatchBinding

    private val handler = Handler(Looper.getMainLooper())

    /** 已累计的计时毫秒数（停止时冻结的值） */
    private var elapsedMillis = 0L

    /** 是否正在计时 */
    private var running = false

    /**
     * 最近一次开始计时时的锚点：
     * startBase = SystemClock.elapsedRealtime() - elapsedMillis
     */
    private var startBase = 0L

    /** 每100毫秒刷新一次显示（精确到0.1秒） */
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (running) {
                updateDisplay(currentElapsed())
                handler.postDelayed(this, UPDATE_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStopwatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 屏幕旋转重建后恢复之前保存的计时状态
        if (savedInstanceState != null) {
            elapsedMillis = savedInstanceState.getLong(KEY_ELAPSED, 0L)
            running = savedInstanceState.getBoolean(KEY_RUNNING, false)
            if (running) {
                // 重新锚定起点，使旋转过程中流逝的时间也被计入
                startBase = SystemClock.elapsedRealtime() - elapsedMillis
                handler.post(updateRunnable)
            } else {
                updateDisplay(elapsedMillis)
            }
        } else {
            updateDisplay(0L)
        }

        // 开始（暂停后继续）
        binding.btnStart.setOnClickListener {
            if (!running) {
                startBase = SystemClock.elapsedRealtime() - elapsedMillis
                running = true
                handler.post(updateRunnable)
            }
        }

        // 停止：冻结当前已计时长
        binding.btnStop.setOnClickListener {
            if (running) {
                elapsedMillis = currentElapsed()
                running = false
                handler.removeCallbacks(updateRunnable)
                updateDisplay(elapsedMillis)
            }
        }

        // 重置
        binding.btnReset.setOnClickListener {
            running = false
            elapsedMillis = 0L
            handler.removeCallbacks(updateRunnable)
            updateDisplay(0L)
        }
    }

    /** 当前实时已计时长 */
    private fun currentElapsed(): Long =
        if (running) SystemClock.elapsedRealtime() - startBase else elapsedMillis

    /** 将毫秒数格式化为 mm:ss.t 并显示 */
    private fun updateDisplay(millis: Long) {
        val totalTenths = millis / 100L
        val minutes = totalTenths / 600L
        val seconds = (totalTenths % 600L) / 10L
        val tenths = totalTenths % 10L
        binding.tvTime.text = String.format(
            Locale.getDefault(),
            FORMAT_TIME,
            minutes,
            seconds,
            tenths
        )
    }

    /**
     * 屏幕旋转等配置变更前保存计时状态，
     * 重建后即可在 onCreate 中恢复，实现连续计时。
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 若正在运行，先把当前实时已计时长保存下来
        if (running) {
            outState.putLong(KEY_ELAPSED, currentElapsed())
        } else {
            outState.putLong(KEY_ELAPSED, elapsedMillis)
        }
        outState.putBoolean(KEY_RUNNING, running)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }

    companion object {
        private const val UPDATE_INTERVAL_MS = 100L
        private const val KEY_ELAPSED = "key_elapsed"
        private const val KEY_RUNNING = "key_running"
        private const val FORMAT_TIME = "%02d:%02d.%d"
    }
}
