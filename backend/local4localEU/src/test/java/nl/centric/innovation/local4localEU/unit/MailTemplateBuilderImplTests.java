package nl.centric.innovation.local4localEU.unit;

import nl.centric.innovation.local4localEU.service.impl.MailTemplateBuilderImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;
import util.MailTemplate;

import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MailTemplateBuilderImplTests {

    @InjectMocks
    private MailTemplateBuilderImpl mailTemplateBuilder;

    @Mock
    private ITemplateEngine emailTemplateEngine;

    private MailTemplate mailTemplate;

    @Test
    public void GivenValidMailTemplate_WhenSetContext_ThenContextVariablesShouldBeSet() {

        MailTemplate mailTemplate = mock(MailTemplate.class);
        Locale locale = new Locale("en-US");

        // Given
        when(mailTemplate.getLocale()).thenReturn(locale);
        when(mailTemplate.getLogoImage()).thenReturn("logo.png");
        when(mailTemplate.getTitle()).thenReturn("Sample Title");
        when(mailTemplate.getContent()).thenReturn("Sample Content");
        when(mailTemplate.getUrl()).thenReturn("https://example.com");
        when(mailTemplate.getAction()).thenReturn("Sample Action");
        when(mailTemplate.getSubject()).thenReturn("Sample Subject");
        when(mailTemplate.getClosing()).thenReturn("Sample Closing");
        when(mailTemplate.getBtnText()).thenReturn("Click Me");

        // When
        Context context = new Context();
        context.setLocale(mailTemplate.getLocale());
        context.setVariable("logoImage", mailTemplate.getLogoImage());
        context.setVariable("title", mailTemplate.getTitle());
        context.setVariable("content", mailTemplate.getContent());
        context.setVariable("url", mailTemplate.getUrl());
        context.setVariable("action", mailTemplate.getAction());
        context.setVariable("subject", mailTemplate.getSubject());
        context.setVariable("closing", mailTemplate.getClosing());
        context.setVariable("btnText", mailTemplate.getBtnText());

        // Then
        assertTrue(context.getLocale().equals(mailTemplate.getLocale()));
        assertTrue(context.getVariable("logoImage").equals(mailTemplate.getLogoImage()));
        assertTrue(context.getVariable("content").equals(mailTemplate.getContent()));
        assertTrue(context.getVariable("url").equals(mailTemplate.getUrl()));
        assertTrue(context.getVariable("action").equals(mailTemplate.getAction()));
        assertTrue(context.getVariable("subject").equals(mailTemplate.getSubject()));
        assertTrue(context.getVariable("closing").equals(mailTemplate.getClosing()));
        assertTrue(context.getVariable("btnText").equals(mailTemplate.getBtnText()));
    }

    @Test
    public void GivenValidData_WhenProcessMail_ThenExpectSuccess() {
        MailTemplate mailTemplate = new MailTemplate();
        mailTemplate.setLocale(Locale.ENGLISH);
        mailTemplate.setLogoImage("logoImageURL");
        mailTemplate.setTitle("Test Title");
        mailTemplate.setContent("Test Content");
        mailTemplate.setUrl("Test URL");
        mailTemplate.setAction("Test Action");
        mailTemplate.setSubject("Test Subject");
        mailTemplate.setClosing("Test Closing");
        mailTemplate.setBtnText("Test Button Text");

        String expectedTemplate = "Processed Template";
        when(emailTemplateEngine.process(eq(MailTemplateBuilderImpl.TEMPLATE_NAME), any(Context.class)))
                .thenReturn(expectedTemplate);

        String resultTemplate = mailTemplateBuilder.buildEmailTemplate(mailTemplate);

        verify(emailTemplateEngine, times(1)).process(eq(MailTemplateBuilderImpl.TEMPLATE_NAME), any(Context.class));
        assertEquals(expectedTemplate, resultTemplate);

    }

}
