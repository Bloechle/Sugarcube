package jj2000.j2k.decoder;

import sugarcube.common.graphics.Image3;
import jj2000.colorspace.ColorSpace;
import jj2000.colorspace.ColorSpaceMapper;
import jj2000.icc.ICCProfiler;
import jj2000.j2k.ModuleSpec;
import jj2000.j2k.codestream.HeaderInfo;
import jj2000.j2k.codestream.reader.BitstreamReaderAgent;
import jj2000.j2k.codestream.reader.HeaderDecoder;
import jj2000.j2k.entropy.decoder.EntropyDecoder;
import jj2000.j2k.fileformat.reader.FileFormatReader;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.ImgDataConverter;
import jj2000.j2k.image.invcomptransf.InvCompTransf;
import jj2000.j2k.image.output.ImgWriter;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.quantization.dequantizer.Dequantizer;
import jj2000.j2k.roi.ROIDeScaler;
import jj2000.j2k.util.FacilityManager;
import jj2000.j2k.util.ISRandomAccessIO;
import jj2000.j2k.util.MsgLogger;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.synthesis.InverseWT;
import sugarcube.formats.pdf.reader.pdf.node.image.PDFImage;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class is the main class of JJ2000's decoder. It instantiates all objects and performs the decoding operations. It then writes the image to the output
 * file or displays it.
 *
 * <p>First the decoder should be initialized with a ParameterList object given through the constructor. The when the run() method is invoked and the decoder
 * executes. The exit code of the class can be obtained with the getExitCode() method, after the constructor and after the run method. A non-zero value
 * indicates that an error has ocurred.</p>
 *
 * <p>The decoding chain corresponds to the following sequence of modules:</p>
 *
 * <ul> <li>BitstreamReaderAgent</li> <li>EntropyDecoder</li> <li>ROIDeScaler</li> <li>Dequantizer</li> <li>InverseWT</li> <li>ImgDataConverter</li>
 * <li>EnumratedColorSpaceMapper, SyccColorSpaceMapper or ICCProfiler</li> <li>ComponentDemixer (if needed)</li> <li>ImgDataAdapter (if ComponentDemixer is
 * needed)</li> <li>ImgWriter</li> <li>BlkImgDataSrcImageProducer</li> </ul>
 *
 * <p>The 2 last modules cannot be used at the same time and corresponds respectively to the writing of decoded image into a file or the graphical display of
 * this same image.</p>
 *
 * <p>The behaviour of each module may be modified according to the current tile-component. All the specifications are kept in modules extending ModuleSpec and
 * accessible through an instance of DecoderSpecs class.</p>
 *
 * @see BitstreamReaderAgent
 * @see EntropyDecoder
 * @see ROIDeScaler
 * @see Dequantizer
 * @see InverseWT
 * @see ImgDataConverter
 * @see InvCompTransf
 * @see ImgWriter
 * @see ModuleSpec
 * @see DecoderSpecs
 *
 */
