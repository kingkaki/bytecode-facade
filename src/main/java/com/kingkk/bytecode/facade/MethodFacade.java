package com.kingkk.bytecode.facade;

import com.kingkk.bytecode.facade.invoke.InvokeFacade;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodFacade {
    protected MethodNode node;
    protected ClassFacade clazz;
    protected boolean isVirtual = false;
    protected String name;
    protected String[] argTypes;
    protected String returnType;
    protected Set<InvokeFacade> invokes = new HashSet<>();
    protected Set<MethodFacade> implementMethods;
    protected Set<MethodFacade> beImplementedMethods;

    public MethodFacade(MethodNode node, ClassFacade clazz) {
        this.node = node;
        this.clazz = clazz;
    }

    public MethodFacade(String owner, String name, String desc) {
        this.node = new MethodNode();
        this.node.name = name;
        this.node.desc = desc;
        ClassFacade clazz = Global.g().getClassFacade(Utils.toGenericName(owner));
        this.clazz = clazz == null ? new ClassFacade(Utils.toGenericName(owner)) : clazz;
        isVirtual = true;
    }

    public String getName() {
        if (name == null && node != null) {
            name = node.name;
        }
        return name;
    }

    public String[] getArgumentTypes() {
        if (argTypes != null) {
            return argTypes;
        }
        Type[] types = Type.getArgumentTypes(node.desc);
        argTypes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            argTypes[i] = Utils.toGenericName(types[i].toString());
        }
        return argTypes;
    }

    public String getReturnType() {
        if (returnType == null) {
            returnType = Utils.toGenericName(Type.getReturnType(node.desc).toString());
        }
        return returnType;
    }

    public ClassFacade getClassFacade() {
        return clazz;
    }

    public Set<InvokeFacade> getInvokes() {
        return invokes;
    }

    public void setInvokes(Set<InvokeFacade> invokes) {
        this.invokes = invokes;
    }

    public Set<MethodFacade> getImplementMethods() {
        if (implementMethods == null) {
            implementMethods = getClassFacade().getImplementClasses().stream()
                    .flatMap(clz -> clz.getMethods().stream())
                    .filter(MethodFacade::hasActiveBody)
                    .filter(m -> m.getSubSignature().equals(getSubSignature()))
                    .collect(Collectors.toSet());
            implementMethods.add(this);
        }
        return implementMethods;
    }

    public Set<MethodFacade> getBeImplementedMethods() {
        if (beImplementedMethods == null) {
            beImplementedMethods = getClassFacade().getBeImplementedClasses().stream()
                    .flatMap(clz -> clz.getMethods().stream())
//                    .filter(MethodFacade::hasActiveBody) // 是不是beImplemented应该不用hasActiveBody
                    .filter(m -> m.getSubSignature().equals(getSubSignature()))
                    .collect(Collectors.toSet());
        }
        return beImplementedMethods;
    }


    public boolean isVirtual() {
        return isVirtual;
    }

    public boolean hasInvoke() {
        return invokes != null && !invokes.isEmpty();
    }

    public boolean hasActiveBody() {
        return node.instructions != null && node.instructions.size() != 0;
    }

    public String getSignature() {
        return Utils.formatMethodSignature(clazz.getClassName(), getName(), getArgumentTypes(), getReturnType());
    }

    public String getSubSignature() {
        return Utils.formatSubSignature(getName(), getArgumentTypes(), getReturnType());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodFacade) {
            return (this.hashCode() == obj.hashCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int clazzCode = clazz == null ? 0 : clazz.hashCode();
        int nameCode = node == null ? 0 : node.name.hashCode();
        int descCode = node == null ? 0 : node.desc.hashCode();

        return clazzCode + 29 * nameCode + 31 * descCode;
    }

    @Override
    public String toString() {
        String[] args = getArgumentTypes();
        String[] shortArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            shortArgs[i] = Utils.toShortName(args[i]);
        }
        return Utils.formatMethodSignature(
                clazz.getClassName(),
                getName(),
                shortArgs,
                Utils.toShortName(getReturnType())
        );
    }
}
