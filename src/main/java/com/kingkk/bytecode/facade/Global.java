package com.kingkk.bytecode.facade;

import com.kingkk.bytecode.facade.invoke.InvokeFacade;
import org.apache.tools.ant.DirectoryScanner;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Global {
    private static Global global = new Global();

    private List<String> jarPaths = new ArrayList<>();
    private Map<String, ClassFacade> clazzMap = new HashMap<>();
    private Map<String, MethodFacade> methodMap = new HashMap<>();

    public static Global g() {
        return global;
    }

    public Global addJarPath(String jarPath) {
        jarPaths.addAll(getAntJars(jarPath));
        return global;
    }

    private static Set<String> getAntJars(String path) {
        Set<String> jars = new HashSet<>();
        if (path.isEmpty()) {
            return jars;
        }
        File jarFile = new File(path);
        if (!jarFile.exists()) { // file not exist, quit.
            System.out.println(path + " not exists.");
            System.exit(-1);
        } else if (jarFile.isFile()) { // is file, add
            return Collections.singleton(path);
        } else if (jarFile.isDirectory()) { // is dir, find all *.class and *.jar, add
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setIncludes(new String[]{"**/*.jar"});
            scanner.setBasedir(path);
            scanner.scan();
            for (String filePath : scanner.getIncludedFiles()) {
                jars.add(path + File.separator + filePath);
            }
        }
        return jars;
    }

    public void load() throws IOException {
        for (String jarPath : jarPaths) {
            for (String clazzPath : getClassFilePaths(jarPath)) {
                URL url = new URL("jar:file:/" + clazzPath);
                ClassReader cr = new ClassReader(url.openConnection().getInputStream());
                ClassNode node = new ClassNode();
                cr.accept(node, 0);
                ClassFacade cf = new ClassFacade(node);
                clazzMap.put(cf.getClassName(), cf);

                for (MethodFacade mf : cf.getMethods()) {
                    methodMap.put(mf.getSignature(), mf);
                }
            }
        }

        // invokes需要methods加载好之后才加载
        for (MethodFacade mf : methodMap.values()) {
            Set<InvokeFacade> invokes = new HashSet<>();
            for (AbstractInsnNode insnNode : mf.node.instructions) {
                if (insnNode instanceof MethodInsnNode) {
                    invokes.add(InvokeFacade.getInvoke((MethodInsnNode) insnNode, mf));
                }
            }
            mf.setInvokes(invokes);
        }
    }

    private static Set<String> getClassFilePaths(String jarPath) throws IOException {
        JarFile jar = new JarFile(jarPath);
        Set<String> paths = new HashSet<>();
        for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
            JarEntry entry = e.nextElement();
            if (!entry.getName().endsWith(".class")) {
                continue;
            }
            paths.add(jarPath + "!/" + entry.getName());
        }
        return paths;
    }

    public Collection<ClassFacade> getClasses() {
        return clazzMap.values();
    }

    public Collection<MethodFacade> getMethods() {
        return methodMap.values();
    }

    public ClassFacade getClassFacade(String className) {
        return clazzMap.get(className);
    }

    public MethodFacade getMethodFacade(String methodName) {
        return methodMap.get(methodName);
    }


}
