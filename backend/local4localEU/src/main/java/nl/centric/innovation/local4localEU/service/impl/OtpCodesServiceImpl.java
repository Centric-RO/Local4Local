package nl.centric.innovation.local4localEU.service.impl;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.entity.OtpCodes;
import nl.centric.innovation.local4localEU.entity.User;
import nl.centric.innovation.local4localEU.repository.OtpCodesRepository;
import nl.centric.innovation.local4localEU.service.interfaces.OtpCodesService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OtpCodesServiceImpl implements OtpCodesService {

    private final OtpCodesRepository otpCodesRepository;

    @Override
    public OtpCodes saveOtpCode(User user) {
        OtpCodes otpCode = OtpCodes.builder()
                .otpCode(otpCodeGenerator())
                .sessionId(UUID.randomUUID())
                .user(user)
                .build();
        otpCodesRepository.save(otpCode);

        return otpCode;
    }

    private int otpCodeGenerator() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

}
