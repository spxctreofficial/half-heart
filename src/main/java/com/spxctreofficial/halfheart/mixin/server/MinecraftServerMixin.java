package com.spxctreofficial.halfheart.mixin.server;

import com.spxctreofficial.halfheart.interfaces.HHServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements HHServer {
    @Unique
    public List<UUID> currentlyEnabledPlayers = new ArrayList<>();


    @Override
    public List<UUID> currentlyEnabledPlayers() {
        return currentlyEnabledPlayers;
    }
}
