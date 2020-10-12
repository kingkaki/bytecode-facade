package com.kingkk.bytecode.facade;

public class Main {
    public static void main(String[] args) throws Exception {
        Global.g().addJarPath(getRtJarPath());
        Global.g().load();

        ClassFacade cf = Global.g().getClassFacade("java.lang.String");
        System.out.println("[C] " + cf);
        System.out.println();
        for (MethodFacade mf : cf.getMethods()) {
            System.out.println("[M] " + mf.getSignature());
            mf.getInvokes().forEach(invoke -> System.out.println("[I] " + invoke));
            System.out.println();
        }
    }

    public static String getRtJarPath() {
        String urlPath = Object.class.getResource("Object.class").getPath();
        return urlPath.substring(6, urlPath.lastIndexOf("!")).replace("+", " ");
    }
}
