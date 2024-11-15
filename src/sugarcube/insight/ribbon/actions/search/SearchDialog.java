package sugarcube.insight.ribbon.actions.search;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import sugarcube.common.system.log.Log;
import sugarcube.common.data.collections.Str;
import sugarcube.common.graphics.geom.Rectangle3;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.interfaces.OnClose;
import sugarcube.common.system.io.File3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.controls.FxListView;
import sugarcube.common.ui.fx.dialogs.FxWindow;
import sugarcube.common.ui.fx.event.FxHandle;
import sugarcube.common.ui.fx.event.FxKeyboard;
import sugarcube.common.ui.fx.fluent.FluentLV;
import sugarcube.insight.core.FxGUI;
import sugarcube.insight.core.IS;
import sugarcube.formats.ocd.objects.OCDImage;
import sugarcube.formats.ocd.objects.OCDPage;
import sugarcube.formats.ocd.objects.OCDTextBlock;
import sugarcube.formats.pdf.resources.icons.Icon;

public class SearchDialog extends FxWindow
{
  private @FXML BorderPane rootPane;
  private @FXML ProgressBar progressBar;
  private @FXML TextField searchField;
  private @FXML ToggleButton caseTg, spaceTg, accentTg, allTg, prevTg, currentTg, nextTg;
  private @FXML Button searchBt, prevFolderBt, nextFolderBt, prevDocBt, nextDocBt;
  private @FXML Label folderLabel, fileLabel;
  private FxListView<SearchResult> listView = new FxListView<SearchResult>();
  private int zoom = 100;
  private boolean searching = false;
  private boolean stopSearch = false;

  private FxGUI gui;

