package nl.centric.innovation.local4localEU.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.centric.innovation.local4localEU.enums.AssetsEnum;
import nl.centric.innovation.local4localEU.enums.EmailHtmlEnum;
import nl.centric.innovation.local4localEU.enums.EmailStructureEnum;
import nl.centric.innovation.local4localEU.enums.EmailTemplateEnum;
import nl.centric.innovation.local4localEU.service.interfaces.EmailService;
import nl.centric.innovation.local4localEU.service.interfaces.MailTemplateBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import util.MailTemplate;
import util.StringUtils;

import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@PropertySource({"classpath:application.properties"})
public class EmailServiceImpl implements EmailService {
    @Value("${local4localEU.default.email.sender}")
    private String emailSender;

    @Value("${local4localEU.server.name}")
    private String baseURL;

    @Value("${taler.base.url}")
    private String talerBaseURL;

    @Value("${taler.base.url}")
    private String talerBaseURL;

    private final ResourceBundleMessageSource messageSource;

    private final MailTemplateBuilder mailTemplateBuilder;

    private final AmazonSimpleEmailService amazonEmailService;

    public static final String UTF_8 = "UTF-8";
    public static final String i8N_FORMAT = "mail.%s.%s";

    public void sendEmail(String fromAddr, String[] toAddr, String subject, String htmlContent, String textContent) {
        log.info("Sending {} email to {}", subject);
        try {

            SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(toAddr))
                    .withMessage(new Message()
                            .withBody(new Body().withHtml(new Content().withCharset(UTF_8).withData(htmlContent))
                                    .withText(new Content().withCharset(UTF_8).withData(textContent)))
                            .withSubject(new Content().withCharset(UTF_8).withData(subject)))
                    .withSource(fromAddr);
            amazonEmailService.sendEmail(request);
        } catch (MessageRejectedException e) {
            log.error("Email could not be sent", e);
        }
    }

    private String buildTemplateText(MailTemplate mailTemplate) {
        StringBuffer textContentBuffer = new StringBuffer();
        textContentBuffer.append(mailTemplate.getTitle());
        textContentBuffer.append(EmailHtmlEnum.RN.getTag());
        textContentBuffer.append(mailTemplate.getContent());
        textContentBuffer.append(EmailHtmlEnum.RN.getTag());
        textContentBuffer.append(mailTemplate.getAction());
        textContentBuffer.append(EmailHtmlEnum.RN.getTag());
        textContentBuffer.append(mailTemplate.getUrl());
        textContentBuffer.append(EmailHtmlEnum.RN.getTag());
        textContentBuffer.append(mailTemplate.getClosing());
        return textContentBuffer.toString();
    }

    private MailTemplate buildGenericTemplate(Locale locale, String language, String url, String templateMiddlePart,
                                              String receiverName) {
        String logoImage = baseURL + AssetsEnum.LOCAL_LOGO.getPath();
        String title = StringUtils.addStringBeforeAndAfter(EmailHtmlEnum.BOLD_START.getTag(),
                getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.TITLE.getStructure(), receiverName),
                EmailHtmlEnum.BOLD_END.getTag());
        String subject = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.SUBJECT.getStructure());
        String action = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.ACTION.getStructure());
        String btnText = getEmailStringText(locale, EmailStructureEnum.GO_TO.getStructure(),
                EmailStructureEnum.BUTTON.getStructure());
        String closing = getEmailStringText(locale, EmailStructureEnum.GENERIC.getStructure(),
                EmailStructureEnum.CLOSING.getStructure());

        return MailTemplate.builder().locale(locale).logoImage(logoImage).title(title).url(url).subject(subject)
                .action(action).btnText(btnText).closing(closing).build();
    }

    @Override
    public void sendPasswordRecoveryEmail(String url, String[] toAddress, String language) {
        MailTemplate mailTemplate = getPasswordRecoveryTemplate(language, url, EmailTemplateEnum.PASSWORD_RECOVER.getTemplate());
        String htmlContent = mailTemplateBuilder.buildEmailTemplate(mailTemplate);
        String textContent = buildTemplateText(mailTemplate);
        sendEmail(emailSender, toAddress, mailTemplate.getSubject(), htmlContent, textContent.toString());
    }

    @Override
    public void sendInviteMerchantEmail(String url, String language, String[] toAddress, String message) {
        MailTemplate mailTemplate = getInviteMerchantTemplate(language, url,
                EmailTemplateEnum.INVITE_MERCHANT.getTemplate(), message);
        String htmlContent = mailTemplateBuilder.buildEmailTemplate(mailTemplate);
        String textContent = buildTemplateText(mailTemplate);
        sendEmail(emailSender, toAddress, mailTemplate.getSubject(), htmlContent, textContent.toString());
    }

    @Override
    public void sendMerchantRegisteredEmail(String url, String language, String merchantName, String[] managerEmails) {
        MailTemplate mailTemplate = getMerchantRegisteredTemplate(language, url, EmailTemplateEnum.MERCHANT_REGISTERED.getTemplate(), merchantName);
        String htmlContent = mailTemplateBuilder.buildEmailTemplate(mailTemplate);
        String textContent = buildTemplateText(mailTemplate);

        sendEmail(emailSender, managerEmails, mailTemplate.getSubject() + merchantName, htmlContent, textContent);
    }

    @Override
    public void sendManagerOtpEmail(String language, String[] managerEmail, Integer otpCode) {
        MailTemplate mailTemplate = getManagerOtpEmailTemplate(language, EmailTemplateEnum.MANAGER_OTP.getTemplate(), otpCode);
        String htmlContent = mailTemplateBuilder.buildEmailTemplate(mailTemplate);
        String textContent = buildTemplateText(mailTemplate);

        sendEmail(emailSender, managerEmail, mailTemplate.getSubject(), htmlContent, textContent);

    }

    @Override
    public void sendApproveMerchantEmail(String[] email, String language, String companyName, UUID token) {
        MailTemplate mailTemplate = getApproveMerchantTemplate(language, baseURL, EmailTemplateEnum.APPROVE_MERCHANT.getTemplate(),
                companyName + EmailHtmlEnum.EXCL.getTag(), token);
        String htmlContent = mailTemplateBuilder.buildEmailTemplate(mailTemplate);
        String textContent = buildTemplateText(mailTemplate);
        sendEmail(emailSender, email, mailTemplate.getSubject(), htmlContent, textContent.toString());
    }

    private MailTemplate getManagerOtpEmailTemplate(String language, String templateMiddlePart, int otpCode) {
        Locale locale = new Locale(language);
        MailTemplate mailTemplate = buildGenericTemplate(locale, language, "", templateMiddlePart, "");

        String content = getContentForManagerOtp(locale, templateMiddlePart, otpCode);
        mailTemplate.setContent(content);
        mailTemplate.setBtnText(null);

        return mailTemplate;
    }

    private MailTemplate getInviteMerchantTemplate(String language, String url, String templateMiddlePart, String message) {
        Locale locale = new Locale(language);
        MailTemplate mailTemplate = buildGenericTemplate(locale, language, url, templateMiddlePart, "");
        String closing = getEmailStringText(locale, EmailStructureEnum.GENERIC.getStructure(),
                EmailStructureEnum.CLOSING.getStructure());
        String btnText = getEmailStringText(locale, EmailStructureEnum.GENERIC.getStructure(),
                EmailStructureEnum.REGISTER_BTN.getStructure());

        mailTemplate.setClosing(closing);
        mailTemplate.setAction(null);
        mailTemplate.setBtnText(btnText);
        mailTemplate.setContent(message);

        return mailTemplate;
    }

    private MailTemplate getApproveMerchantTemplate(String language, String url, String templateMiddlePart, String receiverName, UUID token) {
        Locale locale = new Locale(language);
        MailTemplate mailTemplate = buildGenericTemplate(locale, language, url, templateMiddlePart, receiverName);

        String content = getContentForApproveMerchant(locale, templateMiddlePart, token);
        String btnText = getEmailStringText(locale, EmailStructureEnum.GENERIC.getStructure(),
                EmailStructureEnum.SEE_MAP.getStructure());

        mailTemplate.setAction(null);
        mailTemplate.setBtnText(null);
        mailTemplate.setContent(content);

        return mailTemplate;
    }

    private MailTemplate getPasswordRecoveryTemplate(String language, String url, String templateMiddlePart) {
        Locale locale = new Locale(language);
        MailTemplate mailTemplate = buildGenericTemplate(locale, language, url, templateMiddlePart, "");

        String content = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.CONTENT.getStructure()).replace(EmailHtmlEnum.LINE_BREAK.getTag(), EmailHtmlEnum.RN.getTag());
        mailTemplate.setContent(content);

        return mailTemplate;
    }

    private MailTemplate getMerchantRegisteredTemplate(String language, String url, String templateMiddlePart, String merchantName) {
        Locale locale = new Locale(language);
        MailTemplate mailTemplate = buildGenericTemplate(locale, language, url, templateMiddlePart, "");

        String content = getContentForMerchantRegistered(locale, templateMiddlePart, merchantName);
        mailTemplate.setContent(content);

        return mailTemplate;
    }

    private String getContentForInviteMerchant(Locale locale, String templateMiddlePart) {
        String contentInfo = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.CONTENT.getStructure())
                .replace(EmailHtmlEnum.LINE_BREAK.getTag(), EmailHtmlEnum.RN.getTag());
        return StringUtils.joinStringPieces(contentInfo);
    }

    private String getContentForApproveMerchant(Locale locale, String templateMiddlePart, UUID token) {
        String contentInfo = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.CONTENT.getStructure()).replace(EmailHtmlEnum.LINE_BREAK.getTag(), EmailHtmlEnum.RN.getTag());

        String talerMessage = StringUtils.addStringBeforeAndAfter(EmailHtmlEnum.P_START.getTag(),
                getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.TALER_MESSAGE.getStructure()), EmailHtmlEnum.P_END.getTag());


        String talerInstance = StringUtils.addStringBeforeAndAfter(getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.TALER_INSTANCE.getStructure()),
                EmailHtmlEnum.getLinkTag(talerBaseURL, talerBaseURL), EmailHtmlEnum.LI_END.getTag());


        String talerAccessToken = StringUtils.addStringBeforeAndAfter(getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.TALER_ACCESS_TOKEN.getStructure()),
                String.valueOf(token), EmailHtmlEnum.LI_END.getTag());


        String talerInstructions = StringUtils.addStringBeforeAndAfter(EmailHtmlEnum.LI_START.getTag(),
                getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.TALER_INSTRUCTIONS.getStructure()), EmailHtmlEnum.LI_END.getTag());

        return StringUtils.joinStringPieces(contentInfo, EmailHtmlEnum.LINE_BREAK.getTag(), talerMessage,
                EmailHtmlEnum.getLinkTag(talerBaseURL, talerBaseURL), EmailHtmlEnum.LINE_BREAK.getTag(),
                EmailHtmlEnum.UL_START.getTag(), EmailHtmlEnum.LI_START.getTag(), talerInstance,
                EmailHtmlEnum.LI_START.getTag(), talerAccessToken, talerInstructions, EmailHtmlEnum.UL_END.getTag());
    }


    private String getContentForMerchantRegistered(Locale locale, String templateMiddlePart, String merchantName) {
        String contentInfo = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.CONTENT.getStructure()).replace(EmailHtmlEnum.LINE_BREAK.getTag(), EmailHtmlEnum.RN.getTag());
        String merchant = StringUtils.addStringBeforeAndAfter(EmailHtmlEnum.P_START.getTag(), getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.COMPANY_NAME.getStructure(), merchantName), EmailHtmlEnum.P_END.getTag());
        return StringUtils.joinStringPieces(contentInfo, merchant);
    }

    private String getContentForManagerOtp(Locale locale, String templateMiddlePart, Integer otpCode) {
        String contentInfo = getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.CONTENT.getStructure())
                .replace(EmailHtmlEnum.LINE_BREAK.getTag(), EmailHtmlEnum.RN.getTag());
        String otpCodeContent = StringUtils.addStringBeforeAndAfter(EmailHtmlEnum.H2_START.getTag(), otpCode.toString(), EmailHtmlEnum.H2_END.getTag());
        String otpBeforeContent = StringUtils.addStringBeforeAndAfter(EmailHtmlEnum.LINE_BREAK.getTag(),
                getEmailStringText(locale, templateMiddlePart, EmailStructureEnum.OTP_CODE_MESSAGE.getStructure()), EmailHtmlEnum.LINE_BREAK.getTag());

        return StringUtils.joinStringPieces(contentInfo, otpBeforeContent, otpCodeContent);


    }

    private String getEmailStringText(Locale locale, String templateMiddlePath, String emailPart) {
        return messageSource.getMessage(String.format(i8N_FORMAT, templateMiddlePath, emailPart), null, locale);
    }

    private String getEmailStringText(Locale locale, String templateMiddlePath, String emailPart, String variable) {
        return messageSource.getMessage(String.format(i8N_FORMAT, templateMiddlePath, emailPart), null, locale)
                + variable;
    }
}
