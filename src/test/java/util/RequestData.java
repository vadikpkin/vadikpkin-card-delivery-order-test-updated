package util;

public final class RequestData {

    private final String name;

    private final String tel;

    public RequestData(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public String getTel() {
        return tel;
    }
}
