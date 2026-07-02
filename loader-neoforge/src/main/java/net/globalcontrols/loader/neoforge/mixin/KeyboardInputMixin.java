package net.globalcontrols.loader.neoforge.mixin;

// TODO: mixin into KeyboardHandler.class
// @Mixin(KeyboardHandler.class)
// public class KeyboardInputMixin {
//     @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
//     private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
//         KeyInterceptor interceptor = net.globalcontrols.common.service.KeyInterceptorHolder.get();
//         if (interceptor != null) {
//             interceptor.onKeyEvent(key, action == 1);
//         }
//     }
// }
public class KeyboardInputMixin {}
