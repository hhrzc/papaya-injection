package com.hhrzc.papaya.ppi.tests.support;

import com.hhrzc.papaya.ppi.utils.stage.PpiOptions;

import java.lang.reflect.Field;

/**
 * Test helper: puts {@link PpiOptions} back to its initial state (no stageFrom, not frozen).
 * PpiOptions is static, one per JVM, so every test that touches stages must reset it.
 */
public final class PpiOptionsReset {

    private PpiOptionsReset() {
    }

    public static void reset() {
        set("stageFrom", null);
        set("frozen", false);
    }

    private static void set(String fieldName, Object value) {
        try {
            Field field = PpiOptions.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);              // null target = static field
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot reset PpiOptions." + fieldName + ". Was the field renamed?", e);
        }
    }
}
