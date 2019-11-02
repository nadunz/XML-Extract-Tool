/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class WebElementProperties {
    
    private String webElementName;
    private boolean removeQuot;
    private boolean isSelected;
    private String matchCondition;
    public String name;
    private String type;
    private String value;

    public WebElementProperties(String webElementName, boolean removeQuot) {
        this.webElementName = webElementName;
        this.removeQuot = removeQuot;
    }
    
    public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getMatchCondition() {
        return matchCondition;
    }

    public void setMatchCondition(String matchCondition) {
        this.matchCondition = matchCondition;
    }

    public String getName() {
        if(this.name.startsWith("data-"))
            return "xpath";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        if(this.name.startsWith("data-"))
            return "//*[@" + this.name + "='" + this.value + "']";
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * 
     * @param str - input string to be removed &quot;
     * @return the cleaned string from &quot;
     */
    public String removeQuot(String str){
        return str.replace("\"", "");
    }
    
    @Override
    public String toString(){
        String line1 = "private By " + this.webElementName + getTag() + " = By." + this.getName() + "(\"" + 
                (this.removeQuot ? removeQuot(getValue()) : getValue()) + "\");\n";
        String line2 = "@FindBy(" + this.getName() + "=\"" + (this.removeQuot? removeQuot(getValue()):getValue()) + "\")\n"; 
        String line3 = "public static WebElement " + this.webElementName + getTag() + ";\n"; 
        switch(XMLExtractTool.extractFormat){
            case FIND_BY:
                return line2 + line3 + "\n";
            case BY:
                return line1 + "\n";
            default:
                return null;
        }
    }
    
    /**
     * To generate the tag for appending to the web element name
     * @return 
     */
    private String getTag(){
        // check whether the element property name is 'data-locator'
        if(this.name.startsWith("data-"))
            return generateShortName(this.name);
        return this.name.trim().toLowerCase();
    }
    
    public boolean startsWithData_(){
        return this.name.startsWith("data-");
    }
    
    public boolean isDataLocator(){
        return this.name.equalsIgnoreCase("data-locator");
    }
    
    public String generateShortName(String elementName){
        String sn = "";
        String[] split = elementName.split("-");
        for (String word : split) {
            sn = sn.concat(Character.toString(word.charAt(0)));
        }
        return sn;
    }
    
}
