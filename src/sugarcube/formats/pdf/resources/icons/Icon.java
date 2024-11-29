package sugarcube.formats.pdf.resources.icons;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import sugarcube.common.data.collections.Str;
import sugarcube.common.data.collections.StringMap;
import sugarcube.common.graphics.Color3;
import sugarcube.common.graphics.Image3;
import sugarcube.common.interfaces.Unjammable;
import sugarcube.common.system.io.Class3;
import sugarcube.common.ui.fx.base.Fx;
import sugarcube.common.ui.fx.base.FxText;

import java.util.Comparator;

public class Icon implements Unjammable
{

  public static final String FONT_AWESOME_NAME = "FontAwesome";
  public static final String FONT_AWESOME_FILENAME = "FontAwesome.otf";

  static
  {
    try
    {
      Font.loadFont(Class3.Path(Icon.class, FONT_AWESOME_FILENAME), 10.0);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public static final StringMap<Icon> AWESOME_ICONS = new StringMap<>();
  public static final StringMap<Icon> MATERIAL_ICONS = new StringMap<>();

  public static final Icon I_CURSOR = new Icon('\uf246');
  public static final Icon MOUSE_POINTER = new Icon('\uf245');
  public static final Icon GLASS = new Icon('\uf000');
  public static final Icon MUSIC = new Icon('\uf001');
  public static final Icon SEARCH = new Icon('\uf002');
  public static final Icon ENVELOPE_ALT = new Icon('\uf003');
  public static final Icon HEART = new Icon('\uf004');
  public static final Icon STAR = new Icon('\uf005');
  public static final Icon STAR_ALT = new Icon('\uf006');
  public static final Icon USER = new Icon('\uf007');
  public static final Icon FILM = new Icon('\uf008');
  public static final Icon TH_LARGE = new Icon('\uf009');
  public static final Icon TH = new Icon('\uf00a');
  public static final Icon TH_LIST = new Icon('\uf00b');
  public static final Icon CHECK = new Icon('\uf00c');
  public static final Icon REMOVE = new Icon('\uf00d');
  public static final Icon CLOSE = new Icon('\uf00d');
  public static final Icon TIMES = new Icon('\uf00d');
  public static final Icon SEARCH_PLUS = new Icon('\uf00e');
  public static final Icon SEARCH_MINUS = new Icon('\uf010');
  public static final Icon POWER_OFF = new Icon('\uf011');
  public static final Icon SIGNAL = new Icon('\uf012');
  public static final Icon GEAR = new Icon('\uf013');
  public static final Icon COG = new Icon('\uf013');
  public static final Icon TRASH_ALT = new Icon('\uf014');
  public static final Icon HOME = new Icon('\uf015');
  public static final Icon FILE_ALT = new Icon('\uf016');
  public static final Icon CLOCK_ALT = new Icon('\uf017');
  public static final Icon ROAD = new Icon('\uf018');
  public static final Icon DOWNLOAD = new Icon('\uf019');
  public static final Icon ARROW_CIRCLE_ALT_DOWN = new Icon('\uf01a');
  public static final Icon ARROW_CIRCLE_ALT_UP = new Icon('\uf01b');
  public static final Icon INBOX = new Icon('\uf01c');
  public static final Icon PLAY_CIRCLE_ALT = new Icon('\uf01d');
  public static final Icon ROTATE_RIGHT = new Icon('\uf01e');
  public static final Icon REPEAT = new Icon('\uf01e');
  public static final Icon REFRESH = new Icon('\uf021');
  public static final Icon LIST_ALT = new Icon('\uf022');
  public static final Icon LOCK = new Icon('\uf023');
  public static final Icon FLAG = new Icon('\uf024');
  public static final Icon HEADPHONES = new Icon('\uf025');
  public static final Icon VOLUME_OFF = new Icon('\uf026');
  public static final Icon VOLUME_DOWN = new Icon('\uf027');
  public static final Icon VOLUME_UP = new Icon('\uf028');
  public static final Icon QRCODE = new Icon('\uf029');
  public static final Icon BARCODE = new Icon('\uf02a');
  public static final Icon TAG = new Icon('\uf02b');
  public static final Icon TAGS = new Icon('\uf02c');
  public static final Icon BOOK = new Icon('\uf02d');
  public static final Icon BOOKMARK = new Icon('\uf02e');
  public static final Icon PRINT = new Icon('\uf02f');
  public static final Icon CAMERA = new Icon('\uf030');
  public static final Icon FONT = new Icon('\uf031');
  public static final Icon BOLD = new Icon('\uf032');
  public static final Icon ITALIC = new Icon('\uf033');
  public static final Icon TEXT_HEIGHT = new Icon('\uf034');
  public static final Icon TEXT_WIDTH = new Icon('\uf035');
  public static final Icon ALIGN_LEFT = new Icon('\uf036');
  public static final Icon ALIGN_CENTER = new Icon('\uf037');
  public static final Icon ALIGN_RIGHT = new Icon('\uf038');
  public static final Icon ALIGN_JUSTIFY = new Icon('\uf039');
  public static final Icon LIST = new Icon('\uf03a');
  public static final Icon DEDENT = new Icon('\uf03b');
  public static final Icon OUTDENT = new Icon('\uf03b');
  public static final Icon INDENT = new Icon('\uf03c');
  public static final Icon VIDEO_CAMERA = new Icon('\uf03d');
  public static final Icon PHOTO = new Icon('\uf03e');
  public static final Icon IMAGE = new Icon('\uf03e');
  public static final Icon PICTURE_ALT = new Icon('\uf03e');
  public static final Icon PENCIL = new Icon('\uf040');
  public static final Icon MAP_MARKER = new Icon('\uf041');
  public static final Icon ADJUST = new Icon('\uf042');
  public static final Icon TINT = new Icon('\uf043');
  public static final Icon EDIT = new Icon('\uf044');
  public static final Icon PENCIL_SQUARE_ALT = new Icon('\uf044');
  public static final Icon SHARE_SQUARE_ALT = new Icon('\uf045');
  public static final Icon CHECK_SQUARE_ALT = new Icon('\uf046');
  public static final Icon ARROWS = new Icon('\uf047');
  public static final Icon STEP_BACKWARD = new Icon('\uf048');
  public static final Icon FAST_BACKWARD = new Icon('\uf049');
  public static final Icon BACKWARD = new Icon('\uf04a');
  public static final Icon PLAY = new Icon('\uf04b');
  public static final Icon PAUSE = new Icon('\uf04c');
  public static final Icon STOP = new Icon('\uf04d');
  public static final Icon FORWARD = new Icon('\uf04e');
  public static final Icon FAST_FORWARD = new Icon('\uf050');
  public static final Icon STEP_FORWARD = new Icon('\uf051');
  public static final Icon EJECT = new Icon('\uf052');
  public static final Icon CHEVRON_LEFT = new Icon('\uf053');
  public static final Icon CHEVRON_RIGHT = new Icon('\uf054');
  public static final Icon PLUS_CIRCLE = new Icon('\uf055');
  public static final Icon MINUS_CIRCLE = new Icon('\uf056');
  public static final Icon TIMES_CIRCLE = new Icon('\uf057');
  public static final Icon CHECK_CIRCLE = new Icon('\uf058');
  public static final Icon QUESTION_CIRCLE = new Icon('\uf059');
  public static final Icon INFO_CIRCLE = new Icon('\uf05a');
  public static final Icon CROSSHAIRS = new Icon('\uf05b');
  public static final Icon TIMES_CIRCLE_ALT = new Icon('\uf05c');
  public static final Icon CHECK_CIRCLE_ALT = new Icon('\uf05d');
  public static final Icon BAN = new Icon('\uf05e');
  public static final Icon ARROW_LEFT = new Icon('\uf060');
  public static final Icon ARROW_RIGHT = new Icon('\uf061');
  public static final Icon ARROW_UP = new Icon('\uf062');
  public static final Icon ARROW_DOWN = new Icon('\uf063');
  public static final Icon MAIL_FORWARD = new Icon('\uf064');
  public static final Icon SHARE = new Icon('\uf064');
  public static final Icon EXPAND = new Icon('\uf065');
  public static final Icon COMPRESS = new Icon('\uf066');
  public static final Icon PLUS = new Icon('\uf067');
  public static final Icon MINUS = new Icon('\uf068');
  public static final Icon ASTERISK = new Icon('\uf069');
  public static final Icon EXCLAMATION_CIRCLE = new Icon('\uf06a');
  public static final Icon GIFT = new Icon('\uf06b');
  public static final Icon LEAF = new Icon('\uf06c');
  public static final Icon FIRE = new Icon('\uf06d');
  public static final Icon EYE = new Icon('\uf06e');
  public static final Icon EYE_SLASH = new Icon('\uf070');
  public static final Icon WARNING = new Icon('\uf071');
  public static final Icon EXCLAMATION_TRIANGLE = new Icon('\uf071');
  public static final Icon PLANE = new Icon('\uf072');
  public static final Icon CALENDAR = new Icon('\uf073');
  public static final Icon RANDOM = new Icon('\uf074');
  public static final Icon COMMENT = new Icon('\uf075');
  public static final Icon MAGNET = new Icon('\uf076');
  public static final Icon CHEVRON_UP = new Icon('\uf077');
  public static final Icon CHEVRON_DOWN = new Icon('\uf078');
  public static final Icon RETWEET = new Icon('\uf079');
  public static final Icon SHOPPING_CART = new Icon('\uf07a');
  public static final Icon FOLDER = new Icon('\uf07b');
  public static final Icon FOLDER_ALTPEN = new Icon('\uf07c');
  public static final Icon ARROWS_V = new Icon('\uf07d');
  public static final Icon ARROWS_H = new Icon('\uf07e');
  public static final Icon BAR_CHART_ALT = new Icon('\uf080');
  public static final Icon BAR_CHART = new Icon('\uf080');
  public static final Icon TWITTER_SQUARE = new Icon('\uf081');
  public static final Icon FACEBOOK_SQUARE = new Icon('\uf082');
  public static final Icon CAMERA_RETRO = new Icon('\uf083');
  public static final Icon KEY = new Icon('\uf084');
  public static final Icon GEARS = new Icon('\uf085');
  public static final Icon COGS = new Icon('\uf085');
  public static final Icon COMMENTS = new Icon('\uf086');
  public static final Icon THUMBS_ALT_UP = new Icon('\uf087');
  public static final Icon THUMBS_ALT_DOWN = new Icon('\uf088');
  public static final Icon STAR_HALF = new Icon('\uf089');
  public static final Icon HEART_ALT = new Icon('\uf08a');
  public static final Icon SIGN_OUT = new Icon('\uf08b');
  public static final Icon LINKEDIN_SQUARE = new Icon('\uf08c');
  public static final Icon THUMB_TACK = new Icon('\uf08d');
  public static final Icon EXTERNAL_LINK = new Icon('\uf08e');
  public static final Icon SIGN_IN = new Icon('\uf090');
  public static final Icon TROPHY = new Icon('\uf091');
  public static final Icon GITHUB_SQUARE = new Icon('\uf092');
  public static final Icon UPLOAD = new Icon('\uf093');
  public static final Icon LEMON_ALT = new Icon('\uf094');
  public static final Icon PHONE = new Icon('\uf095');
  public static final Icon SQUARE_ALT = new Icon('\uf096');
  public static final Icon BOOKMARK_ALT = new Icon('\uf097');
  public static final Icon PHONE_SQUARE = new Icon('\uf098');
  public static final Icon TWITTER = new Icon('\uf099');
  public static final Icon FACEBOOK = new Icon('\uf09a');
  public static final Icon GITHUB = new Icon('\uf09b');
  public static final Icon UNLOCK = new Icon('\uf09c');
  public static final Icon CREDIT_CARD = new Icon('\uf09d');
  public static final Icon RSS = new Icon('\uf09e');
  public static final Icon HDD_ALT = new Icon('\uf0a0');
  public static final Icon BULLHORN = new Icon('\uf0a1');
  public static final Icon BELL = new Icon('\uf0f3');
  public static final Icon CERTIFICATE = new Icon('\uf0a3');
  public static final Icon HAND_ALT_RIGHT = new Icon('\uf0a4');
  public static final Icon HAND_ALT_LEFT = new Icon('\uf0a5');
  public static final Icon HAND_ALT_UP = new Icon('\uf0a6');
  public static final Icon HAND_ALT_DOWN = new Icon('\uf0a7');
  public static final Icon HAND_POINTER = new Icon('\uf25a');
  public static final Icon ARROW_CIRCLE_LEFT = new Icon('\uf0a8');
  public static final Icon ARROW_CIRCLE_RIGHT = new Icon('\uf0a9');
  public static final Icon ARROW_CIRCLE_UP = new Icon('\uf0aa');
  public static final Icon ARROW_CIRCLE_DOWN = new Icon('\uf0ab');
  public static final Icon GLOBE = new Icon('\uf0ac');
  public static final Icon WRENCH = new Icon('\uf0ad');
  public static final Icon TASKS = new Icon('\uf0ae');
  public static final Icon FILTER = new Icon('\uf0b0');
  public static final Icon BRIEFCASE = new Icon('\uf0b1');
  public static final Icon ARROWS_ALT = new Icon('\uf0b2');
  public static final Icon GROUP = new Icon('\uf0c0');
  public static final Icon USERS = new Icon('\uf0c0');
  public static final Icon CHAIN = new Icon('\uf0c1');
  public static final Icon LINK = new Icon('\uf0c1');
  public static final Icon CLOUD = new Icon('\uf0c2');
  public static final Icon FLASK = new Icon('\uf0c3');
  public static final Icon CUT = new Icon('\uf0c4');
  public static final Icon SCISSORS = new Icon('\uf0c4');
  public static final Icon COPY = new Icon('\uf0c5');
  public static final Icon FILES_ALT = new Icon('\uf0c5');
  public static final Icon PAPERCLIP = new Icon('\uf0c6');
  public static final Icon SAVE = new Icon('\uf0c7');
  public static final Icon FLOPPY_ALT = new Icon('\uf0c7');
  public static final Icon SQUARE = new Icon('\uf0c8');
  public static final Icon NAVICON = new Icon('\uf0c9');
  public static final Icon REORDER = new Icon('\uf0c9');
  public static final Icon BARS = new Icon('\uf0c9');
  public static final Icon LIST_UL = new Icon('\uf0ca');
  public static final Icon LIST_ALTL = new Icon('\uf0cb');
  public static final Icon STRIKETHROUGH = new Icon('\uf0cc');
  public static final Icon UNDERLINE = new Icon('\uf0cd');
  public static final Icon TABLE = new Icon('\uf0ce');
  public static final Icon MAGIC = new Icon('\uf0d0');
  public static final Icon TRUCK = new Icon('\uf0d1');
  public static final Icon PINTEREST = new Icon('\uf0d2');
  public static final Icon PINTEREST_SQUARE = new Icon('\uf0d3');
  public static final Icon GOOGLE_PLUS_SQUARE = new Icon('\uf0d4');
  public static final Icon GOOGLE_PLUS = new Icon('\uf0d5');
  public static final Icon MONEY = new Icon('\uf0d6');
  public static final Icon CARET_DOWN = new Icon('\uf0d7');
  public static final Icon CARET_UP = new Icon('\uf0d8');
  public static final Icon CARET_LEFT = new Icon('\uf0d9');
  public static final Icon CARET_RIGHT = new Icon('\uf0da');
  public static final Icon COLUMNS = new Icon('\uf0db');
  public static final Icon UNSORTED = new Icon('\uf0dc');
  public static final Icon SORT = new Icon('\uf0dc');
  public static final Icon SORT_DOWN = new Icon('\uf0dd');
  public static final Icon SORT_DESC = new Icon('\uf0dd');
  public static final Icon SORT_UP = new Icon('\uf0de');
  public static final Icon SORT_ASC = new Icon('\uf0de');
  public static final Icon ENVELOPE = new Icon('\uf0e0');
  public static final Icon LINKEDIN = new Icon('\uf0e1');
  public static final Icon ROTATE_LEFT = new Icon('\uf0e2');
  public static final Icon UNDO = new Icon('\uf0e2');
  public static final Icon LEGAL = new Icon('\uf0e3');
  public static final Icon GAVEL = new Icon('\uf0e3');
  public static final Icon DASHBOARD = new Icon('\uf0e4');
  public static final Icon TACHOMETER = new Icon('\uf0e4');
  public static final Icon COMMENT_ALT = new Icon('\uf0e5');
  public static final Icon COMMENTS_ALT = new Icon('\uf0e6');
  public static final Icon FLASH = new Icon('\uf0e7');
  public static final Icon BOLT = new Icon('\uf0e7');
  public static final Icon SITEMAP = new Icon('\uf0e8');
  public static final Icon UMBRELLA = new Icon('\uf0e9');
  public static final Icon PASTE = new Icon('\uf0ea');
  public static final Icon CLIPBOARD = new Icon('\uf0ea');
  public static final Icon LIGHTBULB_ALT = new Icon('\uf0eb');
  public static final Icon EXCHANGE = new Icon('\uf0ec');
  public static final Icon CLOUD_DOWNLOAD = new Icon('\uf0ed');
  public static final Icon CLOUD_UPLOAD = new Icon('\uf0ee');
  public static final Icon USER_MD = new Icon('\uf0f0');
  public static final Icon STETHOSCOPE = new Icon('\uf0f1');
  public static final Icon SUITCASE = new Icon('\uf0f2');
  public static final Icon BELL_ALT = new Icon('\uf0a2');
  public static final Icon COFFEE = new Icon('\uf0f4');
  public static final Icon CUTLERY = new Icon('\uf0f5');
  public static final Icon FILE_TEXT_ALT = new Icon('\uf0f6');
  public static final Icon BUILDING_ALT = new Icon('\uf0f7');
  public static final Icon HOSPITAL_ALT = new Icon('\uf0f8');
  public static final Icon AMBULANCE = new Icon('\uf0f9');
  public static final Icon MEDKIT = new Icon('\uf0fa');
  public static final Icon FIGHTER_JET = new Icon('\uf0fb');
  public static final Icon BEER = new Icon('\uf0fc');
  public static final Icon H_SQUARE = new Icon('\uf0fd');
  public static final Icon PLUS_SQUARE = new Icon('\uf0fe');
  public static final Icon ANGLE_DOUBLE_LEFT = new Icon('\uf100');
  public static final Icon ANGLE_DOUBLE_RIGHT = new Icon('\uf101');
  public static final Icon ANGLE_DOUBLE_UP = new Icon('\uf102');
  public static final Icon ANGLE_DOUBLE_DOWN = new Icon('\uf103');
  public static final Icon ANGLE_LEFT = new Icon('\uf104');
  public static final Icon ANGLE_RIGHT = new Icon('\uf105');
  public static final Icon ANGLE_UP = new Icon('\uf106');
  public static final Icon ANGLE_DOWN = new Icon('\uf107');
  public static final Icon DESKTOP = new Icon('\uf108');
  public static final Icon LAPTOP = new Icon('\uf109');
  public static final Icon TABLET = new Icon('\uf10a');
  public static final Icon MOBILE_PHONE = new Icon('\uf10b');
  public static final Icon MOBILE = new Icon('\uf10b');
  public static final Icon CIRCLE_ALT = new Icon('\uf10c');
  public static final Icon QUOTE_LEFT = new Icon('\uf10d');
  public static final Icon QUOTE_RIGHT = new Icon('\uf10e');
  public static final Icon SPINNER = new Icon('\uf110');
  public static final Icon CIRCLE = new Icon('\uf111');
  public static final Icon MAIL_REPLY = new Icon('\uf112');
  public static final Icon REPLY = new Icon('\uf112');
  public static final Icon GITHUB_ALT = new Icon('\uf113');
  public static final Icon FOLDER_ALT = new Icon('\uf114');
  public static final Icon FOLDER_ALTPEN_ALT = new Icon('\uf115');
  public static final Icon SMILE_ALT = new Icon('\uf118');
  public static final Icon FROWN_ALT = new Icon('\uf119');
  public static final Icon MEH_ALT = new Icon('\uf11a');
  public static final Icon GAMEPAD = new Icon('\uf11b');
  public static final Icon KEYBOARD_ALT = new Icon('\uf11c');
  public static final Icon FLAG_ALT = new Icon('\uf11d');
  public static final Icon FLAG_CHECKERED = new Icon('\uf11e');
  public static final Icon TERMINAL = new Icon('\uf120');
  public static final Icon CODE = new Icon('\uf121');
  public static final Icon MAIL_REPLY_ALL = new Icon('\uf122');
  public static final Icon REPLY_ALL = new Icon('\uf122');
  public static final Icon STAR_HALF_EMPTY = new Icon('\uf123');
  public static final Icon STAR_HALF_FULL = new Icon('\uf123');
  public static final Icon STAR_HALF_ALT = new Icon('\uf123');
  public static final Icon LOCATION_ARROW = new Icon('\uf124');
  public static final Icon CROP = new Icon('\uf125');
  public static final Icon CODE_FORK = new Icon('\uf126');
  public static final Icon UNLINK = new Icon('\uf127');
  public static final Icon CHAIN_BROKEN = new Icon('\uf127');
  public static final Icon QUESTION = new Icon('\uf128');
  public static final Icon INFO = new Icon('\uf129');
  public static final Icon EXCLAMATION = new Icon('\uf12a');
  public static final Icon SUPERSCRIPT = new Icon('\uf12b');
  public static final Icon SUBSCRIPT = new Icon('\uf12c');
  public static final Icon ERASER = new Icon('\uf12d');
  public static final Icon PUZZLE_PIECE = new Icon('\uf12e');
  public static final Icon MICROPHONE = new Icon('\uf130');
  public static final Icon MICROPHONE_SLASH = new Icon('\uf131');
  public static final Icon SHIELD = new Icon('\uf132');
  public static final Icon CALENDAR_ALT = new Icon('\uf133');
  public static final Icon FIRE_EXTINGUISHER = new Icon('\uf134');
  public static final Icon ROCKET = new Icon('\uf135');
  public static final Icon MAXCDN = new Icon('\uf136');
  public static final Icon CHEVRON_CIRCLE_LEFT = new Icon('\uf137');
  public static final Icon CHEVRON_CIRCLE_RIGHT = new Icon('\uf138');
  public static final Icon CHEVRON_CIRCLE_UP = new Icon('\uf139');
  public static final Icon CHEVRON_CIRCLE_DOWN = new Icon('\uf13a');
  public static final Icon HTML5 = new Icon('\uf13b');
  public static final Icon CSS3 = new Icon('\uf13c');
  public static final Icon ANCHOR = new Icon('\uf13d');
  public static final Icon UNLOCK_ALT = new Icon('\uf13e');
  public static final Icon BULLSEYE = new Icon('\uf140');
  public static final Icon ELLIPSIS_H = new Icon('\uf141');
  public static final Icon ELLIPSIS_V = new Icon('\uf142');
  public static final Icon RSS_SQUARE = new Icon('\uf143');
  public static final Icon PLAY_CIRCLE = new Icon('\uf144');
  public static final Icon TICKET = new Icon('\uf145');
  public static final Icon MINUS_SQUARE = new Icon('\uf146');
  public static final Icon MINUS_SQUARE_ALT = new Icon('\uf147');
  public static final Icon LEVEL_UP = new Icon('\uf148');
  public static final Icon LEVEL_DOWN = new Icon('\uf149');
  public static final Icon CHECK_SQUARE = new Icon('\uf14a');
  public static final Icon PENCIL_SQUARE = new Icon('\uf14b');
  public static final Icon EXTERNAL_LINK_SQUARE = new Icon('\uf14c');
  public static final Icon SHARE_SQUARE = new Icon('\uf14d');
  public static final Icon COMPASS = new Icon('\uf14e');
  public static final Icon TOGGLE_DOWN = new Icon('\uf150');
  public static final Icon CARET_SQUARE_ALT_DOWN = new Icon('\uf150');
  public static final Icon TOGGLE_UP = new Icon('\uf151');
  public static final Icon CARET_SQUARE_ALT_UP = new Icon('\uf151');
  public static final Icon TOGGLE_RIGHT = new Icon('\uf152');
  public static final Icon CARET_SQUARE_ALT_RIGHT = new Icon('\uf152');
  public static final Icon EURO = new Icon('\uf153');
  public static final Icon EUR = new Icon('\uf153');
  public static final Icon GBP = new Icon('\uf154');
  public static final Icon DOLLAR = new Icon('\uf155');
  public static final Icon USD = new Icon('\uf155');
  public static final Icon RUPEE = new Icon('\uf156');
  public static final Icon INR = new Icon('\uf156');
  public static final Icon CNY = new Icon('\uf157');
  public static final Icon RMB = new Icon('\uf157');
  public static final Icon YEN = new Icon('\uf157');
  public static final Icon JPY = new Icon('\uf157');
  public static final Icon RUBLE = new Icon('\uf158');
  public static final Icon ROUBLE = new Icon('\uf158');
  public static final Icon RUB = new Icon('\uf158');
  public static final Icon WON = new Icon('\uf159');
  public static final Icon KRW = new Icon('\uf159');
  public static final Icon BITCOIN = new Icon('\uf15a');
  public static final Icon BTC = new Icon('\uf15a');
  public static final Icon FILE = new Icon('\uf15b');
  public static final Icon FILE_TEXT = new Icon('\uf15c');
  public static final Icon SORT_ALPHA_ASC = new Icon('\uf15d');
  public static final Icon SORT_ALPHA_DESC = new Icon('\uf15e');
  public static final Icon SORT_AMOUNT_ASC = new Icon('\uf160');
  public static final Icon SORT_AMOUNT_DESC = new Icon('\uf161');
  public static final Icon SORT_NUMERIC_ASC = new Icon('\uf162');
  public static final Icon SORT_NUMERIC_DESC = new Icon('\uf163');
  public static final Icon THUMBS_UP = new Icon('\uf164');
  public static final Icon THUMBS_DOWN = new Icon('\uf165');
  public static final Icon YOUTUBE_SQUARE = new Icon('\uf166');
  public static final Icon YOUTUBE = new Icon('\uf167');
  public static final Icon XING = new Icon('\uf168');
  public static final Icon XING_SQUARE = new Icon('\uf169');
  public static final Icon YOUTUBE_PLAY = new Icon('\uf16a');
  public static final Icon DROPBOX = new Icon('\uf16b');
  public static final Icon STACK_ALTVERFLOW = new Icon('\uf16c');
  public static final Icon INSTAGRAM = new Icon('\uf16d');
  public static final Icon FLICKR = new Icon('\uf16e');
  public static final Icon ADN = new Icon('\uf170');
  public static final Icon BITBUCKET = new Icon('\uf171');
  public static final Icon BITBUCKET_SQUARE = new Icon('\uf172');
  public static final Icon TUMBLR = new Icon('\uf173');
  public static final Icon TUMBLR_SQUARE = new Icon('\uf174');
  public static final Icon LONG_ARROW_DOWN = new Icon('\uf175');
  public static final Icon LONG_ARROW_UP = new Icon('\uf176');
  public static final Icon LONG_ARROW_LEFT = new Icon('\uf177');
  public static final Icon LONG_ARROW_RIGHT = new Icon('\uf178');
  public static final Icon APPLE = new Icon('\uf179');
  public static final Icon WINDOWS = new Icon('\uf17a');
  public static final Icon ANDROID = new Icon('\uf17b');
  public static final Icon LINUX = new Icon('\uf17c');
  public static final Icon DRIBBBLE = new Icon('\uf17d');
  public static final Icon SKYPE = new Icon('\uf17e');
  public static final Icon FOURSQUARE = new Icon('\uf180');
  public static final Icon TRELLO = new Icon('\uf181');
  public static final Icon FEMALE = new Icon('\uf182');
  public static final Icon MALE = new Icon('\uf183');
  public static final Icon GITTIP = new Icon('\uf184');
  public static final Icon SUN_ALT = new Icon('\uf185');
  public static final Icon MOON_ALT = new Icon('\uf186');
  public static final Icon ARCHIVE = new Icon('\uf187');
  public static final Icon BUG = new Icon('\uf188');
  public static final Icon VK = new Icon('\uf189');
  public static final Icon WEIBO = new Icon('\uf18a');
  public static final Icon RENREN = new Icon('\uf18b');
  public static final Icon PAGELINES = new Icon('\uf18c');
  public static final Icon STACK_EXCHANGE = new Icon('\uf18d');
  public static final Icon ARROW_CIRCLE_ALT_RIGHT = new Icon('\uf18e');
  public static final Icon ARROW_CIRCLE_ALT_LEFT = new Icon('\uf190');
  public static final Icon TOGGLE_LEFT = new Icon('\uf191');
  public static final Icon CARET_SQUARE_ALT_LEFT = new Icon('\uf191');
  public static final Icon DOT_CIRCLE_ALT = new Icon('\uf192');
  public static final Icon WHEELCHAIR = new Icon('\uf193');
  public static final Icon VIMEO_SQUARE = new Icon('\uf194');
  public static final Icon TURKISH_LIRA = new Icon('\uf195');
  public static final Icon TRY = new Icon('\uf195');
  public static final Icon PLUS_SQUARE_ALT = new Icon('\uf196');
  public static final Icon SPACE_SHUTTLE = new Icon('\uf197');
  public static final Icon SLACK = new Icon('\uf198');
  public static final Icon ENVELOPE_SQUARE = new Icon('\uf199');
  public static final Icon WORDPRESS = new Icon('\uf19a');
  public static final Icon OPENID = new Icon('\uf19b');
  public static final Icon INSTITUTION = new Icon('\uf19c');
  public static final Icon BANK = new Icon('\uf19c');
  public static final Icon UNIVERSITY = new Icon('\uf19c');
  public static final Icon MORTAR_BOARD = new Icon('\uf19d');
  public static final Icon GRADUATION_CAP = new Icon('\uf19d');
  public static final Icon YAHOO = new Icon('\uf19e');
  public static final Icon GOOGLE = new Icon('\uf1a0');
  public static final Icon REDDIT = new Icon('\uf1a1');
  public static final Icon REDDIT_SQUARE = new Icon('\uf1a2');
  public static final Icon STUMBLEUPON_CIRCLE = new Icon('\uf1a3');
  public static final Icon STUMBLEUPON = new Icon('\uf1a4');
  public static final Icon DELICIOUS = new Icon('\uf1a5');
  public static final Icon DIGG = new Icon('\uf1a6');
  public static final Icon PIED_PIPER = new Icon('\uf1a7');
  public static final Icon PIED_PIPER_ALT = new Icon('\uf1a8');
  public static final Icon DRUPAL = new Icon('\uf1a9');
  public static final Icon JOOMLA = new Icon('\uf1aa');
  public static final Icon LANGUAGE = new Icon('\uf1ab');
  public static final Icon FAX = new Icon('\uf1ac');
  public static final Icon BUILDING = new Icon('\uf1ad');
  public static final Icon CHILD = new Icon('\uf1ae');
  public static final Icon PAW = new Icon('\uf1b0');
  public static final Icon SPOON = new Icon('\uf1b1');
  public static final Icon CUBE = new Icon('\uf1b2');
  public static final Icon CUBES = new Icon('\uf1b3');
  public static final Icon BEHANCE = new Icon('\uf1b4');
  public static final Icon BEHANCE_SQUARE = new Icon('\uf1b5');
  public static final Icon STEAM = new Icon('\uf1b6');
  public static final Icon STEAM_SQUARE = new Icon('\uf1b7');
  public static final Icon RECYCLE = new Icon('\uf1b8');
  public static final Icon AUTOMOBILE = new Icon('\uf1b9');
  public static final Icon CAR = new Icon('\uf1b9');
  public static final Icon CAB = new Icon('\uf1ba');
  public static final Icon TAXI = new Icon('\uf1ba');
  public static final Icon TREE = new Icon('\uf1bb');
  public static final Icon SPOTIFY = new Icon('\uf1bc');
  public static final Icon DEVIANTART = new Icon('\uf1bd');
  public static final Icon SOUNDCLOUD = new Icon('\uf1be');
  public static final Icon DATABASE = new Icon('\uf1c0');
  public static final Icon FILE_PDF_ALT = new Icon('\uf1c1');
  public static final Icon FILE_WORD_ALT = new Icon('\uf1c2');
  public static final Icon FILE_EXCEL_ALT = new Icon('\uf1c3');
  public static final Icon FILE_POWERPOINT_ALT = new Icon('\uf1c4');
  public static final Icon FILE_PHOTO_ALT = new Icon('\uf1c5');
  public static final Icon FILE_PICTURE_ALT = new Icon('\uf1c5');
  public static final Icon FILE_IMAGE_ALT = new Icon('\uf1c5');
  public static final Icon FILE_ZIP_ALT = new Icon('\uf1c6');
  public static final Icon FILE_ARCHIVE_ALT = new Icon('\uf1c6');
  public static final Icon FILE_SOUND_ALT = new Icon('\uf1c7');
  public static final Icon FILE_AUDIO_ALT = new Icon('\uf1c7');
  public static final Icon FILE_MOVIE_ALT = new Icon('\uf1c8');
  public static final Icon FILE_VIDEO_ALT = new Icon('\uf1c8');
  public static final Icon FILE_CODE_ALT = new Icon('\uf1c9');
  public static final Icon VINE = new Icon('\uf1ca');
  public static final Icon CODEPEN = new Icon('\uf1cb');
  public static final Icon JSFIDDLE = new Icon('\uf1cc');
  public static final Icon LIFE_BOUY = new Icon('\uf1cd');
  public static final Icon LIFE_BUOY = new Icon('\uf1cd');
  public static final Icon LIFE_SAVER = new Icon('\uf1cd');
  public static final Icon SUPPORT = new Icon('\uf1cd');
  public static final Icon LIFE_RING = new Icon('\uf1cd');
  public static final Icon CIRCLE_ALT_NOTCH = new Icon('\uf1ce');
  public static final Icon RA = new Icon('\uf1d0');
  public static final Icon REBEL = new Icon('\uf1d0');
  public static final Icon GE = new Icon('\uf1d1');
  public static final Icon EMPIRE = new Icon('\uf1d1');
  public static final Icon GIT_SQUARE = new Icon('\uf1d2');
  public static final Icon GIT = new Icon('\uf1d3');
  public static final Icon HACKER_NEWS = new Icon('\uf1d4');
  public static final Icon TENCENT_WEIBO = new Icon('\uf1d5');
  public static final Icon QQ = new Icon('\uf1d6');
  public static final Icon WECHAT = new Icon('\uf1d7');
  public static final Icon WEIXIN = new Icon('\uf1d7');
  public static final Icon SEND = new Icon('\uf1d8');
  public static final Icon PAPER_PLANE = new Icon('\uf1d8');
  public static final Icon SEND_ALT = new Icon('\uf1d9');
  public static final Icon PAPER_PLANE_ALT = new Icon('\uf1d9');
  public static final Icon HISTORY = new Icon('\uf1da');
  public static final Icon CIRCLE_THIN = new Icon('\uf1db');
  public static final Icon HEADER = new Icon('\uf1dc');
  public static final Icon PARAGRAPH = new Icon('\uf1dd');
  public static final Icon SLIDERS = new Icon('\uf1de');
  public static final Icon SHARE_ALT = new Icon('\uf1e0');
  public static final Icon SHARE_ALT_SQUARE = new Icon('\uf1e1');
  public static final Icon BOMB = new Icon('\uf1e2');
  public static final Icon SOCCER_BALL_ALT = new Icon('\uf1e3');
  public static final Icon FUTBOL_ALT = new Icon('\uf1e3');
  public static final Icon TTY = new Icon('\uf1e4');
  public static final Icon BINOCULARS = new Icon('\uf1e5');
  public static final Icon PLUG = new Icon('\uf1e6');
  public static final Icon SLIDESHARE = new Icon('\uf1e7');
  public static final Icon TWITCH = new Icon('\uf1e8');
  public static final Icon YELP = new Icon('\uf1e9');
  public static final Icon NEWSPAPER_ALT = new Icon('\uf1ea');
  public static final Icon WIFI = new Icon('\uf1eb');
  public static final Icon CALCULATOR = new Icon('\uf1ec');
  public static final Icon PAYPAL = new Icon('\uf1ed');
  public static final Icon GOOGLE_WALLET = new Icon('\uf1ee');
  public static final Icon CC_VISA = new Icon('\uf1f0');
  public static final Icon CC_MASTERCARD = new Icon('\uf1f1');
  public static final Icon CC_DISCOVER = new Icon('\uf1f2');
  public static final Icon CC_AMEX = new Icon('\uf1f3');
  public static final Icon CC_PAYPAL = new Icon('\uf1f4');
  public static final Icon CC_STRIPE = new Icon('\uf1f5');
  public static final Icon BELL_SLASH = new Icon('\uf1f6');
  public static final Icon BELL_SLASH_ALT = new Icon('\uf1f7');
  public static final Icon TRASH = new Icon('\uf1f8');
  public static final Icon COPYRIGHT = new Icon('\uf1f9');
  public static final Icon AT = new Icon('\uf1fa');
  public static final Icon EYEDROPPER = new Icon('\uf1fb');
  public static final Icon PAINT_BRUSH = new Icon('\uf1fc');
  public static final Icon BIRTHDAY_CAKE = new Icon('\uf1fd');
  public static final Icon AREA_CHART = new Icon('\uf1fe');
  public static final Icon PIE_CHART = new Icon('\uf200');
  public static final Icon LINE_CHART = new Icon('\uf201');
  public static final Icon LASTFM = new Icon('\uf202');
  public static final Icon LASTFM_SQUARE = new Icon('\uf203');
  public static final Icon TOGGLE_OFF = new Icon('\uf204');
  public static final Icon TOGGLE_ALTN = new Icon('\uf205');
  public static final Icon BICYCLE = new Icon('\uf206');
  public static final Icon BUS = new Icon('\uf207');
  public static final Icon IOXHOST = new Icon('\uf208');
  public static final Icon ANGELLIST = new Icon('\uf209');
  public static final Icon CC = new Icon('\uf20a');
  public static final Icon SHEKEL = new Icon('\uf20b');
  public static final Icon SHEQEL = new Icon('\uf20b');
  public static final Icon ILS = new Icon('\uf20b');
  public static final Icon MEANPATH = new Icon('\uf20c');
  public static final Icon OBJECT_GROUP = new Icon('\uf247');

  public final static int DEFAULT_ICON_SIZE = 16;
  public final static String DEFAULT_FONT_SIZE = "1em";
  public final Character character;
  public final String font;

  public static void touch()
  {

  }

  private Icon(Character character)
  {
      this.character = character;
      this.font = FONT_AWESOME_NAME;
      AWESOME_ICONS.put(character.toString(), this);
  }

  public Character getChar()
  {
    return character;
  }

  public String toUnicode()
  {
    return String.format("\\u%04x", (int) character);
  }

  public FxText get(int size)
  {
    return Get(font, toString(), size, null);
  }

  public FxText get(int size, Color3 color)
  {
    return Get(font, toString(), size, color);
  }

  public FxText set(Labeled bt, int size, Color3 color)
  {
    return set(bt, size, 100, "", color);
  }

  public FxText set(Labeled bt, int size, int percent, String tooltip)
  {
    return set(bt, size, percent, tooltip, null);
  }

  public FxText set(Labeled bt, int size, int percent, String tooltip, Color3 color)
  {
    FxText icon = get(Math.round(size * percent / 100), color);
    bt.setGraphic(icon);
    if (Str.HasChar(tooltip))
      bt.setTooltip(new Tooltip(tooltip));
    return icon;
  }

  public FxText set(ButtonBase bt, int size, EventHandler<ActionEvent> handler)
  {
    return set(bt, size, 100, "", handler);
  }

  public FxText set(ButtonBase bt, int size, int percent, String tooltip, EventHandler<ActionEvent> handler)
  {
    return set(bt, size, percent, tooltip, null, handler);
  }

  public FxText set(ButtonBase bt, int size, int percent, String tooltip, Color3 color, EventHandler<ActionEvent> handler)
  {
    FxText text = set(bt, size, percent, tooltip, color);
    if (handler != null)
      bt.setOnAction(handler);
    return text;
  }

  @Override
  public String toString()
  {
    return character.toString();
  }

  public Comparator<Icon> comparator()
  {
    return (o1, o2) -> {
      return o1 != null && o2 != null ? o1.character.compareTo(o2.character) : 0;
    };
  }

  public static FxText Get(Object charString, int size)
  {
    return Awesome(charString, size, null);
  }

  public static FxText Awesome(Object charString, int size, Color3 color)
  {
    return Get(FONT_AWESOME_NAME, charString, size, color);
  }

  public static FxText Material(Object charString, int size, Color3 color)
  {
    return Get(FONT_AWESOME_NAME, charString, size, color);
  }

  public static FxText Get(String font, Object charString, int size, Color3 color)
  {
    try
    {
      FxText label = new FxText(charString.toString());
      color = color == null ? Color3.DUST_WHITE : color;
      return label.icon(font, size, color);
    } catch (Exception e)
    {
      e.printStackTrace();
      return new FxText();
    }
  }

  public Image image(int size, Color3 color)
  {
    return Image(this, size, color);
  }

  public static Image Image(Object awesomeChar, int size, Color3 color)
  {
    Group root = new Group();
    Scene scene = new Scene(root, size, size, Color3.GLASS.fx());
    Node icon = Awesome(awesomeChar, size, color);
    root.getChildren().add(icon);
    return Fx.toFXImage((Image3.wrap(Fx.toBufferedImage(icon.snapshot(null, null)))));
  }

  public static FxText Magic(int size)
  {
    return Get(Icon.MAGIC, size);
  }

  public static FxText Back(int size)
  {
    return Get(Icon.BACKWARD, size);
  }

  public static FxText Add(int size)
  {
    return Awesome(Icon.PLUS, size, Color3.GREEN_LEAF);
  }

  public static FxText Refresh(int size)
  {
    return Get(Icon.REFRESH, size);
  }

  public static FxText Copy(int size)
  {
    return Get(Icon.COPY, size);
  }

  public static FxText Remove(int size)
  {
    return Awesome(Icon.REMOVE, size, Color3.ORANGE_RED);
  }

  public static FxText Gears(int size, Color3 color)
  {
    return Awesome(GEARS, size, color);
  }

  public static FxText Unlock(int size, Color3 color)
  {
    return Awesome(UNLOCK, size, color);
  }

  public static FxText Pointer(int size, Color3 color)
  {
    return Awesome(MOUSE_POINTER, size, color);
  }

  public static FxText Cursor(int size, Color3 color)
  {
    return Awesome(I_CURSOR, size, color);
  }

  public static Icon CheckOrRemove(boolean doCheck)
  {
    return doCheck ? CHECK : REMOVE;
  }


}
