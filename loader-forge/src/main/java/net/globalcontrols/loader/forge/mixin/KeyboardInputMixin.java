package net.globalcontrols.loader.forge.mixin;

// TODO: mixin into KeyboardHandler or GuiScreen based on Forge version
// For 1.12.2-: mixin into GuiScreen.handleKeyboardInput()
// For 1.13+: @Mixin(KeyboardHandler.class)
//   @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
//   private void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
//       KeyInterceptor interceptor = net.globalcontrols.common.service.KeyInterceptorHolder.get();
//       if (interceptor != null) {
//           interceptor.onKeyEvent(key, action == 1);
//       }
//   }
public class KeyboardInputMixin {}
