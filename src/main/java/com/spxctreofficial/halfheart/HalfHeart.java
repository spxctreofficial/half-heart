package com.spxctreofficial.halfheart;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.context.CommandContext;
import com.spxctreofficial.halfheart.interfaces.HHPlayerEntity;
import com.spxctreofficial.halfheart.interfaces.HHServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

import static net.minecraft.server.command.CommandManager.*;

public class HalfHeart implements ModInitializer {

    public static final String MOD_ID = "half-heart";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> dispatcher.register(literal("hell")
                .then(literal("enable")
                        .executes(context -> {
                            LivingEntity entity = (LivingEntity) context.getSource().getEntityOrThrow();
                            return enable(context, ImmutableList.of(entity));
                        })
                        .then(CommandManager.argument("playersEnableMod", EntityArgumentType.players())
                                .executes(context -> {
                                    Collection<? extends LivingEntity> entities = EntityArgumentType.getPlayers(context, "playersEnableMod");
                                    return enable(context, entities);
                                })))
                .then(literal("disable")
                        .executes(context -> {
                            LivingEntity entity = (LivingEntity) context.getSource().getEntityOrThrow();
                            return disable(context, ImmutableList.of(entity));
                        }).then(CommandManager.argument("playersRemoveMod", EntityArgumentType.players())
                                .executes(context -> {
                                    Collection<? extends LivingEntity> entities = EntityArgumentType.getPlayers(context, "playersRemoveMod");
                                    return disable(context, entities);
                                }))))));
    }

    private int enable(CommandContext<ServerCommandSource> context, Collection<? extends LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            MinecraftServer server = entity.getServer();

            if (!(entity instanceof PlayerEntity)) {
                context.getSource().sendError(Text.literal("This is not a player!"));
                break;
            }
            if (((HHPlayerEntity) entity).halfHeartEnabled()) {
                context.getSource().sendError(Text.literal("Half Heart Mode is already enabled for this player!"));
                break;
            }
            if (server == null) {
                context.getSource().sendError(Text.literal("Can't find the server!"));
                break;
            }

            ((HHServer) server).currentlyEnabledPlayers().add(entity.getUuid());
            ((HHPlayerEntity) entity).setHalfHeartEnabled(true);
            entity.setHealth(1F);
            Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(1D);
            context.getSource().sendMessage(Text.literal("The mod has been enabled!"));
        }
        return 0;
    }

    private int disable(CommandContext<ServerCommandSource> context, Collection<? extends LivingEntity> entities) {
        for (LivingEntity entity : entities) {
            MinecraftServer server = entity.getServer();

            if (!(entity instanceof PlayerEntity)) {
                context.getSource().sendError(Text.literal("This is not a player!"));
                break;
            }
            if (!((HHPlayerEntity) entity).halfHeartEnabled()) {
                context.getSource().sendError(Text.literal("Half Heart Mode is already disabled for this player!"));
                break;
            }
            if (server == null) {
                context.getSource().sendError(Text.literal("Can't find the server!"));
                break;
            }

            ((HHServer) server).currentlyEnabledPlayers().remove(entity.getUuid());
            ((HHPlayerEntity) entity).setHalfHeartEnabled(false);
            Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(20D);
            context.getSource().sendMessage(Text.literal("The mod has been disabled!"));
        }
        return 0;
    }
}
