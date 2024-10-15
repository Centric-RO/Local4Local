package nl.centric.innovation.local4localEU.exception;

public class CustomException {
    public static class DtoValidateException extends Exception {
        public DtoValidateException(String message) {
            super(message);
        }
    }

    public static class DtoValidateNotFoundException extends DtoValidateException {
        public DtoValidateNotFoundException(String message) {
            super(message);
        }
    }

    public static class DtoValidateAlreadyExistsException extends DtoValidateException {
        public DtoValidateAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class CaptchaException extends Exception {
        public CaptchaException(String message) {
            super(message);
        }
    }

    public static class AuthenticationLoginException extends Exception {
        public AuthenticationLoginException(String message) {
            super(message);
        }
    }

    public static class InvalidRoleException extends Exception {
        public InvalidRoleException(String message) {
            super(message);
        }
    }

    public static class RecoverException extends Exception {
        public RecoverException(String message) {
            super(message);
        }
    }

    public static class PasswordSameException extends Exception {
        public PasswordSameException(String message) {
            super(message);
        }
    }

    public static class TalerException extends Exception {
        public TalerException(String message) {
            super(message);
        }
    }
}
