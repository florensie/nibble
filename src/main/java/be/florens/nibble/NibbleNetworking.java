package be.florens.nibble;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NibbleNetworking implements ClientModInitializer {

    public static final ResourceLocation NIBBLE_PACKET_ID = new ResourceLocation(Nibble.MOD_ID, "nibble");

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(NibbleNetworking.NIBBLE_PACKET_ID, NibbleNetworking::receiveNibblePacket);
    }

    @Environment(EnvType.SERVER)
    public static void sendNibblePacket(ServerPlayer player, int nutrition) {
        FriendlyByteBuf byteBuf = PacketByteBufs.create();
        byteBuf.writeInt(nutrition);

        ServerPlayNetworking.send(player, NIBBLE_PACKET_ID, byteBuf);
    }

    @Environment(EnvType.CLIENT)
    private static void receiveNibblePacket(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        int nutrition = buf.readInt();

        client.execute(() -> {
            if (client.player == null) {
                Nibble.LOGGER.warn("Received nibble packet but player is not available!");
            }

            client.player.getFoodData().nibble$eatOnlyNutrition(nutrition);
        });
    }
}
