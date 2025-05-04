package meteordevelopment.meteorclient.systems.modules.zencraft;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.starscript.utils.StarscriptError;

import java.util.*;

public class MessageHider extends Module
{

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messagesPrefixs = sgGeneral.add(new StringListSetting.Builder()
        .name("Prefixes")
        .description("Prefixes des messages a enlever")
        .defaultValue(List.of("[ZenNerf]", "Felicitation"))
        .build()
    );

    public MessageHider() {
        super(Categories.Zencraft, "message-hider", "Ne pas afficher des messages qui commence par...");
    }
    @EventHandler
    private void onMessageRecieve(ReceiveMessageEvent event) {
        String msg = event.getMessage().getString();
        for (String cmd : messagesPrefixs.get()) {
            if (msg.startsWith(cmd)) {
                try {
                    event.setCancelled(true);
                } catch (StarscriptError e) {
                    info("ERROR with message-hider !");
                }
                return;
            }
        }
    }
}
