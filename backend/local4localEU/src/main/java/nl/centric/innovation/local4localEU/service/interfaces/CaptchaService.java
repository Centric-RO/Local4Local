package nl.centric.innovation.local4localEU.service.interfaces;

public interface CaptchaService {
    boolean isResponseValid(final String response, final String remoteIp);
}
