package com.abdulkarimalbaik.dev.hajozat.Common;

import android.os.Handler;
import android.os.Looper;

import com.abdulkarimalbaik.dev.hajozat.Interface.UploadCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import androidx.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ProgressRequestBody extends RequestBody {

    //this class help us to upload an image to server

    private File file;
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private UploadCallBack listener;

    public ProgressRequestBody(File file, UploadCallBack listener) {
        this.file = file;
        this.listener = listener;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        FileInputStream in = new FileInputStream(file);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());

            while ((read = in.read(buffer)) != -1){

                handler.post(new ProgressUpdate(uploaded , fileLength));
                uploaded += read;
                sink.write(buffer , 0 , read);

            }

        }
        finally {
            in.close();
        }
    }

    private class ProgressUpdate implements Runnable {

        private long uploaded , fileLength;
        public ProgressUpdate(long uploaded, long fileLength) {
            this.fileLength = fileLength;
            this.uploaded = uploaded;
        }

        @Override
        public void run() {

            listener.onProgressUpdate((int)(100*uploaded/fileLength));
        }
    }
}
