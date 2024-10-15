package nl.centric.innovation.local4localEU.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.centric.innovation.local4localEU.repository.OtpCodesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerCron {

    private final OtpCodesRepository otpCodesRepository;

    @Value("${otp.expiration.time}")
    private String otpExpirationTime;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    //    @Scheduled(cron = "0 0 23 ? * SUN")
    @Scheduled(cron = "0 */20 * * * *")
    @Transactional
    public void deleteExpiredOtps() {
        log.info("Scheduler Delete old otp codes task started at : " + sdf.format(new Date()));
        otpCodesRepository.deleteAllByCreatedDateBefore(LocalDateTime.now().minusMinutes(Integer.parseInt(otpExpirationTime) / 60));
    }

}
