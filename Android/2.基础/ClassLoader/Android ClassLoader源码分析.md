转载请注明链接:https://blog.csdn.net/feather_wch/article/details/88428618

# Android ClassLoader源码分析

版本: 2019-03-12

---

[TOC]

## BaseDexClassLoader

1、BaseDexClassLoader内部重要概念总结
> 1. `DexPathList pathList`: 内部有一个dexElements数组，存储着该ClassLoader的所有dex/resources的路径

1、BaseDexClassLoader源码
```java
package dalvik.system;

/**
 * Base class for common functionality between various dex-based
 * {@link ClassLoader} implementations.
 */
public class BaseDexClassLoader extends ClassLoader {

    /**
     * Hook for customizing how dex files loads are reported.
     *
     * This enables the framework to monitor the use of dex files. The
     * goal is to simplify the mechanism for optimizing foreign dex files and
     * enable further optimizations of secondary dex files.
     *
     * The reporting happens only when new instances of BaseDexClassLoader
     * are constructed and will be active only after this field is set with
     * {@link BaseDexClassLoader#setReporter}.
     */
    /* @NonNull */ private static volatile Reporter reporter = null;

    private final DexPathList pathList;

    /**
     * Constructs an instance.
     * Note that all the *.jar and *.apk files from {@code dexPath} might be
     * first extracted in-memory before the code is loaded. This can be avoided
     * by passing raw dex files (*.dex) in the {@code dexPath}.
     *
     * @param dexPath the list of jar/apk files containing classes and
     * resources, delimited by {@code File.pathSeparator}, which
     * defaults to {@code ":"} on Android.
     * @param optimizedDirectory this parameter is deprecated and has no effect since API level 26.
     * @param librarySearchPath the list of directories containing native
     * libraries, delimited by {@code File.pathSeparator}; may be
     * {@code null}
     * @param parent the parent class loader
     */
    public BaseDexClassLoader(String dexPath, File optimizedDirectory,
            String librarySearchPath, ClassLoader parent) {
        this(dexPath, optimizedDirectory, librarySearchPath, parent, false);
    }

    /**
     * @hide
     */
    public BaseDexClassLoader(String dexPath, File optimizedDirectory,
            String librarySearchPath, ClassLoader parent, boolean isTrusted) {
        super(parent);
        this.pathList = new DexPathList(this, dexPath, librarySearchPath, null, isTrusted);

        if (reporter != null) {
            reportClassLoaderChain();
        }
    }

    /**
     * Reports the current class loader chain to the registered {@code reporter}.
     * The chain is reported only if all its elements are {@code BaseDexClassLoader}.
     */
    private void reportClassLoaderChain() {
        ArrayList<BaseDexClassLoader> classLoadersChain = new ArrayList<>();
        ArrayList<String> classPaths = new ArrayList<>();

        classLoadersChain.add(this);
        classPaths.add(String.join(File.pathSeparator, pathList.getDexPaths()));

        boolean onlySawSupportedClassLoaders = true;
        ClassLoader bootClassLoader = ClassLoader.getSystemClassLoader().getParent();
        ClassLoader current = getParent();

        while (current != null && current != bootClassLoader) {
            if (current instanceof BaseDexClassLoader) {
                BaseDexClassLoader bdcCurrent = (BaseDexClassLoader) current;
                classLoadersChain.add(bdcCurrent);
                classPaths.add(String.join(File.pathSeparator, bdcCurrent.pathList.getDexPaths()));
            } else {
                onlySawSupportedClassLoaders = false;
                break;
            }
            current = current.getParent();
        }

        if (onlySawSupportedClassLoaders) {
            reporter.report(classLoadersChain, classPaths);
        }
    }

    /**
     * Constructs an instance.
     *
     * dexFile must be an in-memory representation of a full dexFile.
     *
     * @param dexFiles the array of in-memory dex files containing classes.
     * @param parent the parent class loader
     *
     * @hide
     */
    public BaseDexClassLoader(ByteBuffer[] dexFiles, ClassLoader parent) {
        // TODO We should support giving this a library search path maybe.
        super(parent);
        this.pathList = new DexPathList(this, dexFiles);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
        Class c = pathList.findClass(name, suppressedExceptions);
        if (c == null) {
            ClassNotFoundException cnfe = new ClassNotFoundException(
                    "Didn't find class \"" + name + "\" on path: " + pathList);
            for (Throwable t : suppressedExceptions) {
                cnfe.addSuppressed(t);
            }
            throw cnfe;
        }
        return c;
    }

    /**
     * @hide
     */
    public void addDexPath(String dexPath) {
        addDexPath(dexPath, false /*isTrusted*/);
    }

    /**
     * @hide
     */
    public void addDexPath(String dexPath, boolean isTrusted) {
        pathList.addDexPath(dexPath, null /*optimizedDirectory*/, isTrusted);
    }

    /**
     * Adds additional native paths for consideration in subsequent calls to
     * {@link #findLibrary(String)}
     * @hide
     */
    public void addNativePath(Collection<String> libPaths) {
        pathList.addNativePath(libPaths);
    }

    @Override
    protected URL findResource(String name) {
        return pathList.findResource(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) {
        return pathList.findResources(name);
    }

    @Override
    public String findLibrary(String name) {
        return pathList.findLibrary(name);
    }

    /**
     * Returns package information for the given package.
     * Unfortunately, instances of this class don't really have this
     * information, and as a non-secure {@code ClassLoader}, it isn't
     * even required to, according to the spec. Yet, we want to
     * provide it, in order to make all those hopeful callers of
     * {@code myClass.getPackage().getName()} happy. Thus we construct
     * a {@code Package} object the first time it is being requested
     * and fill most of the fields with dummy values. The {@code
     * Package} object is then put into the {@code ClassLoader}'s
     * package cache, so we see the same one next time. We don't
     * create {@code Package} objects for {@code null} arguments or
     * for the default package.
     *
     * <p>There is a limited chance that we end up with multiple
     * {@code Package} objects representing the same package: It can
     * happen when when a package is scattered across different JAR
     * files which were loaded by different {@code ClassLoader}
     * instances. This is rather unlikely, and given that this whole
     * thing is more or less a workaround, probably not worth the
     * effort to address.
     *
     * @param name the name of the class
     * @return the package information for the class, or {@code null}
     * if there is no package information available for it
     */
    @Override
    protected synchronized Package getPackage(String name) {
        if (name != null && !name.isEmpty()) {
            Package pack = super.getPackage(name);

            if (pack == null) {
                pack = definePackage(name, "Unknown", "0.0", "Unknown",
                        "Unknown", "0.0", "Unknown", null);
            }

            return pack;
        }

        return null;
    }

    /**
     * @hide
     */
    public String getLdLibraryPath() {
        StringBuilder result = new StringBuilder();
        for (File directory : pathList.getNativeLibraryDirectories()) {
            if (result.length() > 0) {
                result.append(':');
            }
            result.append(directory);
        }

        return result.toString();
    }

    @Override public String toString() {
        return getClass().getName() + "[" + pathList + "]";
    }

    /**
     * Sets the reporter for dex load notifications.
     * Once set, all new instances of BaseDexClassLoader will report upon
     * constructions the loaded dex files.
     *
     * @param newReporter the new Reporter. Setting null will cancel reporting.
     * @hide
     */
    public static void setReporter(Reporter newReporter) {
        reporter = newReporter;
    }

    /**
     * @hide
     */
    public static Reporter getReporter() {
        return reporter;
    }

    /**
     * @hide
     */
    public interface Reporter {
        /**
         * Reports the construction of a BaseDexClassLoader and provides information about the
         * class loader chain.
         * Note that this only reports if all class loader in the chain are BaseDexClassLoader.
         *
         * @param classLoadersChain the chain of class loaders used during the construction of the
         *     class loader. The first element is the BaseDexClassLoader being constructed,
         *     the second element is its parent, and so on.
         * @param classPaths the class paths of the class loaders present in
         *     {@param classLoadersChain}. The first element corresponds to the first class
         *     loader and so on. A classpath is represented as a list of dex files separated by
         *     {@code File.pathSeparator}.
         */
        void report(List<BaseDexClassLoader> classLoadersChain, List<String> classPaths);
    }
}

```

