/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.example.trafficsignsdetection.Classification;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.example.trafficsignsdetection.Utils.Logger;

import java.util.List;

/**
 * Class that takes in preview frames and converts the image to Bitmaps to process with Tensorflow.
 */
public class TensorFlowImageListener {
    private static final Logger LOGGER = new Logger();

    private static final boolean SAVE_PREVIEW_BITMAP = false;

    // These are the settings for the original v1 Inception model. If you want to
    // use a model that's been produced from the TensorFlow for Poets codelab,
    // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
    // INPUT_NAME = "Mul:0", and OUTPUT_NAME = "final_result:0".
    // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
    // the ones you produced.
    private static final int NUM_CLASSES = 43;
    private static final int INPUT_SIZE = 32;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 1;
    private static final String INPUT_NAME = "inp/X:0";
    private static final String OUTPUT_NAME = "out/Softmax:0";

    private static final String MODEL_FILE = "file:///android_asset/output_graph_150epochs.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels_sinais.txt";


    private final TensorFlowClassifier tensorflow = new TensorFlowClassifier();

    public void initialize(final AssetManager assetManager) {
        tensorflow.initializeTensorFlow(
                assetManager, MODEL_FILE, LABEL_FILE, NUM_CLASSES, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD,
                INPUT_NAME, OUTPUT_NAME);
    }

    public String recognizeSign(Bitmap signBitmap){
        String res = "";
        final List<Classifier.Recognition> results = tensorflow.recognizeImage(signBitmap);

        LOGGER.v("%d results", results.size());
        for (final Classifier.Recognition result : results) {
            //LOGGER.v("Result: " + result.getTitle());
            res = result.getTitle();
        }

        return res;
    }

}

