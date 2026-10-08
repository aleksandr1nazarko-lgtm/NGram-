/*
 * NGram settings section: list of NGram features.
 * Item names are intentionally in English, everything inside the screens is in Russian.
 */
package com.radolyn.ayugram.ui.preferences;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.radolyn.ayugram.ngsave.NGSavePreferencesActivity;
import com.radolyn.ayugram.ngsave.NGStr;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
public class NGramPreferencesActivity extends BaseFragment {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TEXT = 1;
    private static final String[] TITLES = {
            "Ghost mode",
            "Save deleted",
            "Local premium",
            "Local banners",
            "Proxy",
            "Plugins"
    };
    private static final int[] ICONS = {
            R.drawable.ayu_ghost,
            R.drawable.msg_delete,
            R.drawable.msg_premium_prolfilestar,
            R.drawable.msg_gallery,
            R.drawable.msg_settings,
            R.drawable.msg_fave
    };
    private static final int[] COLORS = {
            0xFF9B6BFF,
            0xFFFF6B6B,
            0xFF4DA3FF,
            0xFF3FC380,
            0xFFFFA23E,
            0xFF8E8E93
    };
    private RecyclerListView listView;
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("NGram");
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
        listView.setAdapter(new ListAdapter());
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position) -> {
            int index = position - 1;
            if (index == 0) {
                presentFragment(new GhostPreferencesActivity());
            } else if (index == 1) {
                presentFragment(new NGSavePreferencesActivity());
            } else if (index == 2) {
                presentFragment(new com.radolyn.ayugram.ngsave.NGLocalPremiumActivity());
            } else if (index == 3) {
                // NG-HOOK:banners
                showSoon(index);
            } else {
                showSoon(index);
            }
        });
        return fragmentView;
    }
    private void showSoon(int index) {
        if (getParentActivity() == null || index < 0 || index >= TITLES.length) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(TITLES[index]);
        builder.setMessage("Этот раздел появится в следующем обновлении.");
        builder.setPositiveButton("OK", null);
        showDialog(builder.create());
    }
    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_HEADER) {
                view = new HeaderCell(getContext());
            } else {
                view = new TextCell(getContext());
            }
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_HEADER) {
                ((HeaderCell) holder.itemView).setText("Settings NGram");
            } else {
                int index = position - 1;
                ((TextCell) holder.itemView).setTextAndColorfulIcon(TITLES[index], ICONS[index], COLORS[index], index < TITLES.length - 1);
            }
        }
        @Override
        public int getItemCount() {
            return TITLES.length + 1;
        }
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == TYPE_TEXT;
        }
        @Override
        public int getItemViewType(int position) {
            return position == 0 ? TYPE_HEADER : TYPE_TEXT;
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
