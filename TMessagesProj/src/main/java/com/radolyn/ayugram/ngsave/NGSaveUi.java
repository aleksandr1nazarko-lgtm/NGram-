/*
 * NGram: small UI helpers for the "Save deleted" feature.
 */
package com.radolyn.ayugram.ngsave;
import android.app.Activity;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class NGSaveUi {
    public static void showEditHistory(BaseFragment fragment, long dialogId, int mid) {
        Activity activity = fragment.getParentActivity();
        if (activity == null) {
            return;
        }
        ArrayList<NGSaveDb.Edit> edits = NGSaveDb.getInstance().getEdits(fragment.getCurrentAccount(), dialogId, mid);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        for (int a = 0; a < edits.size(); a++) {
            NGSaveDb.Edit e = edits.get(a);
            if (a > 0) {
                sb.append("\n\n");
            }
            sb.append(format.format(new Date(e.versionDate * 1000L))).append('\n');
            if (e.text == null || e.text.isEmpty()) {
                sb.append(NGStr.get(R.string.NGSaveEditNoText));
            } else {
                sb.append(e.text);
            }
            if (e.hadMedia) {
                sb.append('\n').append(NGStr.get(R.string.NGSaveEditMediaChanged));
            }
        }
        if (edits.isEmpty()) {
            sb.append(NGStr.get(R.string.NGSaveEditEmpty));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(NGStr.get(R.string.NGSaveEditHistory));
        builder.setMessage(sb.toString());
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        fragment.showDialog(builder.create());
    }
}
