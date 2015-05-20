/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package it.polito.mobile.androidassignment2.s3client.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;


import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.PersistableDownload;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.exception.PauseException;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import it.polito.mobile.androidassignment2.s3client.Constants;

/*
 * Class that encapsulates downloads, handling all the interaction with the
 * underlying Download and TransferManager
 */
public class DownloadModel extends TransferModel {
    private static final String TAG = "DownloadModel";
    public static final String INTENT_DOWNLOADED = "it.polito.mobile.DOWNLOAD_FINISHED";
    public static final String INTENT_DOWNLOAD_FAILED = "it.polito.mobile.DOWNLOAD_FAILED";
    public static final String EXTRA_FILE_URI = "downloadedFileURI";

    private Download mDownload;
    private PersistableDownload mPersistableDownload;
    private ProgressListener mListener;
    private String mKey;
    private Status mStatus;
    private Uri mUri;

    public DownloadModel(Context context, String key, TransferManager manager) {
        super(context, Uri.parse(key), manager);
        mKey = key;
        mStatus = Status.IN_PROGRESS;
        mListener = new ProgressListener() {
            @Override
            public void progressChanged(ProgressEvent event) {
                if (event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {

                    Intent intent = new Intent(INTENT_DOWNLOADED);

                    intent.putExtra(EXTRA_FILE_URI, mUri.toString());
                    //intent.setData(mUri);
                    getContext().sendBroadcast(intent);
                    Log.d("poliJob", "Sending broadcast intent " + INTENT_DOWNLOADED);
                    mStatus = Status.COMPLETED;

                }
            }
        };
    }

    @Override
    public Status getStatus() {
        return mStatus;
    }

    @Override
    public Transfer getTransfer() {
        return mDownload;
    }

    @Override
    public Uri getUri() {
        return mUri;
    }

    @Override
    public void abort() {
        if (mDownload != null) {
            mStatus = Status.CANCELED;
            try {
                mDownload.abort();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }
        }
    }

    public void download() {
        mStatus = Status.IN_PROGRESS;
        File file = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                getFileName());
        mUri = Uri.fromFile(file);
        try {
            mDownload = getTransferManager().download(
                    Constants.BUCKET_NAME.toLowerCase(Locale.US), mKey, file);
            if (mListener != null) {
                mDownload.addProgressListener(mListener);
            }
        }catch(Exception e){
            Intent intent = new Intent(INTENT_DOWNLOAD_FAILED);

            intent.putExtra(EXTRA_FILE_URI, mUri.toString());
            //intent.setData(mUri);
            getContext().sendBroadcast(intent);
            Log.d("poliJob", "Sending broadcast intent " + INTENT_DOWNLOAD_FAILED);
        }
    }

    @Override
    public void pause() {
        if (mStatus == Status.IN_PROGRESS) {
            mStatus = Status.PAUSED;
            try {
                mPersistableDownload = mDownload.pause();
            } catch (PauseException e) {
                Log.d(TAG, "", e);
            }
        }
    }

    @Override
    public void resume() {
        if (mStatus == Status.PAUSED) {
            mStatus = Status.IN_PROGRESS;
            if (mPersistableDownload != null) {
                mDownload = getTransferManager().resumeDownload(
                        mPersistableDownload);
                mDownload.addProgressListener(mListener);
                mPersistableDownload = null;
            } else {
                download();
            }
        }
    }
}
