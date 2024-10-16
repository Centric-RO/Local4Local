package nl.centric.innovation.local4localEU.enums;

import lombok.Getter;

@Getter
public enum EmailStructureEnum {

    SUBJECT("subject"),
    TITLE("title"),
    CONTENT("content"),
    ACTION("action"),
    BUTTON("button"),
    CLOSING("closing"),
    THANK_YOU("thankYou"),
    REGISTER_BTN("registerBtn"),
    CONFIRM_BTN("confirmBtn"),
    ACCOUNT_CONFIRMATION("accountConfirmation"),
    GO_TO("goTo"),
    GENERIC("generic"),
    LOGO_IMAGE("logoImage"),
    URL("url"),
    BTN_TEXT("btnText"),
    REJECT("contentReject"),
    REASON("reason"),
    SUPPLIER_NAME("supplier"),
    REPRESENTATIVE_NAME("name"),
    COMPANY_NAME("companyName"),
    OTP_CODE_MESSAGE("otpCodeMessage"),
    SEE_MAP("seeMap"),
    TALER_MESSAGE("talerMessage"),
    TALER_INSTANCE("talerInstance"),
    TALER_ACCESS_TOKEN("talerAccessToken"),
    TALER_INSTRUCTIONS("talerInstructions");

    private final String structure;

    EmailStructureEnum(String template) {
        this.structure = template;
    }
}