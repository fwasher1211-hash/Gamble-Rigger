package com.droprig.tracker;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Read-only tracker: it never changes a slot, stack, packet, or server outcome. */
public final class DropRigTracker implements ClientModInitializer {
    private static final Pattern LABEL = Pattern.compile("^([1-9])(?:\\.2)?$");
    private static Integer dropperOne;
    private static Integer dropperTwo;
    private static String lastScreen = "";

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(DropRigTracker::scan);
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> render(drawContext));
    }

    private static void scan(MinecraftClient client) {
        if (client.player == null || !(client.currentScreen instanceof HandledScreen<?>)) {
            dropperOne = null;
            dropperTwo = null;
            lastScreen = "";
            return;
        }

        String screenName = client.currentScreen.getTitle().getString();
        if (!screenName.equals(lastScreen)) {
            dropperOne = null;
            dropperTwo = null;
            lastScreen = screenName;
        }

        for (Slot slot : client.player.currentScreenHandler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            String label = stack.getName().getString().trim();
            Matcher matcher = LABEL.matcher(label);
            if (!matcher.matches()) continue;
            int value = Integer.parseInt(matcher.group(1));
            if (label.endsWith(".2")) dropperTwo = value;
            else dropperOne = value;
        }
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !(client.currentScreen instanceof HandledScreen<?>)) return;
        int x = 8;
        int y = 8;
        String one = dropperOne == null ? "not detected" : Integer.toString(dropperOne);
        String two = dropperTwo == null ? "not detected" : Integer.toString(dropperTwo);
        context.fill(x - 4, y - 4, x + 174, y + 57, 0xB9000000);
        context.drawTextWithShadow(client.textRenderer, "DROP RIG TRACKER", x, y, 0xFFFFFFFF);
        context.drawTextWithShadow(client.textRenderer, "Dropper 1: " + one, x, y + 14, 0xFFB8DFFF);
        context.drawTextWithShadow(client.textRenderer, "Dropper 2: " + two, x, y + 27, 0xFFFFD58A);
        String result;
        if (dropperOne == null || dropperTwo == null) result = "Waiting for both labels";
        else if (dropperOne.equals(dropperTwo)) result = "Tie: " + dropperOne;
        else result = "Higher: Dropper " + (dropperOne > dropperTwo ? "1" : "2");
        context.drawTextWithShadow(client.textRenderer, result, x, y + 42, 0xFFFFFFFF);
    }
}
