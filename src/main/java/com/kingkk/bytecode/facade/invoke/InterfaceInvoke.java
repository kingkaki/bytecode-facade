package com.kingkk.bytecode.facade.invoke;

import com.kingkk.bytecode.facade.MethodFacade;
import org.objectweb.asm.tree.MethodInsnNode;

public class InterfaceInvoke extends InvokeFacade {
    public InterfaceInvoke(MethodInsnNode insnNode, MethodFacade caller) {
        super(insnNode, caller);
    }

    @Override
    public String toString() {
        return "invoke interface " + callee.toString();
    }
}