  public SearchDialog(FxGUI gui, OnClose onClose)
  {
    super("Search Dialog", true, gui.window(), true);
    IS.Darky(windowPane, rootPane);
    this.gui = gui;
    this.icon(Icon.Image(Icon.SEARCH, 48, Color3.ANTHRACITE));
    this.minSize(300, 220);
    this.noModality();

    rootPane.setCenter(listView);

    progressBar.setMaxWidth(Double.MAX_VALUE);
    progressBar.setMaxHeight(5);

    FluentLV<SearchResult> fluent = new FluentLV<SearchResult>(listView);
    fluent.cell(list -> new SearchListCell());
    fluent.listen((obs, old, val) -> searchResultSelected((SearchResult) val));
    fluent.onMouseClicked(2, e -> searchResultSelected(null));

    listView.addEventFilter(ScrollEvent.ANY, e -> {
      if (e.isControlDown())
      {
        e.consume();
        zoom += e.getDeltaY() > 0 ? 10 : -10;
        zoom = (zoom < 25 ? 25 : (zoom > 200 ? 200 : zoom));
        Fx.Run(() -> {
          for (SearchResult result : listView)
            result.width(result.imageWidth * zoom / 100);
        });
      }
    });

    searchField.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER)
        search();
    });

    Icon.SEARCH.set(searchBt, 24, 100, "Search", Color3.DUST_WHITE, e -> search());

    int iconSize = 16;
    prevFolderBt.setGraphic(Icon.CHEVRON_LEFT.get(iconSize));
    nextFolderBt.setGraphic(Icon.CHEVRON_RIGHT.get(iconSize));
    prevFolderBt.setOnAction(e -> loadNextFolder(false));
    nextFolderBt.setOnAction(e -> loadNextFolder(true));
    prevDocBt.setGraphic(Icon.CHEVRON_LEFT.get(iconSize));
    nextDocBt.setGraphic(Icon.CHEVRON_RIGHT.get(iconSize));
    prevDocBt.setOnAction(e -> loadNextOCD(false));
    nextDocBt.setOnAction(e -> loadNextOCD(true));

    caseTg.setTooltip(new Tooltip("Case Sensitive"));
    spaceTg.setTooltip(new Tooltip("Space Sensitive"));
    accentTg.setTooltip(new Tooltip("Accent Sensitive"));
    allTg.setTooltip(new Tooltip("All Pages"));
    prevTg.setTooltip(new Tooltip("Previous Pages"));
    currentTg.setTooltip(new Tooltip("Current Page"));
    nextTg.setTooltip(new Tooltip("Following Pages"));

    FxHandle.Get(scene).key(kb -> {
      if (kb.isState(FxKeyboard.UP) && kb.isControlDown())
      {
        if (kb.isCode(KeyCode.S))
          gui.env().saveOCD(gui.env().ribbon());
        else if (kb.isCode(KeyCode.B))
          gui.env().bookmarkPage();
      }
    });

    this.clearResults();
    this.refresh();
    this.dnd();
    this.setOnClose(onClose);
    this.show();
  }

  public void refresh()
  {
    folderLabel.setText(gui.ocd().fileDirectory().path());
    fileLabel.setText(gui.ocd().fileName());
  }

  public void searchResultSelected(SearchResult res)
  {
    SearchResult result = res == null ? this.listView.getSelectionModel().getSelectedItem() : res;
    gui.ribbon().pager.interactor.clear();
    if (result == null)
    {
      gui.env().loadPage(1, () -> {
      });
    } else
    {
      gui.env().loadPage(result.pageNb, () -> {
        gui.ribbon().pager.interactor.restart(result.box);
        gui.ribbon().env().gui.ensureVisible(gui.ribbon().pager.interactor);
        // just for usability
        // node.requestFocus();
      });
    }
  }

  public SearchScope scope()
  {
    return new SearchScope(gui.pageNb(), allTg.isSelected(), prevTg.isSelected(), currentTg.isSelected(), nextTg.isSelected());
  }

  public SearchNorm norm()
  {
    return new SearchNorm(caseTg.isSelected(), spaceTg.isSelected(), accentTg.isSelected());
  }

  public void search()
  {
    search(scope(), norm());
  }

  public void search(SearchScope scope, SearchNorm norm, String... search)
  {
    if (searching)
    {
      stopSearch = true;
      return;
    }

    if (search.length == 0)
      search = searchField.getText().trim().split(" ");

    this.searching = true;
    this.clearResults();

    for (int i = 0; i < search.length; i++)
      if (!Str.HasChar(search[i] = norm.apply(search[i])))
        search[i] = null;

    this.searchPage(scope.allPages || scope.prevPages ? gui.ocd().firstPage() : gui.page(), scope, norm, search);
    // env.updatePage(page == null ? env.ocd.lastPage() : page);
  }

  public void addResult(OCDPage page, Image3 image, Rectangle3 box)
  {
    String filename = page.entryFilename();
    SearchResult result = new SearchResult(page, image, box);
    this.listView.getItems().add(result);
  }

  public void searchPage(OCDPage page, SearchScope scope, SearchNorm norm, String... search)
  {
    Log.debug(this, ".search - page=" + (page == null ? "null" : page.entryFilename()));
    boolean inMem = page.ensureInMemory();
    Image3 image = null;
    if (search.length > 0)
    {
      for (OCDTextBlock block : page.blocks())
      {
        String text = norm.apply(block.uniString(true));
        for (String token : search)
          if (token != null && (text.contains(token) || token.startsWith(".") && block.isClassname(token.substring(1))
              || token.startsWith("#") && block.isID(token.substring(1))))
          {
            if (image == null)
              image = page.createImage(2);
            addResult(page, image.crop(block.bounds().inflate(10).scale(2)), block.bounds());
          }
      }

      for (OCDImage img : page.images())
      {
        for (String token : search)
          if (token != null && token.startsWith("@image"))
          {
            if (image == null)
              image = page.createImage(2);
            addResult(page, image.crop(img.bounds().inflate(10).scale(2)), img.bounds());
          }
      }
    }

    if (!inMem)
      page.freeFromMemory();

    progressBar.setProgress(page.number() / (double) page.nbOfPages());

    boolean doContinue = !stopSearch && page.hasNext();

    if (doContinue && (scope.currPage || scope.prevPages) && scope.pageNb == page.number())
      doContinue = false;

    if (doContinue)
      Fx.Run(() -> searchPage(page.next(), scope, norm, search));
    else
    {
      this.stopSearch = false;
      this.searching = false;
    }
    // else if (jumpCheck.isSelected())
    // tab.env().loadNextOCD(false, true, () -> search(search));

  }

  // @Override
  // public String crawl(OCDPage page)
  // {
  // Log.debug(this, ".crawl - page=" + (page == null ? "null" :
  // page.entryFilename()));
  // if (page != null)
  // {
  // // this.modelize(page);
  // String styles = findField.getText();
  // if (Text.HasData(styles))
  // {
  // if
  // (page.content().blocks().classnamed(styles.split("\\s*,\\s*")).isPopulated())
  // return styles;
  // } else
  // {
  //
  // String stopMessage = "";
  //
  // boolean inMem = page.ensureInMemory();
  // OCDPageAnalyser analyser = new OCDPageAnalyser().update(page);
  //
  // OCDBlocks blocks =
  // analyser.blocks().regex("^.?.?.?(Landesrecht|LANDESRECHT|DIRITTO
  // FEDERALE|DIRITTO INTERNO|Diritto interno)");
  //
  // OCDBlocks systematic = blocks.classnamed("classement-systematique");
  // if (systematic.isPopulated())
  // stopMessage = "yes";
  //
  // // OCDBlocks cols = blocks.classnamed("column");
  // //
  // // if (cols.isEmpty())
  // // stopMessage = "cols";
  // //
  // // for (OCDTextBlock block : cols)
  // // {
  // // if (block.bounds().minX() < 3)
  // // stopMessage = "cols";
  // // if (block.bounds().maxX() > page.width - 3)
  // // stopMessage = "cols";
  // // }
  //
  // if (!inMem)
  // page.freeFromMemory();
  //
  // return stopMessage;
  //
  // }
  // } else if (this.autojumpCheck.isSelected())
  // {
  // env.loadNextOCD(autosaveCheck.isSelected(), true, () -> crawl(true));
  // }
  //
  // return "";
  // }

  public SearchDialog clearResults()
  {
    this.listView.clearItems();
    this.listView.getItems().add(null);
    return this;
  }

  public File3 nextFolder(boolean forward)
  {
    File3 file = gui.ocd().file();
    File3 folder = file.directory();
    File3 parent = folder.parent().directory();

    String folderName = folder.getName();

    File3 old = null;
    for (File3 next : parent.listFiles())
    {
      if (next.isDirectory())
      {
        if (forward)
        {
          if (old != null && old.getName().equals(folderName))
            return next;
        } else
        {
          if (old != null && next.getName().equals(folderName))
            return old;
        }
        old = next;
      }
    }

    return null;
  }

  public void loadNextFolder(boolean forward)
  {
    File3 folder = nextFolder(forward);
    if (folder != null)
    {
      File3 file = folder.files(".ocd").first();
      if (file != null)
        gui.env().load(file, 1, () -> refresh());
    }
  }

  public void loadNextOCD(boolean forward)
  {
    gui.env().loadNextOCD(false, forward, gui.env().ribbon(), () -> refresh());
  }

  public void close()
  {
    Log.debug(this, ".close");
    this.listView.clearItems();
    super.close();
  }

  @Override
  public void onClose()
  {
    Log.debug(this, ".onClose");
    super.onClose();
  }

  public static void Show(FxGUI gui)
  {
    if (gui.searchDialog == null)
      gui.searchDialog = new SearchDialog(gui, () -> {
        Log.debug(SearchDialog.class, ".Show - anonym onClose");
        gui.searchDialog.close();
        gui.searchDialog = null;
        gui.refresh();
      });
    gui.searchDialog.toFront();
  }
}
