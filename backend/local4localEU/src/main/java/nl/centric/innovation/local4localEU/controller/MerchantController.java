package nl.centric.innovation.local4localEU.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nl.centric.innovation.local4localEU.dto.InviteMerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantDto;
import nl.centric.innovation.local4localEU.dto.MerchantViewDto;
import nl.centric.innovation.local4localEU.entity.Role;
import nl.centric.innovation.local4localEU.exception.CustomException.DtoValidateException;
import nl.centric.innovation.local4localEU.service.interfaces.MerchantInvitationService;
import nl.centric.innovation.local4localEU.service.interfaces.MerchantService;
import nl.centric.innovation.local4localEU.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequiredArgsConstructor
@RequestMapping("/merchant")
public class MerchantController {
    private final MerchantService merchantService;
    private final MerchantInvitationService merchantInvitationService;
    private final UserService userService;

    @Value("${local4localEU.server.name}")
    private String baseURL;

    private static final String RESET_URL = "/recover/reset-password/";

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public ResponseEntity<MerchantDto> saveMerchant(@RequestBody MerchantDto merchantDto,
                                                    @CookieValue(value = "language", defaultValue = "nl-NL") String language) throws DtoValidateException {
        MerchantDto savedMerchant = merchantService.saveMerchant(merchantDto);
        userService.sendMerchantRegisteredEmail(merchantDto.companyName(), language);

        return ResponseEntity.ok(savedMerchant);
    }

    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<MerchantViewDto>> getAllMerchants() {
        return ResponseEntity.ok(merchantService.getAll());
    }

    @GetMapping("/paginated")
    @Secured({Role.ROLE_MANAGER})
    public ResponseEntity<List<MerchantViewDto>> getPaginatedMerchants(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "25") Integer size) {
        return ResponseEntity.ok(merchantService.getPaginatedMerchants(page, size));
    }

    @RequestMapping(path = "/filter/{categoryId}", method = RequestMethod.GET)
    public ResponseEntity<List<MerchantViewDto>> getMerchantsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(merchantService.getByCategory(categoryId));
    }

    @RequestMapping(path = "/count/all", method = RequestMethod.GET)
    @Secured({Role.ROLE_MANAGER})
    public ResponseEntity<Long> countAllMerchants() {
        return ResponseEntity.ok(merchantService.countAll());
    }

    @RequestMapping(path = "/invite", method = RequestMethod.POST)
    @Secured({Role.ROLE_MANAGER})
    public ResponseEntity<Void> inviteSupplier(@RequestBody InviteMerchantDto inviteMerchantDto,
                                               @CookieValue(value = "language", defaultValue = "nl-NL") String language) throws DtoValidateException {

        merchantInvitationService.save(inviteMerchantDto, language);
        return ResponseEntity.ok().build();
    }
}