public class Command {
    boolean isBusy = false;
    private volatile String sharedString;

    public void updateCommand(String sharedString) {
        this.sharedString = sharedString;
    }

    public String getCommand() {
        return sharedString;
    }

    public int getLength(){
        return sharedString.length();
    }
}
