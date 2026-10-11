/*
 * NGram: Accounts section. Switch accounts, rename them locally, delete them,
 * export and import sessions (.ngsess).
 */
package com.radolyn.ayugram.ui.preferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.radolyn.ayugram.ngsave.NGSession;
import com.radolyn.ayugram.ngsave.NGStr;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LaunchActivity;

import java.util.ArrayList;
import java.util.Collections;

public class NGramAccountsActivity extends BaseFragment {
    private static final int REQ_IMPORT = 7762;
    private static final int MENU_IMPORT = 1;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ACCOUNT = 1;
    private static final int TYPE_INFO = 2;

    private RecyclerListView listView;
    private ListAdapter adapter;
    private LinearLayout bottomBar;
    private final ArrayList<Integer> accounts = new ArrayList<>();

    private static SharedPreferences ngPrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences("ngram", Context.MODE_PRIVATE);
    }

    private static SharedPreferences namePrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences("ngram_accounts", Context.MODE_PRIVATE);
    }

    private void loadAccounts() {
        accounts.clear();
        for (int a = 0; a < UserConfig.MAX_ACCOUNT_COUNT; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                accounts.add(a);
            }
        }
        Collections.sort(accounts, (o1, o2) -> Long.compare((long) UserConfig.getInstance(o1).loginTime, (long) UserConfig.getInstance(o2).loginTime));
    }

    private void reload() {
        loadAccounts();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onFragmentCreate() {
        loadAccounts();
        return super.onFragmentCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle("Accounts");
        ActionBarMenu menu = actionBar.createMenu();
        ActionBarMenuItem other = menu.addItem(0, R.drawable.ic_ab_other);
        other.addSubItem(MENU_IMPORT, R.drawable.msg_download, NGStr.get(R.string.NGramImportSession));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                } else if (id == MENU_IMPORT) {
                    startImport();
                }
            }
        });

        FrameLayout frameLayout = new FrameLayout(context);
        fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(adapter = new ListAdapter());
        listView.setPadding(0, 0, 0, AndroidUtilities.dp(130));
        listView.setClipToPadding(false);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        listView.setOnItemClickListener((view, position) -> {
            Integer account = accountAt(position);
            if (account != null && account != UserConfig.selectedAccount && LaunchActivity.instance != null) {
                LaunchActivity.instance.switchToAccount(account, true);
            }
        });
        listView.setOnItemLongClickListener((view, position) -> {
            Integer account = accountAt(position);
            if (account != null) {
                showActions(account);
                return true;
            }
            return false;
        });

        bottomBar = new LinearLayout(context);
        bottomBar.setOrientation(LinearLayout.VERTICAL);
        bottomBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        bottomBar.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10));
        bottomBar.addView(makeButton(context, NGStr.get(R.string.NGramExportAllSessions), v -> exportAll()),
                LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 46, 0, 0, 0, 8));
        bottomBar.addView(makeButton(context, NGStr.get(R.string.NGramImportSession), v -> startImport()),
                LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, 46, 0, 0, 0, 0));
        frameLayout.addView(bottomBar, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM));

        return fragmentView;
    }

    private TextView makeButton(Context context, String text, View.OnClickListener listener) {
        TextView button = new TextView(context);
        button.setGravity(Gravity.CENTER);
        button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        button.setTypeface(AndroidUtilities.bold());
        button.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        button.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8),
                Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        button.setText(text);
        button.setOnClickListener(listener);
        return button;
    }

    @Override
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override
    public void onInsets(int left, int top, int right, int bottom) {
        if (bottomBar != null) {
            bottomBar.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(10), AndroidUtilities.dp(16), AndroidUtilities.dp(10) + bottom);
        }
        if (listView != null) {
            listView.setPadding(0, 0, 0, AndroidUtilities.dp(130) + bottom);
        }
    }

    private Integer accountAt(int position) {
        int index = position - 1;
        if (index >= 0 && index < accounts.size()) {
            return accounts.get(index);
        }
        return null;
    }

    private String displayName(int account) {
        UserConfig config = UserConfig.getInstance(account);
        String custom = namePrefs().getString("name_" + config.getClientUserId(), "");
        if (!TextUtils.isEmpty(custom)) {
            return custom;
        }
        TLRPC.User user = config.getCurrentUser();
        return user != null ? UserObject.getUserName(user) : "ID " + config.getClientUserId();
    }

    private void showActions(final int account) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(displayName(account));
        CharSequence[] items = new CharSequence[]{
                NGStr.get(R.string.NGramAccRename),
                NGStr.get(R.string.NGramExportSession),
                NGStr.get(R.string.NGramAccDelete)
        };
        builder.setItems(items, (dialog, which) -> {
            if (which == 0) {
                showRename(account);
            } else if (which == 1) {
                askExport(new int[]{account});
            } else if (which == 2) {
                confirmDelete(account);
            }
        });
        showDialog(builder.create());
    }

    private void showRename(final int account) {
        if (getParentActivity() == null) {
            return;
        }
        final long userId = UserConfig.getInstance(account).getClientUserId();
        final EditText input = new EditText(getParentActivity());
        input.setSingleLine(true);
        input.setText(namePrefs().getString("name_" + userId, ""));
        input.setSelection(input.getText().length());
        input.setHint(NGStr.get(R.string.NGramAccRenameHint));
        input.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        input.setHintTextColor(Theme.getColor(Theme.key_dialogTextHint));
        FrameLayout box = new FrameLayout(getParentActivity());
        box.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(8), AndroidUtilities.dp(24), 0);
        box.addView(input, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(NGStr.get(R.string.NGramAccRenameTitle));
        builder.setView(box);
        builder.setPositiveButton(NGStr.get(R.string.NGramSave), (dialog, which) -> {
            String value = input.getText().toString().trim();
            SharedPreferences.Editor editor = namePrefs().edit();
            if (value.isEmpty()) {
                editor.remove("name_" + userId);
            } else {
                editor.putString("name_" + userId, value);
            }
            editor.apply();
            reload();
        });
        builder.setNegativeButton(NGStr.get(R.string.NGramCancel), null);
        showDialog(builder.create());
    }

    private void confirmDelete(final int account) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(NGStr.get(R.string.NGramAccDelete));
        builder.setMessage(NGStr.get(R.string.NGramAccDeleteMessage).replace("{name}", displayName(account)));
        builder.setPositiveButton(NGStr.get(R.string.NGramAccDelete), (dialog, which) -> {
            MessagesController.getInstance(account).performLogout(1);
            AndroidUtilities.runOnUIThread(this::reload, 700);
        });
        builder.setNegativeButton(NGStr.get(R.string.NGramCancel), null);
        showDialog(builder.create());
    }

    private void exportAll() {
        if (accounts.isEmpty()) {
            return;
        }
        int[] all = new int[accounts.size()];
        for (int i = 0; i < all.length; i++) {
            all[i] = accounts.get(i);
        }
        askExport(all);
    }

    private void askExport(final int[] targets) {
        if (getParentActivity() == null) {
            return;
        }
        if (ngPrefs().getBoolean("ngram_session_warned", false)) {
            chooseMode(targets);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(NGStr.get(R.string.NGramExportWarnTitle));
        builder.setMessage(NGStr.get(R.string.NGramExportWarnMessage));
        builder.setPositiveButton(NGStr.get(R.string.NGramContinue), (dialog, which) -> {
            ngPrefs().edit().putBoolean("ngram_session_warned", true).apply();
            chooseMode(targets);
        });
        builder.setNegativeButton(NGStr.get(R.string.NGramCancel), null);
        showDialog(builder.create());
    }

    private void chooseMode(final int[] targets) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(NGStr.get(R.string.NGramExportChoose));
        builder.setItems(new CharSequence[]{
                NGStr.get(R.string.NGramExportSessionOnly),
                NGStr.get(R.string.NGramExportSessionFull)
        }, (dialog, which) -> startExport(targets, which == 1));
        showDialog(builder.create());
    }

    private AlertDialog showProgress() {
        AlertDialog progress = new AlertDialog(getParentActivity(), AlertDialog.ALERT_TYPE_SPINNER);
        progress.setCanCancel(false);
        progress.show();
        return progress;
    }

    private void startExport(final int[] targets, final boolean withMessages) {
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog progress = showProgress();
        Utilities.globalQueue.postRunnable(() -> {
            final ArrayList<String> paths = new ArrayList<>();
            boolean failed = false;
            for (int account : targets) {
                try {
                    paths.add(NGSession.export(ApplicationLoader.applicationContext, account, withMessages));
                } catch (NGSession.SessionException e) {
                    FileLog.e(e);
                    failed = true;
                    break;
                }
            }
            final boolean error = failed;
            AndroidUtilities.runOnUIThread(() -> {
                try {
                    progress.dismiss();
                } catch (Exception ignore) {
                }
                if (error) {
                    BulletinFactory.of(this).createErrorBulletin(NGStr.get(R.string.NGramExportError)).show();
                } else {
                    String shown = paths.size() == 1 ? paths.get(0) : "Download/NGram (" + paths.size() + ")";
                    BulletinFactory.of(this).createSuccessBulletin(NGStr.get(R.string.NGramExportDone).replace("{path}", shown)).show();
                }
            });
        });
    }

    private void startImport() {
        if (getParentActivity() == null) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, REQ_IMPORT);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_IMPORT) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                doImport(data.getData());
            }
            return;
        }
        super.onActivityResultFragment(requestCode, resultCode, data);
    }

    private void doImport(final Uri uri) {
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog progress = showProgress();
        Utilities.globalQueue.postRunnable(() -> {
            int errorCode = 0;
            try {
                NGSession.importSession(ApplicationLoader.applicationContext, uri);
            } catch (NGSession.SessionException e) {
                FileLog.e(e);
                errorCode = e.code;
            }
            final int code = errorCode;
            AndroidUtilities.runOnUIThread(() -> {
                try {
                    progress.dismiss();
                } catch (Exception ignore) {
                }
                if (code == 0) {
                    showRestart();
                } else {
                    int text = code == NGSession.ERR_NO_SLOT ? R.string.NGramImportNoSlot
                            : code == NGSession.ERR_BAD_FILE ? R.string.NGramImportBad
                            : code == NGSession.ERR_EXISTS ? R.string.NGramImportExists
                            : R.string.NGramImportError;
                    BulletinFactory.of(this).createErrorBulletin(NGStr.get(text)).show();
                }
            });
        });
    }

    private void showRestart() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(NGStr.get(R.string.NGramImportSession));
        builder.setMessage(NGStr.get(R.string.NGramImportRestart));
        builder.setPositiveButton(NGStr.get(R.string.NGramRestartButton), (dialog, which) -> NGSession.restartApp(ApplicationLoader.applicationContext));
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        showDialog(dialog);
    }

    private class AccountRowCell extends FrameLayout {
        private final BackupImageView avatarView;
        private final SimpleTextView nameView;
        private final SimpleTextView statusView;
        private final AvatarDrawable avatarDrawable = new AvatarDrawable();
        private boolean divider;

        AccountRowCell(Context context) {
            super(context);
            setWillNotDraw(false);
            avatarView = new BackupImageView(context);
            avatarView.setRoundRadius(AndroidUtilities.dp(22));
            addView(avatarView, LayoutHelper.createFrame(44, 44, Gravity.LEFT | Gravity.CENTER_VERTICAL, 16, 0, 0, 0));
            nameView = new SimpleTextView(context);
            nameView.setTextSize(16);
            nameView.setTypeface(AndroidUtilities.bold());
            nameView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            nameView.setGravity(Gravity.LEFT);
            addView(nameView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 22, Gravity.LEFT | Gravity.TOP, 72, 11, 16, 0));
            statusView = new SimpleTextView(context);
            statusView.setTextSize(13);
            statusView.setGravity(Gravity.LEFT);
            addView(statusView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 18, Gravity.LEFT | Gravity.TOP, 72, 35, 16, 0));
        }

        void bind(int account, boolean needDivider) {
            divider = needDivider;
            TLRPC.User user = UserConfig.getInstance(account).getCurrentUser();
            avatarDrawable.setInfo(account, user);
            avatarView.getImageReceiver().setCurrentAccount(account);
            avatarView.setForUserOrChat(user, avatarDrawable);
            nameView.setText(displayName(account));
            boolean active = account == UserConfig.selectedAccount;
            statusView.setText(NGStr.get(active ? R.string.NGramAccActive : R.string.NGramAccInactive));
            statusView.setTextColor(Theme.getColor(active ? Theme.key_windowBackgroundWhiteGreenText : Theme.key_windowBackgroundWhiteGrayText2));
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64), MeasureSpec.EXACTLY));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (divider) {
                canvas.drawLine(AndroidUtilities.dp(72), getHeight() - 1, getWidth(), getHeight() - 1, Theme.dividerPaint);
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_HEADER) {
                view = new HeaderCell(getContext());
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == TYPE_ACCOUNT) {
                view = new AccountRowCell(getContext());
            } else {
                view = new TextInfoPrivacyCell(getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int type = holder.getItemViewType();
            if (type == TYPE_HEADER) {
                ((HeaderCell) holder.itemView).setText(NGStr.get(R.string.NGramAccHeader));
            } else if (type == TYPE_ACCOUNT) {
                int index = position - 1;
                ((AccountRowCell) holder.itemView).bind(accounts.get(index), index < accounts.size() - 1);
            } else {
                ((TextInfoPrivacyCell) holder.itemView).setText(NGStr.get(R.string.NGramAccInfo));
            }
        }

        @Override
        public int getItemCount() {
            return accounts.size() + 2;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == TYPE_ACCOUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            return position == getItemCount() - 1 ? TYPE_INFO : TYPE_ACCOUNT;
        }
    }
}
