package de.gurkenlabs.litiengine.configuration;

import de.gurkenlabs.litiengine.Game;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
  private static final Logger log = Logger.getLogger(Configuration.class.getName());
  private static final Path DEFAULT_CONFIGURATION_FILE = Path.of("config.properties");

  private final List<ConfigurationGroup> configurationGroups;
  private final Path filePath;

  /**
   * Initializes a new instance of the {@code Configuration} class with the default file path.
   *
   * @param configurationGroups The configuration groups managed by this instance.
   */
  public Configuration(final ConfigurationGroup... configurationGroups) {
    this(DEFAULT_CONFIGURATION_FILE, configurationGroups);
  }

  /**
   * Initializes a new instance of the {@code Configuration} class.
   *
   * @param filePath            The path of the file from which to load the settings.
   * @param configurationGroups The configuration groups managed by this instance.
   */
  public Configuration(final Path filePath, final ConfigurationGroup... configurationGroups) {
    this.filePath = filePath;
    this.configurationGroups = new ArrayList<>();
    Collections.addAll(getConfigurationGroups(), configurationGroups);
  }

  /**
   * Gets the strongly typed configuration group if it was previously added to the configuration.
   *
   * @param <T>        The type of the config group.
   * @param groupClass The class that provides the generic type for this method.
   * @return The configuration group of the specified type or null if none can be found.
   */
  public <T extends ConfigurationGroup> T getConfigurationGroup(final Class<T> groupClass) {
    return getConfigurationGroups().stream().filter(groupClass::isInstance).findFirst().map(groupClass::cast).orElse(null);
  }

  public ConfigurationGroup getConfigurationGroup(final String prefix) {
    return getConfigurationGroups().stream().filter(group -> group.getPrefix().equals(prefix)).findFirst().orElse(null);
  }

  /**
   * Gets all {@code ConfigurationGroups} from the configuration.
   *
   * @return All config groups.
   */
  public List<ConfigurationGroup> getConfigurationGroups() {
    return configurationGroups;
  }

  /**
   * Adds the specified configuration group to the configuration.
   *
   * @param group The group to add.
   */
  public void add(ConfigurationGroup group) {
    getConfigurationGroups().add(group);
  }

  /**
   * Gets the path of the file to which this configuration is saved.
   *
   * @return The path of the configuration file.
   * @see #save()
   */
  public Path getFilePath() {
    return this.filePath;
  }

  /**
   * Tries to load the configuration from file in the application folder. If none exists, it tries to load the file from any resource folder. If none
   * exists, it creates a new configuration file in the application folder.
   */
  public void load() {
    if (Files.notExists(getFilePath())) {
      createDefaultSettingsFile();
    } else {
      try (InputStream in = Files.newInputStream(getFilePath(), StandardOpenOption.READ)) {
        final Properties properties = new Properties();
        properties.load(in);
        initializeSettingsByProperties(properties);
        log.log(Level.INFO, "Configuration {0} loaded", this.getFilePath());
      } catch (IOException e) {
        log.log(Level.SEVERE, e.getMessage(), e);
      }

    }
  }

  /**
   * Saves this configuration to a file with the specified name of this instance (config.properties is the engines default config file).
   *
   * @see #getFilePath()
   * @see Configuration#DEFAULT_CONFIGURATION_FILE
   */
  public void save() {
    try (OutputStream out = Files.newOutputStream(getFilePath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
      StandardOpenOption.TRUNCATE_EXISTING)) {
      for (final ConfigurationGroup group : getConfigurationGroups()) {
        if (!Game.isDebug() && group.isDebug()) {
          continue;
        }
        storeConfigurationGroup(out, group);
      }
      log.log(Level.INFO, "Configuration {0} saved", getFilePath());
    } catch (IOException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }


  private static void storeConfigurationGroup(final OutputStream out, final ConfigurationGroup group) {
    try {
      final Properties groupProperties = new CleanProperties();
      group.storeProperties(groupProperties);
      groupProperties.store(out, group.getPrefix() + "SETTINGS");
      out.flush();
    } catch (final IOException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * Creates a default settings file. This method attempts to create a default settings file at the location specified by {@code getFilePath()}. It
   * writes the configuration groups returned by {@code getConfigurationGroups()} to the file. If the file is successfully created, an informational
   * message is logged. If an IOException occurs during this process, a severe error message is logged.
   */
  private void createDefaultSettingsFile() {
    try (OutputStream out = Files.newOutputStream(getFilePath(), StandardOpenOption.WRITE)) {
      getConfigurationGroups().forEach(g -> storeConfigurationGroup(out, g));
      log.log(Level.INFO, "Default configuration {0} created", this.getFilePath());
    } catch (IOException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * Initializes settings based on the provided properties. This method iterates over the provided properties and initializes each configuration group
   * whose prefix matches the start of the property key.
   *
   * @param properties the properties to use for initialization
   */
  private void initializeSettingsByProperties(final Properties properties) {
    properties.stringPropertyNames().forEach(key -> getConfigurationGroups().stream()
      .filter(group -> key.startsWith(group.getPrefix()))
      .forEach(group -> group.initializeByProperty(key, properties.getProperty(key))));
  }
}
