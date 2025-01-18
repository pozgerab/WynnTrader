package com.gertoxq.wynntrader.mixin;

import com.gertoxq.wynntrader.custom.AllIDs;
import com.gertoxq.wynntrader.custom.CustomItem;
import com.gertoxq.wynntrader.screens.market.MarketScreenHandler;
import com.gertoxq.wynntrader.util.GuiPos;
import com.gertoxq.wynntrader.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gertoxq.wynntrader.screens.market.MarketScreenHandler.*;

@Mixin(ClientPlayerInteractionManager.class)
public class CancelClickMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.AFTER), cancellable = true)
    private void cancelClickIf(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (syncId == MarketScreenHandler.CURR_SYNC_ID) {
            client.player.sendMessage(Text.literal("clicked " + slotId));
            if (Utils.isBetween(slotId, 0, 44) && MarketScreenHandler.enabled && !isFindingRealItem) {
                CustomItem item = CustomItem.getItem(player.currentScreenHandler.getCursorStack());
                if (item != null) client.player.sendMessage(Text.literal(item.get(AllIDs.SP_RAW4).toString()));
                client.player.sendMessage(Text.literal("click cancelled"));
                ci.cancel();
            } else if (slotId == GuiPos.Market.SORT.getIndex()) ci.cancel();
        }
    }
}
