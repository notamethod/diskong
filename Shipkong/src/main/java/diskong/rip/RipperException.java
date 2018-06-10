package diskong.rip;

public class RipperException extends Throwable {
    private String messageCode;
    public RipperException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RipperException(String s) {
    super(s);
    }

    public String getMessageCode() {
        return messageCode;
    }

    public RipperException(String error, String info, int exitCode) {
        super(error + "\n" + info);
        //TODO: i18n
        messageCode = error;

    }
}
