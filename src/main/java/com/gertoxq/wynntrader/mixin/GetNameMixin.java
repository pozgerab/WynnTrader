package com.gertoxq.wynntrader.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class GetNameMixin {

    @Inject(method = "getName",at = @At("RETURN"), cancellable = true)
    private void modifyGetNameReturn(@NotNull CallbackInfoReturnable<Text> cir) {
        Text originalText = cir.getReturnValue();
        if (originalText != null) {
            Style style = originalText.getStyle();
            String textContent = originalText.getString();

            if (textContent.endsWith("À")) {
                String modifiedContent = textContent.substring(0, textContent.length() - 1);
                cir.setReturnValue(Text.literal(modifiedContent).setStyle(style));
            }
        }
    }
}
