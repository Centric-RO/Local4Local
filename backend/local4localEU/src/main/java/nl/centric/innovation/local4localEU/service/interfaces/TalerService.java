package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.exception.CustomException.TalerException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

public interface TalerService {
    UUID createTallerInstance(String merchantName) throws URISyntaxException, IOException,
            InterruptedException, TalerException;
}
