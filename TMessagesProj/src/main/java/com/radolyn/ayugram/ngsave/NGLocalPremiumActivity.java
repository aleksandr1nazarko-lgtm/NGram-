/*
 * NGram: "Local premium" screen.
 */
package com.radolyn.ayugram.ngsave;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
public class NGLocalPremiumActivity extends BaseFragment {
    private RecyclerListView listView;
    private RecyclerListView.SelectionAdapter adapter;
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Local premium");
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
        listView.setAdapter(adapter = new RecyclerListView.SelectionAdapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if (viewType == 0) {
                    view = new TextCheckCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                } else {
                    view = new TextInfoPrivacyCell(context);
                }
                return new RecyclerListView.Holder(view);
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (position == 0) {
                    ((TextCheckCell) holder.itemView).setTextAndCheck("Премиум", NGPremium.enabled(), false);
                } else {
                    ((TextInfoPrivacyCell) holder.itemView).setText("Включает премиум-функции только на этом устройстве: интерфейс, реакции, эмодзи-статусы и другие настройки, которые проверяют статус на стороне приложения. Всё, что решает сервер Telegram (лимиты загрузки, скорость скачивания, платные функции), локальный премиум не открывает. Ваш аккаунт остаётся без премиума, другие пользователи его не видят.");
                }
            }
            @Override
            public int getItemCount() {
                return 2;
            }
            @Override
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return holder.getItemViewType() == 0;
            }
            @Override
            public int getItemViewType(int position) {
                return position == 0 ? 0 : 1;
            }
        });
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position) -> {
            if (position == 0) {
                NGPremium.set(!NGPremium.enabled());
                adapter.notifyDataSetChanged();
            }
        });
        return fragmentView;
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
