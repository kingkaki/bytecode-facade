# bytecode-facade
一个写着玩的字节码分析工具

- 基于asm，解析速度快，对不同版本字节码兼容性高
- 功能较少，无法进行完整的语法分析、控制流分析，目前只能算个小工具，不定期维护

简易上手(Main.java)
```java
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
```

output(输出`java.lang.String`的所有方法，以及方法内部调用的函数)
```
[C] java.lang.String

[M] void java.lang.String:<init>()
[I] invoke special void java.lang.Object:<init>()

[M] void java.lang.String:<init>(java.lang.String)
[I] invoke special void java.lang.Object:<init>()

[M] void java.lang.String:<init>(char[])
[I] invoke static char[] java.util.Arrays:copyOf(char[], int)
[I] invoke special void java.lang.Object:<init>()

[M] void java.lang.String:<init>(char[], int, int)
[I] invoke special void java.lang.StringIndexOutOfBoundsException:<init>(int)
[I] invoke static char[] java.util.Arrays:copyOfRange(char[], int, int)
[I] invoke special void java.lang.Object:<init>()

[M] void java.lang.String:<init>(int[], int, int)
[I] invoke static boolean java.lang.Character:isBmpCodePoint(int)
[I] invoke special void java.lang.IllegalArgumentException:<init>(String)
[I] invoke static void java.lang.Character:toSurrogates(int, char[], int)
[I] invoke static boolean java.lang.Character:isValidCodePoint(int)
[I] invoke special void java.lang.Object:<init>()
[I] invoke special void java.lang.StringIndexOutOfBoundsException:<init>(int)
[I] invoke static String java.lang.Integer:toString(int)

[M] void java.lang.String:<init>(byte[], int, int, int)
[I] invoke special void java.lang.Object:<init>()
[I] invoke static void java.lang.String:checkBounds(byte[], int, int)

[M] void java.lang.String:<init>(byte[], int)
[I] invoke special void java.lang.String:<init>(byte[], int, int, int)

[M] void java.lang.String:checkBounds(byte[], int, int)
[I] invoke special void java.lang.StringIndexOutOfBoundsException:<init>(int)

[M] void java.lang.String:<init>(byte[], int, int, java.lang.String)
[I] invoke static void java.lang.String:checkBounds(byte[], int, int)
[I] invoke special void java.lang.NullPointerException:<init>(String)
[I] invoke static char[] java.lang.StringCoding:decode(String, byte[], int, int)
[I] invoke special void java.lang.Object:<init>()

[M] void java.lang.String:<init>(byte[], int, int, java.nio.charset.Charset)
[I] invoke static void java.lang.String:checkBounds(byte[], int, int)
[I] invoke special void java.lang.NullPointerException:<init>(String)
[I] invoke special void java.lang.Object:<init>()
[I] invoke static char[] java.lang.StringCoding:decode(Charset, byte[], int, int)

...
```