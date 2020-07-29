package net.vorps.dispatcher;

public class CreateServerException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;

    public CreateServerException(){
        super();
    }

    public CreateServerException(String message) {
        super(message);
    }
    public CreateServerException(Throwable cause) {
        super(cause);
    }

    public CreateServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
