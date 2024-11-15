/*
 * CVS identifier:
 *
 * $Id: EncoderSpecs.java,v 1.35 2001/05/08 16:10:40 grosbois Exp $
 *
 * Class:                   EncoderSpecs
 *
 * Description:             Hold all encoder specifications
 *
 *
 *
 * COPYRIGHT:
 * 
 * 
 * Copyright (c) 1999/2000 JJ2000 Partners.
 * */
package jj2000.j2k.encoder;

import jj2000.j2k.IntegerSpec;
import jj2000.j2k.ModuleSpec;
import jj2000.j2k.StringSpec;
import jj2000.j2k.entropy.CBlkSizeSpec;
import jj2000.j2k.entropy.PrecinctSizeSpec;
import jj2000.j2k.entropy.ProgressionSpec;
import jj2000.j2k.image.BlkImgDataSrc;
import jj2000.j2k.image.CompTransfSpec;
import jj2000.j2k.image.forwcomptransf.ForwCompTransfSpec;
import jj2000.j2k.quantization.GuardBitsSpec;
import jj2000.j2k.quantization.QuantStepSizeSpec;
import jj2000.j2k.quantization.QuantTypeSpec;
import jj2000.j2k.quantization.quantizer.Quantizer;
import jj2000.j2k.roi.MaxShiftSpec;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.wavelet.analysis.AnWTFilterSpec;

/**
 * This class holds references to each module specifications used in the encoding chain. This avoid big amount of arguments in method calls. A specification
 * contains values of each tile-component for one module. All members must be instance of ModuleSpec class (or its children).
 *
 * @see ModuleSpec 
 *
 */
public class EncoderSpecs
{
  /**
   * ROI maxshift value specifications
   */
  public MaxShiftSpec rois;
  /**
   * Quantization type specifications
   */
  public QuantTypeSpec qts;
  /**
   * Quantization normalized base step size specifications
   */
  public QuantStepSizeSpec qsss;
  /**
   * Number of guard bits specifications
   */
  public GuardBitsSpec gbs;
  /**
   * Analysis wavelet filters specifications
   */
  public AnWTFilterSpec wfs;
  /**
   * Component transformation specifications
   */
  public CompTransfSpec cts;
  /**
   * Number of decomposition levels specifications
   */
  public IntegerSpec dls;
  /**
   * The length calculation specifications
   */
  public StringSpec lcs;
  /**
   * The termination type specifications
   */
  public StringSpec tts;
  /**
   * Error resilience segment symbol use specifications
   */
  public StringSpec sss;
  /**
   * Causal stripes specifications
   */
  public StringSpec css;
  /**
   * Regular termination specifications
   */
  public StringSpec rts;
  /**
   * MQ reset specifications
   */
  public StringSpec mqrs;
  /**
   * By-pass mode specifications
   */
  public StringSpec bms;
  /**
   * Precinct partition specifications
   */
  public PrecinctSizeSpec pss;
  /**
   * Start of packet (SOP) marker use specification
   */
  public StringSpec sops;
  /**
   * End of packet header (EPH) marker use specification
   */
  public StringSpec ephs;
  /**
   * Code-blocks sizes specification
   */
  public CBlkSizeSpec cblks;
  /**
   * Progression/progression changes specification
   */
  public ProgressionSpec pocs;
  /**
   * The number of tiles within the image
   */
  public int nTiles;
  /**
   * The number of components within the image
   */
  public int nComp;

  /**
   * Initialize all members with the given number of tiles and components and the command-line arguments stored in a ParameterList instance
   *
   * @param nt Number of tiles
   *
   * @param nc Number of components
   *
   * @param imgsrc The image source (used to get the image size)
   *
   * @param pl The ParameterList instance
     *
   */
  public EncoderSpecs(int nt, int nc, BlkImgDataSrc imgsrc, ParameterList pl)
  {
    nTiles = nt;
    nComp = nc;

    // ROI
    rois = new MaxShiftSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP);

    // Quantization
    pl.checkList(Quantizer.OPT_PREFIX,
      pl.toNameArray(Quantizer.getParameterInfo()));
    qts = new QuantTypeSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP, pl);
    qsss = new QuantStepSizeSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP, pl);
    gbs = new GuardBitsSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP, pl);

    // Wavelet transform
    wfs = new AnWTFilterSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP, qts, pl);
    dls = new IntegerSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP, pl, "Wlev");

    // Component transformation
    cts = new ForwCompTransfSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE, wfs, pl);

    // Entropy coder
    String[] strLcs =
    {
      "near_opt", "lazy_good", "lazy"
    };
    lcs = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "Clen_calc", strLcs, pl);
    String[] strTerm =
    {
      "near_opt", "easy", "predict", "full"
    };
    tts = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "Cterm_type", strTerm, pl);
    String[] strBoolean =
    {
      "on", "off"
    };
    sss = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "Cseg_symbol", strBoolean, pl);
    css = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "Ccausal", strBoolean, pl);
    rts = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "Cterminate", strBoolean, pl);
    mqrs = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "CresetMQ", strBoolean, pl);
    bms = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      "Cbypass", strBoolean, pl);
    cblks = new CBlkSizeSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP, pl);

    // Precinct partition
    pss = new PrecinctSizeSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE_COMP,
      imgsrc, dls, pl);

    // Codestream
    sops = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE, "Psop",
      strBoolean, pl);
    ephs = new StringSpec(nt, nc, ModuleSpec.SPEC_TYPE_TILE, "Peph",
      strBoolean, pl);

  }
}
