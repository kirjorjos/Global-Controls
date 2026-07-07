package net.globalcontrols.loader.forge.mixin;

import net.globalcontrols.common.service.KeyInterceptorHolder;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class KeyboardInputMixin {

    @Inject(method = "handleKeyboardInput", at = @At("HEAD"), cancellable = true)
    private void onHandleKeyboardInput(CallbackInfo ci) {
        var interceptor = KeyInterceptorHolder.get();
        if (interceptor != null) {
            int key = Keyboard.getEventKey();
            boolean pressed = Keyboard.getEventKeyState();
            interceptor.onKeyEvent(key, pressed);
        }
    }
}
