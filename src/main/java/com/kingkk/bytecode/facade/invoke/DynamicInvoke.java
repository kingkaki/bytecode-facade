package com.kingkk.bytecode.facade.invoke;

import com.kingkk.bytecode.facade.MethodFacade;
import org.objectweb.asm.tree.MethodInsnNode;

public class DynamicInvoke extends InvokeFacade {
    public DynamicInvoke(MethodInsnNode insnNode, MethodFacade caller) {
        super(insnNode, caller);
    }

    @Override
    public String toString() {
        return "invoke dynamic " + callee.toString();
    }
}
