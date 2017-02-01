package xyz.klinker.messenger.shared.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.klinker.messenger.shared.data.model.Conversation;

public class DynamicShortcutUtils {

    private Context context;
    private ShortcutManager manager;

    @SuppressWarnings("WrongConstant")
    public DynamicShortcutUtils(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            manager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
        }
    }

    public void buildDynamicShortcuts(List<Conversation> conversations) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && manager != null) {
            if (conversations.size() > 3) {
                conversations = conversations.subList(0, 3);
            }

            List<ShortcutInfo> infos = new ArrayList<>();

            for (Conversation conversation : conversations) {
                Intent messenger = ActivityUtils.buildForComponent(ActivityUtils.MESSENGER_ACTIVITY);
                messenger.setAction(Intent.ACTION_VIEW);
                messenger.setData(Uri.parse("https://messenger.klinkerapps.com/" + conversation.id));

                Set<String> category = new HashSet<>();
                category.add("android.shortcut.conversation");

                String id = conversation.title == null || conversation.title.isEmpty() ?
                        conversation.id + "" : conversation.title;

                ShortcutInfo info = new ShortcutInfo.Builder(context, id)
                        .setIntent(messenger)
                        .setRank(infos.size())
                        .setShortLabel(conversation.title.isEmpty() ? "No title" : conversation.title)
                        .setCategories(category)
                        .setIcon(getIcon(conversation.imageUri, conversation.colors.color))
                        .build();

                infos.add(info);
            }

            manager.setDynamicShortcuts(infos);
        }
    }

    private Icon getIcon(String uri, int backgroundColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Bitmap image = ImageUtils.clipToCircle(ImageUtils.getBitmap(context, uri));

            Bitmap color = Bitmap.createBitmap(DensityUtil.toDp(context, 48), DensityUtil.toDp(context, 48), Bitmap.Config.ARGB_8888);
            color.eraseColor(backgroundColor);
            color = ImageUtils.clipToCircle(color);

            return image == null ? Icon.createWithBitmap(color) : Icon.createWithBitmap(image);
        } else {
            return null;
        }
    }
}