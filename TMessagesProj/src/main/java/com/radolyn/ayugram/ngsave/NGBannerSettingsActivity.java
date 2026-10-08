/*
 * NGram: "Local banners" settings screen.
 */
package com.radolyn.ayugram.ngsave;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
public class NGBannerSettingsActivity extends BaseFragment {
    // rows: 0 header, 1 enabled, 2 dim, 3 info, 4 header, 5 add, 6 remove own, 7 remove all, 8 info
    private static final int ROWS = 9;
    private RecyclerListView listView;
    private RecyclerListView.SelectionAdapter adapter;
    private long ownKey() {
        return UserConfig.getInstance(currentAccount).getClientUserId();
    }
    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Local banners");
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
                switch (viewType) {
                    case 0:
                        view = new HeaderCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        break;
                    case 1:
                        view = new TextCheckCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        break;
                    case 2:
                        view = new TextCell(context);
                        view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        break;
                    default:
                        view = new TextInfoPrivacyCell(context);
                        break;
                }
                return new RecyclerListView.Holder(view);
            }
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                switch (position) {
                    case 0:
                        ((HeaderCell) holder.itemView).setText("Баннеры");
                        break;
                    case 1:
                        ((TextCheckCell) holder.itemView).setTextAndCheck("Показывать баннеры", NGBanner.enabled(), true);
                        break;
                    case 2:
                        ((TextCheckCell) holder.itemView).setTextAndCheck("Затемнять баннер", NGBanner.dim(), false);
                        break;
                    case 3:
                        ((TextInfoPrivacyCell) holder.itemView).setText("Баннер рисуется на фоне профиля. Он виден только вам и хранится на этом устройстве. Баннер любого другого профиля меняется карандашом в верхнем левом углу его страницы.");
                        break;
                    case 4:
                        ((HeaderCell) holder.itemView).setText("Мой баннер");
                        break;
                    case 5:
                        ((TextCell) holder.itemView).setText("Добавить баннер из галереи", true);
                        ((TextCell) holder.itemView).setColors(-1, Theme.key_windowBackgroundWhiteBlackText);
                        break;
                    case 6:
                        ((TextCell) holder.itemView).setText("Убрать мой баннер", true);
                        ((TextCell) holder.itemView).setColors(-1, Theme.key_windowBackgroundWhiteBlackText);
                        break;
                    case 7:
                        ((TextCell) holder.itemView).setText("Удалить все баннеры", false);
                        ((TextCell) holder.itemView).setColors(-1, Theme.key_text_RedRegular);
                        break;
                    default:
                        ((TextInfoPrivacyCell) holder.itemView).setText("Изображение обрезается по размеру шапки профиля.");
                        break;
                }
            }
            @Override
            public int getItemCount() {
                return ROWS;
            }
            @Override
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                int t = holder.getItemViewType();
                return t == 1 || t == 2;
            }
            @Override
            public int getItemViewType(int position) {
                if (position == 0 || position == 4) {
                    return 0;
                }
                if (position == 1 || position == 2) {
                    return 1;
                }
                if (position >= 5 && position <= 7) {
                    return 2;
                }
                return 3;
            }
        });
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setOnItemClickListener((view, position) -> {
            if (position == 1) {
                NGBanner.setEnabled(!NGBanner.enabled());
                adapter.notifyDataSetChanged();
            } else if (position == 2) {
                NGBanner.setDim(!NGBanner.dim());
                adapter.notifyDataSetChanged();
            } else if (position == 5) {
                NGBanner.pick(this, NGBanner.REQUEST_PICK);
            } else if (position == 6) {
                NGBanner.remove(ownKey());
                BulletinFactory.of(this).createSuccessBulletin("Баннер убран").show();
            } else if (position == 7 && getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle("Удалить все баннеры");
                builder.setMessage("Все локальные баннеры будут удалены с этого устройства.");
                builder.setPositiveButton("Удалить", (dialog, which) -> {
                    NGBanner.clearAll();
                    BulletinFactory.of(this).createSuccessBulletin("Готово").show();
                });
                builder.setNegativeButton("Отмена", null);
                showDialog(builder.create());
            }
        });
        return fragmentView;
    }
    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        NGBanner.handleResult(this, requestCode, resultCode, data, ownKey(), () -> BulletinFactory.of(this).createSuccessBulletin("Баннер добавлен").show());
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
