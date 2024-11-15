package sugarcube.common.ui.fx.dialogs;

import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import sugarcube.common.data.collections.Files3;
import sugarcube.common.data.collections.Str;
import sugarcube.common.system.io.File3;

import java.io.File;
import java.util.List;

public class FxFileChooser
{

    public interface Filable
    {
        void file(File3 file);
    }

    private FileChooser chooser;
    private Filable filable;

    public FxFileChooser()
    {
        chooser = new FileChooser();
    }

    public FxFileChooser(String title)
    {
        this();
        chooser.setTitle(title);
    }

    public FxFileChooser(String title, String directory)
    {
        this(title);
        this.dir(directory);
    }

    public FxFileChooser title(String title)
    {
        chooser.setTitle(title);
        return this;
    }

    public FxFileChooser workDir()
    {
        return this.dir(File3.USER_WORK);
    }

    public FxFileChooser name(String name)
    {
        if (!Str.IsVoid(name))
            this.chooser.setInitialFileName(name);
        return this;
    }

    public FxFileChooser imageExtensionFilter()
    {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        chooser.getExtensionFilters().add(filter);
        return this;
    }

    public FxFileChooser ocdExtensionFilter()
    {
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("OCD File", "*.ocd");
        chooser.getExtensionFilters().add(filter);
        return this;
    }

    public FxFileChooser dir(String path)
    {
        return Str.IsVoid(path) ? this : this.dir(new File3(path).directory());
    }

    public FxFileChooser dir(File file)
    {
        if (file != null)
            this.chooser.setInitialDirectory(file);
        return this;
    }

    public FxFileChooser file(File3 file)
    {
        if (file == null)
            return this;
        dir(file.directory());
        if (!file.isDirectory())
            name(file.name());
        return this;
    }

    public FxFileChooser path(String path)
    {
        if (!Str.IsVoid(path))
        {
            dir(File3.directory(path));
            name(File3.Filename(path));
        }
        return this;
    }

    public FxFileChooser act(Filable filable)
    {
        this.filable = filable;
        return this;
    }

    public File3 open(Node node)
    {
        return open(node.getScene().getWindow());
    }

    public File3 open(Window owner)
    {
        return filer(chooser.showOpenDialog(owner));
    }

    public File3 save(Window owner)
    {
        return filer(chooser.showSaveDialog(owner));
    }

    public Files3 multi(Window owner)
    {
        List<File> list = chooser.showOpenMultipleDialog(owner);
        Files3 files = new Files3();
        if (list != null)
            for (File file : list)
                if (file != null)
                    files.add(filer(new File3(file)));
        return files;
    }

    private File3 filer(File file)
    {
        File3 f = File3.Wrap(file);
        if (filable != null && f != null)
            filable.file(f);
        return f;
    }

    public static FxFileChooser Get(String title)
    {
        return new FxFileChooser(title);
    }

    public static FxFileChooser Get(String title, String directory)
    {
        return new FxFileChooser(title, directory);
    }
}
