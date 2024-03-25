package de.gurkenlabs.utiliti;

import de.gurkenlabs.litiengine.configuration.ConfigurationGroup;
import de.gurkenlabs.litiengine.configuration.ConfigurationGroupInfo;
import de.gurkenlabs.litiengine.util.ColorHelper;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.utiliti.Style.Theme;
import java.awt.Color;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ConfigurationGroupInfo(prefix = "user_")
public class UserPreferences extends ConfigurationGroup {
  public static final float UI_SCALE_MAX = 2.0f;
  public static final float UI_SCALE_MIN = 0.5f;

  private float zoom;
  private boolean showGrid;
  private boolean clampToMap;
  private boolean snapToPixels;
  private boolean snapToGrid;
  private boolean renderBoundingBoxes;
  private boolean renderCustomMapObjects;
  private boolean renderMapIds;
  private boolean renderNames;
  private boolean compressFile;
  private boolean syncMaps;
  private int frameState;
  private int mainSplitter;
  private int selectionEditSplitter;
  private int mapPanelSplitter;
  private int bottomSplitter;
  private int assetsSplitter;
  private int width;
  private int height;

  private float gridLineWidth;
  private String gridColor;
  private int snapDivision;

  private Path lastGameFile;
  private Path[] lastOpenedFiles;
  private float uiScale;

  private Theme theme;

  public UserPreferences() {
    this.zoom = 1.0f;
    this.showGrid = true;
    this.clampToMap = true;
    this.snapToPixels = true;
    this.snapToGrid = true;
    this.renderBoundingBoxes = true;
    this.renderNames = true;
    this.lastGameFile = Path.of(".");
    this.lastOpenedFiles = new Path[10];
    this.compressFile = false;
    this.gridLineWidth = 1.0f;
    this.gridColor = ColorHelper.encode(Style.COLOR_DEFAULT_GRID);
    this.snapDivision = 1;
    this.setUiScale(1.0f);
    this.setTheme(Theme.DARK);
  }

  public void addOpenedFile(Path str) {
    // ensure max 10 elements
    List<Path> newFiles = new ArrayList<>();
    for (int i = 0; i <= this.lastOpenedFiles.length; i++) {
      newFiles.add(null);
    }

    // make space for the new element and clear all duplicates
    for (int i = 1; i < this.lastOpenedFiles.length; i++) {
      if (this.lastOpenedFiles[i - 1] != null && this.lastOpenedFiles[i - 1].equals(str)) {
        continue;
      }

      if (this.lastOpenedFiles[i - 1] == null) {
        newFiles.add(i, null);
      } else {
        newFiles.add(i, this.lastOpenedFiles[i - 1]);
      }
    }

    // add the new element
    newFiles.addFirst(str);
    newFiles.removeAll(Collections.singleton(null));
    // clear array
    this.lastOpenedFiles = new Path[10];

    // fill array
    for (int i = 0; i < newFiles.size(); i++) {
      this.lastOpenedFiles[i] = newFiles.get(i);
    }
  }

  public float getZoom() {
    return this.zoom;
  }

  public void setZoom(float zoom) {
    this.zoom = zoom;
  }

  public boolean showGrid() {
    return this.showGrid;
  }

  public void setShowGrid(boolean showGrid) {
    this.showGrid = showGrid;
  }

  public boolean clampToMap() {
    return this.clampToMap;
  }

  public boolean snapToPixels() {
    return this.snapToPixels;
  }

  public boolean snapToGrid() {
    return this.snapToGrid;
  }

  public void setClampToMap(boolean snapMap) {
    this.clampToMap = snapMap;
  }

  public void setSnapToPixels(boolean snapPixels) {
    this.snapToPixels = snapPixels;
  }

  public void setSnapToGrid(boolean snapGrid) {
    this.snapToGrid = snapGrid;
  }

  public boolean renderBoundingBoxes() {
    return this.renderBoundingBoxes;
  }

  public void setRenderBoundingBoxes(boolean renderBoundingBoxes) {
    this.renderBoundingBoxes = renderBoundingBoxes;
  }

  public boolean renderCustomMapObjects() {
    return this.renderCustomMapObjects;
  }

  public void setRenderCustomMapObjects(boolean renderCustomMapObjects) {
    this.renderCustomMapObjects = renderCustomMapObjects;
  }

  public boolean renderMapIds() {
    return this.renderMapIds;
  }

  public void setRenderMapIds(boolean renderIds) {
    this.renderMapIds = renderIds;
  }

  public Path getLastGameFile() {
    return lastGameFile;
  }

  public void setLastGameFile(Path lastGameFile) {
    this.lastGameFile = lastGameFile;
  }

  public Path[] getLastOpenedFiles() {
    return lastOpenedFiles;
  }

  public void clearOpenedFiles() {
    this.lastOpenedFiles = new Path[10];
  }

  public void setLastOpenedFiles(Path[] lastOpenedFiles) {
    this.lastOpenedFiles = lastOpenedFiles;
  }

  public boolean compressFile() {
    return compressFile;
  }

  public void setCompressFile(boolean compressFile) {
    this.compressFile = compressFile;
  }

  public float getGridLineWidth() {
    return this.gridLineWidth;
  }

  public Color getGridColor() {
    return ColorHelper.decode(this.gridColor);
  }

  public int getSnapDivision() {
    return this.snapDivision;
  }

  public void setGridLineWidth(float gridLineWidth) {
    this.gridLineWidth = gridLineWidth;
  }

  public void setGridColor(String gridColor) {
    this.gridColor = gridColor;
  }

  public void setSnapDivision(int snapDivision) {
    this.snapDivision = snapDivision;
  }

  public boolean syncMaps() {
    return syncMaps;
  }

  public void setSyncMaps(boolean syncMaps) {
    this.syncMaps = syncMaps;
  }

  public int getMainSplitterPosition() {
    return mainSplitter;
  }

  public void setMainSplitter(int mainSplitter) {
    this.mainSplitter = mainSplitter;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getSelectionEditSplitter() {
    return selectionEditSplitter;
  }

  public void setSelectionEditSplitter(int selectionEditSplitter) {
    this.selectionEditSplitter = selectionEditSplitter;
  }

  public int getMapPanelSplitter() {
    return mapPanelSplitter;
  }

  public void setMapPanelSplitter(int mapPanelSplitter) {
    this.mapPanelSplitter = mapPanelSplitter;
  }

  public int getBottomSplitter() {
    return bottomSplitter;
  }

  public void setBottomSplitter(int bottomSplitter) {
    this.bottomSplitter = bottomSplitter;
  }

  public int getAssetsSplitter() {
    return assetsSplitter;
  }

  public void setAssetsSplitter(int assetsSplitter) {
    this.assetsSplitter = assetsSplitter;
  }

  public int getFrameState() {
    return frameState;
  }

  public void setFrameState(int frameState) {
    this.frameState = frameState;
  }

  public boolean renderNames() {
    return renderNames;
  }

  public void setRenderNames(boolean renderNames) {
    this.renderNames = renderNames;
  }

  public float getUiScale() {
    return uiScale;
  }

  public void setUiScale(float uiScale) {
    this.uiScale = MathUtilities.clamp(uiScale, UI_SCALE_MIN, UI_SCALE_MAX);
  }

  public Theme getTheme() {
    return theme;
  }

  public void setTheme(Theme theme) {
    this.theme = theme;
  }
}
