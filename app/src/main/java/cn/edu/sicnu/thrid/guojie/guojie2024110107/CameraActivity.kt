package cn.edu.sicnu.thrid.guojie.guojie2024110107

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import cn.edu.sicnu.thrid.guojie.guojie2024110107.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 子任务3：采用隐式Intent（MediaStore.ACTION_IMAGE_CAPTURE）启动系统相机拍照，
 * 拍照成功后在本Activity的ImageView中显示照片。
 *
 * 要点：
 * 1. 在AndroidManifest中声明CAMERA权限，运行时动态申请；
 * 2. 通过FileProvider向相机应用授予图片文件的临时写权限；
 * 3. 使用 startActivityForResult/onActivityResult 接收拍照结果。
 */
class CameraActivity : Activity() {

    private lateinit var binding: ActivityCameraBinding

    /** 本次拍照生成的图片文件 */
    private var photoFile: File? = null

    companion object {
        private const val REQUEST_CODE_CAMERA_PERMISSION = 100
        private const val REQUEST_CODE_IMAGE_CAPTURE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTakePhoto.setOnClickListener {
            // 已有相机权限则直接拍照，否则先动态申请
            if (checkSelfPermissionCompat()) {
                launchCamera()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_CAMERA_PERMISSION
                )
            }
        }
    }

    /** 兼容方式检查相机权限 */
    private fun checkSelfPermissionCompat(): Boolean =
        checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    /** 创建图片文件并以隐式Intent启动系统相机 */
    private fun launchCamera() {
        val file = createImageFile() ?: return
        photoFile = file

        val photoUri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )

        // 隐式Intent：action为 ACTION_IMAGE_CAPTURE，系统会选择相机应用处理
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        // resolveActivity 确认存在可处理该隐式Intent的相机应用
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, R.string.camera_unavailable, Toast.LENGTH_LONG).show()
        }
    }

    /** 在应用专属外部存储的Pictures目录下创建图片文件 */
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir("Pictures")
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(
            "${getString(R.string.photo_prefix)}${timeStamp}_",
            getString(R.string.photo_suffix),
            storageDir
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoFile?.let { file ->
                binding.ivPhoto.setImageBitmap(decodeSampledBitmap(file))
            }
        }
    }

    /**
     * 对大图进行降采样解码，避免因原始照片过大导致OOM。
     */
    private fun decodeSampledBitmap(file: File) = run {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)

        var sampleSize = 1
        val targetWidth = 1080
        while (options.outWidth / sampleSize > targetWidth) {
            sampleSize *= 2
        }

        BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }.let { decodeOptions ->
            BitmapFactory.decodeFile(file.absolutePath, decodeOptions)
        }
    }
}
