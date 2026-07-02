package net.globalcontrols.loader.quilt.mixin;

import net.globalcontrols.common.service.KeyInterceptorHolder;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardInputMixin {

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        var interceptor = KeyInterceptorHolder.get();
        if (interceptor != null) {
            interceptor.onKeyEvent(key, action == 1);
        }
    }
}
