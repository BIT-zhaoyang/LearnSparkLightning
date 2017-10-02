package Chapter3;

import java.io.Serializable;

class AvgCount implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public int total;
    public int num;
    
    public AvgCount(int total, int num) {
        this.total = total;
        this.num = num;
    }
    
    public double avg() {
        return total / (double) num;
    }
}