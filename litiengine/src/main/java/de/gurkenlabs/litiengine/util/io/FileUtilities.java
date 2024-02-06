package de.gurkenlabs.litiengine.util.io;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public final class FileUtilities {
  private static final Logger log = Logger.getLogger(FileUtilities.class.getName());
  private static final String[] DIR_BLACKLIST = new String[] {"\\bin", "\\screenshots"};
  private static final String FILE_SEPARATOR_WIN = "\\";
  private static final String FILE_SEPARATOR = "/";

  private FileUtilities() {
    throw new UnsupportedOperationException();
  }

  public static void deleteDir(final Path dir) {
    if (Files.notExists(dir)) {
      log.log(Level.WARNING, "Tried to delete directory {0} - not found.", dir);
      return;
    }
    try (Stream<Path> pathStream = Files.walk(dir)) {
      pathStream.sorted(Comparator.reverseOrder()).forEach(f -> {
        try {
          Files.delete(f);
        } catch (IOException e) {
          log.log(Level.SEVERE, e.getMessage(), e);
        }
      });
    } catch (IOException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
    }
  }

  /**
   * Recursively searches for files with specific names or extensions in a given directory. You can either specify a file name such as "file.txt" or a
   * file extension such as ".txt" or even just "txt" because matches are determined using {@link java.lang.String#endsWith}. This search is not
   * case-sensitive.
   *
   * @param dir   the directory to start the search from. It's a {@link java.nio.file.Path} object.
   * @param files the names or file extensions of the files to search for. It's a varargs parameter, so you can specify multiple file names or
   *              extensions.
   * @return a list of {@link java.nio.file.Path} objects representing the absolute paths of the found files. If an IOException occurs during the
   * search, it logs the exception and returns an empty list.
   */
  public static List<Path> findFiles(final Path dir, final String... files) {
    try (Stream<Path> pathStream = Files.find(dir, Integer.MAX_VALUE,
      (path, attr) -> !isBlackListedDirectory(path)
        && Files.isRegularFile(path)
        && Arrays.stream(files).map(String::toLowerCase).anyMatch(file -> path.toString().toLowerCase().endsWith(file)))) {
      return pathStream.toList();
    } catch (IOException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      return List.of();
    }
  }

  public static String getExtension(final Path file) {
    return getExtension(file.getFileName());
  }

  public static String getExtension(final String path) {
    final String fileName = getFileName(path, true);
    if (!fileName.contains(".")) {
      return "";
    }
    try {
      return fileName.substring(fileName.lastIndexOf('.') + 1);
    } catch (final Exception e) {
      return "";
    }
  }

  public static String getFileName(URL path) {
    return getFileName(path.getPath());
  }

  public static String getFileName(final String path) {
    return getFileName(path, false);
  }

  public static String getFileName(final String path, boolean extension) {
    if (path == null
      || path.isEmpty()
      || path.endsWith(FILE_SEPARATOR_WIN)
      || path.endsWith(FILE_SEPARATOR)) {
      return "";
    }

    String name = path;

    if (!extension) {
      final int pos = name.lastIndexOf('.');
      if (pos > 0) {
        name = name.substring(0, pos);
      }
    }

    final int lastBackslash = name.lastIndexOf(FILE_SEPARATOR);
    if (lastBackslash != -1) {
      name = name.substring(lastBackslash + 1);
    } else {
      final int lastForwardSlash = name.lastIndexOf(FILE_SEPARATOR_WIN);
      if (lastForwardSlash != -1) {
        name = name.substring(lastForwardSlash + 1);
      }
    }

    return name;
  }


  /**
   * This method combines the specified basepath with the parts provided as arguments. The output will use the path separator of the current system;
   *
   * @param basePath The base path for the combined path.
   * @param paths    The parts of the path to be constructed.
   * @return The combined path.
   */
  public static String combine(String basePath, final String... paths) {
    basePath = basePath.replace(FILE_SEPARATOR_WIN, FILE_SEPARATOR);
    try {

      URI uri = new URI(basePath.replace(" ", "%20"));

      for (String path : paths) {
        if (path == null) {
          continue;
        }

        path = path.replace(FILE_SEPARATOR_WIN, FILE_SEPARATOR);

        uri = uri.resolve(path.replace(" ", "%20"));
      }

      return uri.toString().replace("%20", " ");
    } catch (URISyntaxException e) {
      log.log(Level.SEVERE, e.getMessage(), e);
      return basePath;
    }
  }

  private static boolean isBlackListedDirectory(Path path) {
    return Arrays.stream(DIR_BLACKLIST).anyMatch(black -> path.toAbsolutePath().toString().contains(black));
  }

  public static String humanReadableByteCount(long bytes) {
    return humanReadableByteCount(bytes, false);
  }

  public static String humanReadableByteCount(long bytes, boolean decimal) {
    int unit = decimal ? 1000 : 1024;
    if (bytes < unit) {
      return bytes + " bytes";
    }
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    String pre = new String[] {"K", "M", "G", "T", "P", "E"}[exp - 1];
    pre = decimal ? pre : pre + "i";
    return String.format(Locale.ROOT, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
  }
}
