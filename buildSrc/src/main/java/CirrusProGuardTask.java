import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import proguard.gradle.ProGuardTask;

/*
Refs:
https://github.com/Guardsquare/proguard/blob/ccab80e755d2af4547a8af0821c2c50e32122470/examples/gradle/proguard.gradle
https://docs.gradle.org/current/userguide/custom_tasks.html#sec:packaging_a_task_class
 */
public abstract class CirrusProGuardTask extends ProGuardTask {

  private static final String JAR_EXT = ".jar";
  private static final String JMOD_EXT = ".jmod";
  private static final String LIBS_DIR = "libs";
  private static final String DELIMITER = "-";
  private static final String PROGUARD = "proguard";
  private static final String RUNTIME_CLASSPATH = "runtimeClasspath";
  private static final String JAR_FILTER = "jarfilter";
  private static final String JAR_FILTER_PATTERN = "!**.jar";
  private static final String FILTER = "filter";
  private static final String FILTER_PATTERN = "!module-info.class";
  private static final String JAVA_HOME = "java.home";
  private static final String JMODS = "jmods";
  private static final String JAVA_BASE = "java.base";
  boolean debugEnabled = false;

  @Override
  public void proguard() throws Exception {
    verbose();
    // Specify the input jars, output jars, and library jars.
    injars(inJarsPath());
    outjars(outJarsPath());
    libraryjars(libraryFilterArgs(), libraryJarFiles());
    // Since Gradle 3.4+ with the deprecation of "compile" in favor of "implementation"
    // Ref: https://stackoverflow.com/a/48067438
    libraryjars(getProjectRuntimeConfigurationFiles());
    // The entry point to the application.
    keep(getEntryPoint().get());
    if (getDebugEnabled()) {
      dontoptimize();
      dontobfuscate();
    } else {
      // Allow methods with same signature, except return type, to get the same obfuscation name.
      overloadaggressively();
      // Put all obfuscated classes into the nameless root package.
      repackageclasses();
      // Allow classes and class members to be made public.
      allowaccessmodification();
      optimizationpasses(3);
    }
    super.proguard();
  }

  private Map<String, String> libraryFilterArgs() {
    return Map.of(JAR_FILTER, JAR_FILTER_PATTERN, FILTER, FILTER_PATTERN);
  }

  private String libraryJarFiles() {
    // As of Java 9, the runtime classes are packaged in modular jmod files.
    return Paths.get(System.getProperty(JAVA_HOME), JMODS, JAVA_BASE) + JMOD_EXT;
  }

  private String inJarsPath() {
    return jarPath(formatJarFilename(getProject().getName(), getProject().getVersion()));
  }

  private String outJarsPath() {
    return jarPath(formatJarFilename(getProject().getName(), PROGUARD, getProject().getVersion()));
  }

  private String jarPath(String filename) {
    return Paths.get(getProject().getBuildDir().toString(), LIBS_DIR, filename).toString();
  }

  private String formatJarFilename(Object... parts) {
    String name = Arrays.stream(parts).map(Object::toString).collect(Collectors.joining(DELIMITER));
    return name + JAR_EXT;
  }

  @Input
  public abstract Property<String> getEntryPoint();

  @Input
  public boolean getDebugEnabled() {
    return debugEnabled;
  }

  private Set<File> getProjectRuntimeConfigurationFiles() {
    ConfigurationContainer configurations = getProject().getConfigurations();
    return Objects.requireNonNull(configurations.findByName(RUNTIME_CLASSPATH)).getFiles();
  }
}
