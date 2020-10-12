package com.kingkk.bytecode.facade;

import org.objectweb.asm.tree.MethodNode;

public class ArrayClassFacade extends ClassFacade {
    public static final String ArrayClassName = "Array";
    public static final ArrayClassFacade INST = new ArrayClassFacade();
    public static final MethodFacade CLONE_METHOD =
            new ArrayMethodFacade("clone", "()Ljava/lang/Object;");

    public ArrayClassFacade() {
        super(ArrayClassName);
        this.isVirtual = false;
    }


    public static class ArrayMethodFacade extends MethodFacade {
        public ArrayMethodFacade(String name, String desc) {
            super(new MethodNode(), INST);
            this.node.name = name;
            this.node.desc = desc;
            this.isVirtual = false;
        }

    }

}
