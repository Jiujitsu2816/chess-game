package software.ulpgc.chess;

/** Converts a textual representation into a model object. */
@FunctionalInterface
public interface Deserializer<T> {
    T deserialize(String text);
}
