package com.kingkk.bytecode.facade.invoke;

import com.kingkk.bytecode.facade.MethodFacade;
import org.objectweb.asm.tree.MethodInsnNode;

public class SpecialInvoke extends InvokeFacade {
    public SpecialInvoke(MethodInsnNode insnNode, MethodFacade caller) {
        super(insnNode, caller);
    }

    @Override
    public String toString() {
        return "invoke special " + callee.toString();
    }
}
