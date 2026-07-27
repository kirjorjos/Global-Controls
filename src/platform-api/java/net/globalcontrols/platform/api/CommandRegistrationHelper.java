package net.globalcontrols.platform.api;

import java.lang.reflect.Proxy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared helper for Fabric- and Quilt-based loaders.
 * Both use the Fabric API's {@code CommandRegistrationCallback} interface via reflection
 * since it may not be available at compile time for all target versions.
 */
public final class CommandRegistrationHelper {
    private static final Logger LOG = Logger.getLogger("GlobalControls");

    private CommandRegistrationHelper() {}

    /**
     * Registers a command adapter with the Fabric command registration callback
     * using reflection. Works on both Fabric and Quilt (which bundles the Fabric API).
     *
     * @param adapter     the command adapter whose {@code register(Object)} method
     *                    will be invoked with the command dispatcher
     * @param classLoader the class loader to use for reflective lookups
     */
    public static void registerViaFabricApi(Object adapter, ClassLoader classLoader) {
        try {
            Class<?> callbackClass = Class.forName(
                "net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback",
                false,
                classLoader
            );
            Object event = callbackClass.getDeclaredField("EVENT").get(null);
            Object listener = Proxy.newProxyInstance(
                classLoader,
                new Class<?>[]{callbackClass},
                (proxy, method, methodArgs) -> {
                    if ("register".equals(method.getName())) {
                        try {
                            adapter.getClass()
                                .getMethod("register", Object.class)
                                .invoke(adapter, methodArgs[0]);
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, "Failed to register command dispatcher", e);
                        }
                    }
                    return null;
                }
            );
            event.getClass().getMethod("register", callbackClass).invoke(event, listener);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Fabric API not available, commands not registered", e);
        }
    }
}
