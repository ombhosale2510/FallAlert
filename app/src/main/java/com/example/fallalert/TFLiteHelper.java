package com.example.fallalert;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteHelper {
    private Interpreter interpreter;

    public TFLiteHelper(AssetManager assetManager, String modelPath) throws IOException {
        Interpreter.Options options = new Interpreter.Options();
        MappedByteBuffer modelBuffer = loadModelFile(assetManager, modelPath);
        interpreter = new Interpreter(modelBuffer, options);
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public int predict(float[] inputData) {
        float[][] input = new float[1][6];
        float[][] output = new float[1][2];

        System.arraycopy(inputData, 0, input[0], 0, inputData.length);

        interpreter.run(input, output);

        return output[0][0] > output[0][1] ? 0 : 1; // 0: No fall, 1: Fall
    }
}
