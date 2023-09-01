package com.spxctreofficial.halfheart.mixin.entity.player;

import com.mojang.authlib.GameProfile;
import com.spxctreofficial.halfheart.HalfHeart;
import com.spxctreofficial.halfheart.interfaces.HHPlayerEntity;
import com.spxctreofficial.halfheart.interfaces.HHServer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements HHPlayerEntity {

    @Unique
    private boolean halfHeartEnabled;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(World world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getServer() != null) if (((HHServer) player.getServer()).currentlyEnabledPlayers().contains(player.getUuid())) halfHeartEnabled = true;

        if (halfHeartEnabled) Objects.requireNonNull(((LivingEntity) (Object) this).getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(1D);

    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("HalfHeartEnabled", halfHeartEnabled);

    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        halfHeartEnabled = nbt.getBoolean("HalfHeartEnabled");
    }

    @Override
    public boolean halfHeartEnabled() {
        return halfHeartEnabled;
    }

    @Override
    public void setHalfHeartEnabled(boolean b) {
        halfHeartEnabled = b;
    }
}
