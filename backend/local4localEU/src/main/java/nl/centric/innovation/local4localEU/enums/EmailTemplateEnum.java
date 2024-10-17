package nl.centric.innovation.local4localEU.enums;

import lombok.Getter;

@Getter
public enum EmailTemplateEnum {

    INVITE_MERCHANT("inviteMerchants"),
    PASSWORD_RECOVER("passwordRecover"),
    MERCHANT_REGISTERED("merchantRegistered"),
    MANAGER_OTP("managerOtp"),
    APPROVE_MERCHANT("approveMerchant"),
    REJECT_MERCHANT("rejectMerchant");

    private final String template;

    EmailTemplateEnum(String template) {
        this.template = template;
    }
}
