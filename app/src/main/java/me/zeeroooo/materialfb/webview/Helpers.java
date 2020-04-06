package me.zeeroooo.materialfb.webview;

import android.graphics.Color;
import android.view.Menu;
import android.webkit.CookieManager;

import me.zeeroooo.materialfb.MFB;

public class Helpers {

    // Method to retrieve a single cookie
    public static String getCookie() {
        final CookieManager cookieManager = CookieManager.getInstance();
        final String cookies = cookieManager.getCookie("https://m.facebook.com/");
        if (cookies != null) {
            final String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains("c_user")) {
                    final String[] temp1 = ar1.split("=");
                    return temp1[1];
                }
            }
        }
        // Return null as we found no cookie
        return null;
    }

    // Uncheck all items menu
    public static void uncheckRadioMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).isChecked()) {
                menu.getItem(i).setChecked(false);
                return;
            }
        }
    }

    static boolean isInteger(String str) {
        return (str.matches("^-?\\d+$"));
    }

    // "clean" and decode an url, all in one
    public static String cleanAndDecodeUrl(String url) {
        return decodeUrl(cleanUrl(url));
    }

    // "clean" an url and remove Facebook tracking redirection
    public static String cleanUrl(String url) {
        return url.replace("http://lm.facebook.com/l.php?u=", "")
                .replace("https://m.facebook.com/l.php?u=", "")
                .replace("http://0.facebook.com/l.php?u=", "")
                .replace("https://lm.facebook.com/l.php?u=", "")
                .replaceAll("&h=.*", "")
                .replaceAll("\\?acontext=.*", "")
                .replace("&SharedWith=", "")
                .replace("www.facebook.com", "m.facebook.com")
                .replace("web.facebook.com", "m.facebook.com");
    }

    // url decoder, recreate all the special characters
    private static String decodeUrl(String url) {
        return url.replace("%3C", "<").replace("%3E", ">").replace("%23", "#").replace("%25", "%")
                .replace("%7B", "{").replace("%7D", "}").replace("%7C", "|").replace("%5C", "\\")
                .replace("%5E", "^").replace("%7E", "~").replace("%5B", "[").replace("%5D", "]")
                .replace("%60", "`").replace("%3B", ";").replace("%2F", "/").replace("%3F", "?")
                .replace("%3A", ":").replace("%40", "@").replace("%3D", "=").replace("%26", "&")
                .replace("%24", "$").replace("%2B", "+").replace("%22", "\"").replace("%2C", ",")
                .replace("%20", " ");
    }

    public static String decodeImg(String img_url) {
        return img_url.replace("\\3a ", ":").replace("efg\\3d ", "oh=").replace("\\3d ", "=").replace("\\26 ", "&").replace("\\", "").replace("&amp;", "&");
    }

    public static String cssThemeEngine(byte themeMode) {
        final StringBuilder stringBuilder = new StringBuilder();
        int sensitiveColor;

        if (themeMode == 1) // if we are using a dark mode + dark accent color, some texts will be black on black, so let's manage this exception
            sensitiveColor = Color.WHITE;
        else
            sensitiveColor = MFB.colorPrimary;

        stringBuilder.append("._4e81, ._4kk6, .blueName, ._2a_i._2a_i a, ._5pxa ._5pxc a, .fcb, ._67i4 ._5jjz ._5c9u, ._vqv .read, ._vqv, ._3bg5 ._52x2, ._6x2x, ._6xqt, .touch ._5lm6, ._1e8b, ._52jb, #u_0_d, #u_0_4l, #u_0_36 {    color: #").append(Integer.toHexString(sensitiveColor).substring(2)).append(" !important; }.touch ._x0b {    border-bottom: 2px solid #").append(Integer.toHexString(MFB.colorPrimaryDark).substring(2)).append(";    color: #").append(Integer.toHexString(MFB.colorPrimaryDark).substring(2)).append("; }.touch ._4_d0 ._4_d1 ._55cx, ._u42 ._55fj, ._6j_d { background: #").append(Integer.toHexString(MFB.colorPrimary).substring(2)).append(" !important;text-shadow: 0 0px; }._7gxn ._59tg, ._8hp4, ._26vk._56bu, .touch ._26vk._56bv, ._36bl ._2x1r, ._36bl ._2thz { background-color: #").append(Integer.toHexString(MFB.colorPrimary).substring(2)).append(" !important; } ._52z5._7gxn {    height: 89px;    border-bottom: 0px;    border-top: 0px;    background: #").append(Integer.toHexString(MFB.colorPrimary).substring(2)).append("; }._129- {    border-top: 0px; }._7izv ._7i-0 {    border-bottom: 1px solid #").append(Integer.toHexString(MFB.colorAccent).substring(2)).append("; }._15kl._15kl ._77li, ._15kj._15kj a, .touch a.sub, ._2eo-._4b44, .touch ._78xz, .touch ._5qc4._5qc4 a, .touch ._5qc4._5qc4._5qc4 a, ._4gux, ._6dsj ._3gin, ._7izv ._7iz_, ._52j9, ._4_d0 ._4_d1, .fcl, .fcg, ._x0a, ._86nv, ._20u1, .touch .mfsm, ._52ja, ._7-1j, ._5tg_, ._5qc3._5qc3 a, ._7msl { color: #").append(Integer.toHexString(MFB.colorAccent).substring(2)).append(" !important; }.aclb, ._67iu, ._2ykg .unreadMessage, ._2b06 {    background-color: #").append(Integer.toHexString(MFB.colorPrimary).substring(2)).append("40 !important; }._86nt._8n4c, ._7cui {    background-color: #").append(Integer.toHexString(MFB.colorPrimaryDark).substring(2)).append("40;    color: #").append(Integer.toHexString(MFB.colorPrimaryDark).substring(2)).append("}._34em ._34ee {    background-color: #").append(Integer.toHexString(MFB.colorPrimary).substring(2)).append(" !important;    color: #fff; }._34ee {    background-color: transparent !important;    border: 1px solid #").append(Integer.toHexString(MFB.colorPrimary).substring(2)).append("; } .touch .btn.iconOnly, .touch .btnC, .touch .btnI.bgb { background: #").append(Integer.toHexString(MFB.colorPrimaryDark).substring(2)).append(";    border: 0px; }.acw {    border: 0; }.touch ._5c9u, .touch ._5ca9, .touch button._5c9u { text-shadow: 0 0px; }._u42 ._5i9c {    border-radius: 50%; }._1e8h {    border-top: 0px; }.ib .l { border-radius: 50%; }");

        if (themeMode == 1 || themeMode == 2)
            stringBuilder.append("._55wo, ._2am8, ._vi6 ._42rg, ._7gxp, ._67i4 ._67iv #messages_search_box .quicksearch, .touch ._1oby ._5c9u, ._10c_, ._77xj, ._6vzz, #timelineBody, ._3-8x, ._3f50>._5rgr, ._45kb>div, ._5-lw, ._13fn, ._hdn._hdn, #u_0_35, #u_0_4k, #u_1_1i, #u_1_0, #u_0_4m, #u_0_5i, #u_0_44, #u_0_37, .touch ._6-l .composerInput, ._13-f, form { background: #1e1e1e !important; }._2v9s, #root, ._59e9, ._vqv, .touch ._4_d0, ._3ioy._3ioy ._uww, ._403n, ._13e_, .groupChromeView.feedRevamp, ._4_xl, ._-j8, ._-j9, ._6o5v, #u_0_2, ._484w, ._-j7, .acw, ._21db { background: #121212 !important; }._6beq, ._5pz4, ._5lp4, ._5lp5, .touch ._1f9d, .touch ._3tap, ._36dc { background: #1e1e1e !important; border-bottom: 0px; border-top: 0px; }._44qk { border-bottom: 0px; }._34ee, textarea, input, ._u42 ._52x7, ._5rgt p, ._5t8z p, .ib .c, ._52jc, .touch ._5b6o ._5b6q, ._21dc, ._2b06 { color: #aaaaaa !important; }.touch ._52t1, ._uww, ._5-lx, .touch ._26vk._56bt[disabled] {    border: 1px solid #1e1e1e !important;    background: #121212; }._3bg5 ._52x6 {    border-top-color: #2b324600 !important;    background: #1e1e1e !important; }._3bg5 ._52x1, ._z-w {    background: #121212 !important;    border-bottom: 0px;    border-top: 0px; }._4gus, ._67i4 ._67iu ._3z10, h1, h2, h3, h4, h5, h6, ._52jj { color: white; } .touch ._55so._59f6::before, .touch.wp.x1-5 ._55so._59f6::before, .touch.wp.x2 ._55so._59f6::before, .touch ._59f6 ._55st ._5c9u { background: #1e1e1e !important; text-shadow: 0 0px; } ._a58 _9_7 _2rgt _1j-f _2rgt { background-color: #121212 !important; }"); // ._7om2, lele

        if (themeMode == 1 || themeMode == 3)
            stringBuilder.append("._34em ._34ee, .touch ._8hq8, ._6j_d ._6j_c, ._59tg, ._u42 ._55fj { color: #").append(Integer.toHexString(MFB.colorAccent).substring(2)).append(" !important; }");

        stringBuilder.append("._5rgr,  ._5rgt p { -webkit-user-select: initial !important; }");

        return stringBuilder.toString();
    }
}
