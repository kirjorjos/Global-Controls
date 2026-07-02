package net.globalcontrols.loader.fabric.mixin;

// TODO: mixin into Keyboard.class
// @Mixin(Keyboard.class)
// public class KeyboardInputMixin {
//     @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
//     private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfoReturnable<Boolean> cir) {
//         KeyInterceptor interceptor = net.globalcontrols.common.service.KeyInterceptorHolder.get();
//         if (interceptor != null) {
//             interceptor.onKeyEvent(key, action == 1);
//         }
//     }
// }
public class KeyboardInputMixin {}
