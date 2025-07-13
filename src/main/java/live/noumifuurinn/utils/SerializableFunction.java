package live.noumifuurinn.utils;

import java.io.Serializable;

public interface SerializableFunction<T, R> extends Serializable {
    R apply(T t);
}
