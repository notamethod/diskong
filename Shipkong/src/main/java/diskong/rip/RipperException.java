package diskong.rip;

public class RipperException extends Throwable {
    public RipperException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RipperException(String s) {
    super(s);
    }
}
