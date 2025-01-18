package com.gertoxq.wynntrader.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import oshi.jna.platform.mac.SystemB;

import java.util.*;

public class Task {
    private static final Map<Integer, Task> taskIds = new HashMap<>();
    private static final List<Integer> toRemove = new ArrayList<>();
    private static long counter = 0;
    private static long nextAvailable = 0;
    private static int id = 0;
    private static boolean live = false;

    public long triggerAt;
    public final int delay;
    public final Runnable task;
    public final Priority priority;
    public final boolean isStatic;
    public boolean done = false;

    public Task(Runnable task, Priority priority) {
        this(task, 0, false, priority);
    }

    public Task(Runnable task, int delay, boolean after, Priority priority) {
        this.task = task;
        this.delay = delay;
        this.isStatic = false;
        this.triggerAt = calculateDynamicTriggerAt(priority, delay);
        this.priority = priority;
        nextAvailable = Math.max(triggerAt, nextAvailable);
        live = true;
        taskIds.put(id, this);
        id++;
    }

    public Task(Runnable task, int delay, Priority priority) {
        this.task = task;
        this.delay = delay;
        this.isStatic = true;
        this.triggerAt = counter + delay;
        this.priority = priority;
        nextAvailable = Math.max(triggerAt, nextAvailable);
        live = true;
        taskIds.put(id, this);
        id++;
    }

    public enum Priority {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }

    public static void schedule(Runnable task) {
        new Task(task, Priority.NORMAL);
    }

    public static void schedule(Runnable task, int delay, Priority priority) {
        new Task(task, delay, true, priority);
    }

    public static void schedule(Runnable task, Priority priority) {
        new Task(task, priority);
    }

    public static void runAfter(Runnable task, int delay) {
        new Task(task, delay, Priority.NORMAL);
    }

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!live) return;
            if (taskIds.isEmpty()) {
                live = false;
                return;
            }

            try {
                taskIds.forEach((integer, task) -> {
                    if (task.done) {
                        toRemove.add(integer);
                    } else if (task.triggerAt <= counter) {
                        nextAvailable = Math.max(task.triggerAt, nextAvailable);
                        client.execute(task.task);
                        task.done = true;
                    }
                });
            } catch (ConcurrentModificationException ignored) {
            }
            try {
                toRemove.forEach(taskIds::remove);
            } catch (IllegalStateException ignored) {
            }
            toRemove.clear();
            counter++;
        });
    }

    private long calculateDynamicTriggerAt(Priority priority, int delay) {
        long adjustedTriggerAt = counter + delay;

        for (Task t : taskIds.values()) {
            if (!t.isStatic && t.priority.ordinal() <= priority.ordinal() && t.triggerAt > counter) {
                adjustedTriggerAt = Math.max(adjustedTriggerAt, t.triggerAt + delay);
            }
        }

        for (Task t : taskIds.values()) {
            if (!t.isStatic && t.priority.ordinal() > priority.ordinal() && t.triggerAt > counter) {
                t.triggerAt = Math.max(t.triggerAt, adjustedTriggerAt + t.delay);
            }
        }

        return adjustedTriggerAt;
    }
}
