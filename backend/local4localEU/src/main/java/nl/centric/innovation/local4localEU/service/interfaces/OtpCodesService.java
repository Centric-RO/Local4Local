package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.User;

public interface OtpCodesService {
    OtpCodes saveOtpCode(User user);
}
