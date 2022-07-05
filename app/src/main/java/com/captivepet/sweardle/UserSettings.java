package com.captivepet.sweardle;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceFragmentCompat;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class UserSettings extends PreferenceFragmentCompat {
    private final String TAG = this.getClass().getSimpleName();

//    private Uri getUri() {
////        Uri suggestions = getActivity().getFilesDir();
////        if (suggestions == null) {
////
////        } else {
////
////        }
//    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    // Request code for creating a PDF document. MODIFIED FOR txt by PM
    private static final int CREATE_FILE = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/txt");
        intent.putExtra(Intent.EXTRA_TITLE, "Sweardle.txt");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

//        wordFeedbackResultLauncher.launch(intent, CREATE_FILE);
    }

    ActivityResultLauncher<Intent> wordFeedbackResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Uri uri = null;
                        Intent data = result.getData();
//                        uri = result.getData();
//                        try {
//                            alterDocument((Uri) result);
//                        } catch (URISyntaxException uriSyntaxException) {
//                            Log.d(TAG, "bummer");
//                        }
                    }
                }
            });

    // https://developer.android.com/training/data-storage/shared/documents-files#open-file
    private void alterDocument(Uri uri, String text) {
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().
                    openFileDescriptor(uri, "a");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            fileOutputStream.write((text + "\n").getBytes());
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close();
            pfd.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
