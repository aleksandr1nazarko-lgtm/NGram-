/*
 * NGram: settings screen for saving deleted messages and edit history.
 * Built on plain Telegram cells (same approach as the Ghost mode screen).
 */

package com.radolyn.ayugram.ngsave;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class NGSavePreferencesActivity extends BaseFragment {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_INFO = 2;
    private static final int TYPE_VALUE = 3;
    private static final int TYPE_ACTION = 4;

    private static final int ACTION_MARKER_STYLE = 1;
    private static final int ACTION_CLEAR_EDITS = 2;
    private static final int ACTION_CLEAR_MARKS = 3;
    private static final int ACTION_CLEAR_ALL = 4;

    private static class Row {
        final int type;
        final String key;
        final int titleRes;
        final int action;
        boolean divider;

        Row(int type, String key, int titleRes, int action, boolean divider) {
            this.type = type;
            this.key = key;
            this.titleRes = titleRes;
            this.action = action;
            this.divider = divider;
        }
    }

    private final ArrayList<Row> rows = new ArrayList<>();

    private RecyclerListView listView;
    private ListAdapter adapter;

    private void header(int res) {
        rows.add(new Row(TYPE_HEADER, null, res, 0, false));
    }

    private void info(int res) {
        rows.add(new Row(TYPE_INFO, null, res, 0, false));
    }

    private void check(String key, int res, boolean divider) {
        rows.add(new Row(TYPE_CHECK, key, res, 0, divider));
    }

    private void value(int action, int res, boolean divider) {
        rows.add(new Row(TYPE_VALUE, null, res, action, divider));
    }

    private void action(int action, int res, boolean divider) {
        rows.add(new Row(TYPE_ACTION, null, res, action, divider));
    }

    private void updateRows() {
        rows.clear();

        header(R.string.NGSaveHeaderDeleted);
        check(NGSaveConfig.SAVE_DELETED, R.string.NGSaveDeleted, false);
        info(R.string.NGSaveDeletedInfo);

        if (NGSaveConfig.get(NGSaveConfig.SAVE_DELETED)) {
            header(R.string.NGSaveHeaderChats);
            check(NGSaveConfig.IN_PRIVATE, R.string.NGSaveInPrivate, true);
            check(NGSaveConfig.IN_BOTS, R.string.NGSaveInBots, true);
            check(NGSaveConfig.IN_GROUPS, R.string.NGSaveInGroups, true);
            check(NGSaveConfig.IN_CHANNELS, R.string.NGSaveInChannels, true);
            check(NGSaveConfig.KEEP_CLEARED, R.string.NGSaveKeepCleared, false);
            info(R.string.NGSaveChatsInfo);

            header(R.string.NGSaveHeaderContent);
            check(NGSaveConfig.K_TEXT, R.string.NGSaveKText, true);
            check(NGSaveConfig.K_PHOTO, R.string.NGSaveKPhoto, true);
            check(NGSaveConfig.K_VIDEO, R.string.NGSaveKVideo, true);
            check(NGSaveConfig.K_VOICE, R.string.NGSaveKVoice, true);
            check(NGSaveConfig.K_FILE, R.string.NGSaveKFile, true);
            check(NGSaveConfig.K_STICKER, R.string.NGSaveKSticker, true);
            check(NGSaveConfig.K_GIF, R.string.NGSaveKGif, true);
            check(NGSaveConfig.K_OTHER, R.string.NGSaveKOther, false);
            info(R.string.NGSaveContentInfo);

            header(R.string.NGSaveHeaderDisplay);
            check(NGSaveConfig.SHOW_MARKER, R.string.NGSaveShowMarker, NGSaveConfig.get(NGSaveConfig.SHOW_MARKER));
            if (NGSaveConfig.get(NGSaveConfig.SHOW_MARKER)) {
                value(ACTION_MARKER_STYLE, R.string.NGSaveMarkerStyle, false);
            }
            info(R.string.NGSaveDisplayInfo);
        }

        header(R.string.NGSaveHeaderEdits);
        check(NGSaveConfig.SAVE_EDITS, R.string.NGSaveEdits, false);
        info(R.string.NGSaveEditsInfo);

        header(R.string.NGSaveHeaderData);
        action(ACTION_CLEAR_EDITS, R.string.NGSaveClearEdits, true);
        action(ACTION_CLEAR_MARKS, R.string.NGSaveClearMarks, true);
        action(ACTION_CLEAR_ALL, R.string.NGSaveClearAll, false);
        info(R.string.NGSaveDataInfo);
    }

    @Override
    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.NGSaveSettings));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new ListAdapter());
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            if (position < 0 || position >= rows.size()) {
                return;
            }
            Row row = rows.get(position);
            if (row.type == TYPE_CHECK) {
                NGSaveConfig.toggle(row.key);
                if (NGSaveConfig.SAVE_DELETED.equals(row.key) || NGSaveConfig.SHOW_MARKER.equals(row.key)) {
                    updateRows();
                }
                adapter.notifyDataSetChanged();
            } else if (row.type == TYPE_VALUE && row.action == ACTION_MARKER_STYLE) {
                showMarkerStyleDialog();
            } else if (row.type == TYPE_ACTION) {
                confirmClear(row.action);
            }
        });

        return fragmentView;
    }

    private void showMarkerStyleDialog() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.NGSaveMarkerStyle));
        builder.setItems(new CharSequence[]{
                LocaleController.getString(R.string.NGSaveMarkerText),
                LocaleController.getString(R.string.NGSaveMarkerIcon),
                LocaleController.getString(R.string.NGSaveMarkerBoth)
        }, (dialog, which) -> {
            NGSaveConfig.setMarkerStyle(which);
            adapter.notifyDataSetChanged();
        });
        showDialog(builder.create());
    }

    private void confirmClear(int action) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.NGSaveClearTitle));
        builder.setMessage(LocaleController.getString(R.string.NGSaveClearMessage));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), (dialog, which) -> {
            if (action == ACTION_CLEAR_EDITS) {
                NGSave.clearEdits();
            } else if (action == ACTION_CLEAR_MARKS) {
                NGSave.clearMarks();
            } else {
                NGSave.clearAll();
            }
            adapter.notifyDataSetChanged();
            BulletinFactory.of(this).createSuccessBulletin(LocaleController.getString(R.string.NGSaveCleared)).show();
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private String markerStyleName() {
        switch (NGSaveConfig.getMarkerStyle()) {
            case NGSaveConfig.MARKER_ICON:
                return LocaleController.getString(R.string.NGSaveMarkerIcon);
            case NGSaveConfig.MARKER_BOTH:
                return LocaleController.getString(R.string.NGSaveMarkerBoth);
            default:
                return LocaleController.getString(R.string.NGSaveMarkerText);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case TYPE_HEADER:
                    view = new HeaderCell(getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case TYPE_CHECK:
                    view = new TextCheckCell(getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case TYPE_VALUE:
                    view = new TextSettingsCell(getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case TYPE_ACTION:
                    view = new TextCell(getContext());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextInfoPrivacyCell(getContext());
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Row row = rows.get(position);
            switch (holder.getItemViewType()) {
                case TYPE_HEADER:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString(row.titleRes));
                    break;
                case TYPE_INFO:
                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.getString(row.titleRes));
                    break;
                case TYPE_CHECK:
                    ((TextCheckCell) holder.itemView).setTextAndCheck(LocaleController.getString(row.titleRes), NGSaveConfig.get(row.key), row.divider);
                    break;
                case TYPE_VALUE:
                    ((TextSettingsCell) holder.itemView).setTextAndValue(LocaleController.getString(row.titleRes), markerStyleName(), row.divider);
                    break;
                case TYPE_ACTION:
                    TextCell cell = (TextCell) holder.itemView;
                    cell.setText(LocaleController.getString(row.titleRes), row.divider);
                    cell.setColors(-1, Theme.key_text_RedRegular);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return rows.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int t = holder.getItemViewType();
            return t == TYPE_CHECK || t == TYPE_VALUE || t == TYPE_ACTION;
        }

        @Override
        public int getItemViewType(int position) {
            return rows.get(position).type;
        }
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        listView.setPadding(0, 0, 0, bottom);
        listView.setClipToPadding(false);
    }
}
