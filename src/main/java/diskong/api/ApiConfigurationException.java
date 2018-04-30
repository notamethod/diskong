package diskong.api;

import java.io.IOException;

public class ApiConfigurationException extends Exception {
    public ApiConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
