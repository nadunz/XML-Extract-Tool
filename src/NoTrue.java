
import java.util.ArrayList;

public class NoTrue {
    // File path
    private String file;
    // no true element list 
    private ArrayList<WebElementProperties> elementList;

    public NoTrue(String file) {
        this.file = file;
        this.elementList = new ArrayList<>();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public ArrayList<WebElementProperties> getElementList() {
        return elementList;
    }

    public void setElementList(ArrayList<WebElementProperties> elementList) {
        this.elementList = elementList;
    }
    
    public void addElement(WebElementProperties wep){
        this.elementList.add(wep);
    }
    
    public int size(){
        return this.elementList.size();
    }
    
}
