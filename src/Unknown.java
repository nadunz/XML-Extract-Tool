
public class Unknown {
    
    // File path for the unknown properties
    private String file;
    // Unknown properties
    private WebElementProperties webElementProp;

    public Unknown(String file, WebElementProperties webElementProp) {
        this.file = file;
        this.webElementProp = webElementProp;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public WebElementProperties getWebElementProp() {
        return webElementProp;
    }

    public void setWebElementProp(WebElementProperties webElementProp) {
        this.webElementProp = webElementProp;
    }
    
}
