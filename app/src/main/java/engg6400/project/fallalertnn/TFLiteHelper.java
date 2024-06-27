package engg6400.project.fallalertnn;

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
    private float[] preprocessInputData(float[] inputData) {
        // Implement your preprocessing logic here (e.g., normalization)
        return inputData;
    }
    public int predict(float[] inputData) {
        float[][] input = new float[1][6];
        float[][] output = new float[1][2];

        System.arraycopy(inputData, 0, input[0], 0, inputData.length);

        interpreter.run(input, output);

        float predictionScore = output[0][0]; // This is the predicted probability

        // Determine predicted class based on threshold (e.g., 0.5)
        int predictedClass = (predictionScore > 0.5) ? 1 : 0;

        return predictedClass;
        //return output[0][0] > output[0][1] ? 0 : 1; // 0: No fall, 1: Fall
    }
    /*public int predict(float[] inputData) {
        // Preprocess input data
        float[] inputTensor = preprocessInputData(inputData);

        // Prepare output buffer
        float[][] outputTensor = new float[1][1]; // Adjust based on your model's output shape

        // Run inference
        interpreter.run(inputTensor, outputTensor);

        // Process the model's output
        float predictionScore = outputTensor[0][0]; // This is the predicted probability

        // Determine predicted class based on threshold (e.g., 0.5)
        int predictedClass = (predictionScore > 0.5) ? 1 : 0;

        return predictedClass;
    }*/
}
