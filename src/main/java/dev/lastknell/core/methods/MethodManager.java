package dev.lastknell.core.methods;

import java.util.HashSet;

import dev.lastknell.core.methods.impl.Join;
import dev.lastknell.core.methods.impl.Ping;
import lombok.Getter;
import lombok.NonNull;

public class MethodManager {

    private @Getter HashSet<IMethod> methods;

    /**
     * @param allowPremade true to allow Manager to use premade methods
     */
    public MethodManager(boolean allowPremade) {
        if (allowPremade) {
            registerPremade();
        }
    }

    private void registerPremade() {
        methods.add(new Join());
        methods.add(new Ping());
    }

    /**
     * @param method register ur own Method
     */
    public void register(@NonNull IMethod method) {
        methods.add(method);
    }

    /**
     * @param method unregister ur own Method
     */
    public void unregister(@NonNull IMethod method) {
        methods.remove(method);
    }

}
