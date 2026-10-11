/* Сгенерировано workflow NGram 6: тексты NGram на русском и английском. */
package com.radolyn.ayugram.ngsave;
import org.telegram.messenger.R;
public class NGTexts {
    public static String find(int id) {
        return "en".equals(NGStr.getLanguage()) ? findEn(id) : findRu(id);
    }
    private static String findRu(int id) {
        if (id == R.string.AyuGhostDisabled) {
            return "\u0420\u0435\u0436\u0438\u043c \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u0430 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d";
        }
        if (id == R.string.AyuGhostDontOnline) {
            return "\u041d\u0435 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c \u00ab\u043e\u043d\u043b\u0430\u0439\u043d\u00bb";
        }
        if (id == R.string.AyuGhostDontRead) {
            return "\u041d\u0435 \u0447\u0438\u0442\u0430\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.AyuGhostDontTyping) {
            return "\u041d\u0435 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c \u00ab\u043f\u0435\u0447\u0430\u0442\u0430\u0435\u0442\u00bb";
        }
        if (id == R.string.AyuGhostEnabled) {
            return "\u0420\u0435\u0436\u0438\u043c \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u0430 \u0432\u043a\u043b\u044e\u0447\u0451\u043d";
        }
        if (id == R.string.AyuGhostEssentialsHeader) {
            return "\u0420\u0435\u0436\u0438\u043c \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u0430";
        }
        if (id == R.string.AyuGhostInfo) {
            return "\u0420\u0435\u0436\u0438\u043c \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u0430 \u0432\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0441\u0440\u0430\u0437\u0443 \u0432\u0441\u0435 \u0447\u0435\u0442\u044b\u0440\u0435 \u043e\u043f\u0446\u0438\u0438 \u0432\u044b\u0448\u0435. \u041a\u0430\u0436\u0434\u0443\u044e \u043e\u043f\u0446\u0438\u044e \u043c\u043e\u0436\u043d\u043e \u043c\u0435\u043d\u044f\u0442\u044c \u043e\u0442\u0434\u0435\u043b\u044c\u043d\u043e.";
        }
        if (id == R.string.AyuGhostMarkReadAfterSend) {
            return "\u0427\u0438\u0442\u0430\u0442\u044c \u043f\u043e\u0441\u043b\u0435 \u043e\u0442\u0432\u0435\u0442\u0430";
        }
        if (id == R.string.AyuGhostOfflineAfterOnline) {
            return "\u0421\u0440\u0430\u0437\u0443 \u00ab\u043e\u0444\u043b\u0430\u0439\u043d\u00bb \u043f\u043e\u0441\u043b\u0435 \u00ab\u043e\u043d\u043b\u0430\u0439\u043d\u00bb";
        }
        if (id == R.string.AyuGhostReadUntil) {
            return "\u041f\u0440\u043e\u0447\u0438\u0442\u0430\u0442\u044c \u0434\u043e";
        }
        if (id == R.string.AyuGhostSettings) {
            return "Ghost mode";
        }
        if (id == R.string.AyuGhostToggle) {
            return "\u0420\u0435\u0436\u0438\u043c \u043f\u0440\u0438\u0437\u0440\u0430\u043a\u0430";
        }
        if (id == R.string.NGSaveChatsInfo) {
            return "\u0415\u0441\u043b\u0438 \u0441\u043e\u0431\u0435\u0441\u0435\u0434\u043d\u0438\u043a \u0443\u0434\u0430\u043b\u0438\u043b \u0432\u0435\u0441\u044c \u0447\u0430\u0442, \u0435\u0433\u043e \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0442\u043e\u0436\u0435 \u043e\u0441\u0442\u0430\u043d\u0443\u0442\u0441\u044f. \u0421\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u043d\u0435 \u0443\u0441\u043f\u0435\u043b\u0438 \u0434\u043e\u0439\u0442\u0438 \u0434\u043e \u044d\u0442\u043e\u0433\u043e \u0443\u0441\u0442\u0440\u043e\u0439\u0441\u0442\u0432\u0430, \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u043d\u0435\u043b\u044c\u0437\u044f.";
        }
        if (id == R.string.NGSaveClearAll) {
            return "\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c \u0432\u0441\u0451";
        }
        if (id == R.string.NGSaveClearEdits) {
            return "\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u044c \u0438\u0441\u0442\u043e\u0440\u0438\u044e \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439";
        }
        if (id == R.string.NGSaveClearMarks) {
            return "\u0417\u0430\u0431\u044b\u0442\u044c \u043f\u043e\u043c\u0435\u0442\u043a\u0438 \u00ab\u0443\u0434\u0430\u043b\u0435\u043d\u043e\u00bb";
        }
        if (id == R.string.NGSaveClearMessage) {
            return "\u042d\u0442\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435 \u043d\u0435\u043b\u044c\u0437\u044f \u043e\u0442\u043c\u0435\u043d\u0438\u0442\u044c.";
        }
        if (id == R.string.NGSaveClearTitle) {
            return "\u041e\u0447\u0438\u0441\u0442\u043a\u0430 \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0445 \u0434\u0430\u043d\u043d\u044b\u0445";
        }
        if (id == R.string.NGSaveCleared) {
            return "\u0413\u043e\u0442\u043e\u0432\u043e";
        }
        if (id == R.string.NGSaveContentInfo) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u044e\u0442\u0441\u044f \u0442\u043e\u043b\u044c\u043a\u043e \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u043e\u0442\u043c\u0435\u0447\u0435\u043d\u043d\u044b\u0445 \u0442\u0438\u043f\u043e\u0432. \u041c\u0435\u0434\u0438\u0430, \u043a\u043e\u0442\u043e\u0440\u043e\u0435 \u043d\u0435 \u0431\u044b\u043b\u043e \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043e \u0434\u043e \u0443\u0434\u0430\u043b\u0435\u043d\u0438\u044f, \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u0443\u0436\u0435 \u043d\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c.";
        }
        if (id == R.string.NGSaveDataInfo) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u043e\u0441\u0442\u0430\u044e\u0442\u0441\u044f \u0432 \u0447\u0430\u0442\u0435. \u041e\u0447\u0438\u0441\u0442\u043a\u0430 \u043f\u043e\u043c\u0435\u0442\u043e\u043a \u0443\u0431\u0438\u0440\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430\u0434\u043f\u0438\u0441\u044c \u00ab\u0443\u0434\u0430\u043b\u0435\u043d\u043e\u00bb, \u0441\u0430\u043c\u0438 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u043d\u0435 \u0443\u0434\u0430\u043b\u044f\u044e\u0442\u0441\u044f.";
        }
        if (id == R.string.NGSaveDeleted) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c \u0443\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.NGSaveDeletedInfo) {
            return "\u0415\u0441\u043b\u0438 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0443\u0434\u0430\u043b\u0438\u043b \u0441\u043e\u0431\u0435\u0441\u0435\u0434\u043d\u0438\u043a \u0438\u043b\u0438 \u0430\u0434\u043c\u0438\u043d\u0438\u0441\u0442\u0440\u0430\u0442\u043e\u0440, \u043e\u043d\u043e \u043e\u0441\u0442\u0430\u0451\u0442\u0441\u044f \u0432 \u0447\u0430\u0442\u0435 \u0441 \u043f\u043e\u043c\u0435\u0442\u043a\u043e\u0439 \u00ab\u0443\u0434\u0430\u043b\u0435\u043d\u043e\u00bb. \u0421\u0432\u043e\u0438 \u0443\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0442\u043e\u0436\u0435 \u0441\u043e\u0445\u0440\u0430\u043d\u044f\u044e\u0442\u0441\u044f, \u0435\u0441\u043b\u0438 \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0430 \u043e\u043f\u0446\u0438\u044f \u043d\u0438\u0436\u0435.";
        }
        if (id == R.string.NGSaveDeletedMarker) {
            return "\u0443\u0434\u0430\u043b\u0435\u043d\u043e";
        }
        if (id == R.string.NGSaveDisplayInfo) {
            return "\u041f\u043e\u043c\u0435\u0442\u043a\u0430 \u043f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0435\u0442\u0441\u044f \u0440\u044f\u0434\u043e\u043c \u0441\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0435\u043c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f.";
        }
        if (id == R.string.NGSaveEditEmpty) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0445 \u0432\u0435\u0440\u0441\u0438\u0439 \u043d\u0435\u0442.";
        }
        if (id == R.string.NGSaveEditHistory) {
            return "\u0418\u0441\u0442\u043e\u0440\u0438\u044f \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439";
        }
        if (id == R.string.NGSaveEditMediaChanged) {
            return "(\u0432\u043b\u043e\u0436\u0435\u043d\u0438\u0435 \u0431\u044b\u043b\u043e \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u043e)";
        }
        if (id == R.string.NGSaveEditNoText) {
            return "(\u0431\u0435\u0437 \u0442\u0435\u043a\u0441\u0442\u0430)";
        }
        if (id == R.string.NGSaveEdits) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c \u0438\u0441\u0442\u043e\u0440\u0438\u044e \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439";
        }
        if (id == R.string.NGSaveEditsInfo) {
            return "\u041f\u0440\u0435\u0436\u043d\u0438\u0435 \u0432\u0435\u0440\u0441\u0438\u0438 \u043e\u0442\u0440\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0445 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0439 \u0441\u043e\u0445\u0440\u0430\u043d\u044f\u044e\u0442\u0441\u044f. \u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u0438 \u0443\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u0439\u0442\u0435 \u0438\u0437\u043c\u0435\u043d\u0451\u043d\u043d\u043e\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0438 \u0432\u044b\u0431\u0435\u0440\u0438\u0442\u0435 \u00ab\u0418\u0441\u0442\u043e\u0440\u0438\u044f \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439\u00bb.";
        }
        if (id == R.string.NGSaveHeaderChats) {
            return "\u0413\u0434\u0435 \u0441\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c";
        }
        if (id == R.string.NGSaveHeaderContent) {
            return "\u0427\u0442\u043e \u0441\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c";
        }
        if (id == R.string.NGSaveHeaderData) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u0451\u043d\u043d\u044b\u0435 \u0434\u0430\u043d\u043d\u044b\u0435";
        }
        if (id == R.string.NGSaveHeaderDeleted) {
            return "\u0423\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.NGSaveHeaderDisplay) {
            return "\u0412\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434";
        }
        if (id == R.string.NGSaveHeaderEdits) {
            return "\u0418\u0441\u0442\u043e\u0440\u0438\u044f \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0439";
        }
        if (id == R.string.NGSaveInBots) {
            return "\u0411\u043e\u0442\u044b";
        }
        if (id == R.string.NGSaveInChannels) {
            return "\u041a\u0430\u043d\u0430\u043b\u044b";
        }
        if (id == R.string.NGSaveInGroups) {
            return "\u0413\u0440\u0443\u043f\u043f\u044b";
        }
        if (id == R.string.NGSaveInPrivate) {
            return "\u041b\u0438\u0447\u043d\u044b\u0435 \u0447\u0430\u0442\u044b";
        }
        if (id == R.string.NGSaveInSecret) {
            return "\u0421\u0435\u043a\u0440\u0435\u0442\u043d\u044b\u0435 \u0447\u0430\u0442\u044b";
        }
        if (id == R.string.NGSaveKFile) {
            return "\u0424\u0430\u0439\u043b\u044b \u0438 \u043c\u0443\u0437\u044b\u043a\u0430";
        }
        if (id == R.string.NGSaveKGif) {
            return "GIF";
        }
        if (id == R.string.NGSaveKOther) {
            return "\u041f\u0440\u043e\u0447\u0435\u0435 (\u043e\u043f\u0440\u043e\u0441\u044b, \u0433\u0435\u043e\u043f\u043e\u0437\u0438\u0446\u0438\u044f, \u043a\u043e\u043d\u0442\u0430\u043a\u0442\u044b)";
        }
        if (id == R.string.NGSaveKPhoto) {
            return "\u0424\u043e\u0442\u043e";
        }
        if (id == R.string.NGSaveKSticker) {
            return "\u0421\u0442\u0438\u043a\u0435\u0440\u044b";
        }
        if (id == R.string.NGSaveKText) {
            return "\u0422\u0435\u043a\u0441\u0442\u043e\u0432\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.NGSaveKVideo) {
            return "\u0412\u0438\u0434\u0435\u043e";
        }
        if (id == R.string.NGSaveKVoice) {
            return "\u0413\u043e\u043b\u043e\u0441\u043e\u0432\u044b\u0435 \u0438 \u043a\u0440\u0443\u0436\u043a\u0438";
        }
        if (id == R.string.NGSaveKeepCleared) {
            return "\u0427\u0430\u0442\u044b, \u043e\u0447\u0438\u0449\u0435\u043d\u043d\u044b\u0435 \u0441\u043e\u0431\u0435\u0441\u0435\u0434\u043d\u0438\u043a\u043e\u043c";
        }
        if (id == R.string.NGSaveMarkerBoth) {
            return "\u0417\u043d\u0430\u0447\u043e\u043a \u0438 \u0442\u0435\u043a\u0441\u0442";
        }
        if (id == R.string.NGSaveMarkerIcon) {
            return "\u0417\u043d\u0430\u0447\u043e\u043a";
        }
        if (id == R.string.NGSaveMarkerStyle) {
            return "\u0412\u0438\u0434 \u043f\u043e\u043c\u0435\u0442\u043a\u0438";
        }
        if (id == R.string.NGSaveMarkerText) {
            return "\u0422\u0435\u043a\u0441\u0442";
        }
        if (id == R.string.NGSaveOwnDeleted) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u044f\u0442\u044c \u0441\u0432\u043e\u0438 \u0443\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.NGSaveSettings) {
            return "Save deleted";
        }
        if (id == R.string.NGSaveShowMarker) {
            return "\u041f\u043e\u043c\u0435\u0447\u0430\u0442\u044c \u0443\u0434\u0430\u043b\u0451\u043d\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.NGramAccActive) {
            return "\u0410\u043a\u0442\u0438\u0432\u043d\u044b\u0439";
        }
        if (id == R.string.NGramAccDelete) {
            return "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u0430\u043a\u043a\u0430\u0443\u043d\u0442";
        }
        if (id == R.string.NGramAccDeleteMessage) {
            return "\u0412\u044b\u0439\u0442\u0438 \u0438\u0437 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0430 {name}? \u0427\u0442\u043e\u0431\u044b \u0432\u043e\u0439\u0442\u0438 \u0441\u043d\u043e\u0432\u0430, \u043f\u043e\u043d\u0430\u0434\u043e\u0431\u0438\u0442\u0441\u044f \u043a\u043e\u0434 \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f.";
        }
        if (id == R.string.NGramAccHeader) {
            return "\u041c\u043e\u0438 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u044b";
        }
        if (id == R.string.NGramAccInactive) {
            return "\u041d\u0435\u0430\u043a\u0442\u0438\u0432\u043d\u044b\u0439";
        }
        if (id == R.string.NGramAccInfo) {
            return "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043d\u0430 \u0430\u043a\u043a\u0430\u0443\u043d\u0442, \u0447\u0442\u043e\u0431\u044b \u043f\u0435\u0440\u0435\u0439\u0442\u0438 \u0432 \u043d\u0435\u0433\u043e. \u0414\u043e\u043b\u0433\u043e\u0435 \u043d\u0430\u0436\u0430\u0442\u0438\u0435: \u043f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c, \u0441\u043a\u0430\u0447\u0430\u0442\u044c \u0441\u0435\u0441\u0441\u0438\u044e \u0438\u043b\u0438 \u0443\u0434\u0430\u043b\u0438\u0442\u044c.";
        }
        if (id == R.string.NGramAccRename) {
            return "\u041f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c";
        }
        if (id == R.string.NGramAccRenameHint) {
            return "\u0412\u0438\u0434\u043d\u043e \u0442\u043e\u043b\u044c\u043a\u043e \u043d\u0430 \u044d\u0442\u043e\u043c \u0443\u0441\u0442\u0440\u043e\u0439\u0441\u0442\u0432\u0435";
        }
        if (id == R.string.NGramAccRenameTitle) {
            return "\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0430";
        }
        if (id == R.string.NGramCancel) {
            return "\u041e\u0442\u043c\u0435\u043d\u0430";
        }
        if (id == R.string.NGramContinue) {
            return "\u041f\u0440\u043e\u0434\u043e\u043b\u0436\u0438\u0442\u044c";
        }
        if (id == R.string.NGramExportAllSessions) {
            return "\u0421\u043a\u0430\u0447\u0430\u0442\u044c \u0432\u0441\u0435 \u0441\u0435\u0441\u0441\u0438\u0438";
        }
        if (id == R.string.NGramExportChoose) {
            return "\u0427\u0442\u043e \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c?";
        }
        if (id == R.string.NGramExportDone) {
            return "\u0421\u0435\u0441\u0441\u0438\u044f \u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0430: {path}";
        }
        if (id == R.string.NGramExportError) {
            return "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0441\u0435\u0441\u0441\u0438\u044e";
        }
        if (id == R.string.NGramExportSession) {
            return "\u0421\u043a\u0430\u0447\u0430\u0442\u044c \u0441\u0435\u0441\u0441\u0438\u044e";
        }
        if (id == R.string.NGramExportSessionFull) {
            return "\u0421\u0435\u0441\u0441\u0438\u044f + \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f";
        }
        if (id == R.string.NGramExportSessionOnly) {
            return "\u0422\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u0441\u0441\u0438\u044f";
        }
        if (id == R.string.NGramExportWarnMessage) {
            return "\u0424\u0430\u0439\u043b \u0441\u0435\u0441\u0441\u0438\u0438 \u0434\u0430\u0451\u0442 \u043f\u043e\u043b\u043d\u044b\u0439 \u0434\u043e\u0441\u0442\u0443\u043f \u043a \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0443. \u041d\u0435 \u043f\u0435\u0440\u0435\u0434\u0430\u0432\u0430\u0439\u0442\u0435 \u0435\u0433\u043e \u043d\u0438\u043a\u043e\u043c\u0443 \u0438 \u0445\u0440\u0430\u043d\u0438\u0442\u0435 \u0432 \u0431\u0435\u0437\u043e\u043f\u0430\u0441\u043d\u043e\u043c \u043c\u0435\u0441\u0442\u0435.";
        }
        if (id == R.string.NGramExportWarnTitle) {
            return "\u0411\u0435\u0440\u0435\u0433\u0438\u0442\u0435 \u0441\u0435\u0441\u0441\u0438\u044e";
        }
        if (id == R.string.NGramImportBad) {
            return "\u042d\u0442\u043e \u043d\u0435 \u0444\u0430\u0439\u043b \u0441\u0435\u0441\u0441\u0438\u0438 NGram";
        }
        if (id == R.string.NGramImportError) {
            return "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0438\u043c\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441\u0435\u0441\u0441\u0438\u044e";
        }
        if (id == R.string.NGramImportExists) {
            return "\u042d\u0442\u043e\u0442 \u0430\u043a\u043a\u0430\u0443\u043d\u0442 \u0443\u0436\u0435 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d";
        }
        if (id == R.string.NGramImportNoSlot) {
            return "\u041d\u0435\u0442 \u0441\u0432\u043e\u0431\u043e\u0434\u043d\u044b\u0445 \u0441\u043b\u043e\u0442\u043e\u0432";
        }
        if (id == R.string.NGramImportRestart) {
            return "\u0421\u0435\u0441\u0441\u0438\u044f \u0438\u043c\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u0430. \u041f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u0435 \u0431\u0443\u0434\u0435\u0442 \u043f\u0435\u0440\u0435\u0437\u0430\u043f\u0443\u0449\u0435\u043d\u043e.";
        }
        if (id == R.string.NGramImportSession) {
            return "\u0418\u043c\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441\u0435\u0441\u0441\u0438\u044e";
        }
        if (id == R.string.NGramLangTitle) {
            return "\u042f\u0437\u044b\u043a NGram";
        }
        if (id == R.string.NGramRestartButton) {
            return "\u041f\u0435\u0440\u0435\u0437\u0430\u043f\u0443\u0441\u0442\u0438\u0442\u044c";
        }
        if (id == R.string.NGramSave) {
            return "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c";
        }
        if (id == R.string.NGramSettings) {
            return "NGram";
        }
        if (id == R.string.NGramSettingsInfo) {
            return "Settings NGram";
        }
        return null;
    }
    private static String findEn(int id) {
        if (id == R.string.AyuGhostDisabled) {
            return "Ghost mode turned off";
        }
        if (id == R.string.AyuGhostDontOnline) {
            return "Don't send online";
        }
        if (id == R.string.AyuGhostDontRead) {
            return "Don't read messages";
        }
        if (id == R.string.AyuGhostDontTyping) {
            return "Don't send typing";
        }
        if (id == R.string.AyuGhostEnabled) {
            return "Ghost mode turned on";
        }
        if (id == R.string.AyuGhostEssentialsHeader) {
            return "Ghost essentials";
        }
        if (id == R.string.AyuGhostInfo) {
            return "Ghost mode turns on all four options above at once. Each option can also be changed separately.";
        }
        if (id == R.string.AyuGhostMarkReadAfterSend) {
            return "Send read status after reply";
        }
        if (id == R.string.AyuGhostOfflineAfterOnline) {
            return "Immediate offline after online";
        }
        if (id == R.string.AyuGhostReadUntil) {
            return "Read until";
        }
        if (id == R.string.AyuGhostSettings) {
            return "Ghost mode";
        }
        if (id == R.string.AyuGhostToggle) {
            return "Ghost mode";
        }
        if (id == R.string.NGSaveChatsInfo) {
            return "If the other person deletes the whole chat, its messages are kept too. Messages that never reached this device cannot be recovered.";
        }
        if (id == R.string.NGSaveClearAll) {
            return "Clear everything";
        }
        if (id == R.string.NGSaveClearEdits) {
            return "Clear edit history";
        }
        if (id == R.string.NGSaveClearMarks) {
            return "Forget deleted markers";
        }
        if (id == R.string.NGSaveClearMessage) {
            return "This cannot be undone.";
        }
        if (id == R.string.NGSaveClearTitle) {
            return "Clear saved data";
        }
        if (id == R.string.NGSaveCleared) {
            return "Done";
        }
        if (id == R.string.NGSaveContentInfo) {
            return "Only messages of the checked types are kept. Media that was not downloaded before the deletion can no longer be loaded from the server.";
        }
        if (id == R.string.NGSaveDataInfo) {
            return "Kept messages stay in the chat. Clearing markers only removes the deleted label and does not delete the messages.";
        }
        if (id == R.string.NGSaveDeleted) {
            return "Keep deleted messages";
        }
        if (id == R.string.NGSaveDeletedInfo) {
            return "When someone else deletes a message, it stays in the chat and is marked as deleted. Your own deleted messages are kept too if the option below is on.";
        }
        if (id == R.string.NGSaveDeletedMarker) {
            return "deleted";
        }
        if (id == R.string.NGSaveDisplayInfo) {
            return "The marker is shown next to the time of the message.";
        }
        if (id == R.string.NGSaveEditEmpty) {
            return "No saved versions.";
        }
        if (id == R.string.NGSaveEditHistory) {
            return "Edit history";
        }
        if (id == R.string.NGSaveEditMediaChanged) {
            return "(the attachment was changed)";
        }
        if (id == R.string.NGSaveEditNoText) {
            return "(no text)";
        }
        if (id == R.string.NGSaveEdits) {
            return "Save edit history";
        }
        if (id == R.string.NGSaveEditsInfo) {
            return "Previous versions of edited messages are saved. Long-press an edited message and choose Edit history.";
        }
        if (id == R.string.NGSaveHeaderChats) {
            return "Where to keep";
        }
        if (id == R.string.NGSaveHeaderContent) {
            return "What to keep";
        }
        if (id == R.string.NGSaveHeaderData) {
            return "Saved data";
        }
        if (id == R.string.NGSaveHeaderDeleted) {
            return "Deleted messages";
        }
        if (id == R.string.NGSaveHeaderDisplay) {
            return "Appearance";
        }
        if (id == R.string.NGSaveHeaderEdits) {
            return "Edit history";
        }
        if (id == R.string.NGSaveInBots) {
            return "Bots";
        }
        if (id == R.string.NGSaveInChannels) {
            return "Channels";
        }
        if (id == R.string.NGSaveInGroups) {
            return "Groups";
        }
        if (id == R.string.NGSaveInPrivate) {
            return "Private chats";
        }
        if (id == R.string.NGSaveInSecret) {
            return "Secret chats";
        }
        if (id == R.string.NGSaveKFile) {
            return "Files and music";
        }
        if (id == R.string.NGSaveKGif) {
            return "GIFs";
        }
        if (id == R.string.NGSaveKOther) {
            return "Other (polls, locations, contacts)";
        }
        if (id == R.string.NGSaveKPhoto) {
            return "Photos";
        }
        if (id == R.string.NGSaveKSticker) {
            return "Stickers";
        }
        if (id == R.string.NGSaveKText) {
            return "Text messages";
        }
        if (id == R.string.NGSaveKVideo) {
            return "Videos";
        }
        if (id == R.string.NGSaveKVoice) {
            return "Voice and round messages";
        }
        if (id == R.string.NGSaveKeepCleared) {
            return "Chats cleared by the other side";
        }
        if (id == R.string.NGSaveMarkerBoth) {
            return "Icon and text";
        }
        if (id == R.string.NGSaveMarkerIcon) {
            return "Icon";
        }
        if (id == R.string.NGSaveMarkerStyle) {
            return "Marker style";
        }
        if (id == R.string.NGSaveMarkerText) {
            return "Text";
        }
        if (id == R.string.NGSaveOwnDeleted) {
            return "Keep your own deleted";
        }
        if (id == R.string.NGSaveSettings) {
            return "Save deleted";
        }
        if (id == R.string.NGSaveShowMarker) {
            return "Mark deleted messages";
        }
        if (id == R.string.NGramAccActive) {
            return "Active";
        }
        if (id == R.string.NGramAccDelete) {
            return "Delete account";
        }
        if (id == R.string.NGramAccDeleteMessage) {
            return "Log out of {name}? You will need a confirmation code to log in again.";
        }
        if (id == R.string.NGramAccHeader) {
            return "My accounts";
        }
        if (id == R.string.NGramAccInactive) {
            return "Inactive";
        }
        if (id == R.string.NGramAccInfo) {
            return "Tap an account to switch to it. Long tap opens rename, session download and delete.";
        }
        if (id == R.string.NGramAccRename) {
            return "Rename";
        }
        if (id == R.string.NGramAccRenameHint) {
            return "Shown only on this device";
        }
        if (id == R.string.NGramAccRenameTitle) {
            return "Account name";
        }
        if (id == R.string.NGramCancel) {
            return "Cancel";
        }
        if (id == R.string.NGramContinue) {
            return "Continue";
        }
        if (id == R.string.NGramExportAllSessions) {
            return "Download all sessions";
        }
        if (id == R.string.NGramExportChoose) {
            return "What to save?";
        }
        if (id == R.string.NGramExportDone) {
            return "Session saved: {path}";
        }
        if (id == R.string.NGramExportError) {
            return "Could not save the session";
        }
        if (id == R.string.NGramExportSession) {
            return "Download session";
        }
        if (id == R.string.NGramExportSessionFull) {
            return "Session + messages";
        }
        if (id == R.string.NGramExportSessionOnly) {
            return "Session only";
        }
        if (id == R.string.NGramExportWarnMessage) {
            return "A session file gives full access to the account. Do not send it to anyone and store it in a safe place.";
        }
        if (id == R.string.NGramExportWarnTitle) {
            return "Keep the session safe";
        }
        if (id == R.string.NGramImportBad) {
            return "This is not an NGram session file";
        }
        if (id == R.string.NGramImportError) {
            return "Could not import the session";
        }
        if (id == R.string.NGramImportExists) {
            return "This account is already added";
        }
        if (id == R.string.NGramImportNoSlot) {
            return "No free slots";
        }
        if (id == R.string.NGramImportRestart) {
            return "Session imported. The app will restart to apply it.";
        }
        if (id == R.string.NGramImportSession) {
            return "Import session";
        }
        if (id == R.string.NGramLangTitle) {
            return "NGram language";
        }
        if (id == R.string.NGramRestartButton) {
            return "Restart";
        }
        if (id == R.string.NGramSave) {
            return "Save";
        }
        if (id == R.string.NGramSettings) {
            return "NGram";
        }
        if (id == R.string.NGramSettingsInfo) {
            return "Settings NGram";
        }
        return null;
    }
}
