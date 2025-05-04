package meteordevelopment.meteorclient.systems.hud;

import com.mojang.brigadier.suggestion.Suggestion;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class HudHighlightWorld extends HudElement
{

    private static final Logger LOGGER = LoggerFactory.getLogger("com.example.addon.hud");
    public static final HudElementInfo<HudHighlightWorld> INFO = new HudElementInfo<>(Hud.GROUP, "Joueurs dans le monde", "Affiche le nom des joueurs présents dans ton monde", HudHighlightWorld::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgScale = settings.createGroup("UI");

    private final Setting<String> uiText = sgGeneral.add(new StringSetting.Builder()
        .name("Texte ui")
        .description("Le texte sur le menu")
        .defaultValue("Joueurs monde :")
        .build()
    );

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("La commande pour détecter les joueurs.")
        .defaultValue("/grades")
        .build()
    );

    private final Setting<Integer> interval = sgGeneral.add(new IntSetting.Builder()
        .name("interval")
        .description("Intervalle (en ticks) entre chaque vérification.")
        .defaultValue(200)
        .min(0)
        .sliderMax(1000)
        .build()
    );

    private final Setting<Integer> limit = sgScale.add(new IntSetting.Builder()
        .name("limit")
        .description("Nombre max de joueurs affichés.")
        .defaultValue(10)
        .min(1)
        .sliderRange(1, 20)
        .build()
    );

    private final Setting<Boolean> shadow = sgScale.add(new BoolSetting.Builder()
        .name("shadow")
        .description("Affiche une ombre derrière le texte.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> primaryColor = sgScale.add(new ColorSetting.Builder()
        .name("primary-color")
        .description("Couleur principale.")
        .defaultValue(new SettingColor())
        .build()
    );

    private final Setting<SettingColor> secondaryColor = sgScale.add(new ColorSetting.Builder()
        .name("secondary-color")
        .description("Couleur secondaire.")
        .defaultValue(new SettingColor(175, 175, 175))
        .build()
    );

    private final List<String> messageCache = new ArrayList<>();

    private final List<Integer> completionIDs = new ArrayList<>();
    private List<String> completionPlayerCache = new ArrayList<>();

    public HudHighlightWorld() {
        super(INFO);
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

    private void doThings()
    {
        if (mc.getNetworkHandler() == null) {
            LOGGER.error("NetworkHandler est null ! Impossible d'envoyer le paquet.");
            return;
        }

        int id = random.nextInt(200);
        completionIDs.add(id);
        mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(id, command.get() + " "));

        timer = 0;
        messageCache.clear();
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

    @Override
    public void tick(HudRenderer renderer) {
        timer++;

        if (timer > interval.get())
        {
            doThings();
        }
        names = completionPlayerCache;


        double width = renderer.textWidth(uiText.get(), shadow.get(), 1);
        double height = renderer.textHeight(shadow.get(), 1);

        if(names != null)
        {
            for (String name : names) {
                width = Math.max(width, renderer.textWidth(name, shadow.get(), 1));
                height += renderer.textHeight(shadow.get(), 1) + 2;
            }
        }

        setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        double y = this.y;
        renderer.text(uiText.get(), x, y, secondaryColor.get(), shadow.get(), 1);
        y += renderer.textHeight(shadow.get(), 1) + 2;
        if(names != null)
        {
            for (String name : names) {
                renderer.text(name, x, y, primaryColor.get(), shadow.get(), 1);
                y += renderer.textHeight(shadow.get(), 1) + 2;
            }
        }
    }
}
