package com.gertoxq.wynntrader.screens;

import com.gertoxq.wynntrader.util.GuiPos;
import com.gertoxq.wynntrader.util.Task;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

import static com.gertoxq.wynntrader.client.WynntraderClient.client;

public class PageTracker {
    private final Set<Integer> updatedSlots = new HashSet<>();
    private final int MAX_SLOT_INDEX = 44;
    private boolean loadComplete = false;
    private long lastUpdateTime;
    private final int TIMEOUT_MS = 500;
    private static final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
    private Runnable onComplete = null;
    private final ScheduledFuture<?> future;
    private final boolean disposable;

    public PageTracker() {
        this(false);
    }

    public PageTracker(boolean disposable) {
        this.disposable = disposable;
        future = exec.scheduleAtFixedRate(this::isLoadComplete, 50, 50, TimeUnit.MILLISECONDS);
        init();
    }

    protected void init() {

    }

    public void clean() {
        if (future != null) {
            future.cancel(false);
        }
    }

    private long getLastUpdateTime() {
        return lastUpdateTime;
    }

    private boolean isLoadDone() {
        return loadComplete;
    }

    public void setLoadComplete(boolean loadComplete) {
        if (this.onComplete == null) {
            System.out.println("scheduled before prev exec");
            client.player.sendMessage(Text.literal("schedule error"));
            return;
        }
        this.loadComplete = loadComplete;
        if (loadComplete) {
            client.player.sendMessage(Text.literal("completed task"));
            onComplete.run();
            updatedSlots.clear();
            setOnComplete(null);
        }
    }

    public void setOnComplete(Runnable onComplete) {
        loadComplete = false;
        this.onComplete = onComplete;
        if (onComplete != null) {
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    public void onSlotUpdate(int slotIndex) {
        updatedSlots.add(slotIndex);
        lastUpdateTime = System.currentTimeMillis();
        isLoadComplete();
    }

    public boolean isLoadComplete() {
        if (onComplete == null) return false;
        if (updatedSlots.contains(44) && updatedSlots.contains(53)) {
            setLoadComplete(true);
        }

        if (!loadComplete && System.currentTimeMillis() - lastUpdateTime > TIMEOUT_MS) {
            setLoadComplete(true);
        }
        return loadComplete;
    }
}
