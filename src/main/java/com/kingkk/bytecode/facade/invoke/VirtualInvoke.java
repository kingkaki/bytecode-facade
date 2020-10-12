package com.kingkk.bytecode.facade.invoke;

import com.kingkk.bytecode.facade.MethodFacade;
import org.objectweb.asm.tree.MethodInsnNode;

public class VirtualInvoke extends InvokeFacade {
    public VirtualInvoke(MethodInsnNode insnNode, MethodFacade caller) {
        super(insnNode, caller);
    }

    @Override
    public String toString() {
        return "invoke virtual " + callee.toString();
    }
}
