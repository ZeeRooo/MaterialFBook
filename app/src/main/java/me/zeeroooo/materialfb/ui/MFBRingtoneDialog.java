package me.zeeroooo.materialfb.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;

import me.zeeroooo.materialfb.MFB;
import me.zeeroooo.materialfb.R;
import me.zeeroooo.materialfb.adapters.AdapterListPreference;

public class MFBRingtoneDialog extends MaterialAlertDialogBuilder {

    private MediaPlayer mediaPlayer;
    private short position = 0;
    private Button button = null;

    public MFBRingtoneDialog(Context context, SharedPreferences sharedPreferences, String type) {
        super(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);

        final RingtoneManager ringtoneManager = new RingtoneManager(context);

        ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);

        final Cursor cursor = ringtoneManager.getCursor();

        final CharSequence[] titleCharSequences = new CharSequence[cursor.getCount()], uriCharSequences = new CharSequence[cursor.getCount()];

        while (cursor.moveToNext()) {
            titleCharSequences[position] = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            uriCharSequences[position] = cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + '/' + cursor.getString(RingtoneManager.ID_COLUMN_INDEX);
            position++;
        }
        cursor.close();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(false);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);

        CharSequence defaultValue = Uri.parse(sharedPreferences.getString(type, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())).toString();

        for (position = 0; position < uriCharSequences.length; position++)
            if (defaultValue.equals(uriCharSequences[position]))
                break;

        setSingleChoiceItems(new AdapterListPreference(context, titleCharSequences), position, (dialogInterface, i) -> {
            position = (short) i;

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(context, Uri.parse(uriCharSequences[i].toString()));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayer.release();
            }

            mediaPlayer.start();
        });

        setPositiveButton(context.getString(android.R.string.ok), (dialogInterface, i) -> {
            sharedPreferences.edit().putString(type, uriCharSequences[position].toString()).apply();
            dialogInterface.dismiss();
        });

        setOnDismissListener(dialogInterface -> mediaPlayer.release());
    }

    @Override
    public AlertDialog show() {
        final AlertDialog alertDialog = super.show();

        for (byte a = -3; a < 0; a++) {
            button = alertDialog.getButton(a);
            if (button != null)
                button.setTextColor(MFB.colorPrimary);
        }

        return alertDialog;
    }
}
