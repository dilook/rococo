package guru.qa.rococo.ex;

public class RequiredParamException extends RuntimeException {
  public RequiredParamException(String message) {
    super(message);
  }
}