### DexPathList

1、DexPathList源码
```java
package dalvik.system;

/**
 * A pair of lists of entries, associated with a {@code ClassLoader}.
 * One of the lists is a dex/resource path &mdash; typically referred
 * to as a "class path" &mdash; list, and the other names directories
 * containing native code libraries. Class path entries may be any of:
 * a {@code .jar} or {@code .zip} file containing an optional
 * top-level {@code classes.dex} file as well as arbitrary resources,
 * or a plain {@code .dex} file (with no possibility of associated
 * resources).
 *
 * <p>This class also contains methods to use these lists to look up
 * classes and resources.</p>
 */
/*package*/ final class DexPathList {
    private static final String DEX_SUFFIX = ".dex";
    private static final String zipSeparator = "!/";

    /** class definition context */
    private final ClassLoader definingContext;

    /**
     * List of dex/resource (class path) elements.
     * Should be called pathElements, but the Facebook app uses reflection
     * to modify 'dexElements' (http://b/7726934).
     */
    private Element[] dexElements;

    /** List of native library path elements. */
    // Some applications rely on this field being an array or we'd use a final list here
    /* package visible for testing */ NativeLibraryElement[] nativeLibraryPathElements;

    /** List of application native library directories. */
    private final List<File> nativeLibraryDirectories;

    /** List of system native library directories. */
    private final List<File> systemNativeLibraryDirectories;

    /**
     * Exceptions thrown during creation of the dexElements list.
     */
    private IOException[] dexElementsSuppressedExceptions;

    /**
     * Construct an instance.
     *
     * @param definingContext the context in which any as-yet unresolved
     * classes should be defined
     *
     * @param dexFiles the bytebuffers containing the dex files that we should load classes from.
     */
    public DexPathList(ClassLoader definingContext, ByteBuffer[] dexFiles) {
        if (definingContext == null) {
            throw new NullPointerException("definingContext == null");
        }
        if (dexFiles == null) {
            throw new NullPointerException("dexFiles == null");
        }
        if (Arrays.stream(dexFiles).anyMatch(v -> v == null)) {
            throw new NullPointerException("dexFiles contains a null Buffer!");
        }

        this.definingContext = definingContext;
        // TODO It might be useful to let in-memory dex-paths have native libraries.
        this.nativeLibraryDirectories = Collections.emptyList();
        this.systemNativeLibraryDirectories =
                splitPaths(System.getProperty("java.library.path"), true);
        this.nativeLibraryPathElements = makePathElements(this.systemNativeLibraryDirectories);

        ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
        this.dexElements = makeInMemoryDexElements(dexFiles, suppressedExceptions);
        if (suppressedExceptions.size() > 0) {
            this.dexElementsSuppressedExceptions =
                    suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
        } else {
            dexElementsSuppressedExceptions = null;
        }
    }

    /**
     * Constructs an instance.
     *
     * @param definingContext the context in which any as-yet unresolved
     * classes should be defined
     * @param dexPath list of dex/resource path elements, separated by
     * {@code File.pathSeparator}
     * @param librarySearchPath list of native library directory path elements,
     * separated by {@code File.pathSeparator}
     * @param optimizedDirectory directory where optimized {@code .dex} files
     * should be found and written to, or {@code null} to use the default
     * system directory for same
     */
    public DexPathList(ClassLoader definingContext, String dexPath,
            String librarySearchPath, File optimizedDirectory) {
        this(definingContext, dexPath, librarySearchPath, optimizedDirectory, false);
    }

    DexPathList(ClassLoader definingContext, String dexPath,
            String librarySearchPath, File optimizedDirectory, boolean isTrusted) {
        if (definingContext == null) {
            throw new NullPointerException("definingContext == null");
        }

        if (dexPath == null) {
            throw new NullPointerException("dexPath == null");
        }

        if (optimizedDirectory != null) {
            if (!optimizedDirectory.exists())  {
                throw new IllegalArgumentException(
                        "optimizedDirectory doesn't exist: "
                        + optimizedDirectory);
            }

            if (!(optimizedDirectory.canRead()
                            && optimizedDirectory.canWrite())) {
                throw new IllegalArgumentException(
                        "optimizedDirectory not readable/writable: "
                        + optimizedDirectory);
            }
        }

        this.definingContext = definingContext;

        ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
        // save dexPath for BaseDexClassLoader
        this.dexElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory,
                                           suppressedExceptions, definingContext, isTrusted);

        // Native libraries may exist in both the system and
        // application library paths, and we use this search order:
        //
        //   1. This class loader's library path for application libraries (librarySearchPath):
        //   1.1. Native library directories
        //   1.2. Path to libraries in apk-files
        //   2. The VM's library path from the system property for system libraries
        //      also known as java.library.path
        //
        // This order was reversed prior to Gingerbread; see http://b/2933456.
        this.nativeLibraryDirectories = splitPaths(librarySearchPath, false);
        this.systemNativeLibraryDirectories =
                splitPaths(System.getProperty("java.library.path"), true);
        List<File> allNativeLibraryDirectories = new ArrayList<>(nativeLibraryDirectories);
        allNativeLibraryDirectories.addAll(systemNativeLibraryDirectories);

        this.nativeLibraryPathElements = makePathElements(allNativeLibraryDirectories);

        if (suppressedExceptions.size() > 0) {
            this.dexElementsSuppressedExceptions =
                suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
        } else {
            dexElementsSuppressedExceptions = null;
        }
    }

    @Override public String toString() {
        List<File> allNativeLibraryDirectories = new ArrayList<>(nativeLibraryDirectories);
        allNativeLibraryDirectories.addAll(systemNativeLibraryDirectories);

        File[] nativeLibraryDirectoriesArray =
                allNativeLibraryDirectories.toArray(
                    new File[allNativeLibraryDirectories.size()]);

        return "DexPathList[" + Arrays.toString(dexElements) +
            ",nativeLibraryDirectories=" + Arrays.toString(nativeLibraryDirectoriesArray) + "]";
    }

    /**
     * For BaseDexClassLoader.getLdLibraryPath.
     */
    public List<File> getNativeLibraryDirectories() {
        return nativeLibraryDirectories;
    }

    /**
     * Adds a new path to this instance
     * @param dexPath list of dex/resource path element, separated by
     * {@code File.pathSeparator}
     * @param optimizedDirectory directory where optimized {@code .dex} files
     * should be found and written to, or {@code null} to use the default
     * system directory for same
     */
    public void addDexPath(String dexPath, File optimizedDirectory) {
      addDexPath(dexPath, optimizedDirectory, false);
    }

    public void addDexPath(String dexPath, File optimizedDirectory, boolean isTrusted) {
        final List<IOException> suppressedExceptionList = new ArrayList<IOException>();
        final Element[] newElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory,
                suppressedExceptionList, definingContext, isTrusted);

        if (newElements != null && newElements.length > 0) {
            final Element[] oldElements = dexElements;
            dexElements = new Element[oldElements.length + newElements.length];
            System.arraycopy(
                    oldElements, 0, dexElements, 0, oldElements.length);
            System.arraycopy(
                    newElements, 0, dexElements, oldElements.length, newElements.length);
        }

        if (suppressedExceptionList.size() > 0) {
            final IOException[] newSuppressedExceptions = suppressedExceptionList.toArray(
                    new IOException[suppressedExceptionList.size()]);
            if (dexElementsSuppressedExceptions != null) {
                final IOException[] oldSuppressedExceptions = dexElementsSuppressedExceptions;
                final int suppressedExceptionsLength = oldSuppressedExceptions.length +
                        newSuppressedExceptions.length;
                dexElementsSuppressedExceptions = new IOException[suppressedExceptionsLength];
                System.arraycopy(oldSuppressedExceptions, 0, dexElementsSuppressedExceptions,
                        0, oldSuppressedExceptions.length);
                System.arraycopy(newSuppressedExceptions, 0, dexElementsSuppressedExceptions,
                        oldSuppressedExceptions.length, newSuppressedExceptions.length);
            } else {
                dexElementsSuppressedExceptions = newSuppressedExceptions;
            }
        }
    }

    /**
     * Splits the given dex path string into elements using the path
     * separator, pruning out any elements that do not refer to existing
     * and readable files.
     */
    private static List<File> splitDexPath(String path) {
        return splitPaths(path, false);
    }

    /**
     * Splits the given path strings into file elements using the path
     * separator, combining the results and filtering out elements
     * that don't exist, aren't readable, or aren't either a regular
     * file or a directory (as specified). Either string may be empty
     * or {@code null}, in which case it is ignored. If both strings
     * are empty or {@code null}, or all elements get pruned out, then
     * this returns a zero-element list.
     */
    private static List<File> splitPaths(String searchPath, boolean directoriesOnly) {
        List<File> result = new ArrayList<>();

        if (searchPath != null) {
            for (String path : searchPath.split(File.pathSeparator)) {
                if (directoriesOnly) {
                    try {
                        StructStat sb = Libcore.os.stat(path);
                        if (!S_ISDIR(sb.st_mode)) {
                            continue;
                        }
                    } catch (ErrnoException ignored) {
                        continue;
                    }
                }
                result.add(new File(path));
            }
        }

        return result;
    }

    private static Element[] makeInMemoryDexElements(ByteBuffer[] dexFiles,
            List<IOException> suppressedExceptions) {
        Element[] elements = new Element[dexFiles.length];
        int elementPos = 0;
        for (ByteBuffer buf : dexFiles) {
            try {
                DexFile dex = new DexFile(buf);
                elements[elementPos++] = new Element(dex);
            } catch (IOException suppressed) {
                System.logE("Unable to load dex file: " + buf, suppressed);
                suppressedExceptions.add(suppressed);
            }
        }
        if (elementPos != elements.length) {
            elements = Arrays.copyOf(elements, elementPos);
        }
        return elements;
    }

    /**
     * Makes an array of dex/resource path elements, one per element of
     * the given array.
     */
    private static Element[] makeDexElements(List<File> files, File optimizedDirectory,
            List<IOException> suppressedExceptions, ClassLoader loader) {
        return makeDexElements(files, optimizedDirectory, suppressedExceptions, loader, false);
    }


    private static Element[] makeDexElements(List<File> files, File optimizedDirectory,
            List<IOException> suppressedExceptions, ClassLoader loader, boolean isTrusted) {
      Element[] elements = new Element[files.size()];
      int elementsPos = 0;
      /*
       * Open all files and load the (direct or contained) dex files up front.
       */
      for (File file : files) {
          if (file.isDirectory()) {
              // We support directories for looking up resources. Looking up resources in
              // directories is useful for running libcore tests.
              elements[elementsPos++] = new Element(file);
          } else if (file.isFile()) {
              String name = file.getName();

              DexFile dex = null;
              if (name.endsWith(DEX_SUFFIX)) {
                  // Raw dex file (not inside a zip/jar).
                  try {
                      dex = loadDexFile(file, optimizedDirectory, loader, elements);
                      if (dex != null) {
                          elements[elementsPos++] = new Element(dex, null);
                      }
                  } catch (IOException suppressed) {
                      System.logE("Unable to load dex file: " + file, suppressed);
                      suppressedExceptions.add(suppressed);
                  }
              } else {
                  try {
                      dex = loadDexFile(file, optimizedDirectory, loader, elements);
                  } catch (IOException suppressed) {
                      /*
                       * IOException might get thrown "legitimately" by the DexFile constructor if
                       * the zip file turns out to be resource-only (that is, no classes.dex file
                       * in it).
                       * Let dex == null and hang on to the exception to add to the tea-leaves for
                       * when findClass returns null.
                       */
                      suppressedExceptions.add(suppressed);
                  }

                  if (dex == null) {
                      elements[elementsPos++] = new Element(file);
                  } else {
                      elements[elementsPos++] = new Element(dex, file);
                  }
              }
              if (dex != null && isTrusted) {
                dex.setTrusted();
              }
          } else {
              System.logW("ClassLoader referenced unknown path: " + file);
          }
      }
      if (elementsPos != elements.length) {
          elements = Arrays.copyOf(elements, elementsPos);
      }
      return elements;
    }

    /**
     * Constructs a {@code DexFile} instance, as appropriate depending on whether
     * {@code optimizedDirectory} is {@code null}. An application image file may be associated with
     * the {@code loader} if it is not null.
     */
    private static DexFile loadDexFile(File file, File optimizedDirectory, ClassLoader loader,
                                       Element[] elements)
            throws IOException {
        if (optimizedDirectory == null) {
            return new DexFile(file, loader, elements);
        } else {
            String optimizedPath = optimizedPathFor(file, optimizedDirectory);
            return DexFile.loadDex(file.getPath(), optimizedPath, 0, loader, elements);
        }
    }

    /**
     * Converts a dex/jar file path and an output directory to an
     * output file path for an associated optimized dex file.
     */
    private static String optimizedPathFor(File path,
            File optimizedDirectory) {
        /*
         * Get the filename component of the path, and replace the
         * suffix with ".dex" if that's not already the suffix.
         *
         * We don't want to use ".odex", because the build system uses
         * that for files that are paired with resource-only jar
         * files. If the VM can assume that there's no classes.dex in
         * the matching jar, it doesn't need to open the jar to check
         * for updated dependencies, providing a slight performance
         * boost at startup. The use of ".dex" here matches the use on
         * files in /data/dalvik-cache.
         */
        String fileName = path.getName();
        if (!fileName.endsWith(DEX_SUFFIX)) {
            int lastDot = fileName.lastIndexOf(".");
            if (lastDot < 0) {
                fileName += DEX_SUFFIX;
            } else {
                StringBuilder sb = new StringBuilder(lastDot + 4);
                sb.append(fileName, 0, lastDot);
                sb.append(DEX_SUFFIX);
                fileName = sb.toString();
            }
        }

        File result = new File(optimizedDirectory, fileName);
        return result.getPath();
    }

    /*
     * TODO (dimitry): Revert after apps stops relying on the existence of this
     * method (see http://b/21957414 and http://b/26317852 for details)
     */
    @SuppressWarnings("unused")
    private static Element[] makePathElements(List<File> files, File optimizedDirectory,
            List<IOException> suppressedExceptions) {
        return makeDexElements(files, optimizedDirectory, suppressedExceptions, null);
    }

    /**
     * Makes an array of directory/zip path elements for the native library search path, one per
     * element of the given array.
     */
    private static NativeLibraryElement[] makePathElements(List<File> files) {
        NativeLibraryElement[] elements = new NativeLibraryElement[files.size()];
        int elementsPos = 0;
        for (File file : files) {
            String path = file.getPath();

            if (path.contains(zipSeparator)) {
                String split[] = path.split(zipSeparator, 2);
                File zip = new File(split[0]);
                String dir = split[1];
                elements[elementsPos++] = new NativeLibraryElement(zip, dir);
            } else if (file.isDirectory()) {
                // We support directories for looking up native libraries.
                elements[elementsPos++] = new NativeLibraryElement(file);
            }
        }
        if (elementsPos != elements.length) {
            elements = Arrays.copyOf(elements, elementsPos);
        }
        return elements;
    }

    /**
     * Finds the named class in one of the dex files pointed at by
     * this instance. This will find the one in the earliest listed
     * path element. If the class is found but has not yet been
     * defined, then this method will define it in the defining
     * context that this instance was constructed with.
     *
     * @param name of class to find
     * @param suppressed exceptions encountered whilst finding the class
     * @return the named class or {@code null} if the class is not
     * found in any of the dex files
     */
    public Class<?> findClass(String name, List<Throwable> suppressed) {
        for (Element element : dexElements) {
            Class<?> clazz = element.findClass(name, definingContext, suppressed);
            if (clazz != null) {
                return clazz;
            }
        }

        if (dexElementsSuppressedExceptions != null) {
            suppressed.addAll(Arrays.asList(dexElementsSuppressedExceptions));
        }
        return null;
    }

    /**
     * Finds the named resource in one of the zip/jar files pointed at
     * by this instance. This will find the one in the earliest listed
     * path element.
     *
     * @return a URL to the named resource or {@code null} if the
     * resource is not found in any of the zip/jar files
     */
    public URL findResource(String name) {
        for (Element element : dexElements) {
            URL url = element.findResource(name);
            if (url != null) {
                return url;
            }
        }

        return null;
    }

    /**
     * Finds all the resources with the given name, returning an
     * enumeration of them. If there are no resources with the given
     * name, then this method returns an empty enumeration.
     */
    public Enumeration<URL> findResources(String name) {
        ArrayList<URL> result = new ArrayList<URL>();

        for (Element element : dexElements) {
            URL url = element.findResource(name);
            if (url != null) {
                result.add(url);
            }
        }

        return Collections.enumeration(result);
    }

    /**
     * Finds the named native code library on any of the library
     * directories pointed at by this instance. This will find the
     * one in the earliest listed directory, ignoring any that are not
     * readable regular files.
     *
     * @return the complete path to the library or {@code null} if no
     * library was found
     */
    public String findLibrary(String libraryName) {
        String fileName = System.mapLibraryName(libraryName);

        for (NativeLibraryElement element : nativeLibraryPathElements) {
            String path = element.findNativeLibrary(fileName);

            if (path != null) {
                return path;
            }
        }

        return null;
    }

    /**
     * Returns the list of all individual dex files paths from the current list.
     * The list will contain only file paths (i.e. no directories).
     */
    /*package*/ List<String> getDexPaths() {
        List<String> dexPaths = new ArrayList<String>();
        for (Element e : dexElements) {
            String dexPath = e.getDexPath();
            if (dexPath != null) {
                // Add the element to the list only if it is a file. A null dex path signals the
                // element is a resource directory or an in-memory dex file.
                dexPaths.add(dexPath);
            }
        }
        return dexPaths;
    }

    /**
     * Adds a collection of library paths from which to load native libraries. Paths can be absolute
     * native library directories (i.e. /data/app/foo/lib/arm64) or apk references (i.e.
     * /data/app/foo/base.apk!/lib/arm64).
     *
     * Note: This method will attempt to dedupe elements.
     * Note: This method replaces the value of {@link #nativeLibraryPathElements}
     */
    public void addNativePath(Collection<String> libPaths) {
        if (libPaths.isEmpty()) {
            return;
        }
        List<File> libFiles = new ArrayList<>(libPaths.size());
        for (String path : libPaths) {
            libFiles.add(new File(path));
        }
        ArrayList<NativeLibraryElement> newPaths =
                new ArrayList<>(nativeLibraryPathElements.length + libPaths.size());
        newPaths.addAll(Arrays.asList(nativeLibraryPathElements));
        for (NativeLibraryElement element : makePathElements(libFiles)) {
            if (!newPaths.contains(element)) {
                newPaths.add(element);
            }
        }
        nativeLibraryPathElements = newPaths.toArray(new NativeLibraryElement[newPaths.size()]);
    }

    /**
     * Element of the dex/resource path. Note: should be called DexElement, but apps reflect on
     * this.
     */
    /*package*/ static class Element {
        /**
         * A file denoting a zip file (in case of a resource jar or a dex jar), or a directory
         * (only when dexFile is null).
         */
        private final File path;

        private final DexFile dexFile;

        private ClassPathURLStreamHandler urlHandler;
        private boolean initialized;

        /**
         * Element encapsulates a dex file. This may be a plain dex file (in which case dexZipPath
         * should be null), or a jar (in which case dexZipPath should denote the zip file).
         */
        public Element(DexFile dexFile, File dexZipPath) {
            this.dexFile = dexFile;
            this.path = dexZipPath;
        }

        public Element(DexFile dexFile) {
            this.dexFile = dexFile;
            this.path = null;
        }

        public Element(File path) {
          this.path = path;
          this.dexFile = null;
        }

        /**
         * Constructor for a bit of backwards compatibility. Some apps use reflection into
         * internal APIs. Warn, and emulate old behavior if we can. See b/33399341.
         *
         * @deprecated The Element class has been split. Use new Element constructors for
         *             classes and resources, and NativeLibraryElement for the library
         *             search path.
         */
        @Deprecated
        public Element(File dir, boolean isDirectory, File zip, DexFile dexFile) {
            System.err.println("Warning: Using deprecated Element constructor. Do not use internal"
                    + " APIs, this constructor will be removed in the future.");
            if (dir != null && (zip != null || dexFile != null)) {
                throw new IllegalArgumentException("Using dir and zip|dexFile no longer"
                        + " supported.");
            }
            if (isDirectory && (zip != null || dexFile != null)) {
                throw new IllegalArgumentException("Unsupported argument combination.");
            }
            if (dir != null) {
                this.path = dir;
                this.dexFile = null;
            } else {
                this.path = zip;
                this.dexFile = dexFile;
            }
        }

        /*
         * Returns the dex path of this element or null if the element refers to a directory.
         */
        private String getDexPath() {
            if (path != null) {
                return path.isDirectory() ? null : path.getAbsolutePath();
            } else if (dexFile != null) {
                // DexFile.getName() returns the path of the dex file.
                return dexFile.getName();
            }
            return null;
        }

        @Override
        public String toString() {
            if (dexFile == null) {
              return (path.isDirectory() ? "directory \"" : "zip file \"") + path + "\"";
            } else {
              if (path == null) {
                return "dex file \"" + dexFile + "\"";
              } else {
                return "zip file \"" + path + "\"";
              }
            }
        }

        public synchronized void maybeInit() {
            if (initialized) {
                return;
            }

            if (path == null || path.isDirectory()) {
                initialized = true;
                return;
            }

            try {
                urlHandler = new ClassPathURLStreamHandler(path.getPath());
            } catch (IOException ioe) {
                /*
                 * Note: ZipException (a subclass of IOException)
                 * might get thrown by the ZipFile constructor
                 * (e.g. if the file isn't actually a zip/jar
                 * file).
                 */
                System.logE("Unable to open zip file: " + path, ioe);
                urlHandler = null;
            }

            // Mark this element as initialized only after we've successfully created
            // the associated ClassPathURLStreamHandler. That way, we won't leave this
            // element in an inconsistent state if an exception is thrown during initialization.
            //
            // See b/35633614.
            initialized = true;
        }

        public Class<?> findClass(String name, ClassLoader definingContext,
                List<Throwable> suppressed) {
            return dexFile != null ? dexFile.loadClassBinaryName(name, definingContext, suppressed)
                    : null;
        }

        public URL findResource(String name) {
            maybeInit();

            if (urlHandler != null) {
              return urlHandler.getEntryUrlOrNull(name);
            }

            // We support directories so we can run tests and/or legacy code
            // that uses Class.getResource.
            if (path != null && path.isDirectory()) {
                File resourceFile = new File(path, name);
                if (resourceFile.exists()) {
                    try {
                        return resourceFile.toURI().toURL();
                    } catch (MalformedURLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            return null;
        }
    }

    /**
     * Element of the native library path
     */
    /*package*/ static class NativeLibraryElement {
        /**
         * A file denoting a directory or zip file.
         */
        private final File path;

        /**
         * If path denotes a zip file, this denotes a base path inside the zip.
         */
        private final String zipDir;

        private ClassPathURLStreamHandler urlHandler;
        private boolean initialized;

        public NativeLibraryElement(File dir) {
            this.path = dir;
            this.zipDir = null;

            // We should check whether path is a directory, but that is non-eliminatable overhead.
        }

        public NativeLibraryElement(File zip, String zipDir) {
            this.path = zip;
            this.zipDir = zipDir;

            // Simple check that should be able to be eliminated by inlining. We should also
            // check whether path is a file, but that is non-eliminatable overhead.
            if (zipDir == null) {
              throw new IllegalArgumentException();
            }
        }

        @Override
        public String toString() {
            if (zipDir == null) {
                return "directory \"" + path + "\"";
            } else {
                return "zip file \"" + path + "\"" +
                  (!zipDir.isEmpty() ? ", dir \"" + zipDir + "\"" : "");
            }
        }

        public synchronized void maybeInit() {
            if (initialized) {
                return;
            }

            if (zipDir == null) {
                initialized = true;
                return;
            }

            try {
                urlHandler = new ClassPathURLStreamHandler(path.getPath());
            } catch (IOException ioe) {
                /*
                 * Note: ZipException (a subclass of IOException)
                 * might get thrown by the ZipFile constructor
                 * (e.g. if the file isn't actually a zip/jar
                 * file).
                 */
                System.logE("Unable to open zip file: " + path, ioe);
                urlHandler = null;
            }

            // Mark this element as initialized only after we've successfully created
            // the associated ClassPathURLStreamHandler. That way, we won't leave this
            // element in an inconsistent state if an exception is thrown during initialization.
            //
            // See b/35633614.
            initialized = true;
        }

        public String findNativeLibrary(String name) {
            maybeInit();

            if (zipDir == null) {
                String entryPath = new File(path, name).getPath();
                if (IoUtils.canOpenReadOnly(entryPath)) {
                    return entryPath;
                }
            } else if (urlHandler != null) {
                // Having a urlHandler means the element has a zip file.
                // In this case Android supports loading the library iff
                // it is stored in the zip uncompressed.
                String entryName = zipDir + '/' + name;
                if (urlHandler.isEntryStored(entryName)) {
                  return path.getPath() + zipSeparator + entryName;
                }
            }

            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof NativeLibraryElement)) return false;
            NativeLibraryElement that = (NativeLibraryElement) o;
            return Objects.equals(path, that.path) &&
                    Objects.equals(zipDir, that.zipDir);
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, zipDir);
        }
    }
}

```