public class Decoder
{
  /**
   * Parses the inputstream to analyze the box structure of the JP2 file.
   */
  private ColorSpace csMap = null;
  /**
   * The exit code of the run method
   */
  private int exitCode;
  /**
   * The parameter list (arguments)
   */
  private ParameterList params;
  /**
   * Information contained in the codestream's headers
   */
  private HeaderInfo header;
  /**
   * The valid list of options prefixes
   */
  private final static char vprfxs[] =
  {
    BitstreamReaderAgent.OPT_PREFIX,
    EntropyDecoder.OPT_PREFIX,
    ROIDeScaler.OPT_PREFIX,
    Dequantizer.OPT_PREFIX,
    InvCompTransf.OPT_PREFIX,
    HeaderDecoder.OPT_PREFIX,
    ColorSpaceMapper.OPT_PREFIX
  };
  private final static String[][] pinfo =
  {
    {
      "res", "<resolution level index>",
      "The resolution level at which to reconstruct the image "
      + " (0 means the lowest available resolution whereas the maximum "
      + "resolution level corresponds to the original image resolution). "
      + "If the given index"
      + " is greater than the number of available resolution levels of the "
      + "compressed image, the image is reconstructed at its highest "
      + "resolution (among all tile-components). Note that this option"
      + " affects only the inverse wavelet transform and not the number "
      + " of bytes read by the codestream parser: this number of bytes "
      + "depends only on options '-nbytes' or '-rate'.", null
    },
    {
      "rate", "<decoding rate in bpp>",
      "Specifies the decoding rate in bits per pixel (bpp) where the "
      + "number of pixels is related to the image's original size (Note:"
      + " this number is not affected by the '-res' option). If it is equal"
      + "to -1, the whole codestream is decoded. "
      + "The codestream is either parsed (default) or truncated depending "
      + "the command line option '-parsing'. To specify the decoding "
      + "rate in bytes, use '-nbytes' options instead.", "-1"
    },
    {
      "nbytes", "<decoding rate in bytes>",
      "Specifies the decoding rate in bytes. "
      + "The codestream is either parsed (default) or truncated depending "
      + "the command line option '-parsing'. To specify the decoding "
      + "rate in bits per pixel, use '-rate' options instead.", "-1"
    },
    {
      "parsing", null,
      "Enable or not the parsing mode when decoding rate is specified "
      + "('-nbytes' or '-rate' options). If it is false, the codestream "
      + "is decoded as if it were truncated to the given rate. If it is "
      + "true, the decoder creates, truncates and decodes a virtual layer"
      + " progressive codestream with the same truncation points in each "
      + "code-block.", "on"
    },
    {
      "ncb_quit", "<max number of code blocks>",
      "Use the ncb and lbody quit conditions. If state information is "
      + "found for more code blocks than is indicated with this option, "
      + "the decoder "
      + "will decode using only information found before that point. "
      + "Using this otion implies that the 'rate' or 'nbyte' parameter "
      + "is used to indicate the lbody parameter which is the number of "
      + "packet body bytes the decoder will decode.", "-1"
    },
    {
      "l_quit", "<max number of layers>",
      "Specifies the maximum number of layers to decode for any code-"
      + "block", "-1"
    },
    {
      "m_quit", "<max number of bit planes>",
      "Specifies the maximum number of bit planes to decode for any code"
      + "-block", "-1"
    },
    {
      "poc_quit", null,
      "Specifies whether the decoder should only decode code-blocks "
      + "included in the first progression order.", "off"
    },
    {
      "one_tp", null,
      "Specifies whether the decoder should only decode the first "
      + "tile part of each tile.", "off"
    },
    {
      "comp_transf", null,
      "Specifies whether the component transform indicated in the "
      + "codestream should be used.", "on"
    },
    {
      "cdstr_info", null,
      "Display information about the codestream. This information is: "
      + "\n- Marker segments value in main and tile-part headers,"
      + "\n- Tile-part length and position within the code-stream.", "off"
    },
    {
      "nocolorspace", null,
      "Ignore any colorspace information in the image.", "off"
    },
    {
      "colorspace_debug", null,
      "Print debugging messages when an error is encountered in the"
      + " colorspace module.", "off"
    }
  };


  public Decoder()
  {
  }

  /**
   * Returns the exit code of the class. This is only initialized after the constructor and when the run method returns.
   *
   * @return The exit code of the constructor and the run() method.
   */
  public int exitCode()
  {
    return exitCode;
  }

