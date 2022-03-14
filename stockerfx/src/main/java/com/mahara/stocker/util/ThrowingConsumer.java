package com.mahara.stocker.util;

import java.util.function.Consumer;

/**
 * 处理Consumer中未处理的检查异常（checked exceptions），转换成运行时异常再抛出。
 * @param <T>
 * @param <E>
 */
public interface ThrowingConsumer<T, E extends Throwable> {
    void accept(T t) throws E;

    static <T, E extends Throwable> Consumer<T> unchecked(ThrowingConsumer<T, E> consumer) {
        return (t) -> {
            try {
                consumer.accept(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}