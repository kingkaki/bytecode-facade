package com.kingkk.bytecode.facade;

import org.objectweb.asm.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Utils {

    public static Map<MethodFacade, MethodFacade> nearestParentMap = new HashMap<>();

    public static String toGenericName(String name) {
        String gname;
        int arrayCount = 0;
        for (byte b : name.getBytes()) {
            if (b == 91) { // "["
                arrayCount++;
            }
        }
        if (arrayCount != 0) {
            name = name.replace("[", "").replace("]", "");
        }
        switch (name) {
            case "Z":
                gname = "boolean";
                break;
            case "C":
                gname = "char";
                break;
            case "B":
                gname = "byte";
                break;
            case "S":
                gname = "short";
                break;
            case "I":
                gname = "int";
                break;
            case "F":
                gname = "float";
                break;
            case "J":
                gname = "long";
                break;
            case "D":
                gname = "double";
                break;
            case "V":
                gname = "void";
                break;
            default:
                if (name.startsWith("L") && name.endsWith(";")) {
                    name = name.substring(1, name.length() - 1);
                }
                gname = name.replace("/", ".");
                break;
        }

        for (int i = 0; i < arrayCount; i++) {
            gname += "[]";
        }
        return gname;
    }

    public static String getMethodSignature(String owner, String name, String desc) {
        String returnType = Utils.toGenericName(Type.getReturnType(desc).toString());
        Type[] types = Type.getArgumentTypes(desc);
        String[] argTypes = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            argTypes[i] = Utils.toGenericName(types[i].toString());
        }
        return Utils.formatMethodSignature(toGenericName(owner), name, argTypes, returnType);
    }

    public static String formatMethodSignature(String className, String methodName, String[] argType, String returnType) {
        String argTypesStr = Arrays.toString(argType);
        return returnType + " " + className + ":" + methodName + "(" +
                argTypesStr.substring(1, argTypesStr.length() - 1) + ")";
    }

    public static String formatSubSignature(String methodName, String[] argType, String returnType) {
        String argTypesStr = Arrays.toString(argType);
        return returnType + " " + methodName + "(" +
                argTypesStr.substring(1, argTypesStr.length() - 1) + ")";
    }

    public static String toShortName(String className) {
        if (!className.contains(".")) {
            return className;
        }
        String[] arr = className.split("\\.");
        return arr[arr.length - 1];
    }


    public static MethodFacade getNearestParent(MethodFacade callee) {
        if (nearestParentMap.containsKey(callee)) {
            return nearestParentMap.get(callee);
        }

        MethodFacade parent;
        ClassFacade parentClz = callee.getClassFacade().getSuperClass();

        while (parentClz != null) {
            Optional<MethodFacade> mf = parentClz.getMethods()
                    .stream()
                    .filter(m -> m.getSubSignature().equals(callee.getSubSignature()))
                    .findFirst();
            if (mf.isPresent()) {
                parent = mf.get();
                nearestParentMap.put(callee, parent);
                return parent;
            }
            parentClz = parentClz.getSuperClass();
        }

        for (ClassFacade inf : callee.getClassFacade().getInterfaces()) {
            parentClz = inf;
            while (parentClz != null) {
                Optional<MethodFacade> mf = parentClz.getMethods()
                        .stream()
                        .filter(m -> m.getSubSignature().equals(callee.getSubSignature()))
                        .findFirst();
                if (mf.isPresent()) {
                    parent = mf.get();
                    nearestParentMap.put(callee, parent);
                    return parent;
                }
                parentClz = parentClz.getSuperClass();
            }
        }

        nearestParentMap.put(callee, null);
        return null;
    }

    public static boolean isArray(String className) {
        return className.contains("[");
    }


}
