package nl.centric.innovation.local4localEU.service.impl;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.enums.EmailStructureEnum;
import nl.centric.innovation.local4localEU.service.interfaces.MailTemplateBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import util.MailTemplate;

@Service
@RequiredArgsConstructor
public class MailTemplateBuilderImpl implements MailTemplateBuilder {

    private final ITemplateEngine templateEngine;
    public static final String TEMPLATE_NAME = "sendEmailTemplate";

    @Override
    public String buildEmailTemplate(MailTemplate mailTemplate) {
        Context context = new Context();
        context.setLocale(mailTemplate.getLocale());

        context.setVariable(EmailStructureEnum.LOGO_IMAGE.getStructure(), mailTemplate.getLogoImage());
        context.setVariable(EmailStructureEnum.TITLE.getStructure(), mailTemplate.getTitle());
        context.setVariable(EmailStructureEnum.CONTENT.getStructure(), mailTemplate.getContent());
        context.setVariable(EmailStructureEnum.URL.getStructure(), mailTemplate.getUrl());
        context.setVariable(EmailStructureEnum.ACTION.getStructure(), mailTemplate.getAction());
        context.setVariable(EmailStructureEnum.SUBJECT.getStructure(), mailTemplate.getSubject());
        context.setVariable(EmailStructureEnum.CLOSING.getStructure(), mailTemplate.getClosing());
        context.setVariable(EmailStructureEnum.BTN_TEXT.getStructure(), mailTemplate.getBtnText());

        return templateEngine.process(TEMPLATE_NAME, context);
    }

}
