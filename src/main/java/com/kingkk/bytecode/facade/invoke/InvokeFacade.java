package com.kingkk.bytecode.facade.invoke;

import com.kingkk.bytecode.facade.ArrayClassFacade;
import com.kingkk.bytecode.facade.Global;
import com.kingkk.bytecode.facade.MethodFacade;
import com.kingkk.bytecode.facade.Utils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class InvokeFacade implements Opcodes {
    protected MethodInsnNode node;

    protected MethodFacade caller;
    protected MethodFacade callee;

    protected InvokeFacade(MethodInsnNode node, MethodFacade caller) {
        this.node = node;
        this.caller = caller;

        if (Utils.isArray(node.owner) && node.name.equals("clone")) {
            this.callee = ArrayClassFacade.CLONE_METHOD;
            return;
        }

        MethodFacade mf = Global.g().getMethodFacade(Utils.getMethodSignature(node.owner, node.name, node.desc));
        this.callee = mf == null ? new MethodFacade(node.owner, node.name, node.desc) : mf;

        if (this.callee.isVirtual()) {
            MethodFacade parent = Utils.getNearestParent(this.callee);
            if (parent != null) {
                this.callee = parent;
            }
        }

    }

    public static InvokeFacade getInvoke(MethodInsnNode insnNode, MethodFacade caller) {
        switch (insnNode.getOpcode()) {
            case INVOKEVIRTUAL:
                return new VirtualInvoke(insnNode, caller);
            case INVOKESPECIAL:
                return new SpecialInvoke(insnNode, caller);
            case INVOKESTATIC:
                return new StaticInvoke(insnNode, caller);
            case INVOKEINTERFACE:
                return new InterfaceInvoke(insnNode, caller);
            case INVOKEDYNAMIC:
                return new DynamicInvoke(insnNode, caller);
            default:
                throw new IllegalArgumentException("illegal opcode.");
        }
    }

    public MethodFacade getCaller() {
        return caller;
    }

    public MethodFacade getCallee() {
        return callee;
    }

    @Override
    public int hashCode() {
        int callerHash = caller == null ? 0 : caller.hashCode();
        int calleeHash = callee == null ? 0 : callee.hashCode();
        return callerHash + calleeHash * 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InvokeFacade) {
            return (this.hashCode() == obj.hashCode());
        }
        return false;
    }
}
