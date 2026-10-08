/*
 * Ghost-mode settings screen. Logic and option set are taken from AyuGram for Android
 * (Radolyn Labs, https://github.com/AyuGram/AyuGram4A, GPL); the UI is rewritten on plain
 * Telegram cells so it does not depend on exteraGram's BasePreferencesActivity.
 */

package com.radolyn.ayugram.ui.preferences;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radolyn.ayugram.AyuConfig;
import com.radolyn.ayugram.utils.AyuState;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class GhostPreferencesActivity extends BaseFragment {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CHECK = 1;
    private static final int TYPE_INFO = 2;

    private RecyclerListView listView;
    private ListAdapter adapter;

    private int rowCount;
    private int headerRow;
    private int ghostToggleRow;
    private int dontReadRow;
    private int dontOnlineRow;
    private int dontTypingRow;
    private int offlineAfterOnlineRow;
    private int markReadAfterSendRow;
    private int infoRow;

    private void updateRows() {
        rowCount = 0;
        headerRow = rowCount++;
        ghostToggleRow = rowCount++;
        dontReadRow = rowCount++;
        dontOnlineRow = rowCount++;
        dontTypingRow = rowCount++;
        offlineAfterOnlineRow = rowCount++;
        markReadAfterSendRow = rowCount++;
        infoRow = rowCount++;
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
        actionBar.setTitle(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostSettings));
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
            if (position == ghostToggleRow) {
                AyuConfig.toggleGhostMode();
                AyuState.setAllowReadPacket(false, -1);
                adapter.notifyDataSetChanged();
                BulletinFactory.of(this).createSuccessBulletin(LocaleController.getString(
                        AyuConfig.isGhostModeActive() ? R.string.AyuGhostEnabled : R.string.AyuGhostDisabled)).show();
            } else if (position == dontReadRow) {
                AyuConfig.editor.putBoolean("sendReadPackets", AyuConfig.sendReadPackets ^= true).apply();
                AyuState.setAllowReadPacket(false, -1);
                adapter.notifyDataSetChanged();
            } else if (position == dontOnlineRow) {
                AyuConfig.editor.putBoolean("sendOnlinePackets", AyuConfig.sendOnlinePackets ^= true).apply();
                adapter.notifyDataSetChanged();
            } else if (position == dontTypingRow) {
                AyuConfig.editor.putBoolean("sendUploadProgress", AyuConfig.sendUploadProgress ^= true).apply();
                adapter.notifyDataSetChanged();
            } else if (position == offlineAfterOnlineRow) {
                AyuConfig.editor.putBoolean("sendOfflinePacketAfterOnline", AyuConfig.sendOfflinePacketAfterOnline ^= true).apply();
                adapter.notifyDataSetChanged();
            } else if (position == markReadAfterSendRow) {
                AyuConfig.editor.putBoolean("markReadAfterSend", AyuConfig.markReadAfterSend ^= true).apply();
                AyuState.setAllowReadPacket(false, -1);
                adapter.notifyDataSetChanged();
            }
        });

        return fragmentView;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_HEADER) {
                view = new HeaderCell(getContext());
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == TYPE_CHECK) {
                view = new TextCheckCell(getContext());
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new TextInfoPrivacyCell(getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case TYPE_HEADER:
                    ((HeaderCell) holder.itemView).setText(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostEssentialsHeader));
                    break;
                case TYPE_INFO:
                    ((TextInfoPrivacyCell) holder.itemView).setText(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostInfo));
                    break;
                case TYPE_CHECK:
                    TextCheckCell cell = (TextCheckCell) holder.itemView;
                    if (position == ghostToggleRow) {
                        cell.setTextAndCheck(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostToggle), AyuConfig.isGhostModeActive(), true);
                    } else if (position == dontReadRow) {
                        cell.setTextAndCheck(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostDontRead), !AyuConfig.sendReadPackets, true);
                    } else if (position == dontOnlineRow) {
                        cell.setTextAndCheck(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostDontOnline), !AyuConfig.sendOnlinePackets, true);
                    } else if (position == dontTypingRow) {
                        cell.setTextAndCheck(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostDontTyping), !AyuConfig.sendUploadProgress, true);
                    } else if (position == offlineAfterOnlineRow) {
                        cell.setTextAndCheck(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostOfflineAfterOnline), AyuConfig.sendOfflinePacketAfterOnline, true);
                    } else if (position == markReadAfterSendRow) {
                        cell.setTextAndCheck(com.radolyn.ayugram.ngsave.NGStr.get(R.string.AyuGhostMarkReadAfterSend), AyuConfig.markReadAfterSend, false);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == TYPE_CHECK;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == headerRow) {
                return TYPE_HEADER;
            } else if (position == infoRow) {
                return TYPE_INFO;
            }
            return TYPE_CHECK;
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
