package meteordevelopment.meteorclient.systems.modules.zencraft;

import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.stream.Collectors;

public class TABColorBanner extends Module
{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<SettingColor> nameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("Nom des joueurs")
        .description("Couleur des noms dans le menu TAB.")
        .defaultValue(new SettingColor(255, 0, 0))
        .build()
    );

    public TABColorBanner() {
        super(Categories.Zencraft, "tab-color-banner", "Les pigeons n'on aucun droit en plus");
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        Text name;
        TextColor color = null;

        Text displayName = playerListEntry.getDisplayName();
        if (displayName != null) {
            color = displayName.getStyle().getColor();
        }

        name = playerListEntry.getDisplayName();

        if (color != null)
        {
            info(String.valueOf(color));
            String nameString = name.getString();

            for (Formatting format : Formatting.values()) {
                if (format.isColor()) nameString = nameString.replace(format.toString(), "");
            }

            name = Text.literal(nameString).setStyle(name.getStyle().withColor(nameColor.get().toTextColor()));
        }


        return name;
    }
}
