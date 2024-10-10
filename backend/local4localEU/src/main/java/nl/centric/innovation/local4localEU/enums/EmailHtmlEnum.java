package nl.centric.innovation.local4localEU.enums;

import lombok.Getter;

public enum EmailHtmlEnum {
    P_START("<p style=\"margin: 0;\">"),
    P_END("</p>"),
    BOLD_START("<b>"),
    BOLD_END("</b>"),
    LINE_BREAK("<br>"),
    RN("\r\n"),
    H2_START("<h2 style=\" text-align: center;\">"),
    H2_END("</h2>"),
    EXCL("!");

    private final String html;

    EmailHtmlEnum(String html) {
        this.html = html;
    }

    public String getTag() {
        return html;
    }
}
