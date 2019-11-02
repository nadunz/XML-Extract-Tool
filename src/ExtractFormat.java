
public enum ExtractFormat {
    
    FIND_BY("@FindBy"),
    BY("By");
    
    public String name;
    private ExtractFormat(String name) {
        this.name = name;
    }
}
