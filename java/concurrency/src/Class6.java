public class Class6 {
    private int i;

    public int getI() {
        synchronized (this) {
            return i;
        }
    }

    public void setI(int i) {
        synchronized (this) {
            this.i = i;
        }
    }
}