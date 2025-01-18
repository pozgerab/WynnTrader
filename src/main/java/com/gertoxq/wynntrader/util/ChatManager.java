package com.gertoxq.wynntrader.util;

import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.time.Instant;
import java.util.BitSet;

import static com.gertoxq.wynntrader.client.WynntraderClient.client;

public class ChatManager {

    public static void sendMessage(String message) {
        try {
            assert client.player != null;

            int offset = 0;
            BitSet acknowledged = new BitSet(20);
            acknowledged.set(0);

            client.player.networkHandler.sendPacket(
                    new ChatMessageC2SPacket(message, Instant.now(), System.nanoTime(),
                            null, new LastSeenMessageList.Acknowledgment(offset, acknowledged)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