  public Image3 decode(PDFImage pdfImage)
  {
    int res; // resolution level to reconstruct
    RandomAccessIO in;
    FileFormatReader ff;
    BitstreamReaderAgent breader;
    HeaderDecoder hd;
    EntropyDecoder entdec;
    ROIDeScaler roids;
    Dequantizer deq;
    InverseWT invWT;
    InvCompTransf ictransf;
    ImgDataConverter converter;
    DecoderSpecs decSpec = null;
    BlkImgDataSrc palettized;
    BlkImgDataSrc channels;
    BlkImgDataSrc resampled;
    BlkImgDataSrc color;
    int i;
    int depth[];
    float rate;
    int nbytes;
    Image3 img = null;

    //IO.WriteBytes(File3.Desk("JP2"+pdfImage.reference()+".jp2"), pdfImage.bytes());

    params = new ParameterList();
    String[][] param = Decoder.getAllParameters();
    for (i = param.length - 1; i >= 0; i--)
      if (param[i][3] != null)
        params.put(param[i][0], param[i][3]);

    boolean verbose = false;
    try
    {
      in = new ISRandomAccessIO(pdfImage.stream().inputStream());
      ff = new FileFormatReader(in);
      ff.readFileFormat();

      if (ff.JP2FFUsed)
        in.seek(ff.getFirstCodeStreamPos());//If the codestream is wrapped in the jp2 fileformat, Read the file format wrapper

      header = new HeaderInfo();
      hd = new HeaderDecoder(in, params, header);
      int nCompCod = hd.getNumComps();
      int nTiles = header.siz.getNumTiles();
      decSpec = hd.getDecoderSpecs();

      if (verbose)
      {
        String info = nCompCod + " component(s) in codestream, " + nTiles + " tile(s)\n";
        info += "Image dimension: ";
        for (int c = 0; c < nCompCod; c++)
          info += header.siz.getCompImgWidth(c) + "x" + header.siz.getCompImgHeight(c) + " ";
        if (nTiles != 1)
          info += "\nNom. Tile dim. (in canvas): " + header.siz.xtsiz + "x" + header.siz.ytsiz;
        FacilityManager.getMsgLogger().printmsg(MsgLogger.INFO, info);
      }
      if (verbose || params.bool("cdstr_info"))
        FacilityManager.getMsgLogger().printmsg(MsgLogger.INFO, "Main header:\n" + header.toStringMainHeader());

      // Get demixed bitdepths
      depth = new int[nCompCod];
      for (i = 0; i < nCompCod; i++)
        depth[i] = hd.getOriginalBitDepth(i);

      breader = BitstreamReaderAgent.createInstance(in, hd, params, decSpec, verbose || params.bool("cdstr_info"), header);
      entdec = hd.createEntropyDecoder(breader, params);
      roids = hd.createROIDeScaler(entdec, params, decSpec);
      deq = hd.createDequantizer(roids, depth, decSpec);
      invWT = InverseWT.createInstance(deq, decSpec);// full page inverse wavelet transform
      res = breader.getImgRes();
      invWT.setImgResLevel(res);
      converter = new ImgDataConverter(invWT, 0);
      ictransf = new InvCompTransf(converter, decSpec, depth, params);


      csMap = new ColorSpace(in, hd, params);
      
//      Log.debug(this,  ".decode - csMap: "+csMap);

      // **** Color space mapping ****
      if (false && ff.JP2FFUsed && params.value("nocolorspace").equals("off"))
      {
        csMap = new ColorSpace(in, hd, params);
        channels = hd.createChannelDefinitionMapper(ictransf, csMap);
        resampled = hd.createResampler(channels, csMap);
        palettized = hd.createPalettizedColorSpaceMapper(resampled, csMap);
        color = hd.createColorSpaceMapper(palettized, csMap);

        if (csMap.debugging())
        {
          FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, "" + csMap);
          FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, "" + channels);
          FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, "" + resampled);
          FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, "" + palettized);
          FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, "" + color);
        }
      }
      else // Skip colorspace mapping
        color = ictransf;

      // This is the last image in the decoding chain and should be assigned by the last transformation:
      BlkImgDataSrc decodedImage = color;
      if (color == null)
        decodedImage = ictransf;
      int mrl = decSpec.dls.getMin();
      if (verbose)
      {
        if (mrl != res)
          FacilityManager.getMsgLogger().println("Reconstructing resolution " + res + " on " + mrl + " (" + breader.getImgWidth(res) + "x" + breader.getImgHeight(res) + ")", 8, 8);
        if (params.real("rate") != -1)
          FacilityManager.getMsgLogger().println("Target rate = " + breader.getTargetRate() + " bpp (" + breader.getTargetNbytes() + " bytes)", 8, 8);
      }

      img = new BlkImgDataSrcImager(pdfImage, decodedImage, csMap.getColorSpace()).decode();

      if (verbose)
      {
        float bitrate = breader.getActualRate();
        int numBytes = breader.getActualNbytes();
        if (ff.JP2FFUsed)
        {
          int imageSize = (int) ((8.0f * numBytes) / bitrate);
          numBytes += ff.getFirstCodeStreamPos();
          bitrate = (numBytes * 8.0f) / imageSize;
        }
        if (params.integer("ncb_quit") == -1)
          FacilityManager.getMsgLogger().println("Actual bitrate = " + bitrate + " bpp (i.e. " + numBytes + " bytes)", 8, 8);
        else
          FacilityManager.getMsgLogger().println("Number of packet body bytes read = " + numBytes, 8, 8);
        FacilityManager.getMsgLogger().flush();
      }
      return img;
    }
    catch (Exception e)
    {
      error(".decode - an exception occurred: " + e.getMessage(), 2);
      e.printStackTrace();
      return null;
    }
  }

  private void error(String msg, int code)
  {
    exitCode = code;
    FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, msg);
  }

  private void error(String msg, int code, Throwable ex)
  {
    exitCode = code;
    FacilityManager.getMsgLogger().printmsg(MsgLogger.ERROR, msg);
    ex.printStackTrace();
  }

  /**
   * Return the information found in the COM marker segments encountered in the decoded codestream.
   *
   */
  public String[] getCOMInfo()
  {
    if (header == null) // The codestream has not been read yet
      return null;
    int nCOMMarkers = header.getNumCOM();
    Enumeration com = header.com.elements();
    String[] infoCOM = new String[nCOMMarkers];
    for (int i = 0; i < nCOMMarkers; i++)
      infoCOM[i] = com.nextElement().toString();
    return infoCOM;
  }

  private void warning(String msg)
  {
    FacilityManager.getMsgLogger().printmsg(MsgLogger.WARNING, msg);
  }

  /**
   * Returns all the parameters used in the decoding chain. It calls parameter from each module and store them in one array (one row per parameter and 4
   * columns).
   *
   * @return All decoding parameters
   *
   * @see #getParameterInfo
   *
   */
  public static String[][] getAllParameters()
  {
    Vector vec = new Vector();
    int i;

    String[][] str = BitstreamReaderAgent.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = EntropyDecoder.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = ROIDeScaler.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = Dequantizer.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = InvCompTransf.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = HeaderDecoder.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = ICCProfiler.getParameterInfo();
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = pinfo;
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        vec.addElement(str[i]);

    str = new String[vec.size()][4];
    if (str != null)
      for (i = str.length - 1; i >= 0; i--)
        str[i] = (String[]) vec.elementAt(i);

    return str;
  }
}
