public class Class6 {
    private int myInt;

    public int getMyInt() {
        synchronized (this) {
            return myInt;
        }
    }

    public void setMyInt(int i) {
        synchronized (this) {
            this.myInt = i;
        }
    }
}
