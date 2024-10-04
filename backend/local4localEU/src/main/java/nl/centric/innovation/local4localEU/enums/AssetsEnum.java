package nl.centric.innovation.local4localEU.enums;

import lombok.Getter;

@Getter
public enum AssetsEnum {

    LOCAL_LOGO("/assets/images/LFL-logo.png");

    private final String path;

    AssetsEnum(String path) {
        this.path = path;
    }
}

