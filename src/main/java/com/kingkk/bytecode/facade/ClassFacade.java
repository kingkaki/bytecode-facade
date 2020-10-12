package com.kingkk.bytecode.facade;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassFacade {
    protected ClassNode node;
    // 标识一些不存在的类
    protected boolean isVirtual = false;

    protected ClassFacade superClass;
    protected List<ClassFacade> interfaces;
    protected List<MethodFacade> methods;

    protected Set<ClassFacade> implementClasses;
    protected Set<ClassFacade> beImplementedClasses;

    public ClassFacade(ClassNode node) {
        this.node = node;
        this.methods = node.methods.stream()
                .map(m -> new MethodFacade(m, this))
                .collect(Collectors.toList());
    }

    public ClassFacade(String className) {
        this.node = new ClassNode();
        this.node.name = className;
        this.methods = new ArrayList<>();
        isVirtual = true;
    }

    public String getClassName() {
        return Utils.toGenericName(node.name);
    }

    public boolean hasSuperClass() {
        return node.superName != null;
    }

    public ClassFacade getSuperClass() {
        if (!hasSuperClass()) {
            superClass = null;
        } else if (superClass == null) {
            ClassFacade cf = Global.g().getClassFacade(Utils.toGenericName(node.superName));
            superClass = cf == null ? new ClassFacade(node.superName) : cf;
        }
        return superClass;
    }

    public boolean hasInterfaces() {
        return node.interfaces != null;
    }

    public List<ClassFacade> getInterfaces() {
        if (!hasInterfaces()) {
            interfaces = null;
        } else if (interfaces == null) {
            interfaces = node.interfaces
                    .stream()
                    .map(Utils::toGenericName)
                    .map(clz -> {
                        ClassFacade cf = Global.g().getClassFacade(clz);
                        return cf == null ? new ClassFacade(clz) : cf;
                    })
                    .collect(Collectors.toList());
        }
        return interfaces;
    }

    public List<MethodFacade> getMethods() {
        return methods;
    }

    public Set<ClassFacade> getImplementClasses() {
        if (implementClasses == null) {
            implementClasses = Global.g().getClasses().parallelStream()
                    .filter(cf -> isImplementClass(cf, this))
                    .collect(Collectors.toSet());
        }
        return implementClasses;
    }

    public Set<ClassFacade> getBeImplementedClasses() {
        if (beImplementedClasses == null) {
            beImplementedClasses = new HashSet<>();
            if (hasSuperClass()) {
                beImplementedClasses.add(getSuperClass());
                beImplementedClasses.addAll(getSuperClass().getBeImplementedClasses());
            }
            if (hasInterfaces()) {
                beImplementedClasses.addAll(getInterfaces());
                for (ClassFacade cf : getInterfaces()) {
                    beImplementedClasses.addAll(cf.getBeImplementedClasses());
                }
            }
        }
        return beImplementedClasses;
    }

    private boolean isImplementClass(ClassFacade implementClass, ClassFacade clazz) {
        if (implementClass.equals(clazz)) {
            return true;
        }
        if (implementClass.hasSuperClass() && isImplementClass(implementClass.getSuperClass(), clazz)) {
            return true;
        }
        if (implementClass.hasInterfaces()
                && implementClass.getInterfaces().stream().anyMatch(impl -> isImplementClass(impl, clazz))) {
            return true;
        }
        return false;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public boolean isInterface() {
        return (node.access & Opcodes.ACC_INTERFACE) != 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassFacade) {
            return (this.hashCode() == obj.hashCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return node == null ? 0 : node.name.hashCode();
    }

    @Override
    public String toString() {
        return getClassName();
    }
}
