package nl.centric.innovation.local4localEU.service.interfaces;

import nl.centric.innovation.local4localEU.exception.CustomException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface TalerService {
    void createTallerInstance(String merchantName) throws URISyntaxException, IOException,
            InterruptedException, CustomException.TalerException;
}
