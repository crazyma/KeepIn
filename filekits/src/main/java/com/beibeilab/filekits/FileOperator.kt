package com.beibeilab.filekits

import android.graphics.Bitmap
import java.net.URL
import okhttp3.Response
import okio.Okio
import java.io.*


class FileOperator {


    //  from Dcard: ImageUtils.saveToDcardFile
    fun saveBitmap(file: File, bitmap: Bitmap, quality: Int = 90) {

        val q =
            if (quality < 0 || quality > 100) 90
            else quality
        FileOutputStream(file.path).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
        }
    }

    // from Dcard: VideoDownloadWorker.saveVideoFile
    private fun saveVideoFile(file: File, urlString: String) {
        try {
            val inputStream = URL(urlString).run {
                openStream()
            }

            var read: Int

            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(256)
            inputStream.use { input ->
                outputStream.use {
                    while (input.read(buffer).also { read = it } != -1) {
                        it.write(buffer, 0, read)
                    }
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * Recommend!
     * This method is a little faster than FileManager#writeOKHttpResponseViaOutputStream
     *
     * @param file
     * @param response
     */
    @Synchronized
    @Throws(IOException::class)
    protected fun writeOKHTTPResponseViaSink(file: File, response: Response) {
        val sink = Okio.buffer(Okio.sink(file))
        //  TODO by Batu: check body is null
        sink.writeAll(response.body()!!.source())
        sink.close()
        response.close()
    }

    /**
     * Same as writeOKHTTPResponseViaSink but little slower
     *
     * @param file
     * @param response
     */
    @Synchronized
    @Throws(IOException::class)
    protected fun writeOKHttpResponseViaOutputStream(file: File, response: Response) {
        //  TODO by Batu: check body is null
        val inputStream = response.body()!!.byteStream()

        val input = BufferedInputStream(inputStream)
        val output = FileOutputStream(file)

        val data = ByteArray(1024)

        var count: Int

        while (input.read(data).also { count = it } != -1) {
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()
    }

    /**
     * If you used OKHttp to download file, recommend using writeOKHTTPResponseViaSink or writeOKHttpResponseViaOutputStream which are much faster than this method
     *
     * @param file
     * @param bytes
     */
    @Synchronized
    @Throws(IOException::class)
    fun writeByteViaOutputStream(file: File, bytes: ByteArray) {
        //  //  TODO by Batu: try to refactor by KTX
        val outputStream = BufferedOutputStream(FileOutputStream(file))
        outputStream.write(bytes)
        outputStream.close()
    }

    /**
     * @param file
     * @return
     * @throws IOException
     */
    @Synchronized
    @Throws(IOException::class)
    fun readFileViaInputStream(file: File): String {
        val data = StringBuilder()
        val reader: BufferedReader

        reader = BufferedReader(InputStreamReader(FileInputStream(file), "utf-8"))
        var line: String?

        while (reader.readLine().also { line = it} != null) {
            data.append(line)
        }

        reader.close()
        return data.toString()
    }

    @Synchronized
    @Throws(IOException::class)
    protected fun copy(src: File, dst: File) {
        val inputStream = FileInputStream(src)

        val input = BufferedInputStream(inputStream)
        val output = FileOutputStream(dst)

        val data = ByteArray(1024)

        var count: Int
        while (input.read(data).also { count = it } != -1) {
            output.write(data, 0, count)
        }

        output.flush()
        output.close()
        input.close()
    }

    /**
     * @param file
     */
    @Synchronized
    protected fun deleteFiles(file: File): Boolean {
        if (file.isDirectory) {
            val fileList = file.listFiles()
            if (fileList != null) {
                for (subFile in fileList) {
                    deleteFiles(subFile)
                }
            }
        }
        return file.delete()
    }

    /*
    from Dcard
    class FileProgressRequestBody(
        private val sourceMaterial: SourceMaterial,
        private val length: Long,
        private val contentType: MediaType?,
        private val listener: ProgressListener?
    ) : RequestBody() {

        abstract class SourceMaterial

        data class FileSourceMaterial(
            val file: File
        ) : SourceMaterial()

        data class UriSourceMaterial(
            val imageUri: Uri,
            val contentResolver: ContentResolver
        ) : SourceMaterial()

        companion object {

            private const val SEGMENT_SIZE = 2048L // okio.Segment.SIZE

            @Throws(IOException::class)
            fun newInstance(
                file: File,
                contentType: MediaType?,
                listener: ProgressListener
            ) = FileProgressRequestBody(
                FileSourceMaterial(file),
                file.length(),
                contentType,
                listener
            )

            @Throws(IOException::class)
            fun newInstance(
                imageUri: Uri,
                contentResolver: ContentResolver,
                contentType: MediaType?,
                listener: ProgressListener
            ) = run {
                val inputStream =
                    contentResolver.openInputStream(imageUri)
                        ?: throw IOException("Unable to open input stream.")
                val length = inputStream.use {
                    inputStream.available().toLong()
                }
                FileProgressRequestBody(
                    UriSourceMaterial(imageUri, contentResolver),
                    length,
                    contentType,
                    listener
                )
            }
        }

        interface ProgressListener {
            fun onProgress(progress: Long)
        }

        override fun contentLength() = length

        override fun contentType() = contentType

        @Throws(IOException::class)
        override fun writeTo(sink: BufferedSink) {
            when (sourceMaterial) {
                is FileSourceMaterial -> Okio.source(sourceMaterial.file)
                is UriSourceMaterial -> {
                    Okio.source(
                        sourceMaterial.contentResolver.openInputStream(sourceMaterial.imageUri)
                            ?: throw IOException("Unable to open input stream.")
                    )
                }
                else -> throw IllegalStateException("Unknown source material: $sourceMaterial")
            }.use { source ->
                var total = 0L
                while (true) {
                    val read = source.read(sink.buffer(), SEGMENT_SIZE)
                    if (read == -1L) break
                    total += read
                    sink.flush()
                    listener?.onProgress(total)
                }
            }
        }
    }
     */

}