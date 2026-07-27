package net.globalcontrols.common.service;

import net.globalcontrols.platform.api.KeyInterceptor;

public final class KeyInterceptorHolder {
    private static KeyInterceptor interceptor;

    public static void set(KeyInterceptor interceptor) {
        KeyInterceptorHolder.interceptor = interceptor;
    }

    public static KeyInterceptor get() {
        return interceptor;
    }

    private KeyInterceptorHolder() {}
}
