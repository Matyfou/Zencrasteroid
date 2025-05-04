package meteordevelopment.meteorclient.systems.modules.zencraft;

/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3d;

import java.util.Set;

public class SkinBlinker extends Module
{

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBodyParts = settings.createGroup("Body Parts");

    public final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("Less = more blinking")
        .decimalPlaces(3)
        .defaultValue(3.5)
        .min(0)
        .sliderMax(10)
        .build()
    );

    public final Setting<Boolean> blinking = sgGeneral.add(new BoolSetting.Builder()
        .name("blinking")
        .description("If disable, just disable the skin layer when activated")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> invert = sgGeneral.add(new BoolSetting.Builder()
        .name("invert")
        .description("invert skin state")
        .defaultValue(true)
        .build()
    );


    public final Setting<Boolean> cape = sgBodyParts.add(new BoolSetting.Builder()
        .name("Cape")
        .description(" ")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> head = sgBodyParts.add(new BoolSetting.Builder()
        .name("Head")
        .description(" ")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> body = sgBodyParts.add(new BoolSetting.Builder()
        .name("Body")
        .description(" ")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> leftArm = sgBodyParts.add(new BoolSetting.Builder()
        .name("Left Arm")
        .description(" ")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> rightArm = sgBodyParts.add(new BoolSetting.Builder()
        .name("Right Arm")
        .description(" ")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> leftLeg = sgBodyParts.add(new BoolSetting.Builder()
        .name("Left Leg")
        .description(" ")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> rightLeg = sgBodyParts.add(new BoolSetting.Builder()
        .name("Right Leg")
        .description(" ")
        .defaultValue(true)
        .build()
    );


    public SkinBlinker() {
        super(Categories.Zencraft, "skin-blinker", "Toggle the skin layers.");
    }

    boolean toogle = false;
    boolean tempToggle = false;
    int clock = 0;

    @Override
    public void onActivate()
    {
        if(blinking.get() == false)
        {
            toogle = true;
        }

    }

    @Override
    public void onDeactivate()
    {
        if(blinking.get() == false)
        {
            toogle = false;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event)
    {
        if(blinking.get() == true)
        {
            clock++;
            if(clock > delay.get())
            {
                clock = 0;
                toogle = !toogle;
            }
        }

        if(toogle != tempToggle)
        {
            tempToggle = toogle;
            boolean toggleInverted;
            if(invert.get())
            {
                toggleInverted = !toogle;
            }
            else
            {
                toggleInverted = toogle;
            }

            if (cape.get()) { mc.options.setPlayerModelPart(PlayerModelPart.CAPE, toggleInverted); }
            if (head.get()) { mc.options.setPlayerModelPart(PlayerModelPart.HAT, toggleInverted); }
            if (body.get()) { mc.options.setPlayerModelPart(PlayerModelPart.JACKET, toggleInverted); }
            if (leftArm.get()) { mc.options.setPlayerModelPart(PlayerModelPart.LEFT_SLEEVE, toggleInverted); }
            if (leftLeg.get()) { mc.options.setPlayerModelPart(PlayerModelPart.LEFT_PANTS_LEG, toggleInverted); }
            if (rightLeg.get()) { mc.options.setPlayerModelPart(PlayerModelPart.RIGHT_PANTS_LEG, toggleInverted); }
            if (rightArm.get()) { mc.options.setPlayerModelPart(PlayerModelPart.RIGHT_SLEEVE, toggleInverted); }

            mc.options.write();
        }
    }
}
