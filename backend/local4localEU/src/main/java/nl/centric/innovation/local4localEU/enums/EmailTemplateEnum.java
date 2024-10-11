package nl.centric.innovation.local4localEU.enums;

import lombok.Getter;

@Getter
public enum EmailTemplateEnum {

    INVITE_MERCHANT("inviteMerchants"),
    PASSWORD_RECOVER("passwordRecover"),
    MERCHANT_REGISTERED("merchantRegistered"),
    MANAGER_OTP("managerOtp"),
    APPROVE_MERCHANT("approveMerchant");

    private final String template;

    EmailTemplateEnum(String template) {
        this.template = template;
    }
}
