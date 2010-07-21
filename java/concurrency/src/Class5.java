public class Class5 {
    private int myInt;

    public synchronized int getMyInt() {
        return myInt;
    }

    public synchronized void setMyInt(int i) {
        this.myInt = i;
    }
}
