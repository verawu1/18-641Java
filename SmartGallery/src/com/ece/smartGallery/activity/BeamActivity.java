package com.ece.smartGallery.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ece.smartGallery.R;
import com.ece.smartGallery.DBLayout.Photo;
import com.ece.smartGallery.util.IO;
import com.ece.smartGallery.util.Utility;

public class BeamActivity extends Activity implements CreateNdefMessageCallback {
	NfcAdapter mNfcAdapter;
	TextView textView;
	private final String TAG = this.getClass().getName();
	private Photo photo;
	private byte[] imageBytes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		TextView textView = (TextView) findViewById(R.id.beam_text);
		textView.setText("on Create");
		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}
		// Register callback
		mNfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d(TAG, "ndef message creating");
		NdefRecord photoRecord = createPhotoRecord();
		NdefRecord imageRecord = createImageRecord();
		NdefMessage msg = new NdefMessage(new NdefRecord[] {
				photoRecord
		,imageRecord
		// ,NdefRecord
				// .createApplicationRecord("com.ece.smartGallery.activity")
				});
		return msg;
	}

	public NdefRecord createImageRecord() {
		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"image/jpeg".getBytes(), new byte[0], imageBytes);
		Log.d(TAG, "image record created, length = " + imageBytes.length);
		return record;
	}

	public NdefRecord createPhotoRecord() {
		byte[] payload = null;
		try {
			payload = IO.getByteArray(photo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (payload != null) {
			NdefRecord record = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE,
					("com.ece.smartGallery:" + Photo.PHOTO).getBytes(),
					new byte[0], payload);
			Log.d(TAG, "photo record created, length = " + payload.length);
			return record;
		} else {
			return null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		} else {
			photo = (Photo) getIntent().getSerializableExtra(Photo.PHOTO);
			Bitmap bm = null;
			try {
				bm = Utility.scaleImage(this, photo.getImage());
			} catch (IOException e) {
				e.printStackTrace();
			}
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 50, stream);
			imageBytes = stream.toByteArray();
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {
		Log.d(TAG, "process intent, NFC message received");
		textView = (TextView) findViewById(R.id.beam_text);
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present

		Log.d(TAG, "recieved NFC: has " + msg.getRecords().length + " records");
		int cnt = 0;
		for (NdefRecord record : msg.getRecords()) {
			Log.d(TAG, "record " + cnt + " has length: "
					+ record.getPayload().length);
			Log.d(TAG,
					"record " + cnt + "is a type of "
							+ new String(record.getType()));
			cnt++;
		}
	}
}
