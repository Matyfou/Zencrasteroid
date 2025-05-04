/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.zencraft;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friend;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import com.mojang.brigadier.suggestion.Suggestion;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.List;

public class HighlightWorld extends Module
{

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("The completion command to detect player names.")
        .defaultValue("/grades")
        .build()
    );

    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("interval")
        .description("Interval (en tick) du check de la commande")
        .defaultValue(200)
        .min(0)
        .sliderMax(1000)
        .build()
    );

    public HighlightWorld() {
        super(Categories.Zencraft, "Highlight", "Afficher sur le menu TAB les joueurs dans ton monde");
    }

    private final List<String> messageCache = new ArrayList<>();

    private final List<Integer> completionIDs = new ArrayList<>();
    private List<String> completionPlayerCache = new ArrayList<>();


    @Override
    public void onActivate() {
        completionIDs.clear();
    }

    @EventHandler
    private void onPacket(PacketEvent.Receive event) {
        if (event.packet instanceof CommandSuggestionsS2CPacket packet) {
            if (completionIDs.contains(packet.id())) {
                var lastUsernames = completionPlayerCache.stream().toList();

                completionPlayerCache = packet.getSuggestions().getList().stream()
                    .map(Suggestion::getText)
                    .toList();

                if (lastUsernames.isEmpty()) return;

                for (String playerName : completionPlayerCache) {
                    if (Objects.equals(playerName, mc.player.getName().getString())) continue;
                    if (playerName.contains(" ")) continue;
                    if (playerName.length() < 3 || playerName.length() > 16) continue;

                    //info("Player : " + playerName);
                }

                completionIDs.remove(Integer.valueOf(packet.id()));
                event.cancel();
            }
        }
    }
    private final Random random = new Random();

    static List<String> names;
    int timer = 0;
    @EventHandler
    private void onTick(TickEvent.Post event) {
        timer++;

        if (timer > interval.get())
        {
            doThings();
        }
        names = completionPlayerCache;
    }

    private void doThings()
    {
        int id = random.nextInt(200);
        completionIDs.add(id);
        mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(id, command.get() + " "));

        timer = 0;
        messageCache.clear();
        //info("TIMER");
    }


    public static List<String> getNames()
    {
        return names;
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event)
    {
        doThings();
    }

        /*
    public Text getPlayerName(PlayerListEntry playerListEntry) {
        info("getPlayerName");
        Text name;
        Color color = null;

        name = playerListEntry.getDisplayName();
        if (name == null) name = Text.literal(playerListEntry.getProfile().getName());

        for (String playerName : completionPlayerCache)
        {
            info(name);
            if (String.valueOf(name) == playerName)
            {
                color = highlightColor.get();
            }
        }


        if (color != null) {
            String nameString = name.getString();

            for (Formatting format : Formatting.values()) {
                if (format.isColor()) nameString = nameString.replace(format.toString(), "");
            }

            name = Text.literal(nameString).setStyle(name.getStyle().withColor(TextColor.fromRgb(color.getPacked())));
        }

        return name;
    }

         */
}
