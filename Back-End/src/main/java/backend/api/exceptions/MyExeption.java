package backend.api.exceptions;

public abstract class MyExeption extends RuntimeException {
    public MyExeption(String message) {
        super(message);
    }

    // 400 Bad Request

    public static class BadRequest extends MyExeption {
        public BadRequest(String message) {
            super(message);
        }
    }

    // 401 Unauthorized
    public static class Unauthorized extends MyExeption {
        public Unauthorized(String message) {
            super(message);
        }
    }

    // 401 Unauthorized (Bad Credentials)
    public static class BadCredentials extends MyExeption {
        public BadCredentials() {
            super("Bad credentials");
        }
        public BadCredentials(String message) {
            super(message);
        }
    }


    // 404 Not Found
    public static class NotFound extends MyExeption {
        public NotFound(String message) {
            super(message);
        }
    }

    // 405 Method Not Allowed
    public static class MethodNotAllowed extends MyExeption {
        public MethodNotAllowed(String message) {
            super(message);
        }
    }

    public static class Forbidden extends MyExeption {
        public Forbidden(String message) {
            super(message);
        }
    }


    // 409 Conflict
    public static class Conflict extends MyExeption {
        public Conflict(String message) {
            super(message);
        }
    }

    // 500 Internal Server Error
    public static class InternalServerError extends MyExeption {
        public InternalServerError(String message) {
            super(message);
        }
    }



}